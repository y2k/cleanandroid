package indrih.cleanandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import indrih.cleanandroid.router.DefaultRouter
import indrih.cleanandroid.router.Router
import org.jetbrains.anko.AnkoLogger

abstract class CleanActivity : AppCompatActivity(), NavHost, AnkoLogger {
    protected var writeToLog = false

    open val router: Router = DefaultRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (writeToLog)
            logMessage("onCreate")
        router.activity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (writeToLog)
            logMessage("onDestroy")
        router.activity = null
    }
}