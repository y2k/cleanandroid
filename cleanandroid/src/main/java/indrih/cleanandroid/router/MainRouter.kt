package indrih.cleanandroid.router

import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanActivity

internal object MainRouter {
    private val argsMap = ArgsMap()

    fun copyAndDelete(): ArgsMap {
        val res = ArgsMap(argsMap.getAllArgs())
        argsMap.deleteAllArgs()
        return res
    }

    fun <Screen : AbstractScreen> navigate(screen: Screen) {
        argsMap.deleteAllArgs()
        argsMap.putArgs(screen.map)
        CleanActivity.navigate(screen.action)
    }

    fun popBackStack() {
        CleanActivity.popBackStack()
    }
}
