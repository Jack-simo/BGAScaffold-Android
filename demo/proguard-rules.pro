# 代码混淆压缩比，在0~7之间
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 不做预校验，preverify 是 proguard 的四个步骤之一，因为 Android 的 dex 并不像 Java 虚拟机需要 preverify，去掉这一步能够加快混淆速度
-dontpreverify
# 不分析和优化字节码，optimize 是 proguard 的四个步骤之一，因为 Android 的 dex 并不像 Java 虚拟机需要 optimize，去掉这一步能够加快混淆速度
-dontoptimize
# 混淆时记录日志
-verbose
# 混淆时所采用的算法。google 推荐算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 避免混淆 Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 忽略警告
-ignorewarnings
# 混淆字典，实现乱花渐欲迷人眼的效果
-obfuscationdictionary proguard-dictionary.txt
-classobfuscationdictionary proguard-dictionary.txt
-packageobfuscationdictionary proguard-dictionary.txt
# 代码文件都移动到根包目录下，即在 / 包之下，这样当有人反编译了你的 APK，将会在根包之下看到 成千上万 的类文件并列着
-repackageclasses
# =========== 记录生成的日志数据 START ===========
# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt
# =========== 记录生成的日志数据 END ===========
-dontwarn **CompatHoneycomb,**CompatCreatorHoneycombMR2
# 处理support包
-dontnote android.support.**
-dontwarn android.support.**
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
# 保留四大组件，自定义的 Application 等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
# 保留 Keep 注解的类名和方法
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}
# 保留 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 不混淆泛型
-keepattributes Signature
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留 Serializable 序列化类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留 Parcelable 序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
# WebView
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# View
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# ====================== 项目自定义 START ======================
## ----------------------------------
##      DataBinding 相关
## ----------------------------------
-keepclasseswithmembers class * extends android.databinding.ViewDataBinding {
    <methods>;
}

## ----------------------------------
##      Glide 4.x 相关
## ----------------------------------
-keep class com.bumptech.glide.Glide { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**
## ----------------------------------
##      Okio 相关
## ----------------------------------
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

## ----------------------------------
##      OkHttp3.x 相关
## ----------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

## ----------------------------------
##      Retrofit 2.x 相关
## ----------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

## ----------------------------------
##      RxJava 1.x 相关
## ----------------------------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

## ----------------------------------
##      GreenDao 3.x 相关
## ----------------------------------
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.greenrobot.greendao.database.**
-dontwarn rx.**
# ====================== 项目自定义 END ======================