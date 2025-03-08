# retain these to support class references for the bundling and translation systems
-keepnames class com.shatteredpixel.** { *; }
-keepnames class com.watabou.** { *; }

# keep classes that are instantiated via reflection
-keep class * extends com.watabou.glscripts.Script
-keep class * implements com.watabou.utils.Bundlable { *; }
-keep class com.watabou.utils.Reflection { *; }
-keep class com.watabou.utils.Bundle { *; }

# keep generators and items that use reflection for instantiation
-keep class com.shatteredpixel.shatteredpixeldungeon.items.Generator$* { *; }
-keep class com.shatteredpixel.shatteredpixeldungeon.levels.rooms.** { *; }

# keep special item classes that may use reflection
-keep class com.shatteredpixel.shatteredpixeldungeon.items.scrolls.** { *; }
-keep class com.shatteredpixel.shatteredpixeldungeon.items.potions.** { *; }
-keep class com.shatteredpixel.shatteredpixeldungeon.items.spells.** { *; }
-keep class com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.** { *; }

# retained to support meaningful stack traces
# note that the mapping file must be referenced in order to make sense of line numbers
# mapping file can be found in core/build/outputs/mapping after running a release build
-keepattributes SourceFile,LineNumberTable

# libGDX stuff
-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*

# needed for libGDX skin reflection used in text fields. Perhaps just don't use skin?
-keep class com.badlogic.gdx.graphics.Color { *; }
-keep class com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldStyle { *; }

# needed for libGDX controllers
-keep class com.badlogic.gdx.controllers.android.AndroidControllers { *; }

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
    <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
    boolean contactFilter(long, long);
    void    beginContact(long);
    void    endContact(long);
    void    preSolve(long, long);
    void    postSolve(long, long);
    boolean reportFixture(long);
    float   reportRayFixture(long, float, float, float, float, float);
}

# protect native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# ensure enum values are preserved
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# protect classes related to unstable/random effects
-keep class * extends com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Enchantment { *; }
-keep class com.shatteredpixel.shatteredpixeldungeon.items.**.*Unstable* { *; }

# ensure native handling works correctly
-keep class com.shatteredpixel.shatteredpixeldungeon.android.AndroidMissingNativesHandler { *; }
-keep class com.watabou.noosa.audio.** { *; }
-keep class com.watabou.input.ControllerHandler { *; }