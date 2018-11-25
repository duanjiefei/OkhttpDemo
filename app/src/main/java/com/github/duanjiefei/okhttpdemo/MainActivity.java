package com.github.duanjiefei.okhttpdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.duanjiefei.okhttpdemo.API.Constant;
import com.github.duanjiefei.okhttpdemo.Bean.NewsData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button button;
    private ProgressBar progressBar;
    private static final int PROGRESS_MESSAGE = 0;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    private String filename = "big_buck_bunny.mp4";
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS_MESSAGE:
                    int progress = msg.arg1;
                    progressBar.setProgress(progress);
                    break;
                    default:
                        break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.pb);
        requestPermission();
        button.setOnClickListener(this);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this,"need the write permission",Toast.LENGTH_LONG).show();
            }else {
                //开始请求权限
                Log.d(TAG,"requestPermission");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"has the permission",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"request the permission fail",Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void downLoadVideo(){
        //1 创建请求的客户端
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = Constant.Video_URL;
        // 2 创建Request 请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 3 发起请求  1：excute()  同步方法    2 ： enqueue() 异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"异步请求失败"+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG,"异步请求成功");
                handleResponse(response);
            }
        });
    }

    private void handleResponse(Response response) {
        InputStream is = null;
        FileOutputStream fos = null;

        is = response.body().byteStream();
        long totalSize = response.body().contentLength();
        File file = new File(path,filename);

        try {
            fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int length = 0;
            int sum = 0;
            while ((length = is.read(bytes)) != -1){
                sum += length;
                fos.write(bytes);
                int progress = (int) ((sum * 1.0f /totalSize) *100);
                Log.d(TAG,"handleResponse sum"+sum);
                Log.d(TAG,"handleResponse totalSize"+totalSize);
                Log.d(TAG,"handleResponse progress"+progress);
                Message message = handler.obtainMessage(PROGRESS_MESSAGE);
                message.arg1 = progress;
                handler.sendMessage(message);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                try {
                    if (is != null){
                    is.close();
                    }
                    if (fos != null){
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void getRequest(){
        //1 创建请求的客户端
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://v.juhe.cn/toutiao/index?type=top&key=a9872c2ef3107a22853d07fd54527067";
        // 2 创建Request 请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 3 发起请求  1：excute()  同步方法    2 ： enqueue() 异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"异步请求失败"+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d(TAG,"异步请求成功"+response.body().string());
                Log.d(TAG,"异步请求成功"+response.code());
                Log.d(TAG,"异步请求成功"+response.isSuccessful());
                //String result = response.body().string();

                try {
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject data =  object.getJSONObject("result");
                    JSONObject newsData = (JSONObject) data.getJSONArray("data").get(0);
                    String category = newsData.getString("category");
                    String author_name = newsData.getString("author_name");
                    String url = newsData.getString("url");
                    Log.d(TAG,"异步请求成功 category  "+category);
                    Log.d(TAG,"异步请求成功 author_name "+author_name);
                    Log.d(TAG,"异步请求成功 url "+url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



    }

    public void doExcute(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1 创建请求的客户端
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String url = "http://v.juhe.cn/toutiao/index?type=top&key=a9872c2ef3107a22853d07fd54527067";
                    // 2 创建Request 请求对象
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    // 3 发起请求  1：excute()  同步方法 必须在子线程中    2 ： enqueue() 异步请求
                    Response response = okHttpClient.newCall(request).execute();
                    Log.d(TAG,"同步请求"+response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                //getRequest();
                //doExcute();
                downLoadVideo();
                break;
            default:
                break;
        }
    }
}
