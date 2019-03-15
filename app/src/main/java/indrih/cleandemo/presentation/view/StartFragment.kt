package indrih.cleandemo.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import indrih.cleandemo.R
import indrih.cleandemo.base.MainFragment
import indrih.cleandemo.contract.start.StartContract
import indrih.cleandemo.presentation.presenter.StartPresenter
import indrih.cleandemo.contract.start.StartContract.Event.*
import kotlinx.android.synthetic.main.fragment_start.view.*

class StartFragment :
    StartContract.View,
    MainFragment<StartContract.Event, StartContract.Presenter>()
{
    override val presenterFactory: () -> StartContract.Presenter = {
        StartPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater
        .inflate(R.layout.fragment_start, container, false) { view ->
            view.next_fragment_button.setOnClickListener {
                val text = view.edit_text.text.toString()
                presenter.timerButtonWasPressed(text)
            }
        }

    override fun notify(event: StartContract.Event) {
        when (event) {
            is DoYouConfirmSwitchToNext ->
                showAlert(
                    event = event,
                    message = "Вы уверены, что хотите передать введённое сообщение на следующий экран?",
                    onOkButtonClick = event.onOkButtonClick,
                    onNoButtonClick = {}
                )
            is Main<*> ->
                notifyMain(event, event.main)
        }
    }
}
