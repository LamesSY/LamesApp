package com.lames.standard.common.composeUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lames.standard.R

@Composable
fun AppBar(
    title: String = "",
    showBack: Boolean = true,
    showClose: Boolean = false,
    rightIcon: Painter? = null,
    rightText: String? = null,
    onBack: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onRightClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(id = R.color.windowBg_1))
    ) {

        /* ---------- 中间标题（绝对居中） ---------- */
        Text(
            text = title,
            color = colorResource(id = R.color.text_1),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        /* ---------- 左侧按钮区 ---------- */
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showBack) {
                AppBarIcon(
                    painter = painterResource(id = R.drawable.ic_back),
                    onClick = onBack
                )
            }

            if (showClose) {
                AppBarIcon(
                    painter = painterResource(id = R.drawable.ic_linear_close),
                    onClick = onClose
                )
            }
        }

        /* ---------- 右侧按钮区 ---------- */
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            when {
                rightIcon != null -> {
                    AppBarIcon(
                        painter = rightIcon,
                        onClick = onRightClick
                    )
                }

                rightText != null -> {
                    Text(
                        text = rightText,
                        color = colorResource(id = R.color.text_1),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(
                                indication = rememberRipple(),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onRightClick?.invoke() }
                            .padding(horizontal = 15.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBarIcon(
    painter: Painter,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .width(55.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(50))
            .clickable(
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.padding(15.dp),
            contentScale = ContentScale.Fit
        )
    }
}
