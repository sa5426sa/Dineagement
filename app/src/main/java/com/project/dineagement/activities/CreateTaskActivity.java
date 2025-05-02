package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.refAllTasks;
import static com.project.dineagement.FBRef.refTasks;
import static com.project.dineagement.FBRef.refUsers;
import static com.project.dineagement.Utilities.format2Display;
import static com.project.dineagement.adapters.TaskAdapter.isUrgent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.dineagement.FBRef;
import com.project.dineagement.R;
import com.project.dineagement.objects.User;
import com.project.dineagement.objects.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editTaskName, editTaskDesc;
    private CheckBox checkImportant;
    private TextView textDueDate;
    private Button btnCreate;

    private User user;

    ArrayList<User> users = new ArrayList<>();

    ArrayList<String> usernames = new ArrayList<>();

    ArrayAdapter<String> adp;

    private Spinner spinner;

    private ValueEventListener vel, vel1;

    private String forUser, forUid;

    private int prevSerial;

    private ArrayList<Task> tasks;

    private boolean isFirstTask = false, updater = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        initViews();
    }

    private void initViews() {
        editTaskName = findViewById(R.id.editTaskName);
        editTaskDesc = findViewById(R.id.editTaskDesc);
        checkImportant = findViewById(R.id.checkImportant);
        spinner = findViewById(R.id.spinner);
        textDueDate = findViewById(R.id.textDueDate);
        btnCreate = findViewById(R.id.btnCreate);

        tasks = new ArrayList<>();

        adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, usernames);
        spinner.setAdapter(adp);
        spinner.setOnItemSelectedListener(this);

        refUsers.child(FBRef.uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> task) {
                if (task.isSuccessful()) user = task.getResult().getValue(User.class);
            }
        });
        vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                usernames.clear();
                usernames.add("Choose...");
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getValue((User.class)) != user && !data.getValue(User.class).isManager()) {
                        User temp = data.getValue(User.class);
                        users.add(temp);
                        String tempName = temp.getUsername();
                        usernames.add(tempName);
                    }
                }
                adp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        vel1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks.clear();
                for (DataSnapshot data : snapshot.getChildren())
                    if (!data.getKey().equals("updater"))
                        for (DataSnapshot data1 : data.getChildren())
                            if (data.getKey().equals(forUid)) tasks.add(data1.getValue(Task.class));
                if (tasks.isEmpty()) isFirstTask = true;
                else {
                    isFirstTask = false;
                    prevSerial = tasks.get(tasks.size() - 1).getSerialNum();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addValueEventListener(vel);
        refAllTasks.child("updater").setValue(updater);
        refAllTasks.addValueEventListener(vel1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refUsers.addValueEventListener(vel);
        refAllTasks.addValueEventListener(vel1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refUsers.removeEventListener(vel);
        refAllTasks.removeEventListener(vel1);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(calendar.getTime());
        return format2Display(date);
    }

    public void openDatePickerDialog(View view) {
        Calendar calNow = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                calNow.get(Calendar.YEAR),
                calNow.get(Calendar.MONTH),
                calNow.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("Choose Date");
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.YEAR, year);
            calSet.set(Calendar.MONTH, month);
            calSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdfSave = new SimpleDateFormat("yyyyMMdd");
            String dateSave = sdfSave.format(calSet.getTime());
            textDueDate.setText(format2Display(dateSave));
        }
    };

    public void createTask(View view) {
        String taskName = editTaskName.getText().toString();
        String taskDesc = editTaskDesc.getText().toString();
        String dateDue = textDueDate.getText().toString();
        int priority;
        if (checkImportant.isChecked() && isUrgent(dateDue, Calendar.getInstance())) priority = 11;
        else if (checkImportant.isChecked()) priority = 10;
        else if (isUrgent(dateDue, Calendar.getInstance())) priority = 1;
        else priority = 0;
        Task newTask;
        if (isFirstTask)
            newTask = new Task(0, taskName, taskDesc, getCurrentDate(), dateDue, forUser, priority, user.getUsername());
        else
            newTask = new Task(prevSerial + 1, taskName, taskDesc, getCurrentDate(), dateDue, forUser, priority, user.getUsername());
        String serialNum = String.valueOf(newTask.getSerialNum());
        refAllTasks.child(forUid).child(serialNum).setValue(newTask);

        Log.i("CreateTaskActivity", "Task created successfully.");
        Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos != 0) {
            forUser = usernames.get(pos);
            forUid = users.get(pos - 1).getUid();
            updater = !updater;
            refAllTasks.child("updater").setValue(updater);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}