package indrih.cleanandroid

import org.junit.Test

sealed class Event : AbstractEvent() {
    object ShowProgress : Event() {
        init { next = HideProgress }
    }
    object HideProgress : Event() {
        init { prev = ShowProgress }
    }

    class Foo : Event() {
        init { isOneTime = true }
    }
}

class Router : CleanContract.Router

class Presenter : CleanPresenter<Event, Router>(Router()) {
    init {
        writeToLog = true
        notifyUI(Event.ShowProgress)
        notifyUI(Event.HideProgress)
    }
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val presenter = Presenter()
    }
}