package indrih.cleanandroid

import androidx.annotation.CallSuper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import indrih.cleanandroid.CleanContract.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Базовая реализация Presenter-а, наследуемая всем остальным Presenter-ам.
 *
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
    protected var writeLog = false

    private var view: CleanContract.View<Event>? = null

    private var firstAttached = true

    private val buffer = ArrayList<Event>()

    @CallSuper
    override fun attachView(view: CleanContract.View<Event>) {
        this.view = view
        if (firstAttached) {
            onFirstAttached{ event ->
                this.view?.notify(event)
            }
            firstAttached = false
        }
        buffer.forEach(view::notify)
        if (writeLog) info("attachView")
    }

    @CallSuper
    override fun onFirstAttached(sendOneTimeEvent: (Event) -> Unit) {
        if (writeLog) info("onFirstAttached")
    }

    @CallSuper
    override fun detachView() {
        view = null
        if (writeLog) info("detachView")
    }

    @CallSuper
    override fun onCleared() {
        buffer.clear()
        coroutineContext.cancelChildren()
    }

    override fun eventIsCommitted(event: Event) {
        buffer.removeAll { it::class == event.clazz }
        if (writeLog) info("eventIsCommitted: ${event.clazz}")
    }

    /**
     * Сюда поступают ивенты, которые должны быть восстановлены после смены конфигурации.
     * Скопления одинаковых ивентов, которые все разом будут вываливаться после смены конфигурации,
     * не произойдёт. Буфер ивентов отчищается от уже поступивших ивентов подобного рода.
     */
    @CallSuper
    protected fun notifyUI(event: Event) {
        buffer.removeAll { it::class == event.clazz }
        buffer.add(event)
        view?.notify(event)
        if (writeLog) info("notifyUI: ${event.clazz}")
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