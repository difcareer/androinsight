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

public class ClassLoaderActivity extends Activity {

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        TextView textView = new TextView(this);
        textView.setText("信息采集中，请稍后......");
        setContentView(textView);
        final String pkgName = getIntent().getStringExtra(Act.pkg.name());
        CommunicationUtil.sendClassloaderBroadcast(this, pkgName);
        IntentFilter intentFilter = new IntentFilter(CommunicationUtil.getHostAction(pkgName));
        registerReceiver(new HostReceiver(new ReceivedCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void onReceived(Intent intent) {
                String act = intent.getStringExtra(CommunicationUtil.ACT);
                if (Act.classloader.name().equals(act)) {
                    ListView listView = new ListView(activity);
                    String json = intent.getStringExtra(CommunicationUtil.EXT);
                    List data = JSON.parseObject(json, List.class);
                    listView.setAdapter(new CommonAdaptor(activity, data, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String classloader = (String) view.getTag();
                            Intent clazzIntent = new Intent(activity, ClassActivity.class);
                            clazzIntent.putExtra(Act.pkg.name(), pkgName);
                            clazzIntent.putExtra(Act.classloader.name(), classloader);
                            startActivity(clazzIntent);
                        }
                    }));
                    activity.setContentView(listView);
                }
            }
        }), intentFilter);
    }

}