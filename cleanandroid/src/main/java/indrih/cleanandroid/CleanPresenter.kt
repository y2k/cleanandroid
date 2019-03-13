package indrih.cleanandroid

import androidx.annotation.CallSuper
import androidx.navigation.NavController
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import org.jetbrains.anko.AnkoLogger
import indrih.cleanandroid.AbstractEvent.ShowMode.*

/**
 * Базовая реализация Presenter-а, от которой нужно наследовать все остальные Presenter-ы.
 * Абстрактный презентер, в котором организована обработка жизненного цикла запускаемых корутин.
 * Все корутины, запускаемые в наследниках этого класса, будут остановлены как только будет
 * вызван метод [onCleared].
 */
abstract class CleanPresenter<Event, Screen> :
    CoroutineScope,
    CleanContract.Presenter<Event>,
    AnkoLogger
        where Event : AbstractEvent,
              Screen : IScreen
{
    protected var writeToLog = false

    private var view: CleanContract.View<Event>? = null

    private var firstAttached = true

    private val buffer = ArrayList<Event>()

    @CallSuper
    override fun attachView(view: CleanContract.View<Event>) {
        this.view = view
        if (firstAttached) {
            onFirstAttached()
            firstAttached = false
        } else {
            buffer.forEach(view::notify)
        }
        if (writeToLog)
            logMessage("attachView")
    }

    private var navController: NavController? = null

    override fun attachNavData(navController: NavController) {
        this.navController = navController
    }

    @CallSuper
    override fun onFirstAttached() {
        if (writeToLog)
            logMessage("onFirstAttached")
    }

    @CallSuper
    override fun detachView() {
        view = null
        if (writeToLog)
            logMessage("detachView")
    }

    @CallSuper
    override fun onCleared() {
        buffer.clear()
        coroutineContext.cancelChildren()
        navController = null
        if (writeToLog)
            logMessage("onCleared")
    }

    /**
     * Данный метод должен быть вызван только для тех ивентов, которые уже совершили то,
     * зачем создавались, и не нуждаются в повторном отображении.
     */
    override fun eventIsCommitted(event: Event) {
        val showMode = event.showMode
        when (showMode) {
            is Chain -> {
                if (showMode.isEnd())
                    deleteChain(event, showMode)
            }
            is Once -> {
                buffer.removeAllEqual(event)
            }
            is EveryTime -> {
                logError("Попытка удалить постоянное уведомление")
            }
        }
    }

    protected fun <S : Screen> navigateTo(screen: S) {
        MainRouter.navigate(screen, requireNotNull(navController))
    }

    /**
     * Сюда поступают ивенты, которые должны быть восстановлены после смены конфигурации.
     * Скопления одинаковых ивентов, которые все разом будут вываливаться после смены конфигурации,
     * не произойдёт. Буфер ивентов отчищается от уже поступивших ивентов подобного рода.
     */
    @CallSuper
    protected fun notifyUI(event: Event, showMode: AbstractEvent.ShowMode = Once()) {
        event.showMode = showMode

        buffer.removeAllEqual(event)
        buffer.add(event)
        view?.notify(event)
        if (writeToLog)
            logMessage("notifyUI: $event")

        when (showMode) {
            is Chain ->
                if (showMode.isEnd())
                    deleteChain(event, showMode)
            is Once ->
                if (buffer.contains(event) && showMode.autoRemoval)
                    buffer.removeAllEqual(event)
        }
    }

    /**
     * Рекурсивно дропает всю цепочку событий, начиная с конца.
     */
    private fun deleteChain(event: AbstractEvent, chain: Chain) {
        buffer.removeAllEqual(event)
        if (writeToLog)
            logMessage("deleteChain: $event")
        chain.prev?.let {
            deleteChain(it, it.showMode as Chain)
        }
    }

    private val job = SupervisorJob()

    /**
     * По умолчанию все корутины будут запускаться в [Dispatchers.Main] контексте.
     */
    protected val standardContext = Dispatchers.Main

    override val coroutineContext: CoroutineContext
        get() = job + standardContext

    /*
     ******************************** Экстеншены ********************************
     */

    protected suspend inline fun delaySeconds(seconds: Int) =
        delay(seconds * 1000L)
}