-keep class !ir.telgeram.Adel.** { *; }
-keep public class com.google.android.gms.* { public *; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-dontwarn !ir.telgeram.Adel.**,**
-dontwarn com.google.android.gms.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**
# Use -keep to explicitly keep any other classes shrinking would remove
-dontoptimize
#-dontshrink
#-dontobfuscate