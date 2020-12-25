package com.example.nbpanalyzer.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nbpanalyzer.utils.HTTPSTrustManager;
import com.example.nbpanalyzer.R;
import com.example.nbpanalyzer.utils.GenKeyFromString;
import com.example.nbpanalyzer.Constant;
import com.example.nbpanalyzer.utils.RSAUtil;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    public static RequestQueue queue;
    private static Context mContext;
    private  static int state=0;
    private  static String username;
    private  static String  password;
    //由于Android边编译边生成的原理，将匹配字符串放入全局，作为静态变量可以提高效率
    public static Pattern p =
            Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        queue = Volley.newRequestQueue(getApplicationContext());
        mContext = this;
        final EditText AccountNumber = (EditText) findViewById(R.id.register_edit_account);
        final EditText Password = (EditText) findViewById(R.id.register_edit_pwd);

        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());//密码不可见
        final EditText Email =(EditText) findViewById(R.id.register_edit_email);

        Button register=(Button)findViewById(R.id.register1);
        Button backto_login=(Button)findViewById(R.id.backto_login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = AccountNumber.getText().toString().trim();
                final String psw = Password.getText().toString().trim();

                final String email=Email.getText().toString().trim();
                username=name;
                password=psw;

                if (isEmail(Email.getText().toString().trim()) &&    Email.getText().toString().trim().length()<=31){
                    // Toast.makeText(mContext,"邮箱格式正确",Toast.LENGTH_SHORT).show();

                    // 获取Rsa 工具类对象
                    RSAUtil rsa = new RSAUtil();

                    // 获取公钥
                    RSAPublicKey pubKey = (RSAPublicKey) GenKeyFromString
                            .getPubKey(Constant.pubKey1);

                    // 使用公钥加密 数据
                    byte[] enRsaByte_psw = new byte[0];
                    byte[] enRsaBytes_user = new byte[0];
                    byte[] enRsaBytes_email = new byte[0];
                    try {
                        enRsaByte_psw = rsa.encrypt(pubKey, psw.getBytes());//密码加密
                        enRsaBytes_user = rsa.encrypt(pubKey, name.getBytes());//用户名加密
                        enRsaBytes_email = rsa.encrypt(pubKey, email.getBytes());//邮箱加密
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /**
                     * base64对byte数组进行编码，进过编码后得到String传输到对服务端解码得出byte数组。
                     */
                    String enRsaStr_psw = new String(Base64.encode(enRsaByte_psw));//密码byte数组转成字符串
                    String enRsaStr_user = new String(Base64.encode(enRsaBytes_user));//用户名byte数组转成字符串
                    String enRsaStr_email = new String(Base64.encode(enRsaBytes_email));//用户名byte数组转成字符串

                    RegisterRequest(enRsaStr_user, enRsaStr_psw, enRsaStr_email);
                    Toast.makeText(mContext, "请稍等...", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(mContext,"邮箱格式错误",Toast.LENGTH_SHORT).show();
                }

            }
        });
        backto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
//                if(state==1){
//
//                    intent.putExtra("username", username);
//                    intent.putExtra("password", password);
//                }
//                startActivity(intent);
                finish();
            }
        });
    }
    public static void RegisterRequest(final String accountNumber, final String password,final String email){
        //请求地址
        String url = "http://39.108.101.179:8080/WebAPPTestwar/RegisterServlet";
        String tag = "Register";    //注②

        //取得请求队列
        RequestQueue requestQueue = queue;


        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        HTTPSTrustManager.allowAllSSL();//允许所有https请求

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("Succeed")) {  //注⑤


                                Toast.makeText(mContext, "注册成功", Toast.LENGTH_LONG).show();
                                state=1;

                            }
                            else if (result.equals("TheUsernameAlreadyExists")){
                                //做自己的登录失败操作，如Toast提示
                                state=0;
                                Toast.makeText(mContext, "该用户名已存在", Toast.LENGTH_LONG).show();
                            }
                            else if(result.equals("TheEmailExists")){
                                Toast.makeText(mContext, "该邮箱已被注册", Toast.LENGTH_LONG).show();
                                state=0;
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
                Toast.makeText(mContext, "请稍后重试", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("AccountNumber", accountNumber);  //注⑥
                params.put("Password", password);
                params.put("Email",email);
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);


    }

    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        // Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
