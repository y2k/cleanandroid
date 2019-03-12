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
    lateinit var showMode: ShowMode
        internal set

    sealed class ShowMode {
        class Once(var autoremoval: Boolean = true) : ShowMode()

        object EveryTime : ShowMode()

        class Chain(
            val prev: AbstractEvent? = null,
            val next: AbstractEvent? = null
        ) : ShowMode() {
            fun isEnd() =
                prev != null && next == null

            init {
                if (prev == null && next == null)
                    throw IllegalArgumentException("Звено цепи должно иметь как минимум одну связь")
            }
        }
    }

    private val kClass = this::class
    private val members = this::class.members

    /**
     * true, если ивенты полностью совпадают.
     */
    fun equalEvent(event: AbstractEvent): Boolean =
        kClass == event.kClass && members == event.members
}
