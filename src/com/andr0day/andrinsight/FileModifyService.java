package com.andr0day.andrinsight;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.FileObserver;
import android.os.IBinder;
import com.andr0day.andrinsight.common.NotificationUtil;
import com.andr0day.andrinsight.common.RootUtil;
import com.andr0day.andrinsight.common.XposedConstant;
import com.andr0day.andrinsight.ui.AppInfoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileModifyService extends Service {

    private List<FileObserver> fileObservers = new ArrayList<FileObserver>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        FileObserver hostObserver = new MyFileObserver(XposedConstant.HOST_FULL_PATH);
        hostObserver.startWatching();
        fileObservers.add(hostObserver);
        FileObserver xposedObserver = new MyFileObserver(XposedConstant.XPOSED_FULL_PATH);
        xposedObserver.startWatching();
        fileObservers.add(xposedObserver);
        registerReceiver(new MyBroadCastReceiver(), new IntentFilter(XposedConstant.HOST_PKG + ".FileObserver"));
        NotificationUtil.getInstance(this).sendNotify(this, new Intent(this, AppInfoActivity.class), "文件监控已开启", "");

    }

    @Override
    public void onDestroy() {
        for (FileObserver it : fileObservers) {
            it.stopWatching();
        }
    }


    class MyFileObserver extends FileObserver {
        private String dir;

        public MyFileObserver(String path) {
            super(path);
            this.dir = path;
        }

        @Override
        public void onEvent(int event, String path) {
            if (path == null) {
                return;
            }

            if ((FileObserver.CREATE & event) != 0 ||
                    (FileObserver.MOVED_TO & event) != 0 ||
                    (FileObserver.MOVED_FROM & event) != 0 ||
                    (FileObserver.MOVE_SELF & event) != 0) {
                RootUtil.safeExecStr("chmod 777 " + dir + "/" + path);
                if (new File(dir + "/" + path).isDirectory()) {
                    FileObserver fileObserver = new MyFileObserver(dir + "/" + path);
                    fileObserver.startWatching();
                    fileObservers.add(fileObserver);
                }
            }

            if ((FileObserver.CLOSE_WRITE & event) != 0) {
                if (dir.equals(XposedConstant.HOST_FULL_PATH) && !path.equals("xposed")) {
                    return;
                }
                RootUtil.safeExecStr("chmod 777 " + dir + "/" + path);
            }
        }
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            FileObserver fileObserver = new MyFileObserver(path);
            fileObserver.startWatching();
            fileObservers.add(fileObserver);
        }
    }
}
