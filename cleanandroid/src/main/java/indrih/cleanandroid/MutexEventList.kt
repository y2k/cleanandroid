package indrih.cleanandroid

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutexEventList<Event : AbstractEvent> {
    private val arrayList = ArrayList<Event>()

    private val mutex = Mutex()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (event in arrayList)
            stringBuilder.append(event.toString())
        return stringBuilder.toString()
    }

    suspend fun removeAllEqual(event: AbstractEvent) {
        mutex.withLock {
            arrayList.removeAll { it.equalEvent(event) }
        }
    }

    suspend fun smartClear() {
        mutex.withLock {
            arrayList.removeAll { it.showMode !is AbstractEvent.ShowMode.EveryTime }
        }
    }

    suspend fun addEvent(event: Event) {
        mutex.withLock {
            arrayList.add(event)
        }
    }

    suspend fun forEachEvent(block: (Event) -> Unit) {
        mutex.withLock {
            arrayList.forEach(block)
        }
    }

    suspend fun contains(event: Event): Boolean {
        mutex.withLock {
            return arrayList.contains(event)
        }
    }
}