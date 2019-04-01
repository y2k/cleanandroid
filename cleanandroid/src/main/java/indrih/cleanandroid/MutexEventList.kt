package indrih.cleanandroid

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutexEventList<Event : AbstractEvent> {
    internal val hashMap = LinkedHashMap<TypeToken<*>, Event>()

    private val mutex = Mutex()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (event in hashMap)
            stringBuilder.append(event.toString())
        return stringBuilder.toString()
    }

    suspend fun removeAllEqual(event: Event) {
        mutex.withLock {
            val token = event.token
            hashMap.keys.removeAll { it == token }
        }
    }

    suspend fun smartClear() {
        mutex.withLock {
            hashMap.values.removeAll { it.showMode !is AbstractEvent.ShowMode.EveryTime }
        }
    }

    suspend fun addEvent(event: Event) {
        mutex.withLock {
            hashMap.put(event.token, event)
        }
    }

    suspend fun forEachEvent(block: (Event) -> Unit) {
        mutex.withLock {
            for (value in hashMap.values)
                block(value)
        }
    }

    suspend fun contains(event: Event): Boolean {
        mutex.withLock {
            val token = event.token
            return hashMap.filter { it.key == token }.isNotEmpty()
        }
    }
}