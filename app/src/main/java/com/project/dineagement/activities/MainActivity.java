package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.refTasks;
import static com.project.dineagement.FBRef.refUsers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.dineagement.FBRef;
import com.project.dineagement.R;
import com.project.dineagement.adapters.TaskAdapter;
import com.project.dineagement.objects.Task;
import com.project.dineagement.objects.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout main;
    private NavigationView mainView;
    private Toolbar toolbar;

    private ListView tasksList;

    private ProgressDialog pd;

    private User user;
    public static ArrayList<Task> tasks;
    private TaskAdapter taskAdapter;
    private ValueEventListener vel;

    private TextView noTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, main, toolbar, R.string.nav_open, R.string.nav_close);
        main.addDrawerListener(toggle);
        toggle.syncState();

        mainView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                main.closeDrawers();
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (main.isDrawerOpen(GravityCompat.START)) main.closeDrawer(GravityCompat.START);
                else finish();
            }
        });
        if(tasksList != null) noTasks.setVisibility(View.INVISIBLE);
        else noTasks.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        main = findViewById(R.id.main);
        mainView = findViewById(R.id.main_view);
        toolbar = findViewById(R.id.toolbar);

        tasksList = findViewById(R.id.tasksList);

        noTasks = findViewById(R.id.noTasks);

        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(MainActivity.this, tasks);
        tasksList.setAdapter(taskAdapter);

        pd = ProgressDialog.show(this, "Connecting to Database", "Gathering data...", true);

        refUsers.child(FBRef.uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().getValue(User.class);
                    if (pd != null) pd.dismiss();
                }
                else Log.e("firebase", "Error getting data: ", task.getException());
            }
        });
        vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks.clear();
                for (DataSnapshot data : snapshot.getChildren())
                    tasks.add(data.getValue(Task.class));
                taskAdapter.notifyDataSetChanged();
                if (pd != null) pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        refTasks.addValueEventListener(vel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refTasks.addValueEventListener(vel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refTasks.removeEventListener(vel);
    }

    public void addTask(View view) {
        Intent createTask = new Intent(this, CreateTaskActivity.class);
        startActivity(createTask);
    }
}