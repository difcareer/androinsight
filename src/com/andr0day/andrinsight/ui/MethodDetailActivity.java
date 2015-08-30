package com.andr0day.andrinsight.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.andr0day.andrinsight.R;
import com.andr0day.andrinsight.communication.Act;
import com.andr0day.andrinsight.hook.SetHookUtil;

public class MethodDetailActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.methoddetail);

        Button stack = (Button) findViewById(R.id.stack);
        Button beforeWatch = (Button) findViewById(R.id.before_watch);
        Button afterWatch = (Button) findViewById(R.id.after_watch);
        Button beforeSetTemp = (Button) findViewById(R.id.before_set_temp);
        Button beforeSetPerm = (Button) findViewById(R.id.before_set_perm);
        Button beforeSetCond = (Button) findViewById(R.id.before_set_cond);
        Button afterSetTemp = (Button) findViewById(R.id.after_set_temp);
        Button afterSetPerm = (Button) findViewById(R.id.after_set_perm);
        Button afterSetCond = (Button) findViewById(R.id.after_set_cond);

        final String pkgName = getIntent().getStringExtra(Act.pkg.name());
        final String clazz = getIntent().getStringExtra(Act.clazz.name());
        final String method = getIntent().getStringExtra(Act.method.name());

        stack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.stack(pkgName, clazz, method);
            }
        });

        beforeWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.beforeWatch(pkgName, clazz, method);
            }
        });

        afterWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.afterWatch(pkgName, clazz, method);
            }
        });

        beforeSetTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.beforeSetTemp(pkgName, clazz, method);
            }
        });

        beforeSetPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.beforeSetPerm(pkgName, clazz, method);
            }
        });

        beforeSetCond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.beforeSetCond(pkgName, clazz, method);
            }
        });

        afterSetTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.afterSetTemp(pkgName, clazz, method);
            }
        });

        afterSetPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.afterSetPerm(pkgName, clazz, method);
            }
        });

        afterSetCond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetHookUtil.afterSetCond(pkgName, clazz, method);
            }
        });

    }
}