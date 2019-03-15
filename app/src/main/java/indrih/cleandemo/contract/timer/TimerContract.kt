package indrih.cleandemo.contract.timer

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanContract
import indrih.cleandemo.R
import indrih.cleandemo.base.MainFragment

interface TimerContract : CleanContract {
    interface View : CleanContract.View<Event>

    sealed class Event : AbstractEvent() {
        class ShowEnteredText(val text: String) : Event()

        class SetCounterValue(val value: Int) : Event()

        /**
         * Оборачиваищий ивент над [MainFragment.MainEvent].
         */
        class Main<T : MainFragment.MainEvent>(val main: T) : Event()
    }

    interface Presenter : CleanContract.Presenter<Event> {
        fun startButtonWasPressed()
        fun stopButtonWasPressed()
        fun nextButtonWasPressed()
    }

    interface Interactor : CleanContract.Interactor

    interface Gateway : CleanContract.Gateway

    sealed class Screen(
        action: Int,
        vararg pairs: Pair<String, Any>
    ) : AbstractScreen(
        action,
        *pairs
    ) {
        object Chain : Screen(
            R.id.action_timerFragment_to_chainFragment
        )
    }
}