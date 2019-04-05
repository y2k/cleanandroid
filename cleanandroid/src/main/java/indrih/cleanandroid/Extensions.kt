package indrih.cleanandroid

import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.error

typealias EventButtonClick = () -> Unit

fun AnkoLogger.logMessage(message: String) =
    info(message)

inline fun <reified E : AbstractEvent> E.withInitToken(): E {
    this.token = object : TypeToken<E>() {}
    return this
}

inline fun <reified E : AbstractEvent> E.init(showMode: AbstractEvent.ShowMode): E {
    this.showMode = showMode
    this.token = object : TypeToken<E>(){}
    return this
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