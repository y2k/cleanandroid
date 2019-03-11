package indrih.cleanandroid

/**
 * Создаваемые ивенты должны наследовать этот абстрактный класс.
 */
abstract class AbstractEvent {
    var prev: AbstractEvent? = null
        protected set(value) {
            if (value != null) {
                val messageError = checkAllForChain(value)
                if (messageError != null)
                    throw IllegalArgumentException(messageError)
            }
            field = value
        }

    var next: AbstractEvent? = null
        protected set(value) {
            if (value != null) {
                val message = checkAllForChain(value)
                if (message != null)
                    throw IllegalArgumentException(message)
            }
            field = value
        }

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

    fun equalEvent(event: AbstractEvent): Boolean =
        kClass == event.kClass && members == event.members
}
