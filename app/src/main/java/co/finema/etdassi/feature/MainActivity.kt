package co.finema.etdassi.feature

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.common.utils.DateConvertUtil.toISOFormat
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ActivityMainBinding
import co.finema.etdassi.feature.qr_reader.QRReaderActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.datetime.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class.java) {

    override val viewModel: MainViewModel by viewModel()

    override fun getLayoutRes() = R.layout.activity_main


    var countBadge = MutableLiveData<Int>()

    override fun onInject() {
        super.onInject()

//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.statusBarColor = Color.TRANSPARENT
//        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT


        binding.bottomNavigation.bottomAppBar.itemIconTintList = null
        binding.bottomNavigation.bottomAppBar.visibility = View.VISIBLE
        val navView: BottomNavigationView = binding.bottomNavigation.bottomAppBar
        val navController = findNavController(R.id.main_nav_content)

//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.bottomNavigation.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, QRReaderActivity::class.java)
            intent.putExtra("type", BioAuthType.SCAN_MAIN)
            startActivity(intent)
        }

        val badgeDrawable = binding.bottomNavigation.bottomAppBar.getOrCreateBadge(R.id.navigation_my_sign)
        badgeDrawable.maxCharacterCount = 3
        badgeDrawable.backgroundColor = ContextCompat.getColor(this, R.color.danger)

        viewModel.mainBadge.observe(this, {
            badgeDrawable.isVisible = it>0
            badgeDrawable.number = it
        })





        navView.setOnNavigationItemReselectedListener {

        }

        test()

    }

    override fun onResume() {
        super.onResume()
        viewModel.checkActionNext()?.let { nextAction ->
            when(nextAction) {
                DoActionEnum.REJECT_DONE_OPEN_REJECT_PAGE -> {
                    val mySignMenu = binding.bottomNavigation.bottomAppBar.findViewById<View>(R.id.navigation_my_sign)
                    mySignMenu.performClick()
                }

                else -> {

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        if (supportFragmentManager.backStackEntryCount <= 1) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
    }




    private fun test() {

        println("TEST ->${BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS}")

//        binding.test.setOnClickListener {
//            val mySignMenu = binding.bottomNavigation.bottomAppBar.findViewById<View>(R.id.navigation_my_sign)
//            mySignMenu.performClick()
//        }
//        val timeStamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis/1000
//        val sss = "2021-08-30T11:01:44Z".toShortDateTime()
//        println( "ssssss => ${66.mod(30)}")
//        println("sdasadsa => ${(61/30).toInt()}")
//        val max: Double = 62.toDouble()/30.toDouble()
//        for (i in 1..max.toInt()) {
//            println("ssssssssss => $i")
//        }
//        val stamp = 1631172934L
//
//
//        println("TIME => ${ Clock.System.now().toEpochMilliseconds()}  ${convertLongToTime(stamp)}")
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task->
//            task.result?.let {
//                println("firebase toke =>$it")
//            }
//        }
//
//        val currentMoment: Instant = Clock.System.now()
//        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC+7")).timeInMillis/1000
//        println("TIME ISO => ${currentTime.toISOFormat()}")
//
//
//        viewModel.runTest()
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy")
        return format.format(date)
    }



}