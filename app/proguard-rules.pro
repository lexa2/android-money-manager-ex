# Taken from https://gist.github.com/Jackgris/c4a71328b1ae346cba04
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
#-dontpreverify

# If you want to enable optimization, you should include the
# following:
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizationpasses 5
-allowaccessmodification

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
#Icon font
-keep class .R
-keep class **.R$* {
    <fields>;
}

# MMEX classes
#-keep class com.money.manager.ex.**
#-keep class com.money.manager.ex.home.RecentDatabasesProvider { *; }

# Parceler library
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class org.parceler.Parceler$$Parcels

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class com.github.mikephil.charting.** { *; }
-dontwarn io.realm.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# EventBus
-keepclassmembers class ** {
    public void onEvent*(***);
}
# Only required if you use AsyncExecutor
#-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}

# IcePick
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

#SQLCipher
#-keep class net.sqlcipher.** { *; }

# Ignore warnings
-dontwarn org.apache.**
-dontwarn com.opencsv.bean.**
-dontwarn com.google.common.**
-dontwarn sun.misc.Unsafe
# https://github.com/square/okio/issues/60
-dontwarn okio.**
# picasso
-dontwarn com.squareup.okhttp.**

# Test for debugging
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#-printmapping build/outputs/mapping/release/mapping.txt
#-dontobfuscate

# ?
-keepattributes InnerClasses