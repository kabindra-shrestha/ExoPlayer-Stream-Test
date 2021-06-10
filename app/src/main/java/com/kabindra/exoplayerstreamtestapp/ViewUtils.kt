package com.kabindra.exoplayerstreamtestapp

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun snackBarDebug(rootView: View, mMessage: String) {
    if (BuildConfig.DEBUG)
        Snackbar.make(rootView, mMessage, Snackbar.LENGTH_LONG).show()
}

fun snackBar(rootView: View, mMessage: String) {
    Snackbar.make(rootView, mMessage, Snackbar.LENGTH_LONG).show()
}

fun snackBarIndefinite(
    rootView: View,
    mMessage: String,
    action: String,
    onActionClicked: () -> Unit
) {
    Snackbar.make(rootView, mMessage, Snackbar.LENGTH_INDEFINITE)
        .setAction(action) { onActionClicked() }.show()
}

fun toastDebug(context: Context, mMessage: String) {
    if (BuildConfig.DEBUG)
        Toast.makeText(context, mMessage, Toast.LENGTH_LONG).show()
}

fun toast(context: Context, mMessage: String) {
    Toast.makeText(context, mMessage, Toast.LENGTH_LONG).show()
}

fun showSoftKeyboard(view: View, activity: Activity, show: Boolean) {
    if (show) {
        if (view.requestFocus()) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    } else {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun showProgressBar(activity: Activity, progressBar: ProgressBar, show: Boolean) {
    if (show) {
        progressBar.visibility = View.VISIBLE
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    } else {
        progressBar.visibility = View.INVISIBLE
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}

fun showPlayerProgressBar(progressBar: ProgressBar, show: Boolean) {
    if (show) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.INVISIBLE
    }
}