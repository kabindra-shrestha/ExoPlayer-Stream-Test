package com.kabindra.exoplayerstreamtestapp

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return "$tag: "
        // return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    backStackTag: String? = null,
    transitionView: Int,
    transitionName: String
) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        if (transitionView > 0) {
            addSharedElement(findViewById(transitionView), transitionName)
        }
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        // backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}