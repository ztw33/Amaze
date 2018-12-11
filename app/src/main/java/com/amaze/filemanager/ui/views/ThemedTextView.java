package com.amaze.filemanager.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.utils.Utils;
import com.amaze.filemanager.utils.theme.AppTheme;

/**
 * Created by vishal on 18/1/17.
 *
 * Class sets text color based on current theme, without explicit method call in app lifecycle
 */

public class ThemedTextView extends android.support.v7.widget.AppCompatTextView {

    public ThemedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (((MainActivity) context).getAppTheme().equals(AppTheme.LIGHT)) {
            setTextColor(Utils.getColor(getContext(), android.R.color.black));
        } else if (((MainActivity) context).getAppTheme().equals(AppTheme.DARK) || ((MainActivity) context).getAppTheme().equals(AppTheme.BLACK)) {
            setTextColor(Utils.getColor(getContext(), android.R.color.white));
        }
    }
}