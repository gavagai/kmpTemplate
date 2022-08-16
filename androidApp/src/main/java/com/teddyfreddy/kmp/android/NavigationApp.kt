package com.teddyfreddy.kmp.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teddyfreddy.kmp.android.ui.decompose.RootComponent
import com.teddyfreddy.kmp.android.ui.geometry.AdaptiveDesign
import kotlinx.coroutines.launch


object AppDestinations {
    const val INBOX = "Inbox"
    const val ARTICLES = "Articles"
    const val DM = "DirectMessages"
    const val GROUPS = "Groups"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    navigationType: AdaptiveDesign.NavigationType,
    contentType: AdaptiveDesign.ContentType,
    component: RootComponent
) {
    // App drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedDestination = AppDestinations.INBOX



    if (navigationType == AdaptiveDesign.NavigationType.PermanentNavigationDrawer) {
        PermanentNavigationDrawer(drawerContent = { AppNavigationDrawerContent(selectedDestination) }) {
            NavigationAppContent(navigationType, contentType, component)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                AppNavigationDrawerContent(
                    selectedDestination,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            NavigationAppContent(
                navigationType, contentType, component,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationAppContent(
    navigationType: AdaptiveDesign.NavigationType,
    contentType: AdaptiveDesign.ContentType,
    component: RootComponent,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == AdaptiveDesign.NavigationType.NavigationRail) {
            AppNavigationRail(
                onDrawerClicked = onDrawerClicked
            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            if (contentType == AdaptiveDesign.ContentType.Expanded) {
                AppExpandedContent(
                    component = component,
                    modifier = Modifier.weight(1f),
                )
            } else {
                AppCompactContent(
                    component = component,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = navigationType == AdaptiveDesign.NavigationType.BottomNavigation) {
                AppBottomNavigationBar()
            }
        }
    }
}


@Composable
@Preview
fun AppNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon =  { Icon(imageVector = Icons.Default.Menu, contentDescription = "stringResource(id = R.string.navigation_drawer)") }
        )
        NavigationRailItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Inbox, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationRailItem(
            selected = false,
            onClick = {/*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Article, "stringResource(id = R.string.tab_article)") }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.Chat, "stringResource(id = R.string.tab_dm)") }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.People, "stringResource(id = R.string.tab_groups)") }
        )
    }
}

@Composable
@Preview
fun AppBottomNavigationBar() {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Chat, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawerContent(
    selectedDestination: String,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "stringResource(id = R.string.app_name).uppercase()",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.MenuOpen,
                    contentDescription = "stringResource(id = R.string.navigation_drawer)"
                )
            }
        }

        NavigationDrawerItem(
            selected = selectedDestination == AppDestinations.INBOX,
            label = { Text(text = "stringResource(id = R.string.tab_inbox)", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription =  "stringResource(id = R.string.tab_inbox)") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == AppDestinations.ARTICLES,
            label = { Text(text = "stringResource(id = R.string.tab_article)", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Article, contentDescription =  "stringResource(id = R.string.tab_article)") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == AppDestinations.DM,
            label = { Text(text = "stringResource(id = R.string.tab_dm)", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Chat, contentDescription =  "stringResource(id = R.string.tab_dm)") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == AppDestinations.GROUPS,
            label = { Text(text = "stringResource(id = R.string.tab_groups)", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector =  Icons.Default.Article, contentDescription =  "stringResource(id = R.string.tab_groups)") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
    }
}


@Composable
fun AppCompactContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
}

@Composable
fun AppExpandedContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
}