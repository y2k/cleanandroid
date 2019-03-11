package indrih.cleanandroid

import kotlin.reflect.KCallable
import kotlin.reflect.KClass

/**
 * Базовый, обобщающий контракт для всех остальных контрактов.
 * Контракт обеспечивает связи View <-> Presenter -> Interactor -> Gateway.
 *
 * Использование [Exception] должно быть сведено к минимуму.
 * [Exception] означает внештатную ситуацию, например как разрыв связи с источником данных.
 * Если нужно указать, что функция отработала с ошибкой, то она должна возвращать
 * nullable-тип, где null - сигнал об ошибке, not-null - сигнал об успехе.
 * Если функция ничего не возвращает (по логике), то она должна возвращать [Boolean] как
 * результат выполнения.
 */
interface CleanContract {
    /**
     * Не выполняет сам каких-либо действий, всю поступающую информацию (действия пользователя)
     * передаёт в Presenter.
     * Выполняет приказы Presenter-а (ивенты [AbstractEvent]),
     * например: покажи информацию, спрячь клавиатуру и т.д.
     */
    interface View<Event : AbstractEvent> {
        /**
         * Уведомления, поступающие от презентера.
         */
        fun notify(event: Event)
    }

    /**
     * Создаваемые ивенты должны наследовать этот абстрактный класс.
     */
    abstract class AbstractEvent {
        private val kClass: KClass<out AbstractEvent> = this::class
        private val members: MutableList<KCallable<*>> = mutableListOf()

        init {
            members.addAll(this::class.members)
        }

        fun equalEvent(event: AbstractEvent): Boolean =
            kClass == event.kClass && members == event.members
    }

    /**
     * Получает уведомления от View о действиях пользователя,
     * командует Interactor-у выполнить какие-либо действия, командует View отобразить изменения.
     */
    interface Presenter<Event : AbstractEvent> {
        /**
         * Инициализирующий метод, связывающий View и Presenter.
         */
        fun attachView(view: View<Event>)

        /**
         * Вызывается только при первом attach.
         * При смене конфигурации вызываться не будет.
         * @param sendOneTimeEvent в него стоит отправлять события, которые
         * должны быть отображены лишь единожды - при вызове [onFirstAttached].
         */
        fun onFirstAttached(sendOneTimeEvent: (Event) -> Unit)

        /**
         * Вызывается когда View больше не отображется.
         */
        fun detachView()

        /**
         * Вызывается когда нужно освободить занимаемые ресурсы (например остановить потоки).
         */
        fun onCleared()

        /**
         * Вызывается [View], когда ивент обработан и вновь вызывать его не нужно.
         */
        fun eventIsCommitted(event: Event)
    }

    /**
     * Содержит более сложную логику/бизнес-логику/реализует use case.
     * Получает приказы от Presenter-а (хоть и не знает о нём напрямую), выполняет опредённые действия,
     * обращается к Gateway для получения информации.
     *
     * Функции должны возвращать nullable-типы, если их задача не была выполнена.
     * Если функция по логике ничего не должна возвращать, то возвращаемый тип
     * должен быть [Boolean].
     * В остальных случаях функции должны возвращать not nullable типы.
     */
    interface Interactor

    /**
     * Более известен как Repository.
     * Оболочка, через которую Interactor работает с данными.
     * Gateway абстрагирует от источника данных и скрывает эту информацию.
     * Это позволит, при желании, быстро поменять используемую БД, поменять
     * принцип работы с сетью, ввести оффлайн режим и т.д.
     *
     * Коротко говоря - абстрактный источник данных.
     *
     * Функции должны возвращать nullable-типы, если их задача не была выполнена.
     * Если функция по логике ничего не должна возвращать, то возвращаемый тип
     * должен быть [Boolean].
     * В остальных случаях функции должны возвращать not nullable типы.
     */
    interface Gateway

    /**
     * Роутер для каждого экрана.
     * Каждый экран должен иметь свой роутер с функциями перехода к
     * другим экранам. Такой подход позволяет для каждого экрана прописать
     * и использовать только те переходы, которые ему доступны.
     */
    interface Router
}