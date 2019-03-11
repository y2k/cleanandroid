package indrih.cleanandroid

/**
 * Создаваемые ивенты должны наследовать этот абстрактный класс.
 * Позволяет организовывать цепочки событий: все события, входящие в эту цепочку,
 * будут удалены из буфера событий только тогда, когда вся цепочка отобразится на экране.
 * Пример использования: Show progress - hide progress. Пока не придёт уведомление hide progress,
 * даже при повороте экрана всё равно необходимо отображать прогресс.
 * Но цепочки имеют ограниченную функциональность - на данном этапе они подходят только для
 * событий, реализованных через object.
 */
abstract class AbstractEvent {
    /**
     * Ссылка на предыдущее звено цепочки.
     */
    var prev: AbstractEvent? = null
        protected set(value) {
            if (value != null) {
                val messageError = checkAllForChain(value)
                if (messageError != null)
                    throw IllegalArgumentException(messageError)
            }
            field = value
        }

    /**
     * Ссылка на следующее звено цепочки.
     */
    var next: AbstractEvent? = null
        protected set(value) {
            if (value != null) {
                val message = checkAllForChain(value)
                if (message != null)
                    throw IllegalArgumentException(message)
            }
            field = value
        }

    /**
     * Если true, то ивент будет удалён сразу после отображения.
     */
    var isOneTime: Boolean = false
        protected set(value) {
            if (value) {
                val message = checkAllForOneTimeEvent()
                if (message != null)
                    throw IllegalArgumentException(message)
            }
            field = value
        }

    private fun checkAllForChain(event: AbstractEvent) =
        when {
            event.kClass == this.kClass ->
                "Циклическая зависимость"
            isOneTime || event.isOneTime->
                "Одноразовый ивент не может иметь цепочек"
            else ->
                null
        }

    private fun checkAllForOneTimeEvent() =
        when {
            prev != null || next != null ->
                "Одноразовый ивент не может иметь цепочек"
            else ->
                null
        }

    private val kClass = this::class
    private val members = this::class.members

    /**
     * true, если ивенты полностью совпадают.
     */
    fun equalEvent(event: AbstractEvent): Boolean =
        kClass == event.kClass && members == event.members
}
