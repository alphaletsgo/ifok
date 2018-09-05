package cn.isif;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import cn.isif.alibs.utils.ALibs;
import cn.isif.alibs.utils.ALog;
import cn.isif.ifok.CallBack;
import cn.isif.ifok.IfOk;
import cn.isif.ifok.OkConfig;
import cn.isif.ifok.Params;
import okhttp3.Call;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    public String TAG = "MainActivity";
    public String url = "http://www.baidu.com";
    public Call call;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ALibs.init(this.getApplicationContext());
        textView = (TextView) findViewById(R.id.progress);
        checkDepPermission();

        OkConfig.Builder builder = new OkConfig.Builder()
                .setSSLSocketFactory(createSSLSocketFactory())
                .setHostnameVerifier(new TrustAllHostnameVerifier())
                .set
                .setTimeout(1000 * 60);

        IfOk.getInstance().init(builder.build());

    }


    private void checkDepPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    public void uploadFile(View view) {
        String currentApkPath = getApplicationContext().getPackageResourcePath();
        File apkFile = new File(currentApkPath);
        Params params = new Params.Builder().build();
        params.attach("test", apkFile);
        params.attach("test1", apkFile);
        params.attach("test2", apkFile);
        call = IfOk.getInstance().post(url, params, new CallBack() {
            @Override
            public void onStart(Request request) {

            }

            @Override
            public void onFail(Exception e) {
                Log.d(TAG,e.getMessage());
            }

            @Override
            public void onSuccess(Object object) {
                Toast.makeText(MainActivity.this, object.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, long networkSpeed, boolean done) {
                textView.setText("" + progress+"---"+networkSpeed);
            }
        });
    }

    public void downloadFile(View view) {
        final String path = getAppRootDirectory() + "weiboyi.apk";
        ALog.d(path);
        String file = "https://192.168.100.151/fred/bin/george/default_channel/weiboyi.apk";
        final String fileDir = new File(path).getParentFile().getAbsolutePath();
        ALog.d(fileDir);
        IfOk.getInstance().download(file, fileDir, new CallBack() {
            @Override
            public void onStart(Request request) {

            }

            @Override
            public void onFail(Exception e) {
                ALog.d(e.getMessage());
            }

            @Override
            public void onSuccess(Object object) {
                try {
                    OpenFileUtil.openFile(MainActivity.this, new File(path));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void updateProgress(int progress, long networkSpeed, boolean done) {
                textView.setText("" + progress+"---"+networkSpeed);
            }
        });

    }
    private String getAppRootDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp" + File.separator;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return path;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

}
