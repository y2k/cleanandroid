package indrih.cleanandroid

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import indrih.cleanandroid.CleanContract.AbstractEvent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Базовая реализация Fragment-а, от которой нужно наследовать все остальные Fragment-ы.
 * [Event] - события, которыми [Presenter] сможет уведомлять о необходимости
 * что-то образить на экране.
 */
abstract class CleanRetainFragment<Event, Presenter> :
    Fragment(),
    CleanContract.View<Event>,
    AnkoLogger
        where Event : AbstractEvent,
              Presenter : CleanContract.Presenter<Event>
{
    /*
     ************************* Основное *************************
     */

    protected abstract val presenterFactory: () -> Presenter // на всякий случай, вдруг я решу изменить место создания презентера

    protected val presenter: Presenter by lazy {
        presenterFactory()
    }

    protected val fragmentView: View
        get() = requireNotNull(_view)
    private var _view: View? = null

    protected var writeToLog = false

    protected fun LayoutInflater.inflate(
        @LayoutRes resource: Int,
        container: ViewGroup?,
        attachToRoot: Boolean = false,
        init: (view: View) -> Unit
    ): View = inflate(resource, container, attachToRoot).also {
        _view = it
        init(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        if (writeToLog) info("fragment created")
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        if (writeToLog) info("view attached")
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        if (writeToLog) info("view detached")
        if (isRemoving) {
            presenter.onCleared()
            if (writeToLog) info("resources cleared")
        }
        hideAlert() // чтобы не возникал экзепшен в случае поднятого алерта
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared() // чтобы точно ничего никуда не утекло
        if (writeToLog) info("fragment destroyed")
    }

    abstract fun onBackPressed()

    /*
     ************************* Информация на экране *************************
     */

    protected fun hideNotifyEvent(event: Event) {
        if (toast != null)
            hideToast()
        if (alertDialog != null)
            hideAlert()
        presenter.eventIsCommitted(event)
    }

    private var toast: Toast? = null

    /**
     * @param event экземпляр события, которое больше не нуждается в отображении после
     * вывода информации на экран.
     */
    protected fun showToast(message: String, event: Event, duration: Int = Toast.LENGTH_SHORT) {
        if (toast != null)
            hideToast()
        toast = Toast.makeText(context, message, duration).also {
            it.show()
        }
        presenter.eventIsCommitted(event)
    }

    private fun hideToast() {
        toast?.cancel()
        toast = null
    }

    private var alertDialog: DialogInterface? = null

    /**
     * @param event экземпляр события, которое больше не нуждается в отображении после
     * вывода информации на экран.
     */
    protected fun showAlert(
        message: String,
        event: Event,
        title: String? = null,
        okButtonText: String = getString(R.string.ok),
        noButtonTest: String = getString(R.string.cancel),
        onOkButtonClick: EventButtonClick?,
        onNoButtonClick: EventButtonClick?,
        cancelable: Boolean = true
    ) {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(requireContext()).apply {
            setMessage(message)
            title?.let { setTitle(it) }
            setCancelable(cancelable)

            onOkButtonClick?.let {
                setPositiveButton(okButtonText) { _, _ ->
                    it.invoke()
                    presenter.eventIsCommitted(event)
                }
            }

            onNoButtonClick?.let {
                setNegativeButton(noButtonTest) { _, _ ->
                    it.invoke()
                    presenter.eventIsCommitted(event)
                }
            }
        }.show()
    }

    private fun hideAlert() {
        alertDialog?.cancel()
        alertDialog = null
    }

    /*
     ************************* Клавиатура *************************
     */

    protected fun showKeyboard(event: Event) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    protected fun hideKeyboard(event: Event) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    /*
     ******************************** Экстеншены ********************************
     */

    protected fun View.visible() {
        visibility = View.VISIBLE
    }

    protected fun View.invisible() {
        visibility = View.INVISIBLE
    }

    protected fun View.gone() {
        visibility = View.GONE
    }
}