package com.lames.standard.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lames.standard.R
import com.lames.standard.view.LmAppBar
import com.lames.standard.webx.WebViewXActivity

abstract class CommonFragment<T : ViewBinding> : Fragment() {

    var containerId: Int = -1
        private set

    val dispatcher by lazy { requireActivity().onBackPressedDispatcher }

    private var _binding: T? = null

    protected val binding: T get() = _binding!!

    protected val parentActivity: CommonActivity<*> by lazy { requireActivity() as CommonActivity<*> }

    protected val viewLifecycleScope get() = viewLifecycleOwner.lifecycleScope

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = getViewBinding(inflater, container)
        containerId = container?.id ?: -1
        _binding!!.root.isClickable = true
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        getLmAppBar()?.let { appBar ->
            appBar.onAppBackClick = { dispatcher.onBackPressed() }
        }
        bindEvent()
        doExtra()
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    protected open fun getLmAppBar() = binding.root.findViewById<LmAppBar>(R.id.lmAppBar)
    protected open fun initialization() {}
    protected open fun bindEvent() {}
    protected open fun doExtra() {}

    public inline fun <reified T : CommonFragment<*>> start(
        containerId: Int = this.containerId,
        args: Bundle? = null,
        tag: String? = null,
        addToBackStack: Boolean = true,
    ) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            if (addToBackStack) add(containerId, T::class.java, args)
            else replace(containerId, T::class.java, args)
            if (this@CommonFragment.requireActivity() is WebViewXActivity) {
                setMaxLifecycle(this@CommonFragment, Lifecycle.State.STARTED)
            }
            if (addToBackStack) addToBackStack(tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public fun setAppBarTitle(str: String) {
        getLmAppBar()?.setTitle(str)
    }

    public fun setAppBarTitle(@StringRes content: Int) {
        getLmAppBar()?.setTitle(content)
    }

    protected fun showProgressDialog(content: String = Constants.Project.EMPTY_STR) {
        parentActivity.showProgressDialog(content)
    }

    fun showProgressDialog(@StringRes content: Int) {
        parentActivity.showProgressDialog(content)
    }

    fun dismissProgressDialog() {
        parentActivity.dismissProgressDialog()
    }

    inline fun <reified T : CommonDialogFragment<*>> showDialogFg(
        tag: String? = null,
        initDialog: ((T) -> Unit) = {},
    ) {
        tag?.let { if (parentFragmentManager.findFragmentByTag(it) != null) return }
        val d = T::class.java.getDeclaredConstructor().newInstance()
        initDialog.invoke(d)
        d.show(parentFragmentManager, tag)
    }

    suspend fun requestPermission(requestStr: String, permissions: List<String>) =
        parentActivity.requestPermission(requestStr, permissions)

    suspend fun requestPermission(requestStr: String, permission: String) =
        parentActivity.requestPermission(requestStr, mutableListOf(permission))


}