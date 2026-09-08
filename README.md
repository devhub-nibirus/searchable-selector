# SearchableSelector

A lightweight and customizable searchable selector for Android, built with Kotlin and XML layouts.

It supports single and multiple selection and can be used from traditional XML views, View Binding, and Jetpack Compose.

## Features

* Single-item selection
* Multiple-item selection
* Real-time search
* Custom search fields
* Preselected items
* Optional maximum selection limit
* Select-all and clear-selection actions
* Empty-results state
* Compatible with XML, View Binding, and Jetpack Compose
* Minimum Android version: API 23

## Installation

For local development, add the library module to your project:

```kotlin
dependencies {
    implementation(project(":searchable-selector"))
}
```

Maven Central:

```kotlin
dependencies {
    implementation(
        "io.github.devhub-nibirus:searchable-selector:1.0.1"
    )
}
```

Make sure your repositories include Maven Central:

```kotlin
repositories {
    google()
    mavenCentral()
}
```

## Single selection

```kotlin
SearchableSelector.showSingle(
    context = this,
    title = "Select a worker",
    items = workers,
    getLabel = { worker ->
        "${worker.firstName} ${worker.lastName}"
    },
    searchText = { worker ->
        "${worker.document} ${worker.firstName} ${worker.lastName}"
    },
    onItemSelected = { selectedWorker ->
        // Handle selected item
    }
)
```

## Multiple selection

```kotlin
SearchableSelector.showMultiple(
    context = this,
    title = "Select workers",
    items = workers,
    selectedItems = selectedWorkers,
    getLabel = { worker ->
        "${worker.firstName} ${worker.lastName}"
    },
    getId = { worker ->
        worker.id
    },
    searchText = { worker ->
        "${worker.document} ${worker.firstName} ${worker.lastName}"
    },
    maxSelection = 5,
    onMaxSelectionReached = { limit ->
        // The maximum selection limit was reached
    },
    onConfirm = { selections ->
        selectedWorkers = selections
    },
    onCancel = {
        // The user cancelled the selection
    }
)
```

## View Binding

```kotlin
binding.btnOpenSelector.setOnClickListener {
    SearchableSelector.showSingle(
        context = this,
        title = "Select an option",
        items = options,
        getLabel = { it.name },
        onItemSelected = { selected ->
            binding.tvSelected.text = selected.name
        }
    )
}
```

## Jetpack Compose

```kotlin
@Composable
fun SearchableSelectorExample(
    items: List<MyItem>
) {
    val context = LocalContext.current
    var selectedItem by remember {
        mutableStateOf<MyItem?>(null)
    }

    Button(
        onClick = {
            SearchableSelector.showSingle(
                context = context,
                title = "Select an option",
                items = items,
                getLabel = { it.name },
                onItemSelected = {
                    selectedItem = it
                }
            )
        }
    ) {
        Text(
            text = selectedItem?.name ?: "Select"
        )
    }
}
```

## Requirements

* Android API 23 or higher
* Kotlin
* AndroidX AppCompat
* Material Components

## Sample application

The `app` module contains examples using:

* XML layouts
* View Binding
* Jetpack Compose

## License

```text
Copyright 2026 Frank Vasquez

Licensed under the Apache License, Version 2.0.
```

See the [LICENSE](LICENSE) file for details.
