package indrih.cleanandroid.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import indrih.cleanandroid.R
import indrih.cleanandroid.base.MainFragment
import indrih.cleanandroid.contract.start.StartContract
import indrih.cleanandroid.presentation.presenter.StartPresenter
import kotlinx.android.synthetic.main.fragment_start.view.*
import indrih.cleanandroid.contract.start.StartContract.Event.*

class StartFragment : MainFragment<StartContract.Event, StartPresenter>() {
    override val presenterFactory: () -> StartPresenter = {
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

    override fun onBackPressed() {
    }
}
