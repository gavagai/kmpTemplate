package com.teddyfreddy.kmp.android.ui.geometry

import android.graphics.Rect
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * Information about the posture of the device
 */
sealed interface DevicePosture {
    object NormalPosture : DevicePosture

    data class BookPosture(
        val hingePosition: Rect
    ) : DevicePosture

    data class Separating(
        val hingePosition: Rect,
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}


/**
 * Flow of [DevicePosture] that emits every time there's a change in the windowLayoutInfo
 */
fun devicePostureFlow(componentActivity: ComponentActivity, lifecycleScope: LifecycleCoroutineScope) : StateFlow<DevicePosture> =
    WindowInfoTracker.getOrCreate(componentActivity).windowLayoutInfo(componentActivity)
    .flowWithLifecycle(componentActivity.lifecycle)
    .map { layoutInfo ->
        val foldingFeature =
            layoutInfo.displayFeatures
                .filterIsInstance<FoldingFeature>()
                .firstOrNull()
        when {
            isBookPosture(foldingFeature) ->
                DevicePosture.BookPosture(foldingFeature.bounds)

            isSeparating(foldingFeature) ->
                DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

            else -> DevicePosture.NormalPosture
        }
    }
    .stateIn(
        scope = lifecycleScope,
        started = SharingStarted.Eagerly,
        initialValue = DevicePosture.NormalPosture
    )


@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}
