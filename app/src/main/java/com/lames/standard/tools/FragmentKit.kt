package com.lames.standard.tools


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.lames.standard.R
import com.lames.standard.common.CommonFragment

fun AppCompatActivity.loadFirstFragment(fcViewId: Int, fragmentClass: Class<out Fragment>, args: Bundle? = null) {
    supportFragmentManager.commit {
        //setReorderingAllowed(true)
        add(fcViewId, fragmentClass, args)
    }
}

fun Fragment.addFg(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    add(containerId, fragmentClass, args)
    addToBackStack("")
}

fun Fragment.replaceFg(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    replace(containerId, fragmentClass, args)
}

fun Fragment.addFgPop(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    remove(this@addFgPop)
    add(containerId, fragmentClass, args)
    addToBackStack("")
}

fun Fragment.replaceFgPop(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    remove(this@replaceFgPop)
    add(containerId, fragmentClass, args)
    addToBackStack("")
}

fun Fragment.addFgMaxLife(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    add(containerId, fragmentClass, args)
    setMaxLifecycle(this@addFgMaxLife, Lifecycle.State.STARTED)
    addToBackStack("")
}