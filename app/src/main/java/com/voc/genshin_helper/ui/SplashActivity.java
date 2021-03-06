package com.voc.genshin_helper.ui;/*
 * Package com.voc.genshin_helper.ui.SplashActivity was
 * Created by Voc-夜芷冰 , Programmer of Xectorda
 * Copyright © 2021 Xectorda 版權所有
 */

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.voc.genshin_helper.BuildConfig;
import com.voc.genshin_helper.R;
import com.voc.genshin_helper.ui.MMXLVIII.Desk2048;
import com.voc.genshin_helper.ui.SipTik.DeskSipTik;
import com.voc.genshin_helper.util.BackgroundReload;
import com.voc.genshin_helper.util.CustomToast;
import com.voc.genshin_helper.util.DownloadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Project Genshin Spirit (原神小幫手) was
 * Created & Develop by Voc-夜芷冰 , Programmer of Xectorda
 * Copyright © 2022 Xectorda 版權所有
 */

public class SplashActivity extends AppCompatActivity {

    public int REQUEST_CODE = 2296;
    Context context ;
    Activity activity ;
    String choice = "N/A";
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_splash);

        context = this;
        activity = this;
        BackgroundReload.BackgroundReload(context,activity);

        sharedPreferences = getSharedPreferences("user_info", 0);
        if (sharedPreferences.getBoolean("theme_light", true) == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (sharedPreferences.getBoolean("theme_night", false) == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (sharedPreferences.getBoolean("theme_default", false) == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        ((TextView) findViewById(R.id.version_tv)).setText(BuildConfig.VERSION_NAME);

        String[] checkList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.EXPAND_STATUS_BAR};
        List<String> needRequestList = checkPermission(activity, checkList);
        if (!needRequestList.isEmpty()) {
            requestPermission(activity, needRequestList.toArray(new String[needRequestList.size()]));
        }else{
            goMain();
        }

    }
    private void goMain() {
        sharedPreferences = getSharedPreferences("user_info", 0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (sharedPreferences.getBoolean("downloadBase", false) == false) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this,R.style.AlertDialogCustom);
            dialog.setCancelable(false);
            dialog.setTitle(context.getString(R.string.update_download_update_base));
            dialog.setMessage(context.getString(R.string.update_download_advice)+"\n"+context.getString(R.string.update_download_base_file_size)+prettyByteCount(getRemoteFileSize("http://113.254.213.196/genshin_spirit/base.zip")));
            dialog.setNegativeButton(context.getString(R.string.update_download_later),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    finish();
                }

            });
            dialog.setPositiveButton(context.getString(R.string.update_download_now),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.start("http://113.254.213.196/genshin_spirit/base.zip","base.zip","/base.zip",context,activity);

                    sharedPreferences = getSharedPreferences("user_info", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("lastUpdateUnix", System.currentTimeMillis());
                    editor.apply();
                }
            });
            dialog.show();
        }else{
            check_updates();
        }
    }

    //https://blog.csdn.net/fitaotao/article/details/119700579

    private List<String> checkPermission(Context context, String[] checkList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < checkList.length; i++) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, checkList[i])) {
                list.add(checkList[i]);
            }
        }
        return list;
    }

    //申請許可權
    private void requestPermission(Activity activity, String requestPermissionList[]) {
        ActivityCompat.requestPermissions(activity, requestPermissionList, 100);
    }

    //用戶作出選擇後，返回申請的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.w("存儲許可權","申請成功");
                        goMain();
                    } else {
                        Log.w("存儲許可權","申請失敗");
                        finish();
                    }
                }
            }

        }
    }

    public String prettyByteCount(Number number) {
        String[] suffix = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        long numValue = ((long) number.longValue());
        int base = 0 ;
        double tmp_val = numValue;
        while (tmp_val> 1024){
            tmp_val = tmp_val/1024;
            base = base +1;
        }

        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        int decimal_num = 2;//sharedPreferences.getInt("decimal_num", 0);
        boolean decimal  = sharedPreferences.getBoolean("decimal", false);
        if (base < suffix.length) {
            return new DecimalFormat("##.##").format(numValue / Math.pow(1024, base)) + suffix[base];
            // Muility
        } else {
            return new DecimalFormat("#,###").format(numValue);
        }
    }
    public static long getRemoteFileSize(String urlSTR) {
        URL url = null;
        try {
            url = new URL(urlSTR);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            return urlConnection.getContentLength();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public void check_updates(){
            sharedPreferences = context.getSharedPreferences("user_info",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            OkHttpClient client = new OkHttpClient();
            String url = "http://113.254.213.196/genshin_spirit/update.json";
            Request request = new Request.Builder().url(url).build();

            long lastUnix = System.currentTimeMillis();

            try {
                Response sponse = client.newCall(request).execute();
                String str = sponse.body().string();
                JSONArray array = new JSONArray(str);
                ArrayList<String> array_download = new ArrayList<String>();
                ArrayList<String> array_fileName = new ArrayList<String>();
                ArrayList<String> array_SfileName = new ArrayList<String>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    long release_unix = object.getLong("release_unix");
                    String fileName = object.getString("fileName");

                    if (i == 0) {
                        lastUnix = release_unix;
                    }

                    if (release_unix > sharedPreferences.getLong("lastUpdateUnix", 1)) {
                        array_download.add("http://113.254.213.196/genshin_spirit/" + fileName);
                        array_fileName.add(fileName);
                        array_SfileName.add("/" + fileName);
                    }
                }
                if(array_download.size()>0){
                    if (getRemoteFileSize("http://113.254.213.196/genshin_spirit/base.zip") > getRemoteFileSizeA(array_download)){
                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(SplashActivity.this,R.style.AlertDialogCustom);
                        dialog.setCancelable(false);
                        dialog.setTitle(context.getString(R.string.update_download_update_curr));
                        dialog.setMessage(context.getString(R.string.update_download_found_update)+context.getString(R.string.update_download_advice)+"\n"+context.getString(R.string.update_download_base_file_size)+" "+prettyByteCount(getRemoteFileSizeA(array_download)));
                        dialog.setNegativeButton(context.getString(R.string.update_download_later),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                Log.wtf("NOTHING","NOTHING");
                            }

                        });
                        long finalLastUnix = lastUnix;
                        dialog.setPositiveButton(context.getString(R.string.update_download_now),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.startAWithRun(array_download,array_fileName,array_SfileName,context,activity,true);
                                editor.putLong("lastUpdateUnix", finalLastUnix);
                                editor.apply();
                            }

                        });
                        dialog.show();
                    }else{
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this,R.style.AlertDialogCustom);
                        dialog.setCancelable(false);
                        dialog.setTitle(context.getString(R.string.update_download_update_base));
                        dialog.setMessage(context.getString(R.string.update_download_advice)+"\n"+context.getString(R.string.update_download_base_file_size)+prettyByteCount(getRemoteFileSize("http://113.254.213.196/genshin_spirit/base.zip")));
                        dialog.setNegativeButton(context.getString(R.string.update_download_later),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                finish();
                            }

                        });
                        dialog.setPositiveButton(context.getString(R.string.update_download_now),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.start("http://113.254.213.196/genshin_spirit/base.zip","base.zip","/base.zip",context,activity);

                                sharedPreferences = getSharedPreferences("user_info", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putLong("lastUpdateUnix", System.currentTimeMillis());
                                editor.apply();
                            }
                        });
                        dialog.show();
                    }

                }else{
                    CustomToast.toast(context,this,context.getString(R.string.update_download_not_found_update));

                    checkStyleUI();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private void runDesk(SharedPreferences sharedPreferences) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getString("styleUI","N/A").equals("2O48")){
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, Desk2048.class));
                }else if (sharedPreferences.getString("styleUI","N/A").equals("SipTik")){
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, DeskSipTik.class));
                }else if (sharedPreferences.getString("styleUI","N/A").equals("Voc")){
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else{
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                SplashActivity.this.finish();

            }
        }, 2000);
    }


    public static long getRemoteFileSizeA (ArrayList<String> urlSTR) {
        URL url = null;
        long size = 0;
        for (int x = 0 ;x< urlSTR.size() ; x++){
            System.out.println(urlSTR.get(x));
            try {
                url = new URL(urlSTR.get(x));
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                size = size+urlConnection.getContentLength();
                System.out.println("getR : "+size);
                System.out.println("getRX : "+urlConnection.getContentLength());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return size;
    }

    public void checkStyleUI(){
        if (sharedPreferences.getString("styleUI","N/A").equals("N/A")){
            final Dialog dialog = new Dialog(context, R.style.NormalDialogStyle_N);
            View view = View.inflate(context, R.layout.fragment_choose_style_ui, null);

            RadioButton style_Voc_rb = view.findViewById(R.id.ui_Voc_rb);
            RadioButton style_2O48_rb = view.findViewById(R.id.ui_2O48_rb);
            RadioButton style_SipTik_rb = view.findViewById(R.id.ui_SipTik_rb);
            FrameLayout menu_ok = view.findViewById(R.id.menu_ok);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            style_Voc_rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    style_Voc_rb.setChecked(true);
                    style_2O48_rb.setChecked(false);
                    style_SipTik_rb.setChecked(false);
                    choice = "Voc";
                }
            });

            style_2O48_rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    style_Voc_rb.setChecked(false);
                    style_2O48_rb.setChecked(true);
                    style_SipTik_rb.setChecked(false);
                    choice = "2O48";
                }
            });

            style_SipTik_rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putString("styleUI","SipTik");
                    editor.apply();
                    style_Voc_rb.setChecked(false);
                    style_2O48_rb.setChecked(false);
                    style_SipTik_rb.setChecked(true);
                    choice = "SipTik";
                }
            });

            menu_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!choice.equals("N/A")){
                        editor.putString("styleUI",choice);
                        editor.apply();
                        dialog.dismiss();
                        runDesk(sharedPreferences);
                    }
                }
            });

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(true);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            lp.width = (int) (width*0.9);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(lp);
            dialog.show();
        }else{
            runDesk(sharedPreferences);
        }
    }
}

