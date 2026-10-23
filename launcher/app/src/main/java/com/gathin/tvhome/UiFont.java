package com.gathin.tvhome;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.core.content.res.ResourcesCompat;

/**
 * The typeface behind one of the font_* strings in res/values/fonts.xml, for views built in code. The string
 * points at a bundled font, or, in locales Outfit can't draw (values-vi), names a system family (#833).
 */
final class UiFont {
    private UiFont() {}

    static Typeface get(Context context, int fontString) {
        Resources res = context.getResources();
        TypedValue value = new TypedValue();
        res.getValue(fontString, value, true);
        String name = String.valueOf(value.string);
        if (name.startsWith("res/")) {
            // ResourcesCompat reads res/font XML on API 24/25 too, where Resources.getFont does not exist.
            Typeface bundled = ResourcesCompat.getFont(context, value.resourceId);
            return bundled != null ? bundled : Typeface.DEFAULT;
        }
        return Typeface.create(name, Typeface.NORMAL);
    }
}
