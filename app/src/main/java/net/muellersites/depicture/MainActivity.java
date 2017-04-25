package net.muellersites.depicture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import net.muellersites.depicture.Objects.Lobby;
import net.muellersites.depicture.Objects.TempUser;
import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Tasks.CreateLobbyTask;
import net.muellersites.depicture.Tasks.JoinLobbyTask;
import net.muellersites.depicture.Tasks.RefreshTokenTask;
import net.muellersites.depicture.Utils.DBHelper;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static DBHelper dbHelper;
    private NavigationView navigationView;
    private User user;
    private Lobby lobby;
    private Button startButton;
    private ConstraintLayout main_layout;
    private String server = "https://muellersites.net/api/";
    private String registration_token;
    private DrawerLayout drawer;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        /*if (android.os.Build.VERSION.SDK_INT >= 23){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        main_layout = (ConstraintLayout) findViewById(R.id.activity_main);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startButton = (Button) findViewById(R.id.new_game_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateDialog();
            }
        });

        Button joinButton = (Button) findViewById(R.id.join_btn);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openJoinDialog();
            }
        });

        updateMainSync();

        registration_token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Dev", "Instance ID: " + registration_token);
        //new SendFirebaseTokenTask(registration_token, deviceId).execute("https://muellersites.net/api/devices/");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_logout){
            Log.d("Dev", "Dummy logout function");
            handleLogout(navigationView, getApplicationContext());
            /*try{
                Boolean successfulLogout = new LogoutTask(this, server).execute(user).get();
                if(successfulLogout){
                    handleLogout(navigationView, getApplicationContext());
                    GridLayout onlineGrid = (GridLayout) findViewById(R.id.main_online_grid);
                    onlineGrid.setVisibility(View.GONE);
                }
            }catch (Exception e){
                System.out.println("Exception during logout: " + e);
            }*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean storageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

                break;

        }

    }

    @Override
    protected void onResume() {
        Log.d("Dev", "On resume");
        updateMainSync();
        super.onResume();
    }

    private void openCreateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_dialog);
        final Button create = (Button) dialog.findViewById(R.id.form_create_button);
        ImageButton exit = (ImageButton) dialog.findViewById(R.id.closeDialog);

        final Boolean[] use_id = new Boolean[1];

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.wordList_spinner);
        Log.d("Dev", String.valueOf(spinner));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.default_wordLists, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Integer[] listPosition = new Integer[1];

        final ConstraintLayout form_choice_layout = (ConstraintLayout) dialog.findViewById(R.id.form_choice_layout);
        final EditText id_field = (EditText) dialog.findViewById(R.id.form_word_list_id);
        final Button chooseButton = (Button) dialog.findViewById(R.id.choose_default_btn);
        final Button enterButton = (Button) dialog.findViewById(R.id.enter_custom_btn);

        View.OnClickListener choiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == enterButton) {
                    use_id[0] = true;
                    id_field.setVisibility(View.VISIBLE);
                } else {
                    use_id[0] = false;
                    spinner.setVisibility(View.VISIBLE);
                }
                form_choice_layout.setVisibility(View.GONE);
                create.setVisibility(View.VISIBLE);
            }
        };

        chooseButton.setOnClickListener(choiceClickListener);
        enterButton.setOnClickListener(choiceClickListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listPosition[0] = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Integer list_id;
                if (use_id[0]) {
                    list_id = Integer.parseInt(id_field.getText().toString());
                } else {
                    list_id = listPosition[0];
                }
                try {
                    user.setInstanceID(registration_token);
                    lobby = new CreateLobbyTask(server + "create_lobby/", list_id).execute(user).get(5000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    Log.d("Dev", "Error during CreateLobbyTask");
                    Snackbar snackbar = Snackbar
                            .make(drawer, "Couldn't create a new lobby", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                if (lobby != null){
                    Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                    Log.d("Dev", "Passing on Lobby: " + lobby.getId() + "; " + lobby.getMessage());
                    intent.putExtra("lobby", lobby);
                    startActivity(intent);
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void openJoinDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.join_dialog);
        Button join = (Button) dialog.findViewById(R.id.form_join_button);
        ImageButton exit = (ImageButton) dialog.findViewById(R.id.closeDialog);

        final EditText lobby_field = (EditText) dialog.findViewById(R.id.form_lobby_id);
        final EditText username_field = (EditText) dialog.findViewById(R.id.form_word_list_id);
        dialog.show();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String lobby_id = lobby_field.getText().toString();
                TempUser tempUser = new TempUser();
                tempUser.setName(username_field.getText().toString());
                tempUser.setInstanceID(registration_token);
                Lobby lobby = new Lobby();
                lobby.setId(lobby_id);
                lobby.setTempUser(tempUser);
                try {
                    lobby = new JoinLobbyTask(server + "join_lobby/" + lobby_id + "/").execute(lobby).get();
                } catch (Exception e) {
                    Log.d("Dev", "Error during JoinLobbyTask");
                    e.printStackTrace();
                    Snackbar snackbar = Snackbar
                            .make(lobby_field.getRootView(), "Couldn't join the lobby", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                if (lobby != null){
                    Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                    intent.putExtra("lobby", lobby);
                    startActivity(intent);
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    static void handleLogout(NavigationView navigationView, Context context){
        Menu nav_Menu = navigationView.getMenu();
        MenuItem login_item = nav_Menu.findItem(R.id.nav_login);
        MenuItem logout_item = nav_Menu.findItem(R.id.nav_logout);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsernameView = (TextView) headerView.findViewById(R.id.nav_username_view);

        dbHelper.clearUsers();

        navUsernameView.setText(context.getResources().getString(R.string.offline_string));

        login_item.setVisible(true);
        logout_item.setVisible(false);
    }

    static void handleLogin(NavigationView navigationView, User user){
        Menu nav_Menu = navigationView.getMenu();
        MenuItem login_item = nav_Menu.findItem(R.id.nav_login);
        MenuItem logout_item = nav_Menu.findItem(R.id.nav_logout);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsernameView = (TextView) headerView.findViewById(R.id.nav_username_view);
        navUsernameView.setText(user.getName());

        login_item.setVisible(false);
        logout_item.setVisible(true);
    }

    void updateMainSync(){
        showProgress(true);
        user = dbHelper.getUser();

        if(!user.getName().equals("FAILURE") && user.getToken() != null){
            try {
                Log.d("Dev", "executing RTT, token: " + user.getToken());
                user.setToken(new RefreshTokenTask(user.getToken()).execute("https://muellersites.net/api/token-refresh/").get());
                dbHelper.updateUser(user);
            } catch (Exception e) {
                Log.e("Dev", "Error executing RefreshTokenTask");
            }
            handleLogin(navigationView, user);
            startButton.setVisibility(View.VISIBLE);
            showProgress(false);
        }else{
            handleLogout(navigationView, getApplicationContext());
            startButton.setVisibility(View.INVISIBLE);
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        final View mProgressView = findViewById(R.id.main_progress);

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        main_layout.setVisibility(show ? View.GONE : View.VISIBLE);
        main_layout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                main_layout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
