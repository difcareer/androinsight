package com.andr0day.andrinsight.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.andr0day.andrinsight.communication.Act;
import com.andr0day.andrinsight.communication.CommunicationUtil;
import com.andr0day.andrinsight.communication.HostReceiver;
import com.andr0day.andrinsight.communication.ReceivedCallback;

import java.util.List;

public class MethodActivity extends Activity {
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        TextView textView = new TextView(this);
        textView.setText("信息采集中，请稍后......");
        setContentView(textView);
        final String pkgName = getIntent().getStringExtra(Act.pkg.name());
        final String clazz = getIntent().getStringExtra(Act.clazz.name());
        CommunicationUtil.sendMethodBroadcast(this, pkgName, clazz);
        IntentFilter intentFilter = new IntentFilter(CommunicationUtil.getHostAction(pkgName));
        registerReceiver(new HostReceiver(new ReceivedCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void onReceived(Intent intent) {
                String act = intent.getStringExtra(CommunicationUtil.ACT);
                String cz = intent.getStringExtra(CommunicationUtil.EXT);
                if (Act.method.name().equals(act) && clazz.equals(cz)) {
                    ListView listView = new ListView(activity);
                    String json = intent.getStringExtra(CommunicationUtil.EXT2);
                    List data = JSON.parseObject(json, List.class);
                    listView.setAdapter(new CommonAdaptor(activity, data, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String method = (String) view.getTag();
                            Intent detailIntent = new Intent(activity, MethodDetailActivity.class);
                            detailIntent.putExtra(Act.pkg.name(), pkgName);
                            detailIntent.putExtra(Act.clazz.name(), clazz);
                            detailIntent.putExtra(Act.method.name(),method);
                            startActivity(detailIntent);
                        }
                    }));
                    activity.setContentView(listView);
                }
            }
        }), intentFilter);
    }
}