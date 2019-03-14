package indrih.cleanandroid

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import org.jetbrains.anko.AnkoLogger

abstract class CleanActivity : AppCompatActivity(), NavHost, AnkoLogger {
    protected var writeToLog = false

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (writeToLog)
            logMessage("onCreate")
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (writeToLog)
            logMessage("onDestroy")
        instance = null
    }

    companion object : AnkoLogger {
        private var instance: CleanActivity? = null

        fun navigate(res: Int) {
            instance?.navController?.navigate(res) ?: run {
                logError("instance == null")
            }
        }
    }
}