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


import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Tasks.StartLobbyTask;


public class LobbyActivity extends AppCompatActivity {


    private Lobby lobby = new Lobby();
    private Button startButton;
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        registerReceiver(broadcastReceiver, new IntentFilter(DepictureFirebaseMessagingService.INTENT_FILTER));

        messageView = (TextView) findViewById(R.id.message);
        TextView idView = (TextView) findViewById(R.id.ID);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        startButton = (Button) findViewById(R.id.start_button);

        if (lobby.getIsOwner()) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new StartLobbyTask("https://muellersites.net/api/start_lobby/").execute((User) lobby.getTempUser());
                }
            });
            startButton.setVisibility(View.VISIBLE);
        }

        messageView.setText(lobby.getMessage());
        idView.setText(String.valueOf(lobby.getId()));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void updateLobby(String status, String msg) {
        switch (status) {
            case "start": {
                Intent intent = new Intent(this, DrawActivity.class);
                intent.putExtra("lobby", lobby);
                intent.putExtra("word", msg);
                startButton.setVisibility(View.GONE);
                messageView.setText(R.string.wait_message);
                startActivity(intent);
                break;
            }
            case "stage 1": {
                Intent intent = new Intent(this, SubmitDescriptionActivity.class);
                intent.putExtra("lobby", lobby);
                startActivity(intent);
                break;
            }
            case "stage 2": {
                Intent intent = new Intent(this, SelectDescriptionActivity.class);
                intent.putExtra("lobby", lobby);
                intent.putExtra("colors", msg);
                startActivity(intent);
                break;
            }
            case "stage 3": {
                startButton.setVisibility(View.VISIBLE);
                startButton.setText(R.string.next_round);
                messageView.setText(R.string.wait_message);
                break;
            }
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
