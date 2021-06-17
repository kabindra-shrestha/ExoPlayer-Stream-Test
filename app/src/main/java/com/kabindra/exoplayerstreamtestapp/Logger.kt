package com.kabindra.exoplayerstreamtestapp

import timber.log.Timber

/**
 * This Logger.kt is an abstraction above the third-party logging library [Timber].
 * But this can be made to use above the [android.util.Log], needs to use reflection to get the class name
 */

/**
 * --------NOTE-----------
 * When using this class, add the following in onCreate() [Application] class.
 * [plantLogger]
 */

/**
 * TODO add different Logger.kt based on flavour but is it really needed and useful ???
 */

/**
 * Call this from [Application]
 */
fun plantLogger() {
    if (BuildConfig.DEBUG) {
        Timber.plant(Timber.DebugTree())
    }
}

fun loggerDebug(msg: String, vararg args: Any) {
    Timber.d(msg, args)
}

fun loggerError(msg: String) {
    Timber.v(msg)
}

fun loggerWarn(msg: String, vararg args: Any) {
    Timber.w(msg, args)
}

fun loggerVerbose(msg: String, vararg args: Any) {
    Timber.v(msg, args)
}
