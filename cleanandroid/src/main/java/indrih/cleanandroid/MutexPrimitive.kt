package indrih.cleanandroid

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MutexPrimitive<T>(private var elem: T) {
    private val mutex = Mutex()

    suspend fun set(value: T) = mutex.withLock {
        elem = value
    }

    suspend fun get(): T = mutex.withLock {
        elem
    }

    internal fun getWithOutLock(): T =
        elem
}

suspend fun MutexPrimitive<Int>.increment() {
    val value = getWithOutLock()
    set(value+1)
}