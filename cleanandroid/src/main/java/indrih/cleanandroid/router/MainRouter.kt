package indrih.cleanandroid.router

import androidx.navigation.NavOptions
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanActivity
import indrih.cleanandroid.logMessage
import org.jetbrains.anko.AnkoLogger

internal object MainRouter : AnkoLogger {
    private val screenList = ArrayList<AbstractScreen>()
    private val argsMap = ArgsMap()

    fun copyAndDelete(): ArgsMap {
        val res = ArgsMap(argsMap.getAllArgs())
        argsMap.deleteAllArgs()
        return res
    }

    fun <Screen : AbstractScreen> navigate(screen: Screen, navOptions: NavOptions?) {
        argsMap.deleteAllArgs()
        argsMap.putArgs(screen.map)
        if (!screen.inclusive)
            screenList.add(screen)
        CleanActivity.navigate(screen.action, navOptions)
    }

    fun navigateUp() {
        CleanActivity.navigateUp()
    }

    fun popBackStack() {
        logMessage("Size: ${screenList.size}")
        val lastScreen = screenList.lastOrNull()
        if (lastScreen != null && screenList.size > 1) {
            screenList.remove(lastScreen)
            CleanActivity.popBackStack(lastScreen)
        } else {
            CleanActivity.moveTaskToBack()
        }
    }
}
