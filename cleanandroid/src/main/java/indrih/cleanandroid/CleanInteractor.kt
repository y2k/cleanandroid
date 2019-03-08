package indrih.cleanandroid

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger

/**
 * Базовая реализация Interactor-а, наследуемая всем остальным Interactor-ам.
 *
 * Создан для того, чтобы единожды прописывать всем реализациям [CleanContract.Interactor]
 * необходимые зависимости.
 */
abstract class CleanInteractor : CleanContract.Interactor, AnkoLogger {
    protected val standardContext = Dispatchers.Default

    protected suspend inline fun <T> def(noinline block: suspend CoroutineScope.() -> T): T =
        withContext(standardContext, block = block)
}