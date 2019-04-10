package indrih.cleanandroid

abstract class AbstractScreen(val action: Int) {
    val map = hashMapOf<String, Any>()

    protected inline fun <reified T : Any> putArg(t: T, name: String = "") {
        map[name] = t
    }

    @Deprecated(message = "Use putArg() if you need pass parameters.", level = DeprecationLevel.ERROR)
    constructor(action: Int, vararg pairs: Pair<String, Any>) : this(action)
}