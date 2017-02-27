package net.muellersites.depicture.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.muellersites.depicture.Objects.User;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class UploadPicTask extends AsyncTask<User, Void, Void> {
    private Exception exception;

    private Context context;
    private String upload_url;

    UploadPicTask(Context context, String upload_url){
        this.context = context;
        this.upload_url = upload_url;
    }

    protected Void doInBackground(User... users) {
        User user = users[0];
        System.out.println("Main, upload token: " + user.getToken());
        URL url;
        String result = "";
        HttpURLConnection connection = null;

        try{
            url = new URL(upload_url);
            String data = "";
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "image/png");

            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            BufferedReader in = null;
            try {
                String line;
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) result += line;
            }finally {
                if(in != null){
                    try{
                        in.close();
                    }catch (IOException e){
                        exception = e;
                    }
                }
            }

            System.out.println(result);

        }catch (IOException e){
            this.exception = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    protected void onPostExecute() {
        if(this.exception == null){
            Log.d("Dev", "Upload finished without any errors");
        }else {
            Log.d("Dev", "Upload failed with following exception:");
            exception.printStackTrace();
        }
    }
}
