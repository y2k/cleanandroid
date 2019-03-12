package indrih.cleanandroid

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.error

typealias EventButtonClick = () -> Unit

fun AnkoLogger.logMessage(message: String) =
    info(message)

fun AnkoLogger.logError(message: String) =
    error(message)