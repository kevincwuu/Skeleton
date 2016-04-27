package adambl4.instamotorsskeleton.utils

import adambl4.instamotorsskeleton.BuildConfig
import android.util.Log
import com.orhanobut.logger.Logger
import timber.log.Timber

/**
 * Created by Adambl4 on 26.04.2016.
 */

class AwesomeDebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        printLogToLogcat(priority, tag, message, t)
    }

    private fun printLogToLogcat(priority: Int, tag: String, message: String, t: Throwable?) {
        if (!BuildConfig.DEBUG) {
            return
        }
        when (priority) {
            Log.VERBOSE -> Logger.t(tag).v(message)
            Log.DEBUG -> Logger.t("tag").d(message)
            Log.INFO -> Logger.t(tag).i(message)
            Log.WARN -> Logger.t(tag).w(message)
            Log.ERROR -> Logger.t(tag).e(t, message)
            Log.ASSERT -> Logger.t(tag).wtf(message)
        }
    }
}


inline fun ti(log:()->String){
    if(BuildConfig.DEBUG)
        Timber.i(log())
}
inline fun td(log:()->String){
    if(BuildConfig.DEBUG)
        Timber.d(log())
}

