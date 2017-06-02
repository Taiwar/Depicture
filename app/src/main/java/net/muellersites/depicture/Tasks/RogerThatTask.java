package net.muellersites.depicture.Tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.muellersites.depicture.Objects.AsyncTaskResult;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RogerThatTask extends AsyncTask<String, Void, AsyncTaskResult<Boolean>> {

    private String url;
    private Context context;

    public RogerThatTask(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    @Override
    protected AsyncTaskResult<Boolean> doInBackground(String... params) {
        byte[] bytes;
        Log.d("Dev", "connecting to " + url);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("hash_id", params[0])
                .build();

        Request request = new Request.Builder()
                .url(url)
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
                Log.e("Dev", "Successfully sent roger that");
                Log.e("Dev", json.toString());
                if (!json.getBoolean("success")) {
                    throw new Exception("Failure during upload");
                }
            }
        } catch (Exception e) {
            Log.e("Dev", "Send roger that failed", e);
            return new AsyncTaskResult<>(e);
        }
        return new AsyncTaskResult<>(Boolean.TRUE);
    }

    @Override
    protected void onPostExecute(final AsyncTaskResult result) {
        Toast toast;
        if (result.getError() != null || isCancelled()) {
            toast = Toast.makeText(context, "Successfully refreshed token", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, "Couldn't refresh token", Toast.LENGTH_LONG);
        }
        toast.show();
    }

}
