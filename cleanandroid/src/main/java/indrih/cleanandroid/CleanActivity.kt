package indrih.cleanandroid

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import indrih.cleanandroid.router.DefaultRouter
import indrih.cleanandroid.router.Router
import org.jetbrains.anko.AnkoLogger

abstract class CleanActivity : AppCompatActivity(), NavHost, AnkoLogger {
    protected var writeToLog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (writeToLog)
            logMessage("onCreate")
        router.activity = this
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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

    protected var router: Router
        get() = RouterStorage.router
        set(value) {
            RouterStorage.router = value
        }

    internal companion object RouterStorage {
        var router: Router = DefaultRouter
    }
}