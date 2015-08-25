package com.andr0day.andrinsight.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.andr0day.andrinsight.R;
import com.andr0day.andrinsight.common.AppUtil;
import com.andr0day.andrinsight.common.DbHelper;
import com.andr0day.andrinsight.common.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by andr0day on 2015/4/9.
 */
public class AppDetailActivity extends Activity {
    private static final String APP_PROCESS = "/system/bin/app_process";
    private static final String MAIN_CLASS = "com.qihoo360.androidtool.ManagerUtil";

    private Button openIt;

    private Button exposedComp;
    private String pkgName;
    private PackageManager packageManager;
    private static final String JAR_FILE = "iso.jar";
    private File jarFile;

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

        jarFile = new File(getFilesDir(), JAR_FILE);
        if (!jarFile.exists()) {
            FileUtils.copyAssetsToFiles(this, JAR_FILE);
        }
        myHandler.sendEmptyMessage(INIT_VIEW);

    }

    public void onResume() {
        super.onResume();

    }

    private void initView() {
        openIt = (Button) findViewById(R.id.open_it);
        exposedComp = (Button) findViewById(R.id.exposed_comp);

        final Intent launcherIntent = AppUtil.getAppLauncherIntent(pkgName, packageManager);
        String launcherCls = "null";
        if (launcherIntent != null) {
            launcherCls = launcherIntent.getComponent().getClassName();
        }
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

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
