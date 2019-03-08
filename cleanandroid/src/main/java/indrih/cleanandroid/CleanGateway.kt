package indrih.cleanandroid

import org.jetbrains.anko.AnkoLogger

/**
 * Базовая реализация Gateway-я, наследуемая всем остальным Gateway-ям.
 *
 * Создан для того, чтобы единожды прописывать всем реализациям [CleanContract.Gateway]
 * необходимые зависимости.
 */
abstract class CleanGateway : CleanContract.Gateway, AnkoLogger