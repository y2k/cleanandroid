package indrih.cleanandroid

import androidx.annotation.CallSuper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import org.jetbrains.anko.AnkoLogger
import indrih.cleanandroid.AbstractEvent.ShowMode.*
import indrih.cleanandroid.router.MainRouter

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

    private var firstAttached = true

    protected val eventScheduler = EventScheduler<Event>()

    @CallSuper
    override fun attachView(view: CleanContract.View<Event>) {
        eventScheduler.attachView(view, writeToLog)

        launch {
            if (firstAttached) {
                onFirstAttached()
                firstAttached = false
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
        coroutineContext.cancelChildren()
        if (writeToLog)
            logMessage("onCleared")
    }

    protected fun dropEventBuffer() {
        GlobalScope.launch {
            eventScheduler.dropEventBuffer()
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
                is Once ->
                    eventScheduler.removeAllEqual(event)

                is EveryTime ->
                    throw IllegalArgumentException("Попытка удалить постоянное уведомление")
            }
        }
    }

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

            if (showMode is Once && showMode.autoRemoval)
                eventScheduler.removeAllEqual(event)
        }
    }

    protected fun createChain() =
        EventChain()

    protected fun deleteChain(chain: EventChain) {
        launch {
            eventScheduler.deleteChain(chain)
        }
    }

    /*
     ******************* Navigation ******************
     */

    protected val allArgs = MainRouter.copyAndDelete().getAllArgs()

    protected inline fun <reified T : Any> getArg(name: String? = null): T =
        allArgs.getArg(name)

    protected fun <S : Screen> navigateTo(screen: S) {
        MainRouter.navigate(screen)
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

    override val coroutineContext: CoroutineContext = job + standardContext

    protected val started = MutexPrimitive(false)

    protected fun singleLaunch(block: suspend CoroutineScope.() -> Unit) {
        launch {
            if (!started.get()) {
                started.set(true)

                try {
                    block()
                } catch (e: Exception) {
                    started.set(false)
                    throw e
                } finally {
                    started.set(false)
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