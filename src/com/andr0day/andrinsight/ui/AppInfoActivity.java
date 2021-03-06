package com.andr0day.andrinsight.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.andrinsight.R;
import com.andr0day.andrinsight.clazz.Fetcher;
import com.andr0day.andrinsight.common.AppUtil;
import com.andr0day.andrinsight.common.CertUtils;
import com.andr0day.andrinsight.common.DbHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AppInfoActivity extends Activity {

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("信息采集中，请稍后......");
        setContentView(textView);
        new AppInfoCollector(this).execute();
//        Fetcher.getInstance().loadClasses();   load app test

    }

    public void onResume() {
        super.onResume();
    }

    class AppInfoCollector extends AsyncTask<Object, Integer, List<PackageInfo>> {

        private Context context;

        private PackageManager packageManager;

        List<ResolveInfo> homes;
        List<InputMethodInfo> inputs;

        public AppInfoCollector(Context context) {
            this.context = context;
            this.packageManager = context.getPackageManager();
            homes = AppUtil.getHomes(context);
            inputs = AppUtil.getInputs(context);
        }


        @Override
        protected List<PackageInfo> doInBackground(Object[] params) {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
            Collections.sort(packageInfos, new Comparator<PackageInfo>() {
                @Override
                public int compare(PackageInfo lhs, PackageInfo rhs) {
                    if (!AppUtil.isSystemApp(lhs) && AppUtil.isSystemApp(rhs)) {
                        return -1;
                    } else if (AppUtil.isSystemApp(lhs) && !AppUtil.isSystemApp(rhs)) {
                        return 1;
                    } else if (AppUtil.isSystemUpdateApp(lhs) && !AppUtil.isSystemUpdateApp(rhs)) {
                        return -1;
                    } else if (!AppUtil.isSystemUpdateApp(lhs) && AppUtil.isSystemUpdateApp(rhs)) {
                        return 1;
                    }
                    return 0;
                }
            });
            return packageInfos;
        }

        @Override
        protected void onPostExecute(List<PackageInfo> packageInfos) {
            ListView listView = new ListView(context);
            listView.setAdapter(new MyAdaptor(packageInfos, packageManager, homes, inputs));
            AppInfoActivity.this.setContentView(listView);
        }

    }

    private class MyAdaptor extends BaseAdapter {

        List<PackageInfo> packageInfos;
        PackageManager packageManager;
        List<ResolveInfo> homes;
        List<InputMethodInfo> inputs;


        MyAdaptor(List<PackageInfo> packageInfos, PackageManager packageManager, List<ResolveInfo> homes, List<InputMethodInfo> inputs) {
            this.packageInfos = packageInfos;
            this.packageManager = packageManager;
            this.homes = homes;
            this.inputs = inputs;
            mLayoutInflater = LayoutInflater.from(AppInfoActivity.this);
        }

        @Override
        public int getCount() {
            return packageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return packageInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.appitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
                viewHolder.appName = (TextView) convertView.findViewById(R.id.appName);
                viewHolder.pkgName = (TextView) convertView.findViewById(R.id.pkgName);
                viewHolder.apkPath = (TextView) convertView.findViewById(R.id.apkPath);
                viewHolder.dataDir = (TextView) convertView.findViewById(R.id.dataDir);
                viewHolder.sigs = (TextView) convertView.findViewById(R.id.sigs);
                viewHolder.debugable = (ImageView) convertView.findViewById(R.id.debugable);
                viewHolder.disabled = (ImageView) convertView.findViewById(R.id.disabled);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < packageInfos.size()) {
                PackageInfo packageInfo = packageInfos.get(position);
                viewHolder.appIcon.setImageDrawable(AppUtil.getAppIcon(packageInfo, packageManager));
                String appName = AppUtil.getAppName(packageInfo, packageManager);
                final String pkgName = AppUtil.getPkgName(packageInfo);
                if (AppUtil.getAppLauncherIntent(pkgName, packageManager) != null) {
                    viewHolder.appName.setText(Html.fromHtml("<u>" + appName + "</u>"));
                } else {
                    viewHolder.appName.setText(appName);
                }

                if (AppUtil.isSystemApp(packageInfo)) {
                    if (AppUtil.isSystemUpdateApp(packageInfo)) {
                        viewHolder.appName.setTextColor(getResources().getColor(R.color.lightYellow));
                    } else {
                        viewHolder.appName.setTextColor(getResources().getColor(R.color.yellow));
                    }
                } else {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.green));
                }

                if (AppUtil.isHome(packageInfo, homes) && !AppUtil.isInput(packageInfo, inputs)) {
                    viewHolder.appName.append(" [桌面]");
                } else if (AppUtil.isInput(packageInfo, inputs) && !AppUtil.isHome(packageInfo, homes)) {
                    viewHolder.appName.append(" [输入法]");
                } else if (AppUtil.isHome(packageInfo, homes) && AppUtil.isInput(packageInfo, inputs)) {
                    viewHolder.appName.append(" [桌面、输入法]");
                }

                if (AppUtil.isDebugable(packageInfo)) {
                    viewHolder.debugable.setImageDrawable(getResources().getDrawable(R.drawable.debug));
                    viewHolder.debugable.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.debugable.setVisibility(View.GONE);
                }

                if (DbHelper.getInstance(AppInfoActivity.this).existIsolate(pkgName)) {
                    viewHolder.disabled.setImageDrawable(getResources().getDrawable(R.drawable.disabled));
                    viewHolder.disabled.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.disabled.setVisibility(View.GONE);
                }

                viewHolder.pkgName.setText(pkgName);
                viewHolder.apkPath.setText(AppUtil.getApkPath(packageInfo));
                viewHolder.dataDir.setText(AppUtil.getDataDir(packageInfo));
                viewHolder.sigs.setText(TextUtils.join(",", CertUtils.getSigMd5s(packageInfo)));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent();
                            intent.putExtra("pkgName", pkgName);
                            intent.setClass(AppInfoActivity.this, AppDetailActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                });
            }
            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView pkgName;
        public TextView apkPath;
        public TextView dataDir;
        public TextView sigs;
        public ImageView debugable;
        public ImageView disabled;
    }

}
