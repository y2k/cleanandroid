package indrih.cleanandroid

class EventScheduler<Event : AbstractEvent> {
    private var view: CleanContract.View<Event>? = null

    fun attachView(view: CleanContract.View<Event>) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    private val buffer = MutexEventList<Event>()

    suspend fun restoreState(view: CleanContract.View<Event>) {
        buffer.forEachEvent(view::notify)
    }

    suspend fun send(event: Event) {
        buffer.removeAllEqual(event)
        buffer.addEvent(event)
        view?.notify(event)
    }

    /**
     * Рекурсивно дропает всю цепочку событий, начиная с конца.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun deleteChain(event: Event, chain: AbstractEvent.ShowMode.Chain) {
        buffer.removeAllEqual(event)
        chain.prev?.let {
            deleteChain(it as Event, it.showMode as AbstractEvent.ShowMode.Chain)
        }
    }

    suspend fun dropEventBuffer() =
        buffer.smartClear()

    suspend fun removeAllEqual(event: Event) {
        if (buffer.contains(event))
            buffer.removeAllEqual(event)
    }
}