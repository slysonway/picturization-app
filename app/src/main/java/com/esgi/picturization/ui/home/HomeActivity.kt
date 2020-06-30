package com.esgi.picturization.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.esgi.picturization.R
import com.esgi.picturization.databinding.NavHeaderBinding
import com.esgi.picturization.ui.home.profile.UserViewModel
import com.esgi.picturization.ui.home.profile.UserViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val factory: UserViewModelFactory by instance<UserViewModelFactory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        val viewHeader = nav_view.getHeaderView(0)
        val navViewHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(viewHeader)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        navViewHeaderBinding.viewmodel = viewModel
        navViewHeaderBinding.lifecycleOwner = this

        val navController = Navigation.findNavController(this, R.id.fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.startFragment, R.id.untreatedListFragment), drawer_layout)
        NavigationUI.setupWithNavController(nav_view, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment),
            appBarConfiguration
        )
    }
}
