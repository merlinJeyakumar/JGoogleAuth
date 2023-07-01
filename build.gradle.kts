
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nativedevps.spreadsheet"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:${libraries.Versions.appCompatVersion}")
    implementation("com.google.android.material:material:${libraries.Versions.materialVersion}")
    implementation(RequiredLibraries.core_ktx)
    implementation(RequiredLibraries.viewmodel_ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation(RequiredLibraries.coroutines_android)
    implementation(RequiredLibraries.coroutines_core)
    implementation(RequiredLibraries.coroutines_test)
    implementation(RequiredLibraries.coroutines_play_services)
    implementation(RequiredLibraries.anko)
    implementation(RequiredLibraries.anko_commons)
    implementation(RequiredLibraries.lifecycle_extension)

    implementation(GoogleMiscLibraries.playservices_auth) {
        exclude("org.apache.httpcomponents")
    }
    implementation(GoogleMiscLibraries.google_sheets)
    implementation(GoogleMiscLibraries.google_oauth_jetty) {
        exclude("org.apache.httpcomponents")
    }
    implementation(GoogleMiscLibraries.google_api_client) {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0") {
        exclude("org.apache.httpcomponents")
    }
}

object GoogleMiscLibraries {
    const val playservices_auth = "com.google.android.gms:play-services-auth:20.1.0" //https://developers.google.com/android/guides/setup
    const val google_sheets = "com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0"//exclude: org.apache.httpcomponents //https://mvnrepository.com/artifact/com.google.apis/google-api-services-sheets/v4-rev612-1.25.0
    const val google_oauth_jetty= "com.google.oauth-client:google-oauth-client-jetty:1.33.1" ////https://mvnrepository.com/artifact/com.google.oauth-client/google-oauth-client-jetty/1.33.1
    const val google_api_client= "com.google.api-client:google-api-client-android:1.33.2" //exclude: org.apache.httpcomponents https://mvnrepository.com/artifact/com.google.api-client/google-api-client-android/1.33.2
}