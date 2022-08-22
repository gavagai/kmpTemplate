package com.teddyfreddy.kmp.android.ui.compose.app

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.teddyfreddy.kmp.android.ui.decompose.app.RootComponent
import com.teddyfreddy.android.ui.adaptive.AdaptiveDesign
import com.teddyfreddy.kmp.android.R
import kotlinx.coroutines.launch


object AppDestinations {
    const val INBOX = "Inbox"
    const val ARTICLES = "Articles"
    const val DM = "DirectMessages"
    const val GROUPS = "Groups"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveNavigationApp(
    navigationType: AdaptiveDesign.NavigationType,
    contentType: AdaptiveDesign.ContentType,
    component: RootComponent
) {
    // App drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val selectedDestination = AppDestinations.ARTICLES

    when (navigationType) {
        AdaptiveDesign.NavigationType.PermanentNavigationDrawer -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    AppNavigationDrawerContent(/* rootComponent.initialDestination */selectedDestination, permanent = true)
                }
            ) {
                AppContent(component, expanded = contentType == AdaptiveDesign.ContentType.Expanded)
            }
        }
        else -> {
            ModalNavigationDrawer(
                drawerContent = {
                    AppNavigationDrawerContent(
                        /* rootComponent.initialDestination */selectedDestination,
                        permanent = false,
                        onDrawerCloseClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                },
                drawerState = drawerState
            ) {
                when (navigationType) {
                    AdaptiveDesign.NavigationType.NavigationRail -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            AppNavigationRail(
                                onDrawerOpenClicked = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                                selectedDestination
                            )
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.inverseOnSurface)
                            ) {
                                AppContent(
                                    component = component,
                                    expanded = contentType == AdaptiveDesign.ContentType.Expanded,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                    AdaptiveDesign.NavigationType.BottomNavigation -> {
                        Scaffold(
                            bottomBar = { AppBottomNavigationBar(selectedDestination) },
                        ) {
                            AppContent(
                                component = component,
                                expanded = contentType == AdaptiveDesign.ContentType.Expanded,
                                modifier = Modifier.padding(paddingValues = it)
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}



@Composable
@Preview
fun AppNavigationRail(
    onDrawerOpenClicked: () -> Unit = {},
    @PreviewParameter(SampleAppDestinationsProvider::class) selectedDestination: String,
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        // Standard item to open modal drawer
        NavigationRailItem(
            selected = false,
            onClick = onDrawerOpenClicked,
            icon =  { Icon(imageVector = Icons.Default.Menu, contentDescription = "stringResource(id = R.string.navigation_drawer)") }
        )

        NavigationRailItem(
            selected = selectedDestination == AppDestinations.INBOX,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Inbox, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationRailItem(
            selected = selectedDestination == AppDestinations.ARTICLES,
            onClick = {/*TODO*/ },
            icon =  { Icon(imageVector = Icons.Default.Article, contentDescription = "stringResource(id = R.string.tab_article)") }
        )
        NavigationRailItem(
            selected = selectedDestination == AppDestinations.DM,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.Chat, contentDescription = "stringResource(id = R.string.tab_dm)") }
        )
        NavigationRailItem(
            selected = selectedDestination == AppDestinations.GROUPS,
            onClick = { /*TODO*/ },
            icon =  { Icon(imageVector = Icons.Outlined.People, contentDescription = "stringResource(id = R.string.tab_groups)") }
        )
    }
}

@Composable
@Preview
fun AppBottomNavigationBar(
    @PreviewParameter(SampleAppDestinationsProvider::class) selectedDestination: String,
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            selected = selectedDestination == AppDestinations.INBOX,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = selectedDestination == AppDestinations.ARTICLES,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = selectedDestination == AppDestinations.DM,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Chat, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
        NavigationBarItem(
            selected = selectedDestination == AppDestinations.GROUPS,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = "stringResource(id = R.string.tab_inbox)") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawerContent(
    @PreviewParameter(SampleAppDestinationsProvider::class) selectedDestination: String,
    permanent: Boolean,
    modifier: Modifier = Modifier,
    onDrawerCloseClicked: () -> Unit = {}
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
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (!permanent) {
                IconButton(onClick = onDrawerCloseClicked) {
                    Icon(
                        imageVector = Icons.Default.MenuOpen,
                        contentDescription = "stringResource(id = R.string.navigation_drawer)"
                    )
                }
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
fun AppContent(
    component: RootComponent,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column {
        Text(if (expanded) "Expanded" else "Compact")
    }
}


class SampleAppDestinationsProvider: PreviewParameterProvider<String> {
    override val values = sequenceOf(AppDestinations.INBOX)
}