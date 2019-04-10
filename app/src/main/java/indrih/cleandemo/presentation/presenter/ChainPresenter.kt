package indrih.cleandemo.presentation.presenter

import indrih.cleanandroid.CleanPresenter
import indrih.cleandemo.base.MainFragment
import indrih.cleandemo.contract.chain.ChainContract
import kotlinx.coroutines.launch
import indrih.cleandemo.contract.chain.ChainContract.Event.*

class ChainPresenter :
    ChainContract.Presenter,
    CleanPresenter<ChainContract.Event, ChainContract.Screen>()
{
    override fun startButtonWasPressed() {
        launch {
            val chain = createChain()

            notifyUI(ShowFirst, showMode = chain)
            delaySeconds(3)
            notifyUI(ShowSecond, showMode = chain)
            delaySeconds(3)
            notifyUI(ShowThird, showMode = chain)
            delaySeconds(3)

            deleteChain(chain)
            notifyUI(Main(MainFragment.MainEvent.HideNotifyEvent))
        }
    }
}