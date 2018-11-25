package com.github.duanjiefei.okhttpdemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.duanjiefei.okhttpdemo.Bean.NewsData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
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
                getRequest();
                //doExcute();
                break;
            default:
                break;
        }
    }
}
