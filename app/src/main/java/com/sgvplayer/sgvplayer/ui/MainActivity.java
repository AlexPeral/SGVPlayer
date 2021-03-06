package com.sgvplayer.sgvplayer.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//Added by Alvaro:
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.sgvplayer.sgvplayer.Mp3ServiceSingleton;

import com.sgvplayer.sgvplayer.R;
import com.sgvplayer.sgvplayer.model.fileNavigator.Mp3File;

import com.sgvplayer.sgvplayer.ui.fragmentSelector.FragmentSelector;
import com.sgvplayer.sgvplayer.model.mp3Service.Mp3ServiceImp;
import com.sgvplayer.sgvplayer.ui.uiClassifier.ClassifierListFragment;
import com.sgvplayer.sgvplayer.ui.uiClassifier.SongInfo;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.AlbumSongsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.AlbumsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.AllSongsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.ArtistSongsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.ArtistsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.GenreSongsFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.GenresFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.MusicMenuFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.MusicTabHostFragment;
import com.sgvplayer.sgvplayer.ui.navigationListener.MainNavigationListener;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.MusicUIRootFragment;
import com.sgvplayer.sgvplayer.ui.uiMusicTabs.PlayerDisplayFragment;

import java.util.List;

public class MainActivity extends MainActivityMp3Service
        implements
        MusicUIRootFragment.OnFragmentInteractionListener,
        MusicMenuFragment.OnFragmentInteractionListener,
        PlayerFragment.OnFragmentInteractionListener,
        PlayerDisplayFragment.OnFragmentInteractionListener,
        ClassifierListFragment.OnClassifierFragmentInteractionListener,
        AllSongsFragment.OnListFragmentInteractionListener,
        ArtistsFragment.OnListFragmentInteractionListener,
        ArtistSongsFragment.OnListFragmentInteractionListener,
        AlbumsFragment.OnListFragmentInteractionListener,
        AlbumSongsFragment.OnListFragmentInteractionListener,
        GenresFragment.OnListFragmentInteractionListener,
        GenreSongsFragment.OnListFragmentInteractionListener,
        FragmentSelector, ServiceConnection {


    private static final int WRITE_EXTERNAL_STORAGE = 1;
    private MusicUIRootFragment musicUIRootFragment;

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getBaseContext(), Mp3ServiceImp.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new MainNavigationListener(drawer, this));

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            //MusicTabHostFragment firstFragment = new MusicTabHostFragment();
            MusicUIRootFragment firstFragment = new MusicUIRootFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
        checkPermissions();


    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, R.string.storage_permissions, Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //TODO cambiar esta basura
                Toast.makeText(this, "IDIOTA TENIAS QUE ACEPTARLO MANCO", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //FRAGMENT LISTENERS:

    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }

    @Override
    public void onAllSongsListFragmentInteraction(List<Mp3File> mp3Files, int index) {
        startPlayerFragment(mp3Files, index);
    }

    @Override
    public void onArtistsListFragmentInteraction(String artistName) {
        startArtistSongsFragment(artistName);
    }

    @Override
    public void onArtistSongsListFragmentInteraction(List<Mp3File> mp3Files, int index) {
        startPlayerFragment(mp3Files, index);
    }

    @Override
    public void onAlbumsListFragmentInteraction(String albumName) {
        startAlbumSongsFragment(albumName);
    }

    @Override
    public void onAlbumSongsListFragmentInteraction(List<Mp3File> mp3Files, int index) {
        startPlayerFragment(mp3Files, index);
    }

    @Override
    public void onGenresListFragmentInteraction(String genreName) {
        startGenreSongsFragment(genreName);
    }

    @Override
    public void onGenreSongsListFragmentInteraction(List<Mp3File> mp3Files, int index) {
        startPlayerFragment(mp3Files, index);
    }

    //Player Fragment:
    @Override public void onMusicChanged(){
        PlayerDisplayFragment secondNewFragment = new PlayerDisplayFragment();
        MusicUIRootFragment rootFragment = (MusicUIRootFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = rootFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, secondNewFragment).commit();
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

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goBackV1(int id){
        // Retrieve this Activiy's parent Up intent
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        // Add the required Intent extras as appropriate
        upIntent.putExtra("KEY", id);
        NavUtils.navigateUpTo(this, upIntent);
    }

    @Override
    public void select(int id) {
        switch (id) {
            case R.id.nav_music:
                moveToMusicUIRootFragment();
                break;
            case R.id.nav_classifier:
                moveToClassifierFragment();
                break;
            case R.id.nav_settings:
                break;
            default:
        }
    }

    private void moveToMusicUIRootFragment() {
        musicUIRootFragment = new MusicUIRootFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, musicUIRootFragment);
        transaction.commit();

    }

    private void moveToClassifierFragment() {
        ClassifierListFragment newFragment = new ClassifierListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
        updateNavBar("Music classifier", false);
    }


    private void startArtistSongsFragment(String artist){
        ArtistSongsFragment newFragment = ArtistSongsFragment.newInstance(1, artist);
        MusicUIRootFragment rootFragment = (MusicUIRootFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = rootFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, newFragment).commit();
        updateNavBar(artist, true);
    }

    private void startAlbumSongsFragment(String album) {
        AlbumSongsFragment newFragment = AlbumSongsFragment.newInstance(1, album);
        MusicUIRootFragment rootFragment = (MusicUIRootFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = rootFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, newFragment).commit();
        updateNavBar(album, true);
    }

    private void startGenreSongsFragment(String genre) {
        GenreSongsFragment newFragment = GenreSongsFragment.newInstance(1, genre);
        MusicUIRootFragment rootFragment = (MusicUIRootFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = rootFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, newFragment).commit();
        updateNavBar(genre, true);
    }

    private void startPlayerFragment(List<Mp3File> mp3FileList, int index) {
        this.playSong(mp3FileList, index);

        PlayerFragment newFragment = new PlayerFragment();
        MusicUIRootFragment rootFragment = (MusicUIRootFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction transaction = rootFragment.getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_player, newFragment);

        PlayerDisplayFragment secondNewFragment = new PlayerDisplayFragment();
        transaction.replace(R.id.fragment_main, secondNewFragment).commit();

        updateNavBar("Now playing", true);
    }

    private void updateNavBar(String newTitle, boolean enableBackButton){
        if (enableBackButton){
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(newTitle);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            getSupportActionBar().setTitle(newTitle);
        }
    }

    @Override
    public void onFragmentInteraction(int id) {
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Mp3ServiceImp.LocalService localService = (Mp3ServiceImp.LocalService) iBinder;
        mp3Service = localService.getService();
        Mp3ServiceSingleton.init(mp3Service);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    @Override
    public boolean isReady() {
        return (mp3Service != null) && mp3Service.isReady();
    }

    @Override
    public void onClassifierFragmentInteraction(int index) {
        startSongInfo(index);
    }

    private void startSongInfo(int index) {
        SongInfo newFragment = SongInfo.newInstance(index);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, newFragment).commit();

    }
}