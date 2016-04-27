package adambl4.instamotorsskeleton

import adambl4.instamotorsskeleton.network.InstamotorsAPI
import adambl4.instamotorsskeleton.utils.AwesomeDebugTree
import android.app.Application
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addPerThreadFactory
import uy.kohesive.injekt.api.addSingleton


/**
 * Created by Adambl4 on 26.04.2016.
 */


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        inject()
        Logger.init("logger")
                .methodCount(1)
                .logLevel(LogLevel.FULL)
                .methodOffset(7)
        Timber.plant(AwesomeDebugTree())
    }
}


fun App.inject() {
    object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            val ctx = this@inject
            addPerThreadFactory { Realm.getInstance(RealmConfiguration.Builder(ctx).build()) }
            addSingleton(InstamotorsAPI.build())
        }
    }
}
