package com.realtime_draw.realtimedraw.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GalleryLinearLayout extends LinearLayout {
    GalleryListView galleryListView;

    public GalleryLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        galleryListView = new GalleryListView(context, attrs);
        addView(galleryListView);
        TextView textView = new TextView(context, attrs);
        textView.setText("Your collection is empty");
        textView.setVisibility(GONE);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(16);
        addView(textView);
        galleryListView.setEmptyView(textView);
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
