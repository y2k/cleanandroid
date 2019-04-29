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
import indrih.cleanandroid.AbstractEvent.ShowMode.EveryTime
import indrih.cleanandroid.AbstractEvent.ShowMode.Once
import org.jetbrains.anko.AnkoLogger

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

    protected open val presenter: Presenter by lazy {
        presenterFactory()
    }

    @Deprecated(
        message = "Use view",
        replaceWith = ReplaceWith("currentView"),
        level = DeprecationLevel.ERROR
    )
    protected val fragmentView: View
        get() = requireNotNull(_view)
    private var _view: View? = null

    protected val currentView: View
        get() = requireNotNull(view)

    protected var writeToLog = false

    /**
     * Если вы сами установите этот флаг в состояние `false` - вам самим придётся
     * обрабатывать сохранение [presenter]-а от пересоздания и закрывать ресурсы в
     * [CleanPresenter.onCleared].
     */
    protected var isRetain = true

    protected open fun LayoutInflater.inflate(
        @LayoutRes resource: Int,
        container: ViewGroup?,
        attachToRoot: Boolean = false,
        init: (view: View) -> Unit
    ): View = inflate(resource, container, attachToRoot).also {
        init(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = isRetain

        if (activity !is CleanActivity)
            throw IllegalStateException("Ваше Activity должно наследоваться от CleanActivity")

        if (writeToLog)
            logMessage("fragment created")
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        if (writeToLog)
            logMessage("view attached")
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        if (writeToLog)
            logMessage("view detached")
        if (isRemoving && isRetain) {
            presenter.onCleared()
            if (writeToLog)
                logMessage("resources cleared")
        }
        hideAlert() // чтобы не возникал экзепшен в случае поднятого алерта
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRetain)
            presenter.onCleared() // чтобы точно ничего никуда не утекло

        if (writeToLog)
            logMessage("fragment destroyed")
    }

    /*
     ************************* Информация на экране *************************
     */

    protected open fun hideNotifyEvent(event: Event) {
        if (toast != null)
            hideToast()
        if (alertDialog != null)
            hideAlert()
        if (event.showMode !is EveryTime)
            presenter.eventIsCommitted(event)
    }

    private var toast: Toast? = null

    /**
     * @param event экземпляр события, которое больше не нуждается в отображении после
     * вывода информации на экран.
     */
    protected open fun showToast(message: String, event: Event, duration: Int = Toast.LENGTH_SHORT) {
        val showMode = event.showMode
        if (showMode is Once)
            showMode.autoRemoval = false

        if (toast != null)
            hideToast()
        toast = Toast.makeText(context, message, duration).also {
            it.show()
        }
        if (showMode !is EveryTime)
            presenter.eventIsCommitted(event)
    }

    protected open fun hideToast() {
        toast?.cancel()
        toast = null
    }

    private var alertDialog: DialogInterface? = null

    /**
     * @param event экземпляр события, которое больше не нуждается в отображении после
     * вывода информации на экран.
     */
    protected open fun showAlert(
        message: String,
        event: Event,
        title: String? = null,
        okButtonText: String = getString(R.string.ok),
        noButtonTest: String = getString(R.string.cancel),
        onOkButtonClick: EventButtonClick?,
        onNoButtonClick: EventButtonClick?,
        cancelable: Boolean = true
    ) {
        val showMode = event.showMode
        if (showMode is Once)
            showMode.autoRemoval = false

        alertDialog?.cancel()
        alertDialog = AlertDialog.Builder(requireContext()).apply {
            setMessage(message)
            title?.let { setTitle(it) }
            setCancelable(cancelable)

            onOkButtonClick?.let {
                setPositiveButton(okButtonText) { _, _ ->
                    if (showMode !is EveryTime)
                        presenter.eventIsCommitted(event)
                    it.invoke()
                }
            }

            onNoButtonClick?.let {
                setNegativeButton(noButtonTest) { _, _ ->
                    if (showMode !is EveryTime)
                        presenter.eventIsCommitted(event)
                    it.invoke()
                }
            }
        }.show()
    }

    protected open fun hideAlert() {
        alertDialog?.dismiss()
        alertDialog = null
    }

    /*
     ************************* Клавиатура *************************
     */

    protected open fun showKeyboard(event: Event) {
        val showMode = event.showMode
        if (showMode is Once)
            showMode.autoRemoval = false

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        if (showMode !is EveryTime)
            presenter.eventIsCommitted(event)
    }

    protected open fun hideKeyboard(event: Event) {
        val showMode = event.showMode
        if (showMode is Once)
            showMode.autoRemoval = false

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        if (showMode !is EveryTime)
            presenter.eventIsCommitted(event)
    }

    /*
     ******************************** Экстеншены ********************************
     */

    protected open fun View.visible() {
        visibility = View.VISIBLE
    }

    protected open fun View.invisible() {
        visibility = View.INVISIBLE
    }

    protected open fun View.gone() {
        visibility = View.GONE
    }
}