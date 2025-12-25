# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.harmony.player.data.** { *; }
-keep class androidx.room.** { *; }

-keepnames class kotlinx.serialization.internal.GeneratedSerializer
-keepclassmembers class kotlinx.serialization.json.** { *; }

-dontwarn kotlinx.serialization.**
