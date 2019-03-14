package indrih.cleanandroid

abstract class AbstractScreen(val action: Int, vararg pairs: Pair<String, Any>) {
    val map: Map<String, Any> = pairs.toMap()
}