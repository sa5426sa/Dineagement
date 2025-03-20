package com.project.dineagement.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.dineagement.R;

import static com.project.dineagement.FBRef.*;

public class MainActivity extends AppCompatActivity {

    Button addTask;
    ListView taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    public void initViews() {
        addTask = findViewById(R.id.addTask);
        taskList = findViewById(R.id.taskList);
    }

    public void addTask(View view) {
    }
}