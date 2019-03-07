package indrih.cleanandroid

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Базовая реализация Interactor-а, наследуемая всем остальным Interactor-ам.
 *
 * Создан для того, чтобы единожды прописывать всем реализациям [BaseContract.Interactor]
 * необходимые зависимости.
 */
abstract class BaseInteractor : BaseContract.Interactor {
    protected val standardContext = Dispatchers.Default

    protected suspend inline fun <T> def(noinline block: suspend CoroutineScope.() -> T): T =
        withContext(standardContext, block = block)
}