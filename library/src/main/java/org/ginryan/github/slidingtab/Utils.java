package org.ginryan.github.slidingtab;

import android.util.TypedValue;
import android.view.View;

public class Utils {

    public static float dipToPx(View view, int dip) {
        return view.getResources().getDisplayMetrics().density * dip;
    }


}
