package indrih.cleandemo.presentation.presenter

import indrih.cleanandroid.CleanPresenter
import indrih.cleandemo.contract.splash.SplashContract

class SplashPresenter :
    SplashContract.Presenter,
    CleanPresenter<SplashContract.Event, SplashContract.Screen>()
{
    override fun onFirstAttached() {
        super.onFirstAttached()
        navigateTo(SplashContract.Screen.Start)
    }
}