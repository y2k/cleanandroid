package indrih.cleanandroid

import org.junit.Test

sealed class Event : AbstractEvent() {
    object ShowProgress : Event()
    object HideProgress : Event()

    class Foo : Event()
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
    }
}