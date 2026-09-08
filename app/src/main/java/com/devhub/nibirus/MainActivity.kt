package com.devhub.nibirus

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devhub.nibirus.data.DemoCity
import com.devhub.nibirus.data.DemoDataProvider
import com.devhub.nibirus.databinding.ActivityMainBinding
import io.github.devhubnibirus.searchableselector.SearchableSelector

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cities = DemoDataProvider.cities
    private var selectedCity: DemoCity? = null
    private var selectedCities: List<DemoCity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        selectedCity = cities.firstOrNull { it.id == savedInstanceState?.getInt("selectedCityId") }
        val selectedIds = savedInstanceState?.getIntArray("selectedCityIds")?.toSet().orEmpty()
        selectedCities = cities.filter { it.id in selectedIds }
        updateResults()

        binding.btnCompose.setOnClickListener {
            startActivity(Intent(this, ComposeExampleActivity::class.java))
        }
        binding.btnSingle.setOnClickListener {
            SearchableSelector.showSingle(
                context = this,
                title = getString(R.string.single_title),
                items = cities,
                getLabel = { it.label },
                searchText = { "${it.name} ${it.department}" },
                onItemSelected = { city ->
                    selectedCity = city
                    updateResults()
                }
            )
        }
        binding.btnMultiple.setOnClickListener {
            SearchableSelector.showMultiple(
                context = this,
                title = getString(R.string.multiple_title),
                items = cities,
                selectedItems = selectedCities,
                getLabel = { it.label },
                getId = { it.id },
                searchText = { "${it.name} ${it.department}" },
                maxSelection = null,
                onMaxSelectionReached = { limit ->
                    Toast.makeText(this, getString(R.string.selection_limit, limit), Toast.LENGTH_SHORT).show()
                },
                onConfirm = { selection ->
                    selectedCities = selection
                    updateResults()
                }
            )
        }
    }

    private fun updateResults() {
        binding.tvSingleResult.text = selectedCity?.label
            ?: getString(R.string.no_single_selection)
        binding.tvMultipleResult.text = if (selectedCities.isEmpty()) {
            getString(R.string.no_multiple_selection)
        } else {
            selectedCities.joinToString("\n") { it.label }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedCity?.let { outState.putInt("selectedCityId", it.id) }
        outState.putIntArray("selectedCityIds", selectedCities.map { it.id }.toIntArray())
    }
}
