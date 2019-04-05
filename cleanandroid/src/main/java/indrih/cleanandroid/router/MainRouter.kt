package indrih.cleanandroid.router

import indrih.cleanandroid.AbstractScreen
import indrih.cleanandroid.CleanActivity

internal object MainRouter {
    private val map = ArgsMap()

    fun copyAndDelete(): ArgsMap {
        val res = ArgsMap(map.getAllArgs())
        map.deleteAllArgs()
        return res
    }

    fun <Screen : AbstractScreen> navigate(screen: Screen) {
        map.deleteAllArgs()
        map.putArgs(screen.map)
        CleanActivity.navigate(screen.action)
    }

    fun popBackStack() {
        CleanActivity.popBackStack()
    }
}
