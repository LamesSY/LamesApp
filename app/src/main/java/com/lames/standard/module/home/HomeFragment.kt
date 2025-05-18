package com.lames.standard.module.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lames.standard.common.CommonFragment
import com.lames.standard.databinding.FragmentHomeBinding

class HomeFragment : CommonFragment<FragmentHomeBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }
}