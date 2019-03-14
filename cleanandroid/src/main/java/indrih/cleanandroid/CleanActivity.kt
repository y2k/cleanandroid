package indrih.cleanandroid

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHost
import org.jetbrains.anko.AnkoLogger

abstract class CleanActivity : AppCompatActivity(), NavHost, AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    companion object {
        private var instance: CleanActivity? = null

        fun navigate(res: Int) {
            instance?.navController?.navigate(res)
        }
    }
}