package indrih.cleanandroid

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import indrih.cleanandroid.BaseContract.*
import indrih.cleanandroid.BaseContract.BaseEvent
import indrih.cleanandroid.BaseContract.BaseEvent.*

/**
 * Базовая реализация фрагмента, наследуемая всем остальным фрагментам.
 *
 * [PresView] - interface View для контракта отображаемого фрагмента,
 * который, в свою очередь, унаследован от [BaseContract.View].
 * Но т.к. Kotlin считает [PresView] классом, а не интерфейсом, приходится
 * наследовать [BaseFragment] от [BaseContract.View], а не от [PresView].
 * Это нужно для реализации общих методов и возможности attach-ить View к Presenter.
 *
 * [Presenter] - interface Presenter для контракта отображаемого фрагмента,
 * который, в свою очередь, унаследован от [BaseContract.Presenter].
 */
abstract class BaseFragment<PresView, Presenter> :
    Fragment(),
    BaseContract.View
        where PresView : BaseContract.View,
              Presenter : BaseContract.Presenter<PresView>
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     * Каст безопасный, т.к. this для наследника [BaseFragment] и this для
     * самого [BaseFragment] общий (ибо это абстрактный класс, он не может
     * иметь собственных объектов), а т.к. этот потомок унаследован от интерфейса [PresView],
     * то и этот объект так же унаследован от него => каст безопасен.
     */
    @Suppress("UNCHECKED_CAST")
    override fun onResume() {
        super.onResume()
        presenter.attachView(this as PresView)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        if (isRemoving)
            presenter.onCleared()
        hideAlert() // чтобы не возникал экзепшен в случае поднятого алерта
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared() // чтобы точно ничего никуда не утекло
    }

    abstract fun onBackPressed()

    /*
     ************************* Информация на экране *************************
     */

    @CallSuper
    override fun <Event : BaseEvent> notify(event: Event) {
        when (event) {
            is HideNotifyEvent -> {
                hideNotifyEvent()
                presenter.eventIsCommitted(event)
            }

            is ShowKeyboard ->
                showKeyboard()

            is HideKeyboard ->
                hideKeyboard()

            else ->
                showAlert(
                    title = "Покажите скриншот этого уведомления разработчику",
                    message = "Необработанный ивент: $event",
                    event = event,
                    onOkButtonClick = {},
                    onNoButtonClick = null,
                    cancelable = false
                )
        }
    }

    private fun hideNotifyEvent() {
        if (toast != null)
            hideToast()
        if (alertDialog != null)
            hideAlert()
    }

    private var toast: Toast? = null

    protected fun showToast(message: String, event: BaseEvent) {
        if (toast != null)
            hideToast()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).also {
            it.show()
        }
        presenter.eventIsCommitted(event)
    }

    private fun hideToast() {
        toast?.cancel()
        toast = null
    }

    private var alertDialog: DialogInterface? = null

    protected fun showAlert(
        message: String,
        event: BaseEvent,
        title: String? = null,
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
                setPositiveButton(getString(R.string.ok)) { _, _ ->
                    it.invoke()
                    presenter.eventIsCommitted(event)
                }
            }

            onNoButtonClick?.let {
                setNegativeButton(getString(R.string.cancel)) { _, _ ->
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

    private var isKeyboardVisible = false

    private fun showKeyboard() {
        if (!isKeyboardVisible) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            isKeyboardVisible = true
        }
    }

    private fun hideKeyboard() {
        if (isKeyboardVisible) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            isKeyboardVisible = false
        }
    }

    /*
     ******************************** Экстеншены ********************************
     */

    protected fun LayoutInflater.inflate(
        @LayoutRes resource: Int,
        container: ViewGroup?,
        attachToRoot: Boolean = false,
        init: (view: View) -> Unit
    ): View = inflate(resource, container, attachToRoot).also {
        _view = it
        init(it)
    }

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