package indrih.cleandemo.presentation.presenter

import indrih.cleanandroid.AbstractEvent
import indrih.cleanandroid.CleanPresenter
import indrih.cleandemo.contract.timer.TimerContract
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import indrih.cleandemo.contract.timer.TimerContract.Event.*

class TimerPresenter :
    TimerContract.Presenter,
    CleanPresenter<TimerContract.Event, TimerContract.Screen>()
{
    override fun onFirstAttached() {
        super.onFirstAttached()
        val text: String = getArg()
        notifyUI(ShowEnteredText(text))
    }

    private var counter = 0
    private var timerJob: Job? = null

    override fun startButtonWasPressed() {
        stopButtonWasPressed()
        timerJob = launch {
            while (true) {
                notifyUI(
                    SetCounterValue(counter++),
                    showMode = AbstractEvent.ShowMode.EveryTime
                )
                delaySeconds(1)
            }
        }
    }

    override fun stopButtonWasPressed() {
        timerJob?.cancel() ?: return
        timerJob = null
    }

    override fun nextButtonWasPressed() {
        router.navigateTo(TimerContract.Screen.Chain)
    }
}