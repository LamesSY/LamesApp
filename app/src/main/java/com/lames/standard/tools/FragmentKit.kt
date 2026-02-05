package com.lames.standard.tools

import android.os.Bundle
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
    supportFragmentManager.commit {
        setReorderingAllowed(true)
        replace(R.id.fcView, fragmentClass, args)
    }
}

/**
 * 叠加新的fragment
 */
fun Fragment.addFg(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setReorderingAllowed(true)
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    add(containerId, fragmentClass, args)
    addToBackStack("")
}

/**
 * 替换新的fragment
 */
fun Fragment.replaceFg(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) = parentFragmentManager.commit {
    setReorderingAllowed(true)
    setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
    replace(containerId, fragmentClass, args)
    addToBackStack("")
}

/**
 * 移除当前fg，并新增fg
 */
fun Fragment.addFgPop(
    fragmentClass: Class<out CommonFragment<*>>,
    args: Bundle? = null,
    containerId: Int,
) {
    parentFragmentManager.popBackStackImmediate()
    parentFragmentManager.commit {
        setReorderingAllowed(true)
        setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        add(containerId, fragmentClass, args)
        addToBackStack("")
    }
}