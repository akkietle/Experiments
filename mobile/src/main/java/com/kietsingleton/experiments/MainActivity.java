package com.kietsingleton.experiments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private FloatingActionButton mFab;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    private static boolean mIsSignedIn = false;
    private SignInButton mGoogleSignInButton;
    private TextView mUserName;
    private TextView mUserEmail;
    private ImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Log in
        LinearLayout header = (LinearLayout) navigationView.getHeaderView(0);
//      Doesn't work on Nougat?
        // RE: http://stackoverflow.com/questions/33351993/navigationview-how-to-click-on-drawer-header-and-make-it-fancy
//        View navHeader = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFERENCES.KEY, Context.MODE_PRIVATE);
        mIsSignedIn = prefs.getBoolean("PREFS_USER_SIGNED_IN", false);


        mUserImage = (ImageView) header.findViewById(R.id.nav_userImageView);
        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
        if (prefs.getString(LoginActivity.PREFERENCES.USER_PHOTO_URL, null) != null)
            new DownloadImageTask(mUserImage).execute(prefs.getString(LoginActivity.PREFERENCES.USER_PHOTO_URL, ""));
        else
            mUserImage.setImageResource(R.mipmap.ic_launcher);

        if (mIsSignedIn) {
            mUserName = (TextView) header.findViewById(R.id.nav_userTextView);
            mUserName.setText(prefs.getString("PREFS_USER_NAME", ""));
            mUserName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Toast.makeText(getApplicationContext(), "Welcome, User!", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            mUserEmail = (TextView) header.findViewById(R.id.nav_emailTextView);
            mUserEmail.setText(prefs.getString("PREFS_USER_EMAIL", ""));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "GSO Failed to connect", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFERENCES.KEY, Context.MODE_PRIVATE);
            mUserEmail.setText(prefs.getString(LoginActivity.PREFERENCES.USER_EMAIL, ""));
            mUserName.setText(prefs.getString(LoginActivity.PREFERENCES.USER_NAME, ""));
            if (prefs.getString(LoginActivity.PREFERENCES.USER_PHOTO_URL, null) != null)
                new DownloadImageTask(mUserImage).execute(prefs.getString(LoginActivity.PREFERENCES.USER_PHOTO_URL, ""));
            else
                mUserImage.setImageResource(R.mipmap.ic_launcher);
        }
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
            return true;
        } else if (id == R.id.action_fullscreen) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // AsyncTask to download image from URL in background
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImage;

        public DownloadImageTask(ImageView imageView) {
            mImage = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            mImage.setImageBitmap(result);
        }
    }
}
