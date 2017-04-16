package net.muellersites.depicture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Tasks.StartLobbyTask;


public class LobbyActivity extends AppCompatActivity {


    private Lobby lobby = new Lobby();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        registerReceiver(broadcastReceiver, new IntentFilter(DepictureFirebaseMessagingService.INTENT_FILTER));

        TextView mContentView = (TextView) findViewById(R.id.message);
        TextView idView = (TextView) findViewById(R.id.ID);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        Button start_button = (Button) findViewById(R.id.start_button);

        if (lobby.getIsOwner()) {
            start_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new StartLobbyTask("https://muellersites.net/api/start_lobby/").execute((User) lobby.getTempUser());
                }
            });
            start_button.setVisibility(View.VISIBLE);
        }

        mContentView.setText(lobby.getMessage());
        idView.setText(String.valueOf(lobby.getId()));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void updateLobby(String status, String msg) {
        if (status.equals("start")) {
            Intent intent = new Intent(this, DrawActivity.class);
            intent.putExtra("lobby", lobby);
            intent.putExtra("word", msg);
            startActivity(intent);
        } else if (status.equals("stage 1")) {
            Intent intent = new Intent(this, SubmitWordActivity.class);
            intent.putExtra("lobby", lobby);
            startActivity(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            String msg = intent.getStringExtra("msg");
            updateLobby(status, msg);
        }
    };

}
