package indrih.cleanandroid

import org.junit.Test

sealed class MainEvent : AbstractEvent() {
    class Foo : MainEvent()
    class Bar : MainEvent()
}

sealed class Event : AbstractEvent() {
    object ShowProgress : Event() {
        init {
            next = Run
        }
    }
    object Run : Event() {
        init {
            prev = ShowProgress
            next = HideProgress
        }
    }
    object HideProgress : Event() {
        init {
            prev = Run
        }
    }
}

class Router : CleanContract.Router

class Presenter : CleanPresenter<Event, Router>(Router()) {
    init {

    }
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val presenter = Presenter()
    }
}