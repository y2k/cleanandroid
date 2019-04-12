package indrih.cleandemo.presentation.presenter

import indrih.cleanandroid.CleanPresenter
import indrih.cleandemo.contract.start.StartContract
import indrih.cleandemo.contract.start.StartContract.Screen.*
import indrih.cleandemo.contract.start.StartContract.Event.*

class StartPresenter :
    StartContract.Presenter,
    CleanPresenter<StartContract.Event, StartContract.Screen>()
{
    override fun timerButtonWasPressed(text: String) {
        notifyUI(
            DoYouConfirmSwitchToNext(
                onOkButtonClick = { router.navigateTo(Timer(text)) }
            )
        )
    }
}