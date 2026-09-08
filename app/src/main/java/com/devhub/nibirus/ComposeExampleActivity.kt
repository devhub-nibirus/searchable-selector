package com.devhub.nibirus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devhub.nibirus.data.DemoDataProvider
import io.github.devhubnibirus.searchableselector.SearchableSelector

// AppCompat supplies the theme required by the library's AlertDialog.
class ComposeExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SelectorExampleScreen(onBack = { finish() })
                }
            }
        }
    }
}

@Composable
private fun SelectorExampleScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val cities = DemoDataProvider.cities
    var selectedCityId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCityIds by rememberSaveable { mutableStateOf(intArrayOf()) }
    val selectedCity = cities.firstOrNull { it.id == selectedCityId }
    val selectedCities = cities.filter { it.id in selectedCityIds }
    val activeDialog = remember { mutableStateOf<AlertDialog?>(null) }

    DisposableEffect(context) {
        onDispose {
            activeDialog.value?.dismiss()
            activeDialog.value = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.compose_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.demo_description))

        // Open dialogs from clicks, never directly during composition.
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            activeDialog.value?.dismiss()
            activeDialog.value = SearchableSelector.showSingle(
                context = context,
                title = context.getString(R.string.single_title),
                items = cities,
                getLabel = { it.label },
                searchText = { "${it.name} ${it.department}" },
                onItemSelected = { selectedCityId = it.id }
            )
        }) {
            Text(stringResource(R.string.single_button))
        }
        Text(selectedCity?.label ?: stringResource(R.string.no_single_selection))

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            activeDialog.value?.dismiss()
            activeDialog.value = SearchableSelector.showMultiple(
                context = context,
                title = context.getString(R.string.multiple_title),
                items = cities,
                selectedItems = selectedCities,
                getLabel = { it.label },
                getId = { it.id },
                searchText = { "${it.name} ${it.department}" },
                maxSelection = 3,
                onMaxSelectionReached = { limit ->
                    Toast.makeText(context, context.getString(R.string.selection_limit, limit), Toast.LENGTH_SHORT).show()
                },
                onConfirm = { selection -> selectedCityIds = selection.map { it.id }.toIntArray() }
            )
        }) {
            Text(stringResource(R.string.multiple_button))
        }
        Text(
            if (selectedCities.isEmpty()) stringResource(R.string.no_multiple_selection)
            else selectedCities.joinToString("\n") { it.label }
        )
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back_to_binding))
        }
    }
}
