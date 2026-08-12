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
    fun semanticShapes_keepSubtleStateDistinction() {
        assertThat(dimension(R.dimen.qs_shape_icon_active_corner_radius))
            .isLessThan(dimension(R.dimen.qs_shape_icon_inactive_corner_radius))
        assertThat(dimension(R.dimen.qs_shape_tile_active_corner_radius))
            .isLessThan(dimension(R.dimen.qs_shape_tile_inactive_corner_radius))
    }

    @Test
    fun compactTiles_preserveMinimumTouchTargets() {
        assumeTrue(context.resources.configuration.smallestScreenWidthDp < 600)
        val minimumTouchTarget = 48 * context.resources.displayMetrics.density

        assertThat(dimension(R.dimen.common_tile_default_tile_height))
            .isAtLeast(minimumTouchTarget)
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
}
