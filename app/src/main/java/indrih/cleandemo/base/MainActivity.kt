package indrih.cleandemo.base

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import indrih.cleanandroid.CleanActivity
import indrih.cleandemo.R

class MainActivity : CleanActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getNavController(): NavController =
        Navigation.findNavController(this, R.id.nav_host_fragment)
}
