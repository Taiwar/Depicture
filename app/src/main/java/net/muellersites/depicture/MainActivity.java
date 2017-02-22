package net.muellersites.depicture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.muellersites.depicture.Objects.User;
import net.muellersites.depicture.Utils.DBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static DBHelper dbHelper;
    private NavigationView navigationView;
    private User user;

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

        if (android.os.Build.VERSION.SDK_INT >= 23){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateMainSync();

        Button startButton = (Button) findViewById(R.id.new_game_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
            }
        });
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
        user = dbHelper.getUser();

        if(!user.getName().equals("FAILURE")){
            handleLogin(navigationView, user);
        }else{
            handleLogout(navigationView, getApplicationContext());
        }
    }

}
