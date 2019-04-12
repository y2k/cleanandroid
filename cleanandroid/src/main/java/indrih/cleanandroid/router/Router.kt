package indrih.cleanandroid.router

import androidx.navigation.NavOptions
import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanActivity

abstract class Router {
    private val argsMap = ArgsMap()

    var activity: CleanActivity? = null

    abstract fun <Screen : AbstractScreen> navigateTo(
        screen: Screen,
        navOptions: NavOptions? = null
    )

    abstract fun popBackStack()

    abstract fun popBackStackTo(screen: AbstractScreen)

    abstract fun navigateUp()

    abstract fun moveTaskToBack()

    fun copyAndDelete(): HashMap<String, Any> {
        val res = argsMap.getAllArgs()
        argsMap.deleteAllArgs()
        return res
    }

    protected fun <Screen : AbstractScreen> setArgs(screen: Screen) {
        argsMap.deleteAllArgs()
        argsMap.putArgs(screen.map)
    }
}
