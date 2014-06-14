package com.realtime_draw.realtimedraw.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrowseLinearLayout extends LinearLayout {
    BrowseListView browseListView;

    public BrowseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        browseListView = new BrowseListView(context, attrs);
        addView(browseListView);
        if (isInEditMode())
            return;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode())
            return;
    }
}
