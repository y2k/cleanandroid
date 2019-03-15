package indrih.cleandemo.base

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.CleanContract
import indrih.cleanandroid.CleanRetainFragment

abstract class MainFragment<Event, Presenter> : CleanRetainFragment<Event, Presenter>()
        where Event : AbstractEvent,
              Presenter : CleanContract.Presenter<Event>
{
    sealed class MainEvent : AbstractEvent() {

        /**
         * Скрывает уведомление, показанное на экране.
         * Работает как для Toast-ов, так и для Alert-ов.
         * Если такового нет, то ничего не происходит.
         */
        object HideNotifyEvent : MainEvent()

        /**
         * Поднять клавиатуру.
         */
        object ShowKeyboard : MainEvent()

        /**
         * Опустить клавиатуру.
         */
        object HideKeyboard : MainEvent()
    }

    fun notifyMain(event: Event, mainEvent: MainEvent) {
        when (mainEvent) {
            is MainEvent.HideNotifyEvent ->
                hideNotifyEvent(event)

            is MainEvent.ShowKeyboard ->
                showKeyboard(event)

            is MainEvent.HideKeyboard ->
                hideKeyboard(event)

            else -> {}
        }
    }
}