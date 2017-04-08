package net.muellersites.depicture.Utils;

import android.util.Log;

import net.muellersites.depicture.Objects.LoginData;
import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticateUser {

    public static User main(LoginData loginData, String server_url) throws IOException, JSONException {
        byte[] bytes;
        User result_user = new User();
        Log.d("Dev", "Auth: connecting to " + server_url);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", loginData.getName())
                .addFormDataPart("password", loginData.getPassword())
                .build();

        Request request = new Request.Builder()
                .url(server_url)
                .post(body)
                .build();


        Log.d("Dev", "Built request");
        try {
            Response response = client.newCall(request).execute();
            bytes =  response.body().bytes();
            response.close();
            if (bytes != null && bytes.length > 0) {
                Log.d("Dev", new String(bytes));
                JSONObject json = new JSONObject(new String(bytes));
                Log.e("Dev", "Success");
                Log.e("Dev", json.toString());
                result_user.setName(loginData.getName());
                result_user.setPassword(loginData.getPassword());
                result_user.setToken(json.getString("token"));
            }
        } catch (Exception e) {
            Log.e("Dev", "User Auth failed");
            Log.e("Dev", e.toString(), e);
            throw e;
        }
        return result_user;
    }
}
