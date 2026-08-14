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

package com.android.systemui.qs.ui.composable

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.compose.PlatformSliderDefaults
import com.android.compose.animation.scene.ContentScope
import com.android.compose.gesture.gesturesDisabled
import com.android.compose.modifiers.thenIf
import com.android.systemui.brightness.ui.compose.BrightnessSliderContainer
import com.android.systemui.brightness.ui.compose.ContainerColors
import com.android.systemui.common.shared.colors.SystemUISliderColors
import com.android.systemui.compose.modifiers.sysuiResTag
import com.android.systemui.media.remedia.ui.compose.Media
import com.android.systemui.media.remedia.ui.compose.MediaPresentationStyle
import com.android.systemui.qs.composefragment.ui.GridAnchor
import com.android.systemui.qs.panels.ui.compose.TileGrid
import com.android.systemui.qs.shared.ui.QuickSettings.Elements
import com.android.systemui.qs.ui.viewmodel.QuickSettingsContainerViewModel
import com.android.systemui.res.R
import com.android.systemui.volume.panel.component.volume.slider.ui.viewmodel.AudioStreamSliderViewModel
import com.android.systemui.volume.panel.component.volume.ui.composable.VolumeSlider
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun ContentScope.QuickSettingsContent(
    viewModel: QuickSettingsContainerViewModel,
    volumeSliderViewModel: AudioStreamSliderViewModel,
    mediaInRow: Boolean,
    modifier: Modifier = Modifier,
    mediaSquishiness: () -> Float = { 1f },
) {
    QuickSettingsPanelLayout(
        brightness =
            @Composable {
                if (viewModel.isBrightnessSliderVisible) {
                    var isBrightnessSliderInteractable by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        snapshotFlow { Elements.QuickSettingsContent.currentAlpha() }
                            .filterNotNull()
                            .collect { isBrightnessSliderInteractable = it >= .5f }
                    }
                    Element(modifier = Modifier, key = Elements.BrightnessSlider) {
                        BrightnessSliderContainer(
                            viewModel.brightnessSliderViewModel,
                            containerColors =
                                ContainerColors(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface,
                                ),
                            modifier =
                                Modifier.padding(
                                        vertical =
                                            dimensionResource(id = R.dimen.qs_brightness_margin_top)
                                    )
                                    .thenIf(!isBrightnessSliderInteractable) {
                                        Modifier.gesturesDisabled()
                                    },
                            dimensions = QuickSettingsShade.Dimensions.brightnessSliderDimensions,
                            sliderColors =
                                SystemUISliderColors.Defaults.copy(
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                        )
                    }
                }
            },
        volume =
            @Composable {
                val volumeSliderState by volumeSliderViewModel.slider.collectAsStateWithLifecycle()
                Box(Modifier.fillMaxWidth().sysuiResTag("volume_slider")) {
                    VolumeSlider(
                        state = volumeSliderState,
                        onValueChange = { value ->
                            volumeSliderViewModel.onValueChanged(volumeSliderState, value)
                        },
                        onValueChangeFinished = volumeSliderViewModel::onValueChangeFinished,
                        onIconTapped = { volumeSliderViewModel.toggleMuted(volumeSliderState) },
                        sliderColors = PlatformSliderDefaults.defaultPlatformSliderColors(),
                        modifier = Modifier.fillMaxWidth(),
                        hapticsViewModelFactory =
                            volumeSliderViewModel.getSliderHapticsViewModelFactory(),
                        showLabel = false,
                        dimensions = QuickSettingsShade.Dimensions.VolumeSliderDimensions,
                        materialSliderColors =
                            SystemUISliderColors.Defaults.copy(
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                        button = {
                            IconButton(
                                onClick = viewModel::onVolumeSettingsClicked,
                                modifier = Modifier.align(Alignment.CenterVertically).size(48.dp),
                            ) {
                                Icon(
                                    painter =
                                        painterResource(R.drawable.ic_fluent_options_24_regular),
                                    contentDescription =
                                        stringResource(R.string.accessibility_volume_settings),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        },
                    )
                }
            },
        tiles =
            @Composable {
                var listening by remember { mutableStateOf(false) }
                LifecycleStartEffect(Unit) {
                    listening = true

                    onStopOrDispose { listening = false }
                }

                Box {
                    GridAnchor()
                    TileGrid(
                        viewModel.tileGridViewModel,
                        listening = { listening },
                        modifier = Modifier.element(Elements.QuickSettingsTiles),
                    )
                }
            },
        media =
            @Composable {
                if (isAlwaysComposedContentVisible()) {
                    Element(key = Media.Elements.MediaCarousel, modifier = Modifier) {
                        Media(
                            viewModelFactory = viewModel.mediaViewModelFactory,
                            presentationStyle = MediaPresentationStyle.Default,
                            behavior = QuickSettingsContainerViewModel.mediaUiBehavior,
                            onDismissed = viewModel::onMediaSwipeToDismiss,
                            mediaSquishiness = mediaSquishiness,
                            location = Media.Location.QS,
                        )
                    }
                } else {
                    // Add an empty box when QS content is not visible to keep the same number of
                    // elements.
                    Box(modifier = Modifier)
                }
            },
        mediaInRow = mediaInRow,
        modifier =
            modifier
                .element(Elements.QuickSettingsContent)
                .padding(horizontal = dimensionResource(id = R.dimen.qs_horizontal_margin))
                .sysuiResTag("quick_settings_panel"),
    )
}

@Composable
private fun QuickSettingsPanelLayout(
    brightness: @Composable () -> Unit,
    tiles: @Composable () -> Unit,
    volume: @Composable () -> Unit,
    media: @Composable () -> Unit,
    mediaInRow: Boolean,
    modifier: Modifier = Modifier,
) {
    if (mediaInRow) {
        Column(
            verticalArrangement = spacedBy(QuickSettingsShade.Dimensions.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Row(
                horizontalArrangement = spacedBy(QuickSettingsShade.Dimensions.HorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) { tiles() }
                Box(modifier = Modifier.weight(1f)) { media() }
            }
            brightness()
            volume()
        }
    } else {
        Column(
            verticalArrangement = spacedBy(QuickSettingsShade.Dimensions.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            tiles()
            brightness()
            volume()
            media()
        }
    }
}
