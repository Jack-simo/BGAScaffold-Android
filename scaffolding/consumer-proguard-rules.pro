## ----------------------------------
##      DataBinding 相关
## ----------------------------------
-keepclasseswithmembers class * extends android.databinding.ViewDataBinding{
    <methods>;
}

## ----------------------------------
##      retrolambda 相关
## ----------------------------------
-dontwarn java.lang.invoke.*