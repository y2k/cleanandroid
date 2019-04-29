package indrih.cleanandroid

abstract class AbstractScreen(
    val action: Int,
    val screenId: Int,
    val inclusive: Boolean = false
) {
    val map = hashMapOf<String, Any>()

    protected inline fun <reified T : Any> putArg(t: T, name: String = "") {
        map[name] = t
    }
}