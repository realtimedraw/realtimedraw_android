package com.realtime_draw.realtimedraw.app;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realtime_draw.realtimedraw.app.util.Desen;
import com.realtime_draw.realtimedraw.app.util.deseneDataSourceClass;

import java.util.List;

public class GalleryListView extends ListView {
    public GalleryListView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        if(isInEditMode())
            return;
        final FullscreenActivity activity = (FullscreenActivity)context;
        deseneDataSourceClass datasource = activity.getDatasource();
        final List<Desen> values = datasource.getAllDesene();
        ArrayAdapter<Desen> adapter = new ArrayAdapter<>(activity, R.layout.gallery_list_item, values);
        setAdapter(adapter);
        if(!attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto", "GalleryLinearLayout_displayOnly", true)){
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    activity.playDesen(values.get(position).getId());
                }
            });
        }
    }
}
