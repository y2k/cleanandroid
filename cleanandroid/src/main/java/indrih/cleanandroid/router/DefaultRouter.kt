package indrih.cleanandroid.router

import androidx.navigation.NavOptions
import indrih.cleanandroid.AbstractScreen
import org.jetbrains.anko.AnkoLogger

object DefaultRouter : Router(), AnkoLogger {
    private val screenList = ArrayList<AbstractScreen>()

    override fun <Screen : AbstractScreen> navigateTo(screen: Screen, navOptions: NavOptions?) {
        super.setArgs(screen)
        if (!screen.inclusive)
            screenList.add(screen)
        activity?.navController?.navigate(screen.action, null, navOptions)
    }

    override fun navigateUp() {
        activity?.navController?.navigateUp()
    }

    override fun popBackStack() {
        val lastScreen = screenList.lastOrNull()
        if (lastScreen != null)
            popBackStackTo(lastScreen)
        else
            moveTaskToBack()
    }

    override fun popBackStackTo(screen: AbstractScreen) {
        activity?.navController?.popBackStack(screen.screenId, screen.inclusive)
    }

    override fun moveTaskToBack() {
        activity?.moveTaskToBack(true)
    }

    override fun clearStack() {
        screenList.clear()
    }
}
