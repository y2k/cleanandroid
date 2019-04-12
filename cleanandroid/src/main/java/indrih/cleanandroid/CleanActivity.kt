package indrih.cleanandroid

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import org.jetbrains.anko.AnkoLogger

abstract class CleanActivity : AppCompatActivity(), NavHost, AnkoLogger {
    protected var writeToLog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (writeToLog)
            logMessage("onCreate")
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (writeToLog)
            logMessage("onCreate")
        instance = this
    }

    /**
     * Вызывает переопределённый [CleanContract.View.popBackStack] или
     * [AppCompatActivity.onBackPressed] в случае отстутствия.
     */
    fun popBackStack(navHostFragment: NavHostFragment) {
        val backPressedListener = navHostFragment
            .childFragmentManager
            .fragments
            .mapNotNull { it as? CleanContract.View<*> }
            .firstOrNull()

        backPressedListener?.popBackStack() ?: super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (writeToLog)
            logMessage("onDestroy")
        instance = null
    }

    companion object : AnkoLogger {
        private var instance: CleanActivity? = null

        fun navigate(res: Int, navOptions: NavOptions?) {
            instance?.navController?.navigate(res, null, navOptions)
        }

        fun popBackStack(screen: AbstractScreen) {
            instance?.navController?.popBackStack(screen.screenId, screen.inclusive)
        }

        fun navigateUp() {
            instance?.navController?.navigateUp()
        }

        fun moveTaskToBack() {
            instance?.moveTaskToBack(true)
        }
    }
}