# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/juhg/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#============================================================================
#   keep all classes that are custom notation
#============================================================================
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*


-optimizationpasses 5                                       # 指定代码的压缩级别
-dontusemixedcaseclassnames                                 # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                            # 是否混淆第三方jar
-dontpreverify                                              # 混淆时是否做预校验
-verbose                                                    # 混淆时是否记录日志

#============================================================================
#   keep gaode map and ignore its wraning
#============================================================================
-dontwarn android.support.v4.**
-dontwarn android.**
-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}

#============================================================================
#   keep umeng share sdk and ignore its wraning
#============================================================================
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**


-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.umeng.soexample.R$*{
    public static final int *;
}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keep class com.linkedin.** { *; }
-keepattributes Signature

#============================================================================
#   keep leancloud chat sdk and ignore its wraning
#============================================================================
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent{*;}

#============================================================================
#   keep all classes that used EventBus compentant
#============================================================================
-keepclassmembers class ** {
    public void onEvent*(**);
}

#============================================================================
#   keep all classes that implements Serializable
#============================================================================
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#============================================================================
#   keep all classes that implements Parcelable
#============================================================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#============================================================================
#   keep Gson and ignore its wraning
#============================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.sxu.CommonProject.bean.** { *; }

#============================================================================
#   keep umeng and ignore its wraning
#============================================================================
-dontwarn com.umeng.**
-dontwarn org.apache.commons.net.**
-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.common.** { *; }
-keep class com.umeng.newxp.** { *; }
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.socialize.sensor.**

#============================================================================
#   keep pulltorefresh library and ignore its warning
#============================================================================
-dontwarn com.handmark.pulltorefresh.library.**
-dontwarn com.handmark.pulltorefresh.library.extras.**
-dontwarn com.handmark.pulltorefresh.library.internal.**
-keep class com.handmark.pulltorefresh.library.** { *;}
-keep class com.handmark.pulltorefresh.library.extras.** { *;}
-keep class com.handmark.pulltorefresh.library.internal.** { *;}

#============================================================================
#   keep all classes that contain reflection calls
#============================================================================
-keepclasseswithmembernames class * {
    native <methods>;
}

#============================================================================
#   keep all classes that are custom component
#============================================================================
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn android.webkit.WebView

-keepattributes Exceptions,InnerClasses,Signature
-keep public class javax.**
-keep public class android.webkit.**
-keep public class [your_pkg].R$*{
    public static final int *;
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 public *;
}

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}

-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**
-keep class com.ultrapower.** {*;}

-dontwarn okio.**
-dontwarn com.qiniu.android.http.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.loopj.android.http.**
-dontwarn com.avos.avoscloud.**
