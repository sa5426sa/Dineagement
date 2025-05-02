package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.refAuth;
import static com.project.dineagement.FBRef.refTasks;
import static com.project.dineagement.FBRef.refUsers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout main;
    private NavigationView mainView;
    private Toolbar toolbar;

    private ListView tasksList;

    private ProgressDialog pd;

    private AlertDialog.Builder builder;

    private User user;
    public static ArrayList<Task> tasks;
    private TaskAdapter taskAdapter;
    private ValueEventListener vel;

    private TextView noTasks;

    private LinearLayout taskHeader;

    private Button addTask;

    private final int show = View.VISIBLE, hide = View.INVISIBLE;

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
                } else if (item.getItemId() == R.id.nav_credits) {
                    intent = new Intent(MainActivity.this, CreditsActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Logout");
                    builder.setMessage("Are you sure you want to logout?");
                    builder.setIcon(R.drawable.baseline_logout_24);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            refAuth.signOut();
                            Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
    }

    private void initViews() {
        main = findViewById(R.id.main);
        mainView = findViewById(R.id.main_view);
        toolbar = findViewById(R.id.toolbar);

        tasksList = findViewById(R.id.tasksList);

        noTasks = findViewById(R.id.noTasks);

        taskHeader = findViewById(R.id.taskHeader);

        addTask = findViewById(R.id.addTask);

        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(MainActivity.this, tasks);
        tasksList.setAdapter(taskAdapter);
        tasksList.setOnItemClickListener(MainActivity.this);

        pd = ProgressDialog.show(this, "Connecting to Database", "Gathering data...", true);

        refUsers.child(FBRef.uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().getValue(User.class);
                    if (user.isManager()) addTask.setVisibility(show);
                    else addTask.setVisibility(hide);

                    if(pd != null) pd.dismiss();
                } else Log.e("firebase", "Error getting data: ", task.getException());
            }
        });
        vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks.clear();
                for (DataSnapshot data : snapshot.getChildren())
                    tasks.add(data.getValue(Task.class));
                if (tasks.isEmpty()) {
                    noTasks.setVisibility(show);
                    taskHeader.setVisibility(hide);
                    return;
                } else {
                    noTasks.setVisibility(hide);
                    taskHeader.setVisibility(show);
                    tasks.sort(new Comparator<Task>() {
                        @Override
                        public int compare(Task task1, Task task2) {
                            return Math.min(task1.getSerialNum(), task2.getSerialNum());
                        }
                    });
                }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this task? This cannot be undone.");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refTasks.child(String.valueOf(pos)).removeValue();
                taskAdapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}