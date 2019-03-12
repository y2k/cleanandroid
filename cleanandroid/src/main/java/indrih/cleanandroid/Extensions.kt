package indrih.cleanandroid

fun <Event : AbstractEvent> ArrayList<Event>.removeAllEqual(event: AbstractEvent) {
    removeAll { it.equalEvent(event) }
}