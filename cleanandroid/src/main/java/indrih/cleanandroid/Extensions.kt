package indrih.cleanandroid

import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.error

typealias EventButtonClick = () -> Unit

fun AnkoLogger.logMessage(message: String) =
    info(message)

fun AnkoLogger.logError(message: String) =
    error(message)

inline fun <reified E : AbstractEvent> E.withInitToken(): E {
    this.token = object : TypeToken<E>() {}
    return this
}

inline fun <reified E : AbstractEvent> E.init(showMode: AbstractEvent.ShowMode): E {
    this.showMode = showMode
    this.token = object : TypeToken<E>(){}
    return this
}