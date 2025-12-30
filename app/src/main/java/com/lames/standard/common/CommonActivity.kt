package com.lames.standard.common

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.hjq.permissions.XXPermissions
import com.lames.standard.R
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.dialog.AlertDialogFragment
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

abstract class CommonActivity<T : ViewBinding> : AppCompatActivity() {

    lateinit var binding: T

    protected var loadingDialog = DialogLoadingController(this)

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val c = newBase?.resources?.configuration ?: return
        c.fontScale = 1f
        applyOverrideConfiguration(c)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTheme(AppConfigMMKV.appThemeResId)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setTransparentStatusBar(true)
        binding = getViewBinding()
        setContentView(binding.root)
        initialization()
        bindEvent()
        doExtra()
        binding.root.findViewById<ImageView>(R.id.appBack)?.onClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    protected abstract fun getViewBinding(): T

    protected open fun initialization() {}
    protected open fun bindEvent() {}
    protected open fun doExtra() {}

    protected fun setTransparentStatusBar(useDarkStatusBarText: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        //如果是黑夜模式则isLightStatusBar要取反
        var isDarkStatusBarText = useDarkStatusBarText
        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            isDarkStatusBarText = !isDarkStatusBarText
        }
        window.decorView.apply {
            systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            systemUiVisibility =
                if (isDarkStatusBarText) systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    protected fun setAppBarTitle(str: String) {
        binding.root.findViewById<TextView>(R.id.appTitle)?.text = str
    }

    protected fun setAppBarTitle(@StringRes content: Int) {
        binding.root.findViewById<TextView>(R.id.appTitle)?.setText(content)
    }

    fun showProgressDialog(content: String = EMPTY_STR) {
        loadingDialog.show(content)
    }

    fun showProgressDialog(@StringRes content: Int) {
        showProgressDialog(forString(content))
    }

    fun dismissProgressDialog() {
        loadingDialog.dismiss()
    }

    protected inline fun <reified T : CommonDialogFragment<*>> showDialogFg(
        tag: String? = null,
        initDialog: ((T) -> Unit) = {}
    ) {
        tag?.let { if (supportFragmentManager.findFragmentByTag(it) != null) return }
        val d = T::class.java.getDeclaredConstructor().newInstance()
        initDialog.invoke(d)
        d.show(supportFragmentManager, tag)
    }

    /**
     * 权限申请，包含了弹窗说明流程。如果是已经获取的权限会直接返回true
     */
    suspend fun requestPermission(requestStr: String, permissions: List<String>) =
        suspendCancellableCoroutine<Boolean> {
            val isGrant = XXPermissions.isGranted(this, permissions)
            if (isGrant) {
                it.resume(true)
                return@suspendCancellableCoroutine
            }
            showDialogFg<AlertDialogFragment> { alert ->
                alert.isCancelable = false
                alert.content = requestStr
                alert.onCancel = {
                    it.resume(false)
                }
                alert.onConfirm = {
                    XXPermissions.with(this).permission(permissions)
                        .request { permissions, allGranted ->
                            it.resume(allGranted)
                        }
                }
            }
        }

    /*override fun dispatchTouchEvent(mv: MotionEvent?): Boolean {
        mv ?: return super.dispatchTouchEvent(mv)
        if (mv.action == MotionEvent.ACTION_DOWN) {
            val focusView = currentFocus
            if (isViewBeingTouched(focusView, mv)) hideKeyboard()
        }
        return super.dispatchTouchEvent(mv)
    }*/

}