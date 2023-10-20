package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.ActivityMainBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.BadgeBox
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders.OrderViewModel
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders.UpdateOrderStatusWorker
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.FadingSnackbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BadgeBox {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var snackbar: FadingSnackbar
    val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //orderViewModel.updateOrdersStatus(applicationContext)
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<UpdateOrderStatusWorker>(
            15, TimeUnit.MINUTES
        ).setInitialDelay(25, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(periodicWorkRequest)

        bottomNavigation = binding.bottomNavigation
        bottomNavigation.menu.clear()
        bottomNavigation.inflateMenu(R.menu.main_nav_menu)
        auth = Firebase.auth
        snackbar = binding.mainSnackBar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        navHostFragment?.let { it ->
            navController = it.navController
            bottomNavigation.setupWithNavController(navController)
            bottomNavigation.setOnItemSelectedListener { item ->
                val selectedDestinationId = item.itemId
                val currentFragment = supportFragmentManager.findFragmentById(selectedDestinationId)

                if (currentFragment != null) {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.remove(currentFragment)
                    fragmentTransaction.commit()
                }

                it.navController.navigate(selectedDestinationId)

                return@setOnItemSelectedListener true
            }
        }

        if (auth.currentUser == null) {
            bottomNavigation.visibility = View.GONE
            snackbar.show(
                messageText = getString(R.string.user_need_to_be_login),
            )
        }
    }

    override fun onNumberCart(number: Int) {
        val badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.navigation_cart)
        if (number > 0) {
            badgeDrawable.isVisible = true
            badgeDrawable.backgroundColor = getColor(R.color.eyoj_orange)
            badgeDrawable.number = number
        } else {
            badgeDrawable.isVisible = false
        }
    }

    override fun onNumberFavorite(number: Int) {
        val badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.navigation_favorite)
        if (number > 0) {
            badgeDrawable.isVisible = true
            badgeDrawable.backgroundColor = getColor(R.color.eyoj_orange)
            badgeDrawable.number = number
        } else {
            badgeDrawable.isVisible = false
        }
    }
}
