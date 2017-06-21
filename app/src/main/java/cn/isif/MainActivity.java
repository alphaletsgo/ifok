package cn.isif;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import cn.isif.ifok.CallBack;
import cn.isif.ifok.IfOk;
import okhttp3.Call;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    public String TAG = "MainActivity";
    public String url = "http://58.com";
    public Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call = IfOk.getInstance().get(url, new CallBack() {
            @Override
            public void onStart(Request request) {

            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onSuccess(Object object) {
                Log.d(TAG,object.toString());
            }

            @Override
            public void updateProgress(int progress, long networkSpeed, boolean done) {

            }
        });
    }

    public void press(View view){
        call.cancel();
    }
}
