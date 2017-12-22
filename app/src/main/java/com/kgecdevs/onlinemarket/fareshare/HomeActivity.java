package com.kgecdevs.onlinemarket.fareshare;

import android.content.Intent;
import android.drm.DrmStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {
private TextView welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        actionBar.setTitle("Jobless Coders-Amri trip");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String username = "user";
        if(user!=null)
            username = user.getDisplayName();

        welcome = findViewById(R.id.welcomebutton);
        welcome.setText("Welcome, "+username+"!");
        welcome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                refresh();
                return false;
            }
        });
        initRecents();
    }

    private void refresh() {
        Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        //initRecents();
    }

    private void initRecents() {
        ArrayList<String> updates = FirebaseHandler.getRecentUpdates(this);
        //Collections.reverse(updates);

        RecyclerView lin = findViewById(R.id.recenttasklist);
        RecentListAdapter adapter = new RecentListAdapter(this, updates);
        lin.setAdapter(adapter);

        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lin.setLayoutManager(manager);

        lin.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar bar = Snackbar.make(findViewById(R.id.rootofhome),"What a life! Sign out failure", Snackbar.LENGTH_SHORT);
            }
        })
        ;
        return true;
    }

    public void addExpenditure(View view) {
        startActivity(new Intent(this, NewTaskGenerateActivity.class));
        finish();
    }
}
