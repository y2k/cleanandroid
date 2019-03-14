package indrih.cleanandroid.base

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import indrih.cleanandroid.CleanActivity
import indrih.cleanandroid.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CleanActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getNavController(): NavController =
        Navigation.findNavController(this, R.id.nav_host_fragment)

    override fun onBackPressed() {
        // находим отображаемый фрагмент, наследованный от CleanRetainFragment
        val backPressedListener = nav_host_fragment
            .childFragmentManager
            .fragments
            .mapNotNull { it as? MainFragment<*, *> }
            .firstOrNull()

        backPressedListener?.onBackPressed() ?: super.onBackPressed()
    }
}
