package indrih.cleanandroid

import org.jetbrains.anko.AnkoLogger

/**
 * Базовая реализация Gateway-я, от которой нужно наследовать все остальные Gateway-и.
 * Пусть сейчас здесь нет чего-то нужного - всё может измениться. ;)
 */
abstract class CleanGateway : CleanContract.Gateway, AnkoLogger