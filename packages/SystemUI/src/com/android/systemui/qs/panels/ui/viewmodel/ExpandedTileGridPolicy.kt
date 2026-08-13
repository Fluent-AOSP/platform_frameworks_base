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

package com.android.systemui.qs.panels.ui.viewmodel

/** Display policy for the Fluent expanded Quick Settings grid. */
object ExpandedTileGridPolicy {
    const val TileSpan = 1
    private const val MaxColumns = 3

    /**
     * Preserve narrower upstream/media-aware layouts while capping expanded QS at three columns.
     */
    fun displayColumns(upstreamEffectiveColumns: Int): Int =
        upstreamEffectiveColumns.coerceIn(1, MaxColumns)
}
