package fr.swiftapp.territorymanager

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract.Colors
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.internal.GsonBuildConfig
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.settings.getNameList
import fr.swiftapp.territorymanager.settings.updateNamesList
import fr.swiftapp.territorymanager.ui.components.HyperlinkText
import fr.swiftapp.territorymanager.ui.dialogs.ViewNamesDialog
import fr.swiftapp.territorymanager.ui.theme.TerritoryManagerTheme
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

private lateinit var saveFileLauncher: ActivityResultLauncher<Intent>
private lateinit var loadFileLauncher: ActivityResultLauncher<Intent>

private lateinit var versionName: String

class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        versionName = packageManager.getPackageInfo(packageName, 0).versionName

        val db = TerritoryDatabase.getDatabase(this)

        val onBackPressedDispatcher = onBackPressedDispatcher
        saveFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data?.data
                    if (uri != null) {
                        lifecycleScope.launch {
                            db.territoryDao().exportAll().collect {
                                val names = getNameList(this@SettingsActivity)
                                val gson =
                                    GsonBuilder().serializeNulls().disableHtmlEscaping().create()

                                val json = JsonObject()
                                json.addProperty("names", names)
                                json.add("territories", gson.toJsonTree(it))

                                contentResolver.openOutputStream(uri)?.use { out ->
                                    out.write(json.toString().toByteArray())
                                }
                            }
                        }
                    }
                }
            }

        loadFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data?.data
                    if (uri != null) {
                        val stringBuilder = StringBuilder()
                        var inputStream: InputStream? = null

                        try {
                            inputStream = contentResolver.openInputStream(uri)
                            inputStream?.bufferedReader()?.use { reader ->
                                reader.forEachLine {
                                    stringBuilder.append(it)
                                }
                            }

                            val gson =
                                GsonBuilder().serializeNulls().disableHtmlEscaping().create()
                            val json =
                                gson.fromJson(stringBuilder.toString(), JsonObject::class.java)

                            lifecycleScope.launch {
                                updateNamesList(this@SettingsActivity, json.get("names").asString)

                                json.get("territories").asJsonArray.forEach {
                                    db.territoryDao()
                                        .insert(gson.fromJson(it, Territory::class.java))
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            inputStream?.close()
                        }
                    }
                }
            }

        setContent {
            val scrollBehavior =
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

            TerritoryManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                navigationIcon = {
                                    IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Retour"
                                        )
                                    }
                                },
                                title = {
                                    Text(text = "Options")
                                },
                                scrollBehavior = scrollBehavior
                            )
                        },
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        SettingsItems(it)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItems(padding: PaddingValues) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val names = remember { mutableStateListOf<String>() }

    val coroutineScope = rememberCoroutineScope()
    val getAll: () -> Unit = {
        coroutineScope.launch {
            val data = getNameList(context)
            names.clear()
            if (data != "") names.addAll(data.split(','))
        }
    }

    val updateNames: () -> Unit = {
        coroutineScope.launch {
            updateNamesList(context, names.joinToString(","))
        }
    }

    ViewNamesDialog(
        isOpen = openDialog.value,
        names = names,
        close = { openDialog.value = false },
        updateNames = { i -> names.removeAt(i); updateNames() }
    )

    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                getAll()
                openDialog.value = true
            }
        ) {
            Column(Modifier.padding(15.dp)) {
                Text(
                    text = "Proclamateurs",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "La liste des noms de tout les proclamateurs",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp, 0.dp, 0.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "backup_territory_manager.json")
                }

                saveFileLauncher.launch(intent)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Exporter",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Sauvegarder les noms et les territoires",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.rounded_arrow_circle_up_24),
                    contentDescription = "Import data",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp, 0.dp, 0.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }

                loadFileLauncher.launch(intent)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Importer",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Importer les noms et les territoires depuis un fichier .json",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    painter = painterResource(id = R.drawable.rounded_arrow_circle_down_24),
                    contentDescription = "Import data",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Icon(
            painter = painterResource(id = R.drawable.github_mark),
            contentDescription = "GitHub logo",
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "This project is available on ",
            )
            HyperlinkText(fullText = "GitHub", hyperLinks = mapOf(
                "GitHub" to "https://github.com/Swiftapp-hub/Territory-Manager"
            ), linkTextColor = MaterialTheme.colorScheme.primary, linkTextFontWeight = FontWeight.Bold, linkTextDecoration = TextDecoration.Underline)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "Version $versionName", color = MaterialTheme.colorScheme.outline)
    }
}
