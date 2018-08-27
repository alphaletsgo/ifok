package cn.isif;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

import cn.isif.ifok.CallBack;
import cn.isif.ifok.IfOk;
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
        textView = (TextView) findViewById(R.id.progress);
        checkDepPermission();
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

        String file = "https://timesofoman.com/uploads/images/2017/09/13/734320.jpg";
        IfOk.getInstance().download(file, "sdcard/", new CallBack() {
            @Override
            public void onStart(Request request) {

            }

            @Override
            public void onFail(Exception e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void updateProgress(int progress, long networkSpeed, boolean done) {
                textView.setText(progress+"---"+networkSpeed);
            }
        });

    }


}
