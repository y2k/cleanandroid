package indrih.cleanandroid

import org.junit.Test
import indrih.cleanandroid.AbstractEvent.ShowMode.*

sealed class Event : AbstractEvent() {
    object ShowProgress : Event()
    object HideProgress : Event()

    class Foo : Event()
}

class Router : CleanContract.Router

class Presenter : CleanPresenter<Event, Router>(Router()) {
    init {
        writeToLog = true
        notifyUI(
            Event.ShowProgress,
            showMode = Chain(next = Event.HideProgress)
        )
        notifyUI(
            Event.HideProgress,
            showMode = Chain(prev = Event.ShowProgress)
        )
    }
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val presenter = Presenter()
    }
}