import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.lames.standard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lames.standard"
        minSdk = 28
        targetSdk = 33
        versionCode = 100
        versionName = "1.0.0"
        setProperty("archivesBaseName", "LamesApp_${versionName}_${versionCode}")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APP_ID", "\"FAKE_APP_ID\"")
        buildConfigField("String", "APP_SECRET", "\"FAKE_APP_SECRET\"")
        buildConfigField("String", "AGREEMENT_ID", "\"FAKE_AGREEMENT_ID\"")
        buildConfigField("String", "BASE_URL", "\"FAKE_BASE_URL\"")
        buildConfigField("String", "URL_DOMAIN", "\"FAKE_URL_DOMAIN\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            signingConfig = null
            //isMinifyEnabled = true
            //isShrinkResources = true
            //proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-third-rules.pro")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-third-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    signingConfigs {
        create("lamesSign") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }
    flavorDimensions += "api"
    productFlavors {
        create("Dev") {
            dimension = "api"
            signingConfig = signingConfigs.getByName("lamesSign")
        }
        create("Prd") {
            dimension = "api"
            signingConfig = signingConfigs.getByName("lamesSign")
        }
        create("Full") {
            dimension = "api"
            signingConfig = signingConfigs.getByName("lamesSign")
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.androidxKtx)
    implementation(libs.appcompat)
    implementation(libs.activityKtx)
    implementation(libs.fragmentKtx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.okhttp)
    implementation(libs.rxhttp)
    implementation(libs.glide)
    implementation(libs.brv)
    implementation(libs.mmkv)
    implementation(libs.refreshKernel)
    implementation(libs.refreshHeader)
    implementation(libs.refreshFooter)
    implementation(libs.gson)
    implementation(libs.flexbox)
    implementation(libs.permissions)
    //implementation(libs.hwService)
    //implementation(libs.hwClient)
    implementation(libs.mpAndroid)
    implementation(libs.pickerView)
    implementation(libs.wechatSdkAndroidWithoutMta)
    implementation(libs.androidImageCropper)
    implementation(libs.ossAndroidSdk)
    implementation(libs.swiperefreshlayout)
    implementation(libs.alicloudAndroidPush)
    implementation(libs.workRuntimeKtx)
    implementation(libs.cameraCore)
    implementation(libs.cameraLifecycle)
    implementation(libs.cameraView)
    implementation(libs.cameraExtensions)
    implementation(libs.core)
    implementation(libs.zxingAndroidEmbedded)
    //云康宝体脂秤
    implementation(libs.qnscalesdkx)
    implementation(libs.scanplus)

    kapt(libs.rxhttpCompiler)
    kapt(libs.glideCompiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.exJunit)
    androidTestImplementation(libs.espressoCore)

}
