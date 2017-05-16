package net.muellersites.depicture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Tasks.NextRoundTask;
import net.muellersites.depicture.Tasks.StartLobbyTask;
import net.muellersites.depicture.Tasks.StopLobbyTask;


public class LobbyActivity extends AppCompatActivity {


    private Lobby lobby = new Lobby();
    private Button startButton;
    private Button nextButton;
    private TextView messageView;
    private ConstraintLayout lobby_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        registerReceiver(broadcastReceiver, new IntentFilter(DepictureFirebaseMessagingService.INTENT_FILTER));

        lobby_content = (ConstraintLayout) findViewById(R.id.lobby_content_layout);

        messageView = (TextView) findViewById(R.id.message);
        TextView idView = (TextView) findViewById(R.id.ID);

        lobby = (Lobby) getIntent().getSerializableExtra("lobby");

        startButton = (Button) findViewById(R.id.start_button);
        nextButton = (Button) findViewById(R.id.next_button);

        if (lobby.getIsOwner()) {
            startButton.setVisibility(View.VISIBLE);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress(true);
                    try {
                        Boolean success = new StartLobbyTask("https://muellersites.net/api/start_lobby/").execute((User) lobby.getTempUser()).get();
                        showProgress(false);
                        if (!success) {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.lobby_frame), R.string.start_lobby_error, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        Log.e("Dev", "Exception during startLobby");
                        Log.e("Dev", e.toString(), e);
                    }
                }
            });

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress(true);
                    try {
                        Boolean success = new NextRoundTask("https://muellersites.net/api/next_round/").execute(lobby.getTempUser().getToken()).get();
                        showProgress(false);
                        if (!success) {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.lobby_frame), R.string.next_round_error, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        Log.e("Dev", "Exception during startLobby");
                        Log.e("Dev", e.toString(), e);
                    }
                }
            });
        }

        messageView.setText(lobby.getMessage());
        idView.setText(String.valueOf(lobby.getId()));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (lobby.getIsOwner()) {
            openConfirmDialog();
        }
    }

    private void openConfirmDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_dialog);
        TextView questionView = (TextView) dialog.findViewById(R.id.confirm_question);
        questionView.setText(R.string.confirmation_question);
        Button yes = (Button) dialog.findViewById(R.id.yes_button);
        Button no = (Button) dialog.findViewById(R.id.no_button);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Dev", "End game");
                showProgress(true);
                try {
                    Boolean success = new StopLobbyTask("https://muellersites.net/api/stop_lobby/").execute((User) lobby.getTempUser()).get();
                } catch (Exception e) {
                    Log.e("Dev", "Couldn't stop lobby", e);
                }
                showProgress(false);
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                if (lobby.getIsOwner()) {
                    nextButton.setVisibility(View.VISIBLE);
                }
                messageView.setText(R.string.waiting_for_owner);
                break;
            }
            case "ended": {
                this.finish();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        Log.d("Dev", "showing Progress?: " + show);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        lobby_content.setVisibility(show ? View.GONE : View.VISIBLE);
        lobby_content.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lobby_content.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        final ProgressBar progressView = (ProgressBar) findViewById(R.id.lobby_progress);

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
