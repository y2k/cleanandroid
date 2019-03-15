package indrih.cleanandroid

fun <Event : AbstractEvent> ArrayList<Event>.removeAllEqual(event: AbstractEvent) {
    removeAll { it.equalEvent(event) }
}

fun <Event : AbstractEvent> ArrayList<Event>.smartClear() {
    for (event in this) {
        if (event.showMode !is AbstractEvent.ShowMode.EveryTime)
            remove(event)
    }
}