package indrih.cleanandroid

internal object MainRouter {
    private val map = ArgsMap()

    fun getArgs() =
        map.copyAndDelete()

    fun <Screen : AbstractScreen> navigate(screen: Screen) {
        map.addAll(screen.map)
        CleanActivity.navigate(screen.action)
    }

    fun popBackStack() {
        CleanActivity.popBackStack()
    }
}

internal class ArgsMap(val map: HashMap<String, Any> = hashMapOf()) {
    fun copyAndDelete() =
        ArgsMap(getAllArgs())

    fun addAll(newMap: Map<String, Any>) {
        map.clear()
        map += newMap
    }

    private fun getAllArgs(): HashMap<String, Any> {
        val map1 = HashMap(map)
        map.clear()
        return map1
    }
}

inline fun <reified T : Any> HashMap<String, Any>.getArg(name: String? = null): T {
    val map = this
    val map1 = if (name != null)
        map.filter { it.key == name }
    else
        map

    val map2 = map1.mapNotNull { it.value as? T }
    return when {
        map2.isEmpty() -> throw NullPointerException("Не найден аргумент")
        map2.size > 1 -> throw NullPointerException("Найдено более одного аргумента")
        else -> map2.first()
    }
}