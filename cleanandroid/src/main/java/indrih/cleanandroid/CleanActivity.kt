package indrih.cleanandroid

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import androidx.navigation.Navigation
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

        fun navigate(res: Int) {
            instance?.navController?.navigate(res)
        }

        fun popBackStack() {
            instance?.navController?.popBackStack()
        }

        fun popBackStack(destinationId: Int, inclusive: Boolean) {
            instance?.navController?.popBackStack(destinationId, inclusive)
        }
    }
}