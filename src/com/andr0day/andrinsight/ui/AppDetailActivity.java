package com.andr0day.andrinsight.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import com.andr0day.andrinsight.R;
import com.andr0day.andrinsight.common.AppUtil;
import com.andr0day.andrinsight.common.ConfigUtil;

/**
 * Created by andr0day on 2015/4/9.
 */
public class AppDetailActivity extends Activity {

    private Button openIt;
    private Button exposedComp;
    private Button enableXposed;
    private Button disableXposed;
    private Button classloaderWatcher;


    private String pkgName;
    private PackageManager packageManager;

    private static final int INIT_VIEW = 1;

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_VIEW:
                    initView();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdetail);
        pkgName = getIntent().getStringExtra("pkgName");
        packageManager = getPackageManager();

        myHandler.sendEmptyMessage(INIT_VIEW);

    }

    public void onResume() {
        super.onResume();

    }

    private void initView() {
        openIt = (Button) findViewById(R.id.open_it);
        exposedComp = (Button) findViewById(R.id.exposed_comp);
        enableXposed = (Button) findViewById(R.id.enable_xposed);
        disableXposed = (Button) findViewById(R.id.disable_xposed);
        classloaderWatcher = (Button) findViewById(R.id.classloader);


        final Intent launcherIntent = AppUtil.getAppLauncherIntent(pkgName, packageManager);
        if (launcherIntent == null) {
            openIt.setEnabled(false);
        } else {
            openIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(launcherIntent);
                }
            });
        }

        exposedComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppDetailActivity.this, ExportedActivity.class));
            }
        });

        enableXposed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigUtil.setConfig(pkgName, "enable", "true");
                setXposedButtonStatus();
            }
        });

        disableXposed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigUtil.setConfig(pkgName, "enable", "false");
                setXposedButtonStatus();
            }
        });


    }


    private void setXposedButtonStatus() {
        boolean xposedEnabled = "true".equals(ConfigUtil.getConfig(pkgName, "enable", "false"));
        if (xposedEnabled) {
            enableXposed.setEnabled(false);
            disableXposed.setEnabled(true);
        } else {
            enableXposed.setEnabled(true);
            disableXposed.setEnabled(false);
        }
    }
}
