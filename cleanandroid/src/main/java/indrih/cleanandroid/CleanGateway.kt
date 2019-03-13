package indrih.cleanandroid

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.anko.AnkoLogger

/**
 * Базовая реализация Gateway-я, от которой нужно наследовать все остальные Gateway-и.
 */
abstract class CleanGateway : CleanContract.Gateway, AnkoLogger {
    protected val mutex = Mutex()

    /*
     *************************** Mutable collection *****************************
     */

    protected suspend fun <T> MutableCollection<T>.safeAdd(elem: T) =
        mutex.withLock { add(elem) }

    protected suspend fun <T> MutableCollection<T>.safeAddAll(collection: Collection<T>) =
        mutex.withLock { addAll(collection) }

    protected suspend fun <T> MutableCollection<T>.safeRemove(elem: T) =
        mutex.withLock { remove(elem) }

    protected suspend fun <T> MutableCollection<T>.safeForEach(block: suspend (T) -> Unit) =
        mutex.withLock { for (elem in this) block(elem) }

    protected suspend fun <T> MutableCollection<T>.safeSize() =
        mutex.withLock { size }

    protected suspend fun <T> MutableCollection<T>.safeClear() =
        mutex.withLock { clear() }

    protected suspend fun <T> MutableCollection<T>.safeFirstOrNull(predicate: (T) -> Boolean) =
        mutex.withLock { firstOrNull(predicate) }

    protected suspend fun <T> MutableCollection<T>.safeFirstOrNull() =
        mutex.withLock { firstOrNull() }

    protected suspend fun <T> MutableCollection<T>.safeLastOrNull(predicate: (T) -> Boolean) =
        mutex.withLock { lastOrNull(predicate) }

    protected suspend fun <T> MutableCollection<T>.safeLastOrNull() =
        mutex.withLock { lastOrNull() }

    /*
     *************************** Map *****************************
     */

    protected suspend fun <K, V> MutableMap<K, V>.safePut(key: K, value: V) =
        mutex.withLock { put(key, value) }

    protected suspend fun <K, V> MutableMap<K, V>.safeAddAll(map: MutableMap<K, V>) =
        mutex.withLock { putAll(map) }

    protected suspend fun <K, V> MutableMap<K, V>.safeRemove(key: K) =
        mutex.withLock { remove(key) }

    protected suspend fun <K, V> MutableMap<K, V>.safeForEach(block: suspend (MutableMap.MutableEntry<K, V>) -> Unit) =
        mutex.withLock { for (elem in this) block(elem) }

    protected suspend fun <K, V> MutableMap<K, V>.safeSize() =
        mutex.withLock { size }

    protected suspend fun <K, V> MutableMap<K, V>.safeClear() =
        mutex.withLock { clear() }
}