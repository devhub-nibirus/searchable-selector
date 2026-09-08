package io.github.devhubnibirus.searchableselector

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.text.Normalizer
import java.util.Locale

object SearchableSelector {

    fun <T> showSingle(
        context: Context,
        title: String,
        items: List<T>,
        getLabel: (T) -> String,
        searchText: (T) -> String = getLabel,
        initialQuery: String = "",
        onCancel: (() -> Unit)? = null,
        onItemSelected: (T) -> Unit
    ): AlertDialog {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.searchable_selector_dialog, null, false)

        val searchInput = dialogView.findViewById<EditText>(R.id.etBuscar)

        val itemsList = dialogView.findViewById<ListView>(R.id.lvItems)

        val emptyText = dialogView.findViewById<TextView>(R.id.tvEmpty)

        val selectionActions = dialogView.findViewById<LinearLayout>(R.id.contentSelectionActions)

        selectionActions.visibility = View.GONE

        val filteredItems = items.toMutableList()

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            filteredItems.map(getLabel).toMutableList()
        )

        itemsList.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton(
                R.string.searchable_selector_close
            ) { _, _ ->
                onCancel?.invoke()
            }
            .create()

        fun updateList() {
            adapter.clear()
            adapter.addAll(filteredItems.map(getLabel))
            adapter.notifyDataSetChanged()

            updateEmptyState(
                listView = itemsList,
                emptyText = emptyText,
                isEmpty = filteredItems.isEmpty()
            )
        }

        fun applyFilter(query: String) {
            filteredItems.clear()
            filteredItems.addAll(
                filterItems(
                    items = items,
                    query = query,
                    searchText = searchText
                )
            )
            updateList()
        }

        itemsList.setOnItemClickListener { _, _, position, _ ->
            filteredItems.getOrNull(position)?.let { selectedItem ->
                onItemSelected(selectedItem)
                dialog.dismiss()
            }
        }

        searchInput.addTextChangedListener(
            createTextWatcher(::applyFilter)
        )

        dialog.setOnCancelListener {
            onCancel?.invoke()
        }

        searchInput.setText(initialQuery)
        searchInput.setSelection(initialQuery.length)

        updateList()
        dialog.show()

        return dialog
    }

    fun <T> showMultiple(
        context: Context,
        title: String,
        items: List<T>,
        selectedItems: List<T> = emptyList(),
        getLabel: (T) -> String,
        getId: (T) -> Any,
        searchText: (T) -> String = getLabel,
        initialQuery: String = "",
        maxSelection: Int? = null,
        onMaxSelectionReached: ((Int) -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onConfirm: (List<T>) -> Unit
    ): AlertDialog {
        require(maxSelection == null || maxSelection > 0) {
            "maxSelection debe ser mayor que cero"
        }

        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.searchable_selector_dialog, null, false)

        val searchInput =
            dialogView.findViewById<EditText>(R.id.etBuscar)

        val itemsList =
            dialogView.findViewById<ListView>(R.id.lvItems)

        val emptyText =
            dialogView.findViewById<TextView>(R.id.tvEmpty)

        val selectionActions =
            dialogView.findViewById<LinearLayout>(
                R.id.contentSelectionActions
            )

        val selectAllButton =
            dialogView.findViewById<Button>(R.id.btnSelectAll)

        val clearSelectionButton =
            dialogView.findViewById<Button>(
                R.id.btnClearSelection
            )

        selectionActions.visibility = View.VISIBLE

        val selectedIds = selectedItems
            .map(getId)
            .let { ids ->
                if (maxSelection == null) {
                    ids
                } else {
                    ids.take(maxSelection)
                }
            }
            .toMutableSet()

        val filteredItems = items.toMutableList()

        val adapter = object : ArrayAdapter<T>(
            context,
            android.R.layout.simple_list_item_multiple_choice,
            mutableListOf()
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {
                val view = super.getView(
                    position,
                    convertView,
                    parent
                )

                val checkedText = view.findViewById<CheckedTextView>(
                    android.R.id.text1
                )

                getItem(position)?.let { item ->
                    checkedText.text = getLabel(item)
                    checkedText.isChecked = getId(item) in selectedIds
                }

                return view
            }
        }

        itemsList.choiceMode = ListView.CHOICE_MODE_NONE
        itemsList.adapter = adapter

        fun updateSelectionState() {
            adapter.notifyDataSetChanged()

            clearSelectionButton.isEnabled =
                selectedIds.isNotEmpty()

            selectAllButton.isEnabled =
                maxSelection == null ||
                        selectedIds.size < maxSelection
        }

        fun updateList() {
            adapter.clear()
            adapter.addAll(filteredItems)
            adapter.notifyDataSetChanged()

            updateSelectionState()

            updateEmptyState(
                listView = itemsList,
                emptyText = emptyText,
                isEmpty = filteredItems.isEmpty()
            )
        }

        fun applyFilter(query: String) {
            filteredItems.clear()
            filteredItems.addAll(
                filterItems(
                    items = items,
                    query = query,
                    searchText = searchText
                )
            )
            updateList()
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(
                R.string.searchable_selector_accept
            ) { _, _ ->
                val result = items.filter { item ->
                    getId(item) in selectedIds
                }

                onConfirm(result)
            }
            .setNegativeButton(
                R.string.searchable_selector_cancel
            ) { _, _ ->
                onCancel?.invoke()
            }
            .create()

        itemsList.setOnItemClickListener { _, _, position, _ ->
            val item = filteredItems.getOrNull(position)
                ?: return@setOnItemClickListener

            val id = getId(item)

            if (id in selectedIds) {
                selectedIds.remove(id)
            } else {
                val limitReached =
                    maxSelection != null &&
                            selectedIds.size >= maxSelection

                if (limitReached) {
                    onMaxSelectionReached?.invoke(maxSelection!!)
                } else {
                    selectedIds.add(id)
                }
            }

            updateSelectionState()
        }

        selectAllButton.setOnClickListener {
            val availableItems = filteredItems.filter { item ->
                getId(item) !in selectedIds
            }

            val remainingSlots = maxSelection?.let { limit ->
                (limit - selectedIds.size).coerceAtLeast(0)
            }

            val itemsToSelect = remainingSlots?.let {
                availableItems.take(it)
            } ?: availableItems

            selectedIds.addAll(itemsToSelect.map(getId))
            updateSelectionState()

            if (
                maxSelection != null &&
                availableItems.size > itemsToSelect.size
            ) {
                onMaxSelectionReached?.invoke(maxSelection)
            }
        }

        clearSelectionButton.setOnClickListener {
            selectedIds.clear()
            updateSelectionState()
        }

        searchInput.addTextChangedListener(
            createTextWatcher(::applyFilter)
        )

        dialog.setOnCancelListener {
            onCancel?.invoke()
        }

        dialog.setOnShowListener {
            updateSelectionState()
        }

        searchInput.setText(initialQuery)
        searchInput.setSelection(initialQuery.length)

        updateList()
        dialog.show()

        return dialog
    }

    private fun <T> filterItems(
        items: List<T>,
        query: String,
        searchText: (T) -> String
    ): List<T> {
        val normalizedQuery = normalize(query)

        if (normalizedQuery.isEmpty()) {
            return items
        }

        return items.filter { item ->
            normalize(searchText(item))
                .contains(normalizedQuery)
        }
    }

    private fun normalize(value: String): String {
        return Normalizer
            .normalize(value, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .trim()
            .lowercase(Locale.ROOT)
    }

    private fun updateEmptyState(
        listView: ListView,
        emptyText: TextView,
        isEmpty: Boolean
    ) {
        listView.visibility =
            if (isEmpty) View.GONE else View.VISIBLE

        emptyText.visibility =
            if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun createTextWatcher(
        onTextChanged: (String) -> Unit
    ): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                onTextChanged(text?.toString().orEmpty())
            }

            override fun afterTextChanged(
                editable: Editable?
            ) = Unit
        }
    }
}