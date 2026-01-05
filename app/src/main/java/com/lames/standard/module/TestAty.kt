package com.lames.standard.module

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lames.standard.common.composeUI.BasicComposeAty

class TestAty : BasicComposeAty() {

    @Composable
    override fun PageContent() {
        BasicPage("测试标题1") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                repeat(1235) {
                    val str = (50..9999).random().toString()
                    Text(str, modifier = Modifier.padding(15.dp))
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TestAty::class.java)
            context.startActivity(intent)
        }
    }
}