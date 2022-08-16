package com.teddyfreddy.android.ui.adaptive

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass


class AdaptiveDesign {

    enum class NavigationType {
        BottomNavigation, NavigationRail, PermanentNavigationDrawer
    }
    enum class ContentType {
        Compact, Expanded
    }

    companion object {
        fun navigationType(windowWidthSizeClass: WindowWidthSizeClass, foldingDevicePosture: DevicePosture) : NavigationType {
            return when (windowWidthSizeClass) {
                WindowWidthSizeClass.Compact -> NavigationType.BottomNavigation
                WindowWidthSizeClass.Medium -> NavigationType.NavigationRail
                WindowWidthSizeClass.Expanded -> {
                    if (foldingDevicePosture is DevicePosture.BookPosture) {
                        NavigationType.NavigationRail
                    } else {
                        NavigationType.PermanentNavigationDrawer
                    }
                }
                else -> NavigationType.BottomNavigation
            }
        }

        fun contentType(windowWidthSizeClass: WindowWidthSizeClass, foldingDevicePosture: DevicePosture) : ContentType {
            return when (windowWidthSizeClass) {
                WindowWidthSizeClass.Compact -> ContentType.Compact
                WindowWidthSizeClass.Medium ->
                    if (foldingDevicePosture is DevicePosture.BookPosture
                        || foldingDevicePosture is DevicePosture.Separating) {
                        ContentType.Expanded
                    } else {
                        ContentType.Compact
                    }
                WindowWidthSizeClass.Expanded -> ContentType.Expanded
                else -> ContentType.Compact
            }
        }
    }
}