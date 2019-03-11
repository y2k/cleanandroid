package indrih.cleanandroid

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

typealias EventButtonClick = () -> Unit

fun AnkoLogger.log(message: String) =
    info(message)