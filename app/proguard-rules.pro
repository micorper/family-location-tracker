# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.annotation.Annotation { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.example.familylocation.** { *; }

# ZXing QR Code
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# AndroidX
-keep class androidx.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# Keep model classes
-keep class com.example.familylocation.model.** { *; }
