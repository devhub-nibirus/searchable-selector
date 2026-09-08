plugins {
    alias(libs.plugins.android.library)
    id("com.vanniktech.maven.publish") version "0.37.0"
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

        //consumerProguardFiles("consumer-rules.pro")
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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "io.github.devhub-nibirus",
        artifactId = "searchable-selector",
        version = "1.0.1"
    )

    pom {
        name.set("SearchableSelector")
        description.set(
            "A searchable selector for Android with single " +
                    "and multiple selection support."
        )
        inceptionYear.set("2026")
        url.set(
            "https://github.com/devhub-nibirus/searchable-selector"
        )

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set(
                    "https://www.apache.org/licenses/LICENSE-2.0.txt"
                )
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("devhub-nibirus")
                name.set("Frank Vasquez")
                url.set("https://github.com/devhub-nibirus")
            }
        }

        scm {
            url.set(
                "https://github.com/devhub-nibirus/searchable-selector"
            )
            connection.set(
                "scm:git:https://github.com/" +
                        "devhub-nibirus/searchable-selector.git"
            )
            developerConnection.set(
                "scm:git:ssh://git@github.com/" +
                        "devhub-nibirus/searchable-selector.git"
            )
        }
    }
}