plugins {
    id("com.android.application")
}

android {
    namespace = "com.ml.study"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ml.study"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ""
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 把 assets 里的 index.html 原样打包，不做压缩转换（否则 WebView 读取会慢）
    androidResources {
        noCompress += listOf("html")
    }
}

dependencies {
    // 刻意零依赖：不用 AndroidX、不用第三方库，构建快、体积小、完全离线
}
