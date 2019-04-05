package indrih.cleanandroid.router

internal class ArgsMap(
    private val map: HashMap<String, Any> = hashMapOf()
) {
    fun putArgs(newMap: Map<String, Any>) {
        map += newMap
    }

    fun getAllArgs() =
        HashMap(map)

    fun deleteAllArgs() =
        map.clear()
}
