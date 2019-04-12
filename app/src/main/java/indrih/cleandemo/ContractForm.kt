package indrih.cleandemo

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanContract
import indrih.cleandemo.base.MainFragment

interface Contract : CleanContract {
    interface View : CleanContract.View<Event>

    sealed class Event : AbstractEvent() {

        /**
         * Оборачиваищий ивент над [MainFragment.MainEvent].
         */
        class Main<T : MainFragment.MainEvent>(val main: T) : Event()
    }

    interface Presenter : CleanContract.Presenter<Event>

    interface Interactor : CleanContract.Interactor

    interface Gateway : CleanContract.Gateway

    sealed class Screen(action: Int) : AbstractScreen(action)
}