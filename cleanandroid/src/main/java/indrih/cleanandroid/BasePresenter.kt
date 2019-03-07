package indrih.cleanandroid

import androidx.annotation.CallSuper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Базовая реализация Presenter-а, наследуемая всем остальным Presenter-ам.
 *
 * Абстрактный презентер, в котором организована обработка жизненного цикла запускаемых корутин.
 * Все корутины, запускаемые в наследниках этого класса, будут остановлены как только будет
 * вызван метод [detachView].
 */
abstract class BasePresenter<View, Router>(
    val router: Router
) :
    CoroutineScope,
    BaseContract.Presenter<View>
        where View : BaseContract.View,
              Router : BaseContract.Router
{
    private var view: View? = null

    private var firstAttached = true

    private val buffer = ArrayList<BaseContract.BaseEvent>()

    @CallSuper
    override fun attachView(view: View) {
        this.view = view
        if (firstAttached) {
            onFirstAttached()
            firstAttached = false
        }
        buffer.forEach(view::notify)
    }

    @CallSuper
    override fun onFirstAttached() = Unit

    @CallSuper
    override fun detachView() {
        view = null
    }

    @CallSuper
    override fun onCleared() {
        buffer.clear()
        coroutineContext.cancelChildren()
    }

    override fun <Event : BaseContract.BaseEvent> eventIsCommitted(event: Event) {
        buffer.removeAll { it::class == event::class }
    }

    @CallSuper
    protected fun notifyUI(event: BaseContract.BaseEvent) {
        buffer.removeAll { it::class == event::class }
        buffer.add(event)
        view?.notify(event)
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