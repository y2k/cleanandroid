package indrih.cleanandroid

import androidx.annotation.CallSuper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import indrih.cleanandroid.CleanContract.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Базовая реализация Presenter-а, от которой нужно наследовать все остальные Presenter-ы.
 * Абстрактный презентер, в котором организована обработка жизненного цикла запускаемых корутин.
 * Все корутины, запускаемые в наследниках этого класса, будут остановлены как только будет
 * вызван метод [onCleared].
 */
abstract class CleanPresenter<Event, Router>(
    val router: Router
) :
    CoroutineScope,
    CleanContract.Presenter<Event>,
    AnkoLogger
        where Event : AbstractEvent,
              Router : CleanContract.Router
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
        }
        buffer.forEach(view::notify)
        if (writeToLog)
            info("attachView")
    }

    @CallSuper
    override fun onFirstAttached() {
        if (writeToLog)
            info("onFirstAttached")
    }

    @CallSuper
    override fun detachView() {
        view = null
        if (writeToLog)
            info("detachView")
    }

    @CallSuper
    override fun onCleared() {
        buffer.clear()
        coroutineContext.cancelChildren()
        if (writeToLog)
            info("onCleared")
    }

    /**
     * Данный метод должен быть вызван только для тех ивентов, которые уже совершили то,
     * зачем создавались, и не нуждаются в повторном отображении.
     */
    override fun eventIsCommitted(event: Event) {
        if (event.prev != null && event.next == null)
            deleteChain(event)
        else if (!event.isOneTime)
            buffer.removeAll { it.equalEvent(event) }
    }

    /**
     * Сюда поступают ивенты, которые должны быть восстановлены после смены конфигурации.
     * Скопления одинаковых ивентов, которые все разом будут вываливаться после смены конфигурации,
     * не произойдёт. Буфер ивентов отчищается от уже поступивших ивентов подобного рода.
     */
    @CallSuper
    protected fun notifyUI(event: Event) {
        if (event.isOneTime) {
            view?.notify(event)
        } else {
            buffer.removeAll { it.equalEvent(event) }
            buffer.add(event)
            view?.notify(event)

            if (event.prev != null && event.next == null)
                deleteChain(event)
        }

        if (writeToLog)
            info("notifyUI: $event")
    }

    private fun deleteChain(event: AbstractEvent) {
        buffer.removeAll { it.equalEvent(event) }
        if (writeToLog)
            info("deleteChain: $event")
        event.prev?.let {
            deleteChain(it)
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