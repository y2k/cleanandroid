package indrih.cleandemo.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import indrih.cleandemo.R
import indrih.cleandemo.base.MainFragment
import indrih.cleandemo.contract.splash.SplashContract
import indrih.cleandemo.presentation.presenter.SplashPresenter

class SplashFragment : MainFragment<SplashContract.Event, SplashContract.Presenter>() {
    override val presenterFactory = ::SplashPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater
        .inflate(R.layout.fragment_splash, container, false)

    override fun notify(event: SplashContract.Event) {}
}
