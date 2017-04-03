package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Objects.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class UploadUrlTask extends AsyncTask<String, Void, Void> {

    private String url;

    public UploadUrlTask(String url) {
        this.url = url;
    }

    @Override
    protected Void doInBackground(String... params) {
        byte[] bytes;
        Log.d("Dev", "connecting to " + params[0]);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_url", url)
                .build();

        Request request = new Request.Builder()
                .url(params[0])
                .post(body)
                .build();


        Log.d("Dev", "Built request");
        try {
            Response response = client.newCall(request).execute();
            Log.d("Dev", "Successfully sent request");
            bytes =  response.body().bytes();
            response.close();
            if (bytes != null && bytes.length > 0) {
                Log.d("Dev", new String(bytes));
                JSONObject json = new JSONObject(new String(bytes));
                Log.e("Dev", "Successfully uploaded pic");
                Log.e("Dev", json.toString());
                if (!json.getBoolean("success")) {
                    throw new Exception("Failure during upload");
                }
            }
        } catch (Exception e) {
            Log.e("Dev", "Pic Upload failed");
            //Log.e("Dev", e.getMessage());
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

}
