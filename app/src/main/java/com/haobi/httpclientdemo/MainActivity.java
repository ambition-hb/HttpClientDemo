package com.haobi.httpclientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用HttpClient访问聚合API，查询手机归属地
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_get;
    private Button btn_post;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_get = (Button)findViewById(R.id.btn_get);
        btn_post = (Button)findViewById(R.id.btn_post);
        tv = (TextView)findViewById(R.id.tv);
        btn_get.setOnClickListener(this);
        btn_post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_get:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        useHttpClientGet("http://apis.juhe.cn/mobile/get?phone=13429667914&key=fef8795fcfa0a1977582d8c31b529112");
                    }
                }).start();
                break;
            case R.id.btn_post:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        useHttpClientPost("http://apis.juhe.cn/mobile/get?");
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private HttpClient createHttpClient(){
        HttpParams mDefaultHttpParams = new BasicHttpParams();
        //设置连接超时
        HttpConnectionParams.setConnectionTimeout(mDefaultHttpParams,15000);
        //设置请求超时
        HttpConnectionParams.setSoTimeout(mDefaultHttpParams, 15000);
        HttpConnectionParams.setTcpNoDelay(mDefaultHttpParams, true);
        HttpProtocolParams.setVersion(mDefaultHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(mDefaultHttpParams, HTTP.UTF_8);
        //持续握手
        HttpProtocolParams.setUseExpectContinue(mDefaultHttpParams, true);
        HttpClient mHttpClient = new DefaultHttpClient(mDefaultHttpParams);
        return mHttpClient;
    }

    //HttpClient的GET方法
    private void useHttpClientGet(String url){
        HttpGet mHttpGet = new HttpGet(url);
        mHttpGet.addHeader("Connection", "Keep-Alive");
        try{
            HttpClient mHttpClient = createHttpClient();
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            int code = mHttpResponse.getStatusLine().getStatusCode();
            if(null != mHttpEntity){
                InputStream mInputStream = mHttpEntity.getContent();
                String response = converStreamToString(mInputStream);
                showResponse(response);
                Log.d("useHttpClientGet: ", "请求状态吗：" + code + "\n请求结果：\n" + response);
                mInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //HttpClient的POST方法
    private void useHttpClientPost(String url){
        HttpPost mHttpPost = new HttpPost(url);
        mHttpPost.addHeader("Connection", "Keep-Alive");
        try{
            HttpClient mHttpClient = createHttpClient();
            List<NameValuePair> postParams = new ArrayList<>();
            //要传递的参数
            //phone=13429667914&key=fef8795fcfa0a1977582d8c31b529112
            postParams.add(new BasicNameValuePair("phone", "18856842651"));
            postParams.add(new BasicNameValuePair("key", "fef8795fcfa0a1977582d8c31b529112"));
            mHttpPost.setEntity(new UrlEncodedFormEntity(postParams));
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            int code = mHttpResponse.getStatusLine().getStatusCode();
            if (null != mHttpEntity){
                InputStream mInputStream = mHttpEntity.getContent();
                String respose = converStreamToString(mInputStream);
                showResponse(respose);
                Log.d("useHttpClientPost: ", "请求状态吗：" + code + "\n请求结果：\n" + respose);
                mInputStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //将字节流转换为字符流
    private String converStreamToString(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = reader.readLine()) != null){
            sb.append(line + "\n");
        }
        String response = sb.toString();
        return response;
    }

    //将response结果展示展示出来
    private void showResponse(final String response){
        //Activity不允许在子线程中进行UI操作
        //通过该方法可以将线程切换到主线程，然后再更新UI元素
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(response);
                    }
                });
            }
        }).start();
    }
}
