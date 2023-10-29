package fr.swiftapp.territorymanager.ui.nav

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import androidx.navigation.navigation
import fr.swiftapp.territorymanager.R
import fr.swiftapp.territorymanager.SettingsActivity
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.ui.pages.AddTerritoryPage
import fr.swiftapp.territorymanager.ui.pages.DispoPage
import fr.swiftapp.territorymanager.ui.pages.EditTerritory
import fr.swiftapp.territorymanager.ui.pages.EnCoursPage
import fr.swiftapp.territorymanager.ui.pages.TousPage

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavPage() {
    val tabItems = listOf(stringResource(R.string.all), stringResource(R.string.in_progress), stringResource(R.string.available))
    val tabItemsRoutes = listOf("All", "InProgress", "Available")
    var selectedItem by remember {
        mutableIntStateOf(0)
    }
    var navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val parentRouteName = navBackStackEntry.value?.destination?.route

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current

    val db = TerritoryDatabase.getDatabase(context)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    if (parentRouteName != null) {
                        Text(
                            text = when (parentRouteName) {
                                "All" -> stringResource(R.string.territories)
                                "InProgress" -> stringResource(R.string.territories_in_working)
                                "Available" -> stringResource(R.string.territories_dispo)
                                "AddTerritory" -> stringResource(R.string.add_territory)
                                else -> {
                                    if (parentRouteName.contains("EditTerritory")) stringResource(R.string.details)
                                    else stringResource(R.string.loading)
                                }
                            },
                        )
                    }
                },
                navigationIcon = {
                    if (parentRouteName != null) {
                        AnimatedVisibility(
                            visible = (parentRouteName == "AddTerritory") || (parentRouteName.contains(
                                "EditTerritory"
                            )),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (parentRouteName != null) {
                        AnimatedVisibility(
                            visible = (parentRouteName != "AddTerritory") && (!parentRouteName.contains(
                                "EditTerritory"
                            )),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(onClick = {
                                startActivity(
                                    context,
                                    Intent(context, SettingsActivity::class.java),
                                    null
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = stringResource(R.string.settings)
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = parentRouteName == "All",
                enter = fadeIn(initialAlpha = 0f),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("AddTerritory", navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        })
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_territory)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.add_territory), fontSize = 16.sp)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar {
                tabItemsRoutes.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = parentRouteName == item,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item, navOptions {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            })
                        },
                        icon = {
                            when (item) {
                                tabItemsRoutes[0] -> Icon(
                                    painter = painterResource(id = R.drawable.rounded_home_24),
                                    contentDescription = stringResource(R.string.home)
                                )

                                tabItemsRoutes[1] -> Icon(
                                    painter = painterResource(id = R.drawable.rounded_logout_24),
                                    contentDescription = stringResource(R.string.in_progress)
                                )

                                tabItemsRoutes[2] -> Icon(
                                    painter = painterResource(id = R.drawable.rounded_check_circle_24),
                                    contentDescription = stringResource(R.string.available)
                                )
                            }
                        },
                        label = {
                            Text(text = tabItems[index])
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            navController = navController,
            startDestination = "Home"
        ) {
            navigation(startDestination = "All", route = "Home") {
                composable("All", deepLinks = listOf(NavDeepLink("deeplink://home"))) {
                    TousPage(database = db, navController = navController)
                }
                composable(
                    "AddTerritory",
                    deepLinks = listOf(NavDeepLink("deeplink://addTerritory"))
                ) {
                    AddTerritoryPage(database = db, navController = navController)
                }
                composable(
                    "EditTerritory/{territoryId}",
                    arguments = listOf(navArgument("territoryId") { type = NavType.IntType }),
                    deepLinks = listOf(NavDeepLink("deeplink://editTerritory/{territoryId}"))
                ) {
                    EditTerritory(
                        database = db,
                        navController = navController,
                        it.arguments?.getInt("territoryId")
                    )
                }
            }

            composable(
                "InProgress",
                deepLinks = listOf(NavDeepLink("deeplink://enCours"))
            ) {
                EnCoursPage(database = db)
            }

            composable(
                "Available",
                deepLinks = listOf(NavDeepLink("deeplink://dispo"))
            ) {
                DispoPage(database = db)
            }
        }
    }
}