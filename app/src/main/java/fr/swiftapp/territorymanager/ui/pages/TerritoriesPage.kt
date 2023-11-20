package fr.swiftapp.territorymanager.ui.pages

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import fr.swiftapp.territorymanager.R
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.settings.getNameList
import fr.swiftapp.territorymanager.settings.getNameListAsFlow
import fr.swiftapp.territorymanager.ui.components.ChipWithSubItems
import fr.swiftapp.territorymanager.ui.components.MaterialButtonToggleGroup
import fr.swiftapp.territorymanager.ui.lists.TerritoryListItem
import kotlinx.coroutines.launch

@Composable
fun TerritoriesPage(database: TerritoryDatabase, navController: NavController) {
    var isShops by rememberSaveable {
        mutableStateOf(false)
    }
    var status by rememberSaveable {
        mutableIntStateOf(0)
    }
    var publisher by remember {
        mutableIntStateOf(0)
    }

    val names = getNameListAsFlow(LocalContext.current).collectAsState(initial = "")

    val territories = database.territoryDao().getAll(if (isShops) 1 else 0).collectAsState(initial = emptyList())
    val finalList = remember {
        mutableStateListOf<Territory>()
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

    LaunchedEffect(status, territories.value, Unit) {
        finalList.clear()
        when (status) {
            0 -> {
                finalList.addAll(territories.value)
            }
            1 -> {
                finalList.addAll(territories.value.filter { it.isAvailable }.sortedBy { it.returnDate })
            }
            2 -> {
                finalList.addAll(territories.value.filter { !it.isAvailable }.sortedBy { it.givenDate })
            }
        }
    }

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
            modifier = Modifier
                .padding(10.dp, 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ChipWithSubItems(
                chipLabel = "Status : ",
                chipItems = listOf(stringResource(R.string.all), stringResource(id = R.string.available), stringResource(R.string.in_progress)),
                onClick = { status = it },
                value = status
            )

            names.value?.split(',')?.let { listOf("Tous les proclamateurs", *it.toTypedArray()) }?.let {list ->
                ChipWithSubItems(
                    chipLabel = "",
                    chipItems = list,
                    onClick = { publisher = it },
                    value = publisher
                )
            }
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