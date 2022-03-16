package co.finema.etdassi

import androidx.multidex.MultiDexApplication
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.sharedModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : MultiDexApplication() {

    private val userHelper: UserHelper by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(sharedModule)
        }
    }
}