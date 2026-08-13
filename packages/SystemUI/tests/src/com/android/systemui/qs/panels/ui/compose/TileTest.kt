/*
 * Copyright (C) 2025 The Android Open Source Project
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

import android.service.quicksettings.Tile.STATE_UNAVAILABLE
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
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
import com.android.systemui.haptics.msdl.tileHapticsViewModelFactory
import com.android.systemui.plugins.qs.QSTile
import com.android.systemui.qs.FakeQSTile
import com.android.systemui.qs.panels.ui.compose.infinitegrid.Tile
import com.android.systemui.qs.panels.ui.compose.infinitegrid.TilePresentation
import com.android.systemui.qs.panels.ui.compose.infinitegrid.TileTestTags
import com.android.systemui.qs.panels.ui.viewmodel.BounceableTileViewModel
import com.android.systemui.qs.panels.ui.viewmodel.TileViewModel
import com.android.systemui.qs.pipeline.shared.TileSpec
import com.android.systemui.testKosmos
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class TileTest : SysuiTestCase() {
    @get:Rule val composeRule = createComposeRule()
    private val kosmos = testKosmos()
    private val tileHapticsViewModelFactory = kosmos.tileHapticsViewModelFactory

    @Composable
    private fun TestTile(
        tile: TileViewModel,
        iconOnly: Boolean,
        presentation: TilePresentation = TilePresentation.Material,
    ) {
        PlatformTheme {
            SceneTransitionLayout(
                rememberMutableSceneTransitionLayoutState(SceneA),
                modifier = Modifier.size(140.dp),
            ) {
                scene(SceneA) {
                    Box(Modifier.fillMaxSize()) {
                        Tile(
                            tile = tile,
                            iconOnly = iconOnly,
                            presentation = presentation,
                            squishiness = { 1f },
                            coroutineScope = rememberCoroutineScope(),
                            bounceableInfo =
                                BounceableInfo(
                                    BounceableTileViewModel(),
                                    previousTile = null,
                                    nextTile = null,
                                    bounceEnd = true,
                                ),
                            tileHapticsViewModelFactory = tileHapticsViewModelFactory,
                            detailsViewModel = null,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun click_largeTile_shouldReceiveClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(QSTile.State().apply { label = "largeTile" })
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeTile").performClick()

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun click_largeDualTargetTile_shouldReceiveClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "largeDualTargetTile"
                handlesSecondaryClick = true
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeDualTargetTile").performClick()

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun click_smallTile_shouldReceiveClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(QSTile.State().apply { contentDescription = "smallTile" })
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallTile").performClick()

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun click_smallDualTargetTile_shouldReceiveSecondaryClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                contentDescription = "smallDualTargetTile"
                handlesSecondaryClick = true
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallDualTargetTile").performClick()

        assertThat(tile.interactions)
            .containsExactly(InteractableFakeQSTile.Interaction.SECONDARY_CLICK)
    }

    @Test
    fun longClick_largeTile_shouldReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(QSTile.State().apply { label = "largeTile" })
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeTile").performTouchInput { longClick() }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.LONG_CLICK)
    }

    @Test
    fun longClick_largeDualTargetTile_shouldReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "largeDualTargetTile"
                handlesSecondaryClick = true
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeDualTargetTile").performTouchInput { longClick() }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.LONG_CLICK)
    }

    @Test
    fun longClick_smallTile_shouldReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(QSTile.State().apply { contentDescription = "smallTile" })
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallTile").performTouchInput { longClick() }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.LONG_CLICK)
    }

    @Test
    fun longClick_smallDualTargetTile_shouldReceiveClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                contentDescription = "smallDualTargetTile"
                handlesSecondaryClick = true
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallDualTargetTile").performTouchInput {
            longClick()
        }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun longClick_smallDualTargetTile_doesNotHandleLongClick_shouldReceiveClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                contentDescription = "smallDualTargetTile"
                handlesSecondaryClick = true
                handlesLongClick = false
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallDualTargetTile").performTouchInput {
            longClick()
        }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun longClick_largeDualTargetTile_doesNotHandleLongClick_shouldNotReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "largeDualTargetTile"
                handlesSecondaryClick = true
                handlesLongClick = false
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeDualTargetTile").performTouchInput { longClick() }

        // Long clicks default to a normal click when a tile does not handle long clicks
        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun longClick_smallTile_doesNotHandleLongClick_shouldNotReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                contentDescription = "smallTile"
                handlesLongClick = false
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallTile").performTouchInput { longClick() }

        // Long clicks default to a normal click when a tile does not handle long clicks
        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun longClick_largeTile_doesNotHandleLongClick_shouldReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "largeTile"
                handlesLongClick = false
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = false) }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("largeTile").performTouchInput { longClick() }

        // Long clicks default to a normal click when a tile does not handle long clicks
        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.CLICK)
    }

    @Test
    fun longClick_smallDualTargetTile_isUnavailable_shouldReceiveLongClick() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                contentDescription = "smallDualTargetTile"
                handlesSecondaryClick = true
                state = STATE_UNAVAILABLE
            }
        )
        val viewModel =
            TileViewModel(tile.fakeTile, TileSpec.Companion.create("test"), Expandable())

        composeRule.setContent { TestTile(viewModel, iconOnly = true) }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("smallDualTargetTile").performTouchInput {
            longClick()
        }

        assertThat(tile.interactions).containsExactly(InteractableFakeQSTile.Interaction.LONG_CLICK)
    }

    @Test
    fun fluentCompactTile_placesLabelBelowSurface_andAnnouncesSecondaryLabelOnce() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "Wi-Fi"
                secondaryLabel = "Office network"
                contentDescription = "Wi-Fi"
            }
        )
        val viewModel = TileViewModel(tile.fakeTile, TileSpec.create("test"), Expandable())

        composeRule.setContent {
            TestTile(viewModel, iconOnly = false, presentation = TilePresentation.FluentCompact)
        }
        composeRule.waitForIdle()

        val surface =
            composeRule.onNodeWithTag(
                resIdToTestTag(TileTestTags.fluentCompactSurface(viewModel.spec))
            )
        val label =
            composeRule.onNodeWithTag(
                resIdToTestTag(TileTestTags.fluentCompactLabel(viewModel.spec))
            )
        surface.assertHeightIsEqualTo(56.dp).assertIsDisplayed()
        val labelBounds = label.getBoundsInRoot()
        assertThat(labelBounds.bottom).isNotEqualTo(labelBounds.top)
        assertThat(surface.getBoundsInRoot().bottom).isAtMost(labelBounds.top)
        composeRule.onAllNodesWithContentDescription("Wi-Fi, Office network").assertCountEquals(1)
    }

    @Test
    fun fluentCompactTile_doesNotRepeatSecondaryLabelAlreadyInStateDescription() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "Wi-Fi"
                secondaryLabel = "Connected"
                contentDescription = "Wi-Fi"
                stateDescription = "Connected"
            }
        )
        val viewModel = TileViewModel(tile.fakeTile, TileSpec.create("test"), Expandable())

        composeRule.setContent {
            TestTile(viewModel, iconOnly = false, presentation = TilePresentation.FluentCompact)
        }
        composeRule.waitForIdle()

        composeRule.onAllNodesWithContentDescription("Wi-Fi").assertCountEquals(1)
        composeRule.onAllNodesWithContentDescription("Wi-Fi, Connected").assertCountEquals(0)
    }

    @Test
    fun fluentCompactTile_announcesSecondaryLabelThatIsOnlyAContentDescriptionSubstring() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "Auto-rotate"
                secondaryLabel = "On"
                contentDescription = "Rotation"
            }
        )
        val viewModel = TileViewModel(tile.fakeTile, TileSpec.create("test"), Expandable())

        composeRule.setContent {
            TestTile(viewModel, iconOnly = false, presentation = TilePresentation.FluentCompact)
        }
        composeRule.waitForIdle()

        composeRule.onAllNodesWithContentDescription("Rotation, On").assertCountEquals(1)
    }

    @Test
    fun fluentCompactIconDualTarget_preservesSecondaryClickAndLongClickMapping() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "Internet"
                contentDescription = "Internet"
                handlesSecondaryClick = true
            }
        )
        val viewModel = TileViewModel(tile.fakeTile, TileSpec.create("test"), Expandable())

        composeRule.setContent {
            TestTile(viewModel, iconOnly = true, presentation = TilePresentation.FluentCompact)
        }
        composeRule.waitForIdle()
        val interaction =
            composeRule.onNodeWithTag(
                resIdToTestTag(TileTestTags.fluentCompactInteraction(viewModel.spec))
            )

        interaction.assertHeightIsEqualTo(56.dp).performClick()
        interaction.performTouchInput { longClick() }

        assertThat(tile.interactions)
            .containsExactly(
                InteractableFakeQSTile.Interaction.SECONDARY_CLICK,
                InteractableFakeQSTile.Interaction.CLICK,
            )
            .inOrder()
    }

    @Test
    fun fluentCompactLargeDualTarget_preservesSeparateMainAndSecondaryTargets() {
        val tile = InteractableFakeQSTile()
        tile.fakeTile.changeState(
            QSTile.State().apply {
                label = "Internet"
                contentDescription = "Internet"
                handlesSecondaryClick = true
            }
        )
        val viewModel = TileViewModel(tile.fakeTile, TileSpec.create("test"), Expandable())

        composeRule.setContent {
            TestTile(viewModel, iconOnly = false, presentation = TilePresentation.FluentCompact)
        }
        composeRule.waitForIdle()

        composeRule
            .onNodeWithTag(resIdToTestTag(TileTestTags.fluentCompactInteraction(viewModel.spec)))
            .assertHeightIsEqualTo(56.dp)
            .performTouchInput { click(centerLeft) }
        composeRule
            .onNodeWithTag(resIdToTestTag("qs_tile_toggle_target"), useUnmergedTree = true)
            .assertHeightIsEqualTo(48.dp)
            .performClick()

        assertThat(tile.interactions)
            .containsExactly(
                InteractableFakeQSTile.Interaction.CLICK,
                InteractableFakeQSTile.Interaction.SECONDARY_CLICK,
            )
            .inOrder()
    }

    private class InteractableFakeQSTile {
        val interactions = mutableListOf<Interaction>()

        val fakeTile =
            FakeQSTile(
                user = 0,
                available = true,
                onClick = { interactions.add(Interaction.CLICK) },
                onLongClick = { interactions.add(Interaction.LONG_CLICK) },
                onSecondaryClick = { interactions.add(Interaction.SECONDARY_CLICK) },
            )

        enum class Interaction {
            CLICK,
            SECONDARY_CLICK,
            LONG_CLICK,
        }
    }
}
