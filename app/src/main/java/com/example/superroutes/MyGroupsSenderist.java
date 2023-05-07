package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

public class MyGroupsSenderist extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        searchBar = findViewById(R.id.searchview_my_groups);
        searchBar.setOnQueryTextListener(this);
    }

    public void onClickCreateNewRoute(View view) {
        startActivity(new Intent(this, CreateNewGroup.class));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MyGroupsSenderist.this, MainActivity.class));
    }
}