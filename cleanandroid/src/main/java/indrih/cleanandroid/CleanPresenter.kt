package indrih.cleanandroid

import androidx.annotation.CallSuper
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
              Screen : AbstractScreen
{
    var writeToLog = false

    /*
     ******************* View ******************
     */

    private val firstAttached = MutexPrimitive(true)

    val eventScheduler = EventScheduler<Event>()

    @CallSuper
    override fun attachView(view: CleanContract.View<Event>) {
        eventScheduler.attachView(view)

        launch {
            if (firstAttached.get()) {
                onFirstAttached()
                firstAttached.set(false)
            } else {
                eventScheduler.restoreState(view)
            }
            if (writeToLog)
                logMessage("attachView")
        }
    }

    @CallSuper
    override fun onFirstAttached() {
        if (writeToLog)
            logMessage("onFirstAttached")
    }

    @CallSuper
    override fun detachView() {
        eventScheduler.detachView()
        if (writeToLog)
            logMessage("detachView")
    }

    @CallSuper
    override fun onCleared() {
        GlobalScope.launch {
            eventScheduler.onCleared()
            firstAttached.set(true)
            coroutineContext.cancelChildren()
            if (writeToLog)
                logMessage("onCleared")
        }
    }

    /**
     * Данный метод должен быть вызван только для тех ивентов, которые уже совершили то,
     * зачем создавались, и не нуждаются в повторном отображении.
     */
    override fun eventIsCommitted(event: Event) {
        val showMode = event.showMode
        launch {
            when (showMode) {
                is Chain ->
                    if (showMode.isEnd())
                        eventScheduler.deleteChain(event, showMode)

                is Once ->
                    eventScheduler.removeAllEqual(event)

                is EveryTime ->
                    logError("Попытка удалить постоянное уведомление")
            }
        }
    }

    protected fun <S : Screen> navigateTo(screen: S) {
        MainRouter.navigate(screen)
    }

    protected inline fun <reified T : Any> getArg(name: String? = null): T =
        MainRouter.getArg(name)

    protected fun getAllArgs(): HashMap<String, Any> =
        MainRouter.getAllArgs()

    /**
     * Сюда поступают ивенты, которые должны быть восстановлены после смены конфигурации.
     * Скопления одинаковых ивентов, которые все разом будут вываливаться после смены конфигурации,
     * не произойдёт. Буфер ивентов отчищается от уже поступивших ивентов подобного рода.
     */
    @CallSuper
    protected inline fun <reified E : Event> notifyUI(
        event: E,
        showMode: AbstractEvent.ShowMode = Once()
    ) {
        event.init(showMode)

        launch {
            eventScheduler.send(event)
            if (writeToLog)
                logMessage("notifyUI: $event")

            when (showMode) {
                is Chain ->
                    if (showMode.isEnd() && showMode.autoRemoval)
                        eventScheduler.deleteChain(event, showMode)
                is Once ->
                    if (showMode.autoRemoval)
                        eventScheduler.removeAllEqual(event)
            }
        }
    }

    override fun popBackStack() {
        MainRouter.popBackStack()
    }

    /*
     ******************* Coroutine ******************
     */

    private val job = SupervisorJob()

    /**
     * По умолчанию все корутины будут запускаться в [Dispatchers.Main] контексте.
     */
    protected val standardContext = Dispatchers.Main

    override val coroutineContext: CoroutineContext
        get() = job + standardContext

    @Volatile
    protected var started = false

    protected fun singleLaunch(block: suspend CoroutineScope.() -> Unit) {
        if (!started) {
            started = true
            launch {
                try {
                    block()
                } catch (e: Exception) {
                    started = false
                    throw e
                } finally {
                    started = false
                }
            }
        }
    }

    /*
     ******************************** Экстеншены ********************************
     */

    protected suspend inline fun delaySeconds(seconds: Int) =
        delay(seconds * 1000L)
}