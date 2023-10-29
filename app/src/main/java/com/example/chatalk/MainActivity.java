package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    CircleImageView image_profile,profileImageHeader;

    TextView usernameHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Chatalk");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.menu_header);

        navigationView.setNavigationItemSelectedListener(this);
//        profileImageHeader = findViewById(R.id.profile_image_header);
//        usernameHeader = findViewById(R.id.usernameHeader);
        image_profile = findViewById(R.id.profile_image);
        profileImageHeader = view.findViewById(R.id.profile_image_header);
        usernameHeader = view.findViewById(R.id.usernameHeader);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home){

        }else if (item.getItemId() == R.id.profile) {

        }else if (item.getItemId() == R.id.friendlist) {

        }else if (item.getItemId() == R.id.findfriend) {

        }else if (item.getItemId() == R.id.chat) {

        }else if (item.getItemId() == R.id.logout) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.chat){
            Toast.makeText(MainActivity.this,"chat",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem searchIcon = menu.findItem(R.id.search_bar);

        SearchView searchBar = (SearchView)searchIcon.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(searchManager != null){
            searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //
                return false;
            }
        });
        return true;
    }
}