package indrih.cleanandroid

import kotlinx.coroutines.runBlocking
import org.junit.Test

sealed class MainEvent : AbstractEvent() {
    object M1 : MainEvent()
    object M2 : MainEvent()
    object M3 : MainEvent()
}

sealed class Event : AbstractEvent() {
    object ShowProgress : Event()
    object HideProgress : Event()

    class Foo<M : MainEvent>(val m: M) : Event()
    class Fii(val i: Int) : Event()
}

internal val mutexEventList = MutexEventList<Event>()

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        mutexEventList.addEvent(Event.Foo(MainEvent.M1).withInitToken())
        mutexEventList.addEvent(Event.Foo(MainEvent.M2).withInitToken())
        mutexEventList.addEvent(Event.Foo(MainEvent.M3).withInitToken())
        mutexEventList.addEvent(Event.Fii(10).withInitToken())

        mutexEventList.hashMap.forEach {
            println("key: ${it.key}, value: ${it.value}")
        }

        println(mutexEventList.contains(Event.Foo(MainEvent.M1).withInitToken()))

        println(mutexEventList.removeAllEqual(Event.Foo(MainEvent.M1).withInitToken()))

        mutexEventList.hashMap.forEach {
            println("key: ${it.key}, value: ${it.value}")
        }
        return@runBlocking
    }
}