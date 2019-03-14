package indrih.cleanandroid

import java.lang.Exception

object MainRouter {
    val map = hashMapOf<String, Any>()

    inline fun <reified T : Any> getArg(name: String? = null): T {
        val map1 = if (name != null)
            map.filter { it.key == name }
        else
            map

        val map2 = map1.mapNotNull { it.value as? T }
        val res = when {
            map2.isEmpty() -> throw Exception()
            map2.size > 1 -> throw Exception()
            else -> map2.first()
        }
        map.values.remove(res)
        return res
    }

    fun getAllArgs(): HashMap<String, Any> {
        val map1 = HashMap(map)
        map.clear()
        return map1
    }

    fun <Screen : AbstractScreen> navigate(screen: Screen) {
        map.clear()
        map += screen.map
        CleanActivity.navigate(screen.action)
    }
}