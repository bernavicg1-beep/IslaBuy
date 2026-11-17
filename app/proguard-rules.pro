# --- IslaBuy release keep rules ---

# Firestore deserializes into our data classes via reflection, so keep them intact.
-keep class com.islabuy.camiguin.data.model.** { *; }
-keepclassmembers class com.islabuy.camiguin.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Firebase / Firestore
-keep class com.google.firebase.** { *; }
-keep class com.google.firestore.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.firestore.**

# osmdroid
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# OkHttp / Okio
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# Coil
-dontwarn coil.**

# Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Keep annotations used by Compose and DataStore
-dontwarn org.jetbrains.annotations.**
