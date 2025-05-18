package com.lames.standard.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.lames.standard.R
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.databinding.DialogFragmentListBinding
import com.lames.standard.databinding.ItemRvDialogListBinding
import com.drake.brv.utils.divider
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup

class ListDialogFragment<T>(
    private val listContent: MutableList<T>,
    private val itemClickBlock: ((Int) -> Unit) = {},
    private val converterBlock: ((T) -> String),
) : CommonDialogFragment<DialogFragmentListBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) =
        DialogFragmentListBinding.inflate(inflater)

    override fun initialization() {
        binding.rvDialogList.linear().divider(R.drawable.shape_layout_divider).setup {
            addType<String>(R.layout.item_rv_dialog_list)
            onBind {
                val itemBind = getBinding<ItemRvDialogListBinding>()
                itemBind.tvDialogListItem.text = getModel()
            }
            onClick(R.id.tvDialogListItem) {
                itemClickBlock.invoke(this.modelPosition)
                dismiss()
            }
        }.models = listContent.map(converterBlock)
    }

    companion object {
        inline fun <reified T : Any> show(
            fragmentManager: FragmentManager,
            listContent: MutableList<T>,
            noinline converterBlock: ((T) -> String),
            noinline itemClickBlock: ((index: Int) -> Unit) = {},
        ) {
            val f = ListDialogFragment(listContent, itemClickBlock, converterBlock)
            f.show(fragmentManager, null)
        }
    }


}