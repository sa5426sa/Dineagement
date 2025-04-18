package com.project.dineagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.project.dineagement.R;

public class CreateTaskActivity extends AppCompatActivity {

    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        initViews();
    }

    public void initViews() {
        create = findViewById(R.id.create);
    }

    public void create(View view) {
    }

    public void returnToMain() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }
}