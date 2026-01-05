package com.lames.standard.common.composeUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.lames.standard.R
import com.lames.standard.tools.forColor

abstract class BasicComposeAty : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PageContent()
            }
        }
    }

    @Composable
    abstract fun PageContent()

    @Composable
    open fun BasicPage(
        //appBar相关⬇
        title: String = "",
        showBack: Boolean = true,
        showClose: Boolean = false,
        rightIcon: Painter? = null,
        rightText: String? = null,
        onBack: (() -> Unit)? = { onBackPressedDispatcher.onBackPressed() },
        onClose: (() -> Unit)? = null,
        onRightClick: (() -> Unit)? = null,
        //appBar相关⬆
        statusBarColor: Color = Color(forColor(R.color.windowBg_1)),
        statusIconDarkMode: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        ApplyStatusBarStyle(statusBarColor, statusIconDarkMode)
        Box(Modifier.fillMaxSize()) {
            Column {
                statusSpacer(statusBarColor)
                AppBar(title, showBack, showClose, rightIcon, rightText, onBack, onClose, onRightClick)
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            ) {
                content()
            }
        }
    }

    @Composable
    open fun ApplyStatusBarStyle(
        statusBarColor: Color = Color(forColor(R.color.windowBg_1)),
        statusIconDarkMode: Boolean = true,
    ) {
        val window = window
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        SideEffect {
            window.statusBarColor = statusBarColor.toArgb()
            controller.isAppearanceLightStatusBars = statusIconDarkMode
        }
    }

    @Composable
    private fun statusSpacer(statusBarColor: Color) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .background(statusBarColor)
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
    }

}