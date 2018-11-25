package com.github.duanjiefei.okhttpdemo;

import android.util.Log;

import com.github.duanjiefei.okhttpdemo.API.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static  final String TAG = ExampleUnitTest.class.getSimpleName();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testHello(){
        System.out.println("hello test");
    }
    @Test
    public void testGetRequest(){
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
    @Test
    public void testPostForm(){
        //测试post  表单
       OkHttpClient okHttpClient = new OkHttpClient();
       FormBody body = new FormBody.Builder()
               .add("name","duanjiefei")
               .add("sex","nan").build();
       Request request = new Request.Builder()
               .url(Constant.HTTP_BIN)
               .post(body)
               .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPostJson(){
        //测试post  表单
        OkHttpClient okHttpClient = new OkHttpClient();
 /*       JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name","duanjiefei");
            jsonObject.put("sex","nan");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        String body = "{\"name\":\"duanjiefei\"}";//jsonObject.toString();
        //MediaType.parse("application/json;charset=utf-8")  构建json类型的requestBody
        RequestBody requestBody  = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),body);
        try {
            Request request = new Request.Builder()
                    .url(Constant.HTTP_BIN)
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInterceptor() {
        //创建拦截器
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();//获取Request 对象
                Response response = chain.proceed(request);//将截取的获取Request重新执行
                System.out.println(response.body().string());
                return response;
            }
        };
        //1 创建请求的客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)//添加拦截器
                .build();
        String url = "http://v.juhe.cn/toutiao/index?type=top&key=a9872c2ef3107a22853d07fd54527067";
        // 2 创建Request 请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 3 发起请求  1：excute()  同步方法    2 ： enqueue() 异步请求
        try {
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testCache(){
        //创建Cache 缓存对象
        Cache cache = new Cache(new File("Cache.cache"),10*1024*1024);
        //1 创建请求的客户端
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        String url = "http://v.juhe.cn/toutiao/index?type=top&key=a9872c2ef3107a22853d07fd54527067";
        // 2 创建Request 请求对象
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_CACHE)
                .build();

        // 3 发起请求  1：excute()  同步方法    2 ： enqueue() 异步请求
        try {
            Response response = okHttpClient.newCall(request).execute();
            Response cacheResponse = response.cacheResponse();
            Response netResponse = response.networkResponse();

            if (response == null){
                System.out.println("response == null");
            }else {
                System.out.println("response != null");
            }

            if (cacheResponse == null){
                System.out.println("cacheResponse == null");
            }else {
                System.out.println("cacheResponse != null");
            }

            if (netResponse == null){
                System.out.println("netResponse == null");
            }else {
                System.out.println("netResponse != null");
            }


            if (netResponse != null){
                System.out.println("from net  ");
            }

            if (cacheResponse != null){
                System.out.println("from cache  ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}