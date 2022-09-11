val baseURL = System.getenv("BASE_URL") ?: "https://www.gapfilm.ir/"
val sourceChannel = System.getenv("SOURCE_CHANNEL") ?: "gp_website"
val agent = System.getenv("AGENT") ?: "mj"
val adTraceApiKey = System.getenv("ADTRACE_API_KEY") ?: "sfgklwo7csps"
val najvaApiKey = System.getenv("NAJVA_API_KEY") ?: "79cfe251-7660-4e58-9ecc-b53837ef23e9"
val najvaWebsiteID = System.getenv("NAJVA_WEBSITE_ID") ?: "27891"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.gapfilm.app"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "BASE_URL", toValidString(baseURL))
        buildConfigField("String", "SOURCE_CHANNEL", toValidString(sourceChannel))
        buildConfigField("String", "AGENT", toValidString(agent))
        buildConfigField("String", "ADTRACE_API_KEY", toValidString(adTraceApiKey))

        manifestPlaceholders["NAJVA_API_KEY"] = najvaApiKey
        manifestPlaceholders["NAJVA_WEBSITE_ID"] = najvaWebsiteID
        manifestPlaceholders["HOST"] = getHost(baseURL)
        manifestPlaceholders["HOST_FULL"] = "www.${getHost(baseURL)}"
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.app.compat)
    implementation(libs.androidx.drawer.layout)
    implementation(libs.android.install.referrer)
    implementation(libs.google.identifier)
    implementation(libs.google.firebase.iid)
    implementation(libs.najva)
    implementation(libs.adtrace)
}

fun toValidString(str: String) = "\"$str\""
fun getHost(domain: String) = domain
    .replace(Regex("^http[s]*:\\/\\/(www\\.)*"), "")
    .replace(Regex("\\/$"), "")