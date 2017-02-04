package com.example.dscs.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom class to work with Google Material Design icons.
 */
public class IconFont extends TextView {

    private static final String ICON_FONT = "MaterialIcons-Regular.ttf";
    private static Typeface sIconFont;

    public IconFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            if (sIconFont == null) {
                sIconFont = Typeface.createFromAsset(context.getResources().getAssets(), ICON_FONT);
            }
            setTypeface(sIconFont);
        }
    }

}
