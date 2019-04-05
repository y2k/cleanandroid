package indrih.cleanandroid

import org.jetbrains.anko.AnkoLogger

class EventScheduler<Event : AbstractEvent> : AnkoLogger {
    private var view: CleanContract.View<Event>? = null
    private var writeToLog = false

    fun attachView(view: CleanContract.View<Event>, writeToLog: Boolean) {
        this.view = view
        this.writeToLog = writeToLog
        if (writeToLog)
            logMessage("attachView")
    }

    fun detachView() {
        this.view = null
        if (writeToLog)
            logMessage("detachView")
    }

    private val buffer = MutexEventList<Event>()

    suspend fun restoreState(view: CleanContract.View<Event>) {
        buffer.forEachEvent(view::notify)
        if (writeToLog) {
            logMessage("restoreState")
            buffer.forEachEvent {
                logMessage(it.token.toString())
            }
        }
    }

    suspend fun send(event: Event) {
        buffer.removeAllEqual(event)
        buffer.addEvent(event)
        if (writeToLog)
            logMessage("sendEvent: $event")

        val showMode = event.showMode
        if (showMode is AbstractEvent.ShowMode.EventChain) {
            showMode.add(event)
            if (writeToLog)
                logMessage("sendEvent: EventChain $event")
        }
        view?.notify(event)
    }

    /**
     * Рекурсивно дропает всю цепочку событий, начиная с конца.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun deleteChain(chain: AbstractEvent.ShowMode.EventChain) {
        if (writeToLog)
            logMessage("deleteChain")
        chain.delete(onEachEvent = {
            buffer.removeAllEqual(it)
            if (writeToLog)
                logMessage("deleteChain: ${it.token}")
        })
    }

    suspend fun dropEventBuffer() =
        buffer.smartClear()

    suspend fun removeAllEqual(event: Event) {
        if (buffer.contains(event)) {
            buffer.removeAllEqual(event)
            if (writeToLog)
                logMessage("removeAllEqual: ${event.token}")
        }
    }
}