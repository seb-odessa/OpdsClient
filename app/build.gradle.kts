plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.opds.client"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.opds.client"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters.clear()
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")
        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    externalNativeBuild {
        ndkBuild {
            path = file("./jni/Android.mk")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "35.0.0"
    ndkVersion = "27.0.12077973"

    sourceSets {
        this.getByName("main") {
            jniLibs.srcDirs("./jni/libs")
        }
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.runner)

}