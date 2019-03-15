package indrih.cleandemo.presentation.presenter

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.CleanPresenter
import indrih.cleandemo.base.MainFragment.MainEvent.*
import indrih.cleandemo.contract.chain.ChainContract
import kotlinx.coroutines.launch
import indrih.cleandemo.contract.chain.ChainContract.Event.*

class ChainPresenter :
    ChainContract.Presenter,
    CleanPresenter<ChainContract.Event, ChainContract.Screen>()
{
    init {
        writeToLog = true
    }
    override fun startButtonWasPressed() {
        launch {
            notifyUI(
                ShowFirst,
                showMode = AbstractEvent.ShowMode.Chain(
                    next = ShowSecond
                )
            )
            delaySeconds(3)
            notifyUI(
                ShowSecond,
                showMode = AbstractEvent.ShowMode.Chain(
                    prev = ShowFirst,
                    next = ShowThird
                )
            )
            delaySeconds(3)
            notifyUI(
                ShowThird,
                showMode = AbstractEvent.ShowMode.Chain(
                    prev = ShowSecond
                )
            )
            delaySeconds(3)
            notifyUI(Main(HideNotifyEvent))
        }
    }
}