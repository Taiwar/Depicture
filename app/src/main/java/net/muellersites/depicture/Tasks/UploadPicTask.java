package net.muellersites.depicture.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class UploadPicTask extends AsyncTask<String, Void, String> {

    private File file;
    private String IMGUR_API_KEY = "1234";

    public UploadPicTask(File file) {
        this.file = file;
    }

    @Override
    protected String doInBackground(String... params) {
        byte[] bytes;
        Log.d("Dev", "connecting to Imgur...");

        final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/jpg");

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        /*RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Scribble")
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_IMAGE,  file))
                .build();*/

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Depicture_Scribble")
                .addFormDataPart("image", "depicture_scribble.jpg",
                        RequestBody.create(MEDIA_TYPE_IMAGE, file))
                .build();


        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_API_KEY)
                .url("https://api.imgur.com/3/image")
                .post(body)
                .build();

        /*Request request = new Request.Builder()
                .url(params[0])
                .post(body)
                .build();*/

        /*Request request = new Request.Builder()
                .url(params[0])
                .post(body)
                .build();*/


        Log.d("Dev", "Built request");
        //Log.d("Dev", bodyToString(request));
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
                return "https://i.imgur.com/" + json.getJSONObject("data").getString("id");
            }
        } catch (Exception e) {
            Log.e("Dev", "Pic Upload failed");
            //Log.e("Dev", e.getMessage());
            Log.e("Dev", e.toString(), e);
        }
        return null;
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
