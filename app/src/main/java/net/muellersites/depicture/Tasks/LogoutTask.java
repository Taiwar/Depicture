package net.muellersites.depicture.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.muellersites.depicture.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LogoutTask extends AsyncTask<User, Void, Boolean> {

    private Exception exception;
    private Context context;
    private String server;

    public LogoutTask(Context context, String server){
        this.context = context;
        this.server = server;
    }

    @Override
    protected Boolean doInBackground(User... users) {
        String result = "";
        HttpURLConnection connection = null;
        try{
            URL url = new URL(server);
            String data = "utoken=" + URLEncoder.encode(users[0].getToken(), "UTF-8");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            BufferedReader in = null;
            try {
                String line;
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null){
                    result += line;
                }
            }finally {
                if(in != null){
                    try{
                        in.close();
                    }catch (IOException ex){
                        System.out.println("IOException while closing in: " + ex);
                    }

                }
            }

            try {
                JSONObject json = new JSONObject(result);
                return (Boolean) json.get("success");
            }catch (JSONException ex){
                Log.e("Dev", "Cought JSONException");
                ex.printStackTrace();
            }

            return false;

        }catch (IOException e){
            this.exception = e;
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(this.exception == null){
            Toast toast = Toast.makeText(context, "Successfully logged you out!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
