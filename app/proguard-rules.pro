# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.so.luotk.* { *; }
-keep class com.so.luotk.models.** { *; }
#-keep class com.github.kittinunf.fuel.android.util.AndroidEnvironment
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
#-keepclassmembers class *  { com.facebook.react.uimanager.UIProp<fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**
-keep,includedescriptorclasses class com.facebook.react.bridge.** { *; }

# okhttp

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class com.so.luotk.* { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okio.** { *; }
-dontwarn okio.**

# WebRTC

-keep class org.webrtc.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid

# Jisti Meet SDK

-keep class org.jitsi.meet.** { *; }
-keep class org.jitsi.meet.sdk.** { *; }

# We added the following when we switched minifyEnabled on. Probably because we
# ran the app and hit problems...

-keep class com.facebook.react.bridge.CatalystInstanceImpl { *; }
-keep class com.facebook.react.bridge.ExecutorToken.* { *; }
-keep class com.facebook.react.bridge.JavaScriptExecutor { *; }
-keep class com.facebook.react.bridge.ModuleRegistryHolder.* { *; }
-keep class com.facebook.react.bridge.ReadableType { *; }
-keep class com.facebook.react.bridge.queue.NativeRunnable { *; }
-keep class com.facebook.react.devsupport.** { *; }

-dontwarn com.facebook.react.devsupport.**
-dontwarn com.google.appengine.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.servlet.**

# ^^^ We added the above when we switched minifyEnabled on.

# Rule to avoid build errors related to SVGs.
-keep public class com.horcrux.svg.** {*;}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}
-keepclassmembers enum * { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.facebook.applinks.** { *; }
-keepclassmembers class com.facebook.applinks.** { *; }
-keep class com.facebook.FacebookSdk { *; }
-keep class com.huawei.hms.ads.** { *; }
-keep interface com.huawei.hms.ads.** { *; }