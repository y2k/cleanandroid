package indrih.cleandemo.contract.chain

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanContract
import indrih.cleandemo.base.MainFragment

interface ChainContract : CleanContract {
    interface View : CleanContract.View<Event>

    sealed class Event : AbstractEvent() {
        object ShowFirst : Event()
        object ShowSecond : Event()
        object ShowThird : Event()

        /**
         * Оборачиваищий ивент над [MainFragment.MainEvent].
         */
        class Main<T : MainFragment.MainEvent>(val main: T) : Event()
    }

    interface Presenter : CleanContract.Presenter<Event> {
        fun startButtonWasPressed()
    }

    interface Interactor : CleanContract.Interactor

    interface Gateway : CleanContract.Gateway

    sealed class Screen(action: Int) : AbstractScreen(action)
}