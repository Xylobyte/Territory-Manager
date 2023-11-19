package fr.swiftapp.territorymanager.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import fr.swiftapp.territorymanager.ui.components.ChipWithSubItems
import fr.swiftapp.territorymanager.ui.components.MaterialButtonToggleGroup
import fr.swiftapp.territorymanager.ui.lists.TerritoryListItem
import kotlinx.coroutines.launch

@Composable
fun TerritoriesPage(database: TerritoryDatabase, navController: NavController) {
    var isShops by remember {
        mutableStateOf(false)
    }

    val territories = database.territoryDao().getAll(if (isShops) 1 else 0).collectAsState(initial = emptyList())
    var finalList = remember {
        mutableStateListOf<Territory>()
    }

    LaunchedEffect(territories.value) {
        finalList.clear()
        finalList.addAll(territories.value)
    }

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

    val context = LocalContext.current

    Column {
        MaterialButtonToggleGroup(
            items = listOf(stringResource(id = R.string.territories), stringResource(R.string.shops)),
            value = if (isShops) 1 else 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 4.dp),
            onClick = { isShops = it == 1 }
        )

        Row(
            modifier = Modifier.padding(10.dp, 4.dp)
        ) {
            ChipWithSubItems(
                chipLabel = "Status : ",
                chipItems = listOf(stringResource(R.string.all), stringResource(id = R.string.available), stringResource(R.string.in_progress)),
                onClick = {
                    finalList.clear()

                    if (it == context.getString(R.string.all)) {
                        finalList.addAll(territories.value)
                    } else if (it == context.getString(R.string.available)) {
                        finalList.addAll(territories.value.filter { it.isAvailable }.sortedBy { it.returnDate })
                    } else if (it == context.getString(R.string.in_progress)) {
                        finalList.addAll(territories.value.filter { !it.isAvailable }.sortedBy { it.givenDate })
                    }
                }
            )
        }

        if (finalList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_territories),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
                items(finalList) { territory ->
                    key(territory.id) {
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
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}