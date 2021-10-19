package org.ginryan.oemtab.tablayout;

import android.util.TypedValue;
import android.view.View;

public class Utils {

    public static float dipToPx(View view, int dip) {
        return TypedValue.applyDimension(dip, TypedValue.COMPLEX_UNIT_DIP, view.getResources().getDisplayMetrics());
    }

    public static float spToPx(View view, int sp) {
        return TypedValue.applyDimension(sp, TypedValue.COMPLEX_UNIT_SP, view.getResources().getDisplayMetrics());
    }

    public static float ptToPx(View view, int pt) {
        return TypedValue.applyDimension(pt, TypedValue.COMPLEX_UNIT_SP, view.getResources().getDisplayMetrics());
    }

}
