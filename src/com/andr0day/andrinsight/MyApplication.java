package com.andr0day.andrinsight;

import android.app.Application;
import android.content.Intent;
import com.andr0day.andrinsight.common.FileUtils;
import com.andr0day.andrinsight.common.RootUtil;
import com.andr0day.andrinsight.common.XposedConstant;

public class MyApplication extends Application {


    public void onCreate() {
        super.onCreate();
        FileUtils.copyAssetsToFiles(this, XposedConstant.SO_NAME);
        RootUtil.safeExecStr("chmod 777 " + XposedConstant.SO_FULL_PATH);
        startService(new Intent(this, FileModifyService.class));
    }


}
