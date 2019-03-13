package indrih.cleanandroid

import org.junit.Test
import indrih.cleanandroid.AbstractEvent.ShowMode.*

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