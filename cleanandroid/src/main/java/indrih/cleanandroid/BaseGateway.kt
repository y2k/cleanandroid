package indrih.cleanandroid

/**
 * Базовая реализация Gateway-я, наследуемая всем остальным Gateway-ям.
 *
 * Создан для того, чтобы единожды прописывать всем реализациям [BaseContract.Gateway]
 * необходимые зависимости.
 */
abstract class BaseGateway : BaseContract.Gateway