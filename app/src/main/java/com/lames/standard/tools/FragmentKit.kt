package com.lames.standard.tools

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.lames.standard.R
import com.lames.standard.common.CommonFragment

fun AppCompatActivity.loadFirstFragment(fcViewId: Int, fragmentClass: Class<out Fragment>, args: Bundle? = null) {
    supportFragmentManager.commit {
        setReorderingAllowed(true)
        replace(fcViewId, fragmentClass, args)
    }
}

fun AppCompatActivity.loadFirstFragment(fragmentClass: Class<out Fragment>, args: Bundle? = null) {
    loadFirstFragment(R.id.fcView, fragmentClass, args)
}

fun Fragment.startFg(fragmentClass: Class<out CommonFragment<*>>, args: Bundle? = null) {
    val containerId = (requireView().parent as ViewGroup).id
    parentFragmentManager.commit {
        setReorderingAllowed(true)
        setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        replace(containerId, fragmentClass, args)
        addToBackStack(fragmentClass.simpleName)
    }
}

fun Fragment.startFgPop(fragmentClass: Class<out CommonFragment<*>>, args: Bundle? = null) {
    val containerId = (requireView().parent as ViewGroup).id
    val hasEntry = parentFragmentManager.backStackEntryCount > 0
    if (hasEntry) parentFragmentManager.popBackStack()
    parentFragmentManager.commit {
        setReorderingAllowed(true)
        setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        replace(containerId, fragmentClass, args)
        if (hasEntry) addToBackStack(fragmentClass.simpleName)
    }
}