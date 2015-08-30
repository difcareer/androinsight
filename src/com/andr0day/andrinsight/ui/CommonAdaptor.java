package com.andr0day.andrinsight.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.andr0day.andrinsight.R;

import java.util.ArrayList;
import java.util.List;

public class CommonAdaptor extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    List<String> data = new ArrayList<String>();
    View convertView;
    View.OnClickListener clickListener;

    CommonAdaptor(Context context, List<String> data,View.OnClickListener clickListener) {
        if (data != null) {
            this.data = data;
        }
        this.clickListener = clickListener;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.commonitem, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(data.get(i));
        viewHolder.textView.setTag(data.get(i));
        viewHolder.textView.setOnClickListener(clickListener);
        return convertView;
    }


    public final class ViewHolder {
        public TextView textView;
    }
}
