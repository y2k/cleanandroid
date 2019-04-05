package indrih.cleanandroid

import com.google.gson.reflect.TypeToken

/**
 * Создаваемые ивенты должны наследовать этот абстрактный класс.
 */
abstract class AbstractEvent {
    lateinit var showMode: ShowMode
    lateinit var token: TypeToken<*>

    /**
     * Режимы отображения Event-а.
     */
    sealed class ShowMode {
        /**
         * Будет отображён лишь до тех пор, пока не выполнит своего назначения.
         * @param autoRemoval если `true`, то после вывода на экран такие события будут удалены
         * автоматически (за исключением тех, что будут переданы в [CleanRetainFragment.showAlert] и т.д.).
         * Если Вы хотите вручную отчищать событие (это можно сделать с помощью
         * [CleanPresenter.eventIsCommitted]) - установите значение `false`.
         */
        class Once(var autoRemoval: Boolean = true) : ShowMode()

        /**
         * Будет отображаться при каждом [CleanPresenter.attachView] вплоть до [CleanPresenter.onCleared].
         */
        object EveryTime : ShowMode()

        /**
         * Позволяет организовывать цепочки событий: все события, входящие в эту цепочку,
         * будут удалены из буфера событий только тогда, когда будет вызван [delete].
         * Пример использования: Show progress - hide progress. Пока не придёт уведомление hide progress,
         * даже при повороте экрана всё равно необходимо отображать прогресс.
         */
        class EventChain internal constructor() : ShowMode() {
            private val list = MutexEventList<AbstractEvent>()

            private var isFinished = false

            internal suspend fun add(event: AbstractEvent) {
                if (isFinished) {
                    throw IllegalStateException(
                        "Данный EventChain уже завершил свою работу и не может быть переиспользован"
                    )
                }
                list.addEvent(event)
            }

            internal suspend fun delete(onEachEvent: suspend (AbstractEvent) -> Unit) {
                isFinished = true
                list.forEachEventSuspend(onEachEvent)
            }
        }

        /**
         * Позволяет организовывать цепочки событий: все события, входящие в эту цепочку,
         * будут удалены из буфера событий только тогда, когда вся цепочка отобразится на экране.
         * Пример использования: Show progress - hide progress. Пока не придёт уведомление hide progress,
         * даже при повороте экрана всё равно необходимо отображать прогресс.
         * Но цепочки имеют ограниченную функциональность - на данном этапе они подходят только для
         * событий, реализованных через object.
         */
        @Deprecated(
            message = "Используйте createChain() и deleteChain()",
            level = DeprecationLevel.ERROR
        )
        class Chain(
            val prev: AbstractEvent? = null,
            val next: AbstractEvent? = null,
            var autoRemoval: Boolean = true
        ) : ShowMode() {
            fun isEnd() =
                prev != null && next == null

            init {
                if (prev == null && next == null)
                    throw IllegalArgumentException("Звено цепи должно иметь как минимум одну связь")

                if (prev != null)
                    checkChainLinkForCorrectness(prev)

                if (next != null)
                    checkChainLinkForCorrectness(next)
            }

            private fun checkChainLinkForCorrectness(event: AbstractEvent) {
                val showMode = try {
                    event.showMode
                } catch (e: Exception) {
                    return
                }

                try {
                    //assert(showMode is Chain)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Все звенья цепи должны иметь showMode Chain")
                }
            }
        }
    }
}