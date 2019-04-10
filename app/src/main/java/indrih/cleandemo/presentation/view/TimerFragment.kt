package indrih.cleandemo.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import indrih.cleandemo.R
import indrih.cleandemo.base.MainFragment
import indrih.cleandemo.contract.timer.TimerContract
import indrih.cleandemo.presentation.presenter.TimerPresenter
import indrih.cleandemo.contract.timer.TimerContract.Event.*
import kotlinx.android.synthetic.main.fragment_timer.view.*

class TimerFragment :
    TimerContract.View,
    MainFragment<TimerContract.Event, TimerContract.Presenter>()
{
    override val presenterFactory = ::TimerPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater
        .inflate(R.layout.fragment_timer, container, false) { view ->
            view.start_button.setOnClickListener {
                presenter.startButtonWasPressed()
            }

            view.stop_button.setOnClickListener {
                presenter.stopButtonWasPressed()
            }

            view.next_button.setOnClickListener {
                presenter.nextButtonWasPressed()
            }
        }

    override fun notify(event: TimerContract.Event) {
        when (event) {
            is SetCounterValue ->
                fragmentView.counter_text_view.text = event.value.toString()

            is ShowEnteredText ->
                showAlert(
                    title = "Текст, введённый в предыдущем фрагменте",
                    message = event.text,
                    onOkButtonClick = {},
                    onNoButtonClick = null,
                    event = event
                )

            is Main<*> ->
                notifyMain(event, event.main)
        }
    }
}
