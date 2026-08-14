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

package com.android.systemui.statusbar.pipeline.battery.ui.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.systemui.common.ui.compose.load
import com.android.systemui.lifecycle.rememberViewModel
import com.android.systemui.res.R
import com.android.systemui.statusbar.phone.domain.interactor.IsAreaDark
import com.android.systemui.statusbar.pipeline.battery.ui.viewmodel.BatteryViewModel

@Composable
fun BatteryWithEstimate(
    viewModelFactory: BatteryViewModel.Factory,
    isDarkProvider: () -> IsAreaDark,
    textColor: Color,
    showEstimate: Boolean,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    useFluentIcon: Boolean = false,
) {
    val viewModel =
        rememberViewModel(traceName = "BatteryWithEstimate") { viewModelFactory.create() }

    val batteryHeight =
        with(LocalDensity.current) {
            BatteryViewModel.getStatusBarBatteryHeight(LocalContext.current).toDp()
        }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showIcon) {
            if (useFluentIcon) {
                val iconRes =
                    if (viewModel.isCharging) {
                        R.drawable.ic_fluent_battery_charge_24_regular
                    } else {
                        fluentBatteryIconRes(viewModel.level)
                    }
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = viewModel.contentDescription.load(),
                    tint = textColor,
                    modifier = Modifier.height(20.dp).align(Alignment.CenterVertically),
                )
            } else {
                UnifiedBattery(
                    viewModel = viewModel,
                    isDarkProvider = isDarkProvider,
                    modifier = Modifier.height(batteryHeight).align(Alignment.CenterVertically),
                )
            }
        }
        if (showEstimate) {
            viewModel.batteryTimeRemainingEstimate?.let {
                Text(
                    text = it,
                    color = textColor,
                    style = BatteryViewModel.getStatusBarBatteryTextStyle(LocalContext.current),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(iterations = 1),
                )
            }
        }
    }
}

private fun fluentBatteryIconRes(level: Int?): Int =
    when (((level ?: 0).coerceIn(0, 100) + 5) / 10) {
        0 -> R.drawable.ic_fluent_battery_0_24_regular
        1 -> R.drawable.ic_fluent_battery_1_24_regular
        2 -> R.drawable.ic_fluent_battery_2_24_regular
        3 -> R.drawable.ic_fluent_battery_3_24_regular
        4 -> R.drawable.ic_fluent_battery_4_24_regular
        5 -> R.drawable.ic_fluent_battery_5_24_regular
        6 -> R.drawable.ic_fluent_battery_6_24_regular
        7 -> R.drawable.ic_fluent_battery_7_24_regular
        8 -> R.drawable.ic_fluent_battery_8_24_regular
        9 -> R.drawable.ic_fluent_battery_9_24_regular
        else -> R.drawable.ic_fluent_battery_10_24_regular
    }
