package com.lames.standard.module.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lames.standard.common.CommonFragment
import com.lames.standard.databinding.FragmentMineBinding

class MineFragment : CommonFragment<FragmentMineBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(inflater, container, false)
    }
}