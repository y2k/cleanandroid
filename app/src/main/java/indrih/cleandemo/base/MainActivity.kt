package indrih.cleandemo.base

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import indrih.cleanandroid.CleanActivity
import indrih.cleandemo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CleanActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        popBackStack(nav_host_fragment as NavHostFragment)
    }

    override fun getNavController(): NavController =
        Navigation.findNavController(this, R.id.nav_host_fragment)
}
