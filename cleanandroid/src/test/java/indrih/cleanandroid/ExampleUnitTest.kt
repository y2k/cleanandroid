package indrih.cleanandroid

import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.reflect.Constructor
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

sealed class MainEvent : CleanContract.AbstractEvent() {
    object A : MainEvent()
    class Foo : MainEvent()
}

sealed class EventFirst : CleanContract.AbstractEvent() {
    object B : EventFirst()
    class C : EventFirst()

    class Main<T : MainEvent>(main: T) : EventFirst() {
        init { withInit(main) }
    }
}

class Res {
    private val buffer = ArrayList<EventFirst>()

    fun add(t: EventFirst) {
        buffer.add(t)
    }

    fun printClasses() {
        for (t in buffer) {
            assert(t.clazz != null)
        }
    }
}

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val res = Res()
        //res.add(EventFirst.C())
        //res.add(EventFirst.B)

        res.add(EventFirst.Main(MainEvent.A))
        res.add(EventFirst.Main(MainEvent.Foo()))
        res.printClasses()
    }

}