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
    protected var writeToLog = false

    private var view: CleanContract.View<Event>? = null

    private var firstAttached = true

    private val buffer = ArrayList<Event>()

    @CallSuper
    override fun attachView(view: CleanContract.View<Event>) {
        this.view = view
        if (firstAttached) {
            launch {
                onFirstAttached()
                firstAttached = false
            }
        }
        buffer.forEach(view::notify)
        if (writeToLog) info("attachView")
    }

    @CallSuper
    override suspend fun onFirstAttached() {
        if (writeToLog) info("onFirstAttached")
    }

    @CallSuper
    override fun detachView() {
        view = null
        if (writeToLog) info("detachView")
    }

    @CallSuper
    override fun onCleared() {
        buffer.clear()
        coroutineContext.cancelChildren()
    }

    override fun eventIsCommitted(event: Event) {
        buffer.removeAll { it::class == event.clazz }
        if (writeToLog) info("eventIsCommitted: ${event.clazz}")
    }

    /**
     * Если вызов идёт из [onFirstAttached], то данный ивент не будет добавлен в буфер.
     * Это сделано потому, что ивенты, отправляемые из [onFirstAttached], должны быть доставлены
     * лишь единожды. Если ивент нужно доставлять как обычно, то отправку нужно
     * положить в [attachView] (обязательно после вызова super.attachView).
     */
    @CallSuper
    protected fun notifyUI(event: Event) {
        if (!firstAttached) {
            buffer.removeAll { it::class == event.clazz }
            buffer.add(event)
        }
        view?.notify(event)
        if (writeToLog) info("notifyUI: ${event.clazz}")
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