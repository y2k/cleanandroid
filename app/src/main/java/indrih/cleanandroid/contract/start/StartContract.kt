package indrih.cleanandroid.contract.start

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanContract
import indrih.cleanandroid.EventButtonClick
import indrih.cleanandroid.base.MainFragment

interface StartContract : CleanContract {
    interface View : CleanContract.View<Event>

    sealed class Event : AbstractEvent() {
        class DoYouConfirmSwitchToNext(
            val onOkButtonClick: EventButtonClick = {}
        ) : Event()

        /**
         * Оборачиваищий ивент над [MainFragment.MainEvent].
         */
        class Main<T : MainFragment.MainEvent>(val main: T) : Event()
    }

    interface Presenter : CleanContract.Presenter<Event>

    interface Interactor : CleanContract.Interactor

    interface Gateway : CleanContract.Gateway

    sealed class Screen(
        action: Int,
        vararg pairs: Pair<String, Any>
    ) : AbstractScreen(action, *pairs)
}