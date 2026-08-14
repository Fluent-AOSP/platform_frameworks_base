/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Shared visual renderer for Quick Settings brightness and volume controls. */
@Composable
fun FluentQuickSettingsSliderTrack(
    fraction: Float,
    enabled: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    iconColor: Color = inactiveColor,
    icon: (@Composable BoxScope.(Color) -> Unit)? = null,
) {
    val coercedFraction = fraction.coerceIn(0f, 1f)
    Box(
        modifier =
            modifier.fillMaxWidth().height(32.dp).drawWithCache {
                val lineHeight = 4.dp.toPx()
                val lineTop = (size.height - lineHeight) / 2f
                val cornerRadius = CornerRadius(lineHeight / 2f)
                val activeWidth = size.width * coercedFraction
                val activeLeft =
                    if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) {
                        size.width - activeWidth
                    } else {
                        0f
                    }
                onDrawBehind {
                    drawRoundRect(
                        color = inactiveColor,
                        topLeft = Offset(0f, lineTop),
                        size = Size(size.width, lineHeight),
                        cornerRadius = cornerRadius,
                    )
                    if (enabled && activeWidth > 0f) {
                        drawRoundRect(
                            color = activeColor,
                            topLeft = Offset(activeLeft, lineTop),
                            size = Size(activeWidth, lineHeight),
                            cornerRadius = cornerRadius,
                        )
                    }
                }
            }
    ) {
        icon?.invoke(this, iconColor)
    }
}

/** Windows 11-style two-layer slider thumb with a stable 20 dp interaction visual. */
@Composable
fun FluentQuickSettingsSliderThumb(
    enabled: Boolean,
    accentColor: Color,
    surfaceColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(20.dp)
                .background(surfaceColor, CircleShape)
                .border(1.dp, outlineColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(10.dp).background(if (enabled) accentColor else outlineColor, CircleShape)
        )
    }
}
