plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    useLibrary("org.apache.http.legacy")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    // 其他依賴項

    // CameraX 依賴項
    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.camera:camera-core:1.1.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.camera:camera-camera2:1.1.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.camera:camera-view:1.0.0-alpha28")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha28")

    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.google.android.material:material:1.12.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //noinspection UseTomlInstead,GradleDependency
    implementation ("androidx.room:room-runtime:2.5.0")
    //noinspection UseTomlInstead,GradleDependency
    annotationProcessor ("androidx.room:room-compiler:2.5.0")

    implementation(libs.activation)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.mediarouter)
    implementation(libs.legacy.support.v4)
    implementation(libs.activity)
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
