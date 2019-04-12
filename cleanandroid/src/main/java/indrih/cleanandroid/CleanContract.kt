package indrih.cleanandroid

/**
 * Базовый, обобщающий контракт для всех остальных контрактов.
 * Контракт обеспечивает связи View <-> Presenter -> Interactor -> Gateway.
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
     * Получает уведомления от View о действиях пользователя,
     * командует Interactor-у выполнить какие-либо действия, командует View отобразить изменения.
     */
    interface Presenter<Event : AbstractEvent> {
        var activity: CleanActivity

        /**
         * Инициализирующий метод, связывающий View и Presenter.
         */
        fun attachView(view: View<Event>)

        /**
         * Вызывается только при первом attach.
         * При смене конфигурации вызываться не будет.
         */
        fun onFirstAttached()

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

        @Deprecated(
            message = "Use router.navigateUp()",
            replaceWith = ReplaceWith("router.navigateUp()"),
            level = DeprecationLevel.ERROR
        )
        fun navigateUp()

        @Deprecated(
            message = "Use router.popBackStack()",
            replaceWith = ReplaceWith("router.popBackStack()"),
            level = DeprecationLevel.ERROR
        )
        fun popBackStack()
    }

    /**
     * Содержит более сложную логику/бизнес-логику/реализует use case.
     * Получает приказы от Presenter-а (хоть и не знает о нём напрямую), выполняет опредённые действия,
     * обращается к Gateway для получения информации.
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
     */
    interface Gateway
}
