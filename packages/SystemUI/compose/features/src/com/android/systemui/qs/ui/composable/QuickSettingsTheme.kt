/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android.systemui.qs.ui.composable

import android.view.ContextThemeWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.android.systemui.Flags.notificationShadeBlur
import com.android.systemui.res.R

@Composable
fun QuickSettingsTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val themedContext =
        remember(context) { ContextThemeWrapper(context, R.style.Theme_SystemUI_QuickSettings) }
    val platformColorScheme = MaterialTheme.colorScheme
    val useDarkPalette = platformColorScheme.surface.luminance() < .5f
    val accent =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_accent_dark else R.color.fluent_qs_accent_light
        )
    val onAccent =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_on_accent_dark
            else R.color.fluent_qs_on_accent_light
        )
    val surface =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_surface_opaque_dark
            else R.color.fluent_qs_surface_opaque_light
        )
    val useTranslucentControls = notificationShadeBlur()
    val controlFill =
        colorResource(
            when {
                useDarkPalette && useTranslucentControls -> R.color.fluent_qs_control_fill_dark
                useDarkPalette -> R.color.fluent_qs_control_fill_opaque_dark
                useTranslucentControls -> R.color.fluent_qs_control_fill_light
                else -> R.color.fluent_qs_control_fill_opaque_light
            }
        )
    val secondaryControlFill =
        colorResource(
            when {
                useDarkPalette && useTranslucentControls ->
                    R.color.fluent_qs_control_fill_secondary_dark
                useDarkPalette -> R.color.fluent_qs_control_fill_secondary_opaque_dark
                useTranslucentControls -> R.color.fluent_qs_control_fill_secondary_light
                else -> R.color.fluent_qs_control_fill_secondary_opaque_light
            }
        )
    val disabledControlFill =
        colorResource(
            when {
                useDarkPalette && useTranslucentControls ->
                    R.color.fluent_qs_control_fill_disabled_dark
                useDarkPalette -> R.color.fluent_qs_control_fill_disabled_opaque_dark
                useTranslucentControls -> R.color.fluent_qs_control_fill_disabled_light
                else -> R.color.fluent_qs_control_fill_disabled_opaque_light
            }
        )
    val controlStroke =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_control_stroke_dark
            else R.color.fluent_qs_control_stroke_light
        )
    val panelTint =
        if (useTranslucentControls) {
            colorResource(
                if (useDarkPalette) R.color.fluent_qs_panel_tint_dark
                else R.color.fluent_qs_panel_tint_light
            )
        } else {
            surface
        }
    val tooltip =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_tooltip_dark else R.color.fluent_qs_tooltip_light
        )
    val onTooltip =
        colorResource(
            if (useDarkPalette) R.color.fluent_qs_on_tooltip_dark
            else R.color.fluent_qs_on_tooltip_light
        )
    val fluentColorScheme =
        platformColorScheme.copy(
            primary = accent,
            onPrimary = onAccent,
            primaryContainer = accent,
            onPrimaryContainer = onAccent,
            secondary = accent,
            onSecondary = onAccent,
            tertiary = accent,
            onTertiary = onAccent,
            tertiaryFixed = tooltip,
            onTertiaryFixed = onTooltip,
            surface = surface,
            surfaceBright = surface,
            surfaceDim = panelTint,
            surfaceContainer = controlFill,
            surfaceContainerHigh = controlFill,
            surfaceContainerLow = secondaryControlFill,
            surfaceContainerLowest = disabledControlFill,
            outlineVariant = controlStroke,
        )

    MaterialTheme(
        colorScheme = fluentColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
    ) {
        CompositionLocalProvider(LocalContext provides themedContext) { content() }
    }
}
