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

import android.service.quicksettings.Tile.STATE_ACTIVE
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.android.compose.animation.scene.SceneTransitionLayout
import com.android.compose.animation.scene.TestScenes.SceneA
import com.android.compose.animation.scene.rememberMutableSceneTransitionLayoutState
import com.android.compose.theme.PlatformTheme
import com.android.systemui.SysuiTestCase
import com.android.systemui.animation.Expandable
import com.android.systemui.compose.modifiers.resIdToTestTag
import com.android.systemui.integration.SystemUiIntegrationTest
import com.android.systemui.kosmos.useUnconfinedTestDispatcher
import com.android.systemui.plugins.qs.QSTile
import com.android.systemui.qs.FakeQSTile
import com.android.systemui.qs.panels.ui.compose.infinitegrid.TileTestTags
import com.android.systemui.qs.panels.ui.viewmodel.TileViewModel
import com.android.systemui.qs.pipeline.shared.TileSpec
import com.android.systemui.testKosmos
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@SystemUiIntegrationTest
@RunWith(AndroidJUnit4::class)
class PaginatedInfiniteGridLayoutTest : SysuiTestCase() {

    @get:Rule val composeRule = createComposeRule()

    private val kosmos = testKosmos().useUnconfinedTestDispatcher()
    private val specs = (0..6).map { TileSpec.create("fluent_page_tile_$it") }
    private val tiles =
        specs.mapIndexed { index, spec ->
            val tile = FakeQSTile(user = 0)
            tile.setTileSpec(spec.spec)
            tile.changeState(
                QSTile.State().apply {
                    label = "Tile $index"
                    contentDescription = "Tile $index"
                    state = STATE_ACTIVE
                }
            )
            TileViewModel(tile, spec, Expandable())
        }

    @Test
    fun defaultWrapper_swipeMovesFromSixTileFirstPageToSecondPage() {
        composeRule.setContent {
            PlatformTheme {
                SceneTransitionLayout(
                    state = rememberMutableSceneTransitionLayoutState(SceneA),
                    modifier = Modifier.width(360.dp),
                ) {
                    scene(SceneA) {
                        with(kosmos.paginatedGridLayout) {
                            TileGrid(
                                tiles = tiles,
                                modifier = Modifier.fillMaxWidth(),
                                listening = { true },
                                enableRevealEffect = false,
                            )
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()

        (0..5).forEach { surface(it).assertIsDisplayed() }
        surface(6).assertIsNotDisplayed()

        val firstPageBounds = (0..5).map { surface(it).getBoundsInRoot() }
        assertThat(firstPageBounds.map { it.top }.distinct()).hasSize(2)
        firstPageBounds.chunked(3).forEach { row -> assertThat(row.map { it.left }).isInOrder() }

        composeRule.onNodeWithTag(resIdToTestTag("qs_pager")).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        surface(6).assertIsDisplayed()
        surface(0).assertIsNotDisplayed()
    }

    private fun surface(index: Int) =
        composeRule.onNodeWithTag(resIdToTestTag(TileTestTags.fluentCompactSurface(specs[index])))
}
