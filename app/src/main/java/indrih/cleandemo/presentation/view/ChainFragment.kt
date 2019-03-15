package indrih.cleandemo.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import indrih.cleandemo.R
import indrih.cleandemo.base.MainFragment
import indrih.cleandemo.contract.chain.ChainContract
import indrih.cleandemo.presentation.presenter.ChainPresenter
import kotlinx.android.synthetic.main.fragment_chain.view.*
import indrih.cleandemo.contract.chain.ChainContract.Event.*

class ChainFragment :
    ChainContract.View,
    MainFragment<ChainContract.Event, ChainContract.Presenter>()
{
    override val presenterFactory: () -> ChainContract.Presenter = {
        ChainPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater
        .inflate(R.layout.fragment_chain, container, false) { view ->
            view.start_button.setOnClickListener {
                presenter.startButtonWasPressed()
            }
        }

    private fun showAlertSpecific(message: String, event: ChainContract.Event) {
        showAlert(
            title = "Это уведомление будет отображаться 3 секунды",
            message = message,
            onOkButtonClick = null,
            onNoButtonClick = null,
            event = event,
            cancelable = false
        )
    }

    override fun notify(event: ChainContract.Event) {
        when (event) {
            is ShowFirst ->
                showAlertSpecific("1", event)

            is ShowSecond ->
                showAlertSpecific("2", event)

            is ShowThird ->
                showAlertSpecific("3", event)

            is Main<*> ->
                notifyMain(event, event.main)
        }
    }
}
