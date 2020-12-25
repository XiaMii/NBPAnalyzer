package com.example.nbpanalyzer.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nbpanalyzer.utils.GenKeyFromString;
import com.example.nbpanalyzer.Constant;
import com.example.nbpanalyzer.R;
import com.example.nbpanalyzer.utils.RSAUtil;
import com.example.nbpanalyzer.communication.VolleyCallback;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

//import com.example.administrator.loginclient.HttpsUtils.HTTPSTrustManager;

public class loginActivity extends AppCompatActivity {

    public static RequestQueue queue;
    private static Context mContext;
    private static String afterencrypt;
    private static String TAG = "MainActivity";
    public static String USER_NAME = "username";
    public static VolleyCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(getApplicationContext());
        mContext = this;
        final EditText AccountNumber = (EditText) findViewById(R.id.login_edit_account);//输入用户名
        final EditText Password = (EditText) findViewById(R.id.login_edit_pwd);//输入密码

        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());//密码不可见
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        AccountNumber.setText(username);
        Password.setText(password);

        Button login = (Button) findViewById(R.id.login_btn_login);
        Button register = (Button) findViewById(R.id.register);
        Button forget_password = (Button) findViewById(R.id.forgetpassword);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = AccountNumber.getText().toString().trim();
                final String psw = Password.getText().toString().trim();
                //接下来是传输加密
                // 获取Rsa 工具类对象
                RSAUtil rsa = new RSAUtil();
                // 获取公钥
                RSAPublicKey pubKey = (RSAPublicKey) GenKeyFromString
                        .getPubKey(Constant.pubKey1);
                // 使用公钥加密 数据
                byte[] enRsaByte_psw = new byte[0];
                byte[] enRsaBytes_user = new byte[0];
                try {
                    enRsaByte_psw = rsa.encrypt(pubKey, psw.getBytes());//密码加密
                    enRsaBytes_user = rsa.encrypt(pubKey, name.getBytes());//用户名加密
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /**
                 * base64对byte数组进行编码，进过编码后得到String传输到对服务端解码得出byte数组。
                 */
                String enRsaStr_psw = new String(Base64.encode(enRsaByte_psw));//密码byte数组转成字符串
                String enRsaStr_user = new String(Base64.encode(enRsaBytes_user));//用户名byte数组转成字符串
                LoginRequest(enRsaStr_user,enRsaStr_psw);//提交登录表单
                Toast.makeText(mContext, "请稍等...", Toast.LENGTH_SHORT).show();
            }
        });
        //注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, RegisterActivity.class);
                startActivity(intent);
                Log.e(TAG, "onClick: 注册" );
            }
        });
        //忘记密码
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ForgetPswActivity.class);
//                startActivity(intent);
                Log.e(TAG, "onClick: 忘记密码" );
            }
        });
        callback = new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                Intent intent = new Intent();
                intent.putExtra(USER_NAME, result);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };
    }
    public static void LoginRequest(final String accountNumber, final String password) {
        //请求地址

        String url = "http://127.0.0.1:8080/WebAPPTestwar/LoginServlet";
        String tag = "Login";
        //取得请求队列l
        RequestQueue requestQueue = queue;
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //   HTTPSTrustManager.allowAllSSL();//允许所有https请求
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)

        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");
                            String result = jsonObject.getString("Result");
                            if (result.equals("TheUserDoesNotExist")) {
                                Toast.makeText(mContext, "账户不存在", Toast.LENGTH_LONG).show();
                            } else if (result.equals("PasswordError")) {
                                //做自己的登录失败操作，如Toast提示
                                Toast.makeText(mContext, "密码错误", Toast.LENGTH_LONG).show();
                            } else if (result.equals("CorrectPassword")) {
                                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(mContext, LoginSuccessActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//关掉所要到的界面中间的activity
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mContext.startActivity(intent);
                                String name = jsonObject.getString("Name");
                                callback.onSuccess(name);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                            Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("AccountNumber", accountNumber);
                params.put("Password", password);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

}

