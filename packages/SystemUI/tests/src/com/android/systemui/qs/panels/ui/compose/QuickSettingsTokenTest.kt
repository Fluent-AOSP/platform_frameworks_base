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

package com.android.systemui.qs.panels.ui.compose

import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.android.systemui.SysuiTestCase
import com.android.systemui.res.R
import com.google.common.truth.Truth.assertThat
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class QuickSettingsTokenTest : SysuiTestCase() {
    @Test
    fun commonTileShapes_referenceSemanticQuickSettingsTokens() {
        assertDimensionAlias(
            R.dimen.common_tile_default_active_icon_corner_radius,
            R.dimen.qs_shape_icon_active_corner_radius,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_inactive_icon_corner_radius,
            R.dimen.qs_shape_icon_inactive_corner_radius,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_active_tile_corner_radius,
            R.dimen.qs_shape_tile_active_corner_radius,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_inactive_tile_corner_radius,
            R.dimen.qs_shape_tile_inactive_corner_radius,
        )
    }

    @Test
    fun commonTileLayout_referencesSemanticQuickSettingsTokens() {
        assertDimensionAlias(
            R.dimen.common_tile_default_icon_size,
            R.dimen.qs_size_tile_icon_only_icon,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_large_tile_icon_size,
            R.dimen.qs_size_tile_labeled_icon,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_content_spacing,
            R.dimen.qs_spacing_tile_content,
        )
        assertDimensionAlias(
            R.dimen.common_tile_default_start_padding,
            R.dimen.qs_spacing_tile_start,
        )
        assertDimensionAlias(R.dimen.common_tile_default_end_padding, R.dimen.qs_spacing_tile_end)
        assertDimensionAlias(
            R.dimen.common_tile_default_dual_target_end_padding,
            R.dimen.qs_spacing_tile_dual_target_end,
        )
    }

    @Test
    fun quickSettingsChrome_referencesSemanticShapeTokens() {
        assertDimensionAlias(
            R.dimen.overlay_shade_panel_shape_radius,
            R.dimen.qs_shape_panel_corner_radius,
        )
        assertDimensionAlias(
            R.dimen.overlay_qs_layout_brightness_rounded_corner,
            R.dimen.qs_shape_brightness_container_corner_radius,
        )
    }

    @Test
    fun semanticShapes_useFluentControlGeometry() {
        assertThat(dimensionDp(R.dimen.qs_shape_icon_active_corner_radius)).isEqualTo(4f)
        assertThat(dimension(R.dimen.qs_shape_icon_active_corner_radius))
            .isEqualTo(dimension(R.dimen.qs_shape_icon_inactive_corner_radius))
        assertThat(dimensionDp(R.dimen.qs_shape_tile_active_corner_radius)).isEqualTo(4f)
        assertThat(dimension(R.dimen.qs_shape_tile_active_corner_radius))
            .isEqualTo(dimension(R.dimen.qs_shape_tile_inactive_corner_radius))
    }

    @Test
    fun compactTileLayout_usesFoundationGeometry() {
        assumeTrue(context.resources.configuration.smallestScreenWidthDp < 600)

        assertThat(dimensionDp(R.dimen.qs_size_tile_icon_only_icon)).isEqualTo(20f)
        assertThat(dimensionDp(R.dimen.qs_size_tile_labeled_icon)).isEqualTo(20f)
        assertThat(dimensionDp(R.dimen.qs_spacing_tile_content)).isEqualTo(8f)
        assertThat(dimensionDp(R.dimen.qs_spacing_tile_start)).isEqualTo(8f)
        assertThat(dimensionDp(R.dimen.qs_spacing_tile_end)).isEqualTo(12f)
        assertThat(dimensionDp(R.dimen.qs_spacing_tile_dual_target_end)).isEqualTo(8f)
    }

    @Test
    fun compactChrome_usesFoundationGeometry() {
        assumeTrue(context.resources.configuration.smallestScreenWidthDp < 600)

        assertThat(dimensionDp(R.dimen.qs_shape_panel_corner_radius)).isEqualTo(8f)
        assertThat(dimensionDp(R.dimen.qs_shape_brightness_container_corner_radius)).isEqualTo(4f)
        assertThat(dimensionDp(R.dimen.qs_shape_edit_grid_corner_radius)).isEqualTo(8f)
        assertThat(dimensionDp(R.dimen.qs_shape_toolbar_button_background_corner_radius))
            .isEqualTo(4f)
        assertThat(dimensionDp(R.dimen.qs_shape_toolbar_feedback_corner_radius)).isEqualTo(4f)
        assertThat(dimensionDp(R.dimen.shade_panel_margin_horizontal)).isEqualTo(12f)
        assertThat(dimensionDp(R.dimen.overlay_qs_layout_horizontal_padding)).isEqualTo(12f)
        assertThat(dimensionDp(R.dimen.overlay_qs_layout_vertical_padding)).isEqualTo(12f)
        assertThat(dimensionDp(R.dimen.overlay_qs_layout_brightness_icon_size)).isEqualTo(20f)
        assertThat(dimensionDp(R.dimen.overlay_qs_layout_brightness_track_height)).isEqualTo(32f)
        assertThat(dimensionDp(R.dimen.overlay_qs_layout_brightness_thumb_height)).isEqualTo(48f)
        assertThat(dimensionDp(R.dimen.toolbar_button_colored_background_size)).isEqualTo(32f)
        assertThat(dimensionDp(R.dimen.toolbar_button_icon_size)).isEqualTo(20f)
        assertThat(dimensionDp(R.dimen.toolbar_button_size)).isEqualTo(48f)
        assertThat(dimension(R.dimen.qs_shape_brightness_container_corner_radius))
            .isLessThan(dimension(R.dimen.overlay_qs_layout_brightness_track_height) / 2f)
        assertThat(dimension(R.dimen.qs_shape_toolbar_button_background_corner_radius))
            .isLessThan(dimension(R.dimen.toolbar_button_colored_background_size) / 2f)
    }

    @Test
    fun compactTiles_preserveMinimumTouchTargets() {
        assumeTrue(context.resources.configuration.smallestScreenWidthDp < 600)
        val minimumTouchTarget = 48 * context.resources.displayMetrics.density

        assertThat(dimensionDp(R.dimen.common_tile_default_tile_height)).isEqualTo(56f)
        assertThat(dimensionDp(R.dimen.common_tile_default_toggle_target_size)).isEqualTo(48f)
        assertThat(dimension(R.dimen.common_tile_default_tile_height)).isAtLeast(minimumTouchTarget)
        assertThat(dimension(R.dimen.common_tile_default_toggle_target_size))
            .isAtLeast(minimumTouchTarget)
    }

    private fun assertDimensionAlias(@DimenRes consumer: Int, @DimenRes token: Int) {
        val unresolvedValue = TypedValue()
        context.resources.getValue(consumer, unresolvedValue, false)

        assertThat(unresolvedValue.type).isEqualTo(TypedValue.TYPE_REFERENCE)
        assertThat(unresolvedValue.data).isEqualTo(token)
        assertThat(dimension(consumer)).isEqualTo(dimension(token))
    }

    private fun dimension(@DimenRes resource: Int): Float = context.resources.getDimension(resource)

    private fun dimensionDp(@DimenRes resource: Int): Float =
        dimension(resource) / context.resources.displayMetrics.density
}
