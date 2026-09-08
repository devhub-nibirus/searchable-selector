plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.devhubnibirus.searchableselector"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    // Actívalo después de renombrar todos los recursos.
    resourcePrefix = "searchable_selector_"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    /*
     * Es parte de la API pública porque SearchableSelector
     * devuelve AlertDialog de AppCompat.
     */
    api(libs.androidx.appcompat)

    implementation(libs.material)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}