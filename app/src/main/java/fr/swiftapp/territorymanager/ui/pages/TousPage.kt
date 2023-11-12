package fr.swiftapp.territorymanager.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import fr.swiftapp.territorymanager.R
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.ui.lists.TerritoryListItem
import kotlinx.coroutines.launch

@Composable
fun TousPage(database: TerritoryDatabase, navController: NavController) {
    val territories = database.territoryDao().getAll().collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()
    val updateItem: (territory: Territory) -> Unit = { territory ->
        coroutineScope.launch {
            database.territoryDao().update(territory)
        }
    }

    val scrollState = rememberLazyListState()
    LaunchedEffect(Unit) {
        scrollState.animateScrollToItem(0)
    }

    if (territories.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.no_territories),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
            items(territories.value) { territory ->
                TerritoryListItem(
                    territory,
                    true,
                    { updateItem(it) },
                    {
                        navController.navigate("EditTerritory/${territory.id}", navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        })
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}