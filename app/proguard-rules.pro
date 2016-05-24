-keep class !android.support.v7.internal.view.menu.**,** {*;}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn
-ignorewarnings

-keep class android.support.v7.graphics.** { *; }

-keep class com.squareup.picasso.*{ *; }
-dontwarn com.squareup.picasso.**

-keep class okio.*{ *; }
-dontwarn okio.**

-keep class kellinwood.logging.log4j.*{ *; }
-dontwarn kellinwood.logging.log4j.**