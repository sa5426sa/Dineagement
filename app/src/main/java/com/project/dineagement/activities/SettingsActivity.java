package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.refAuth;
import static com.project.dineagement.FBRef.refUsers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.project.dineagement.FBRef;
import com.project.dineagement.R;
import com.project.dineagement.objects.User;

public class SettingsActivity extends AppCompatActivity {

    private TextView name, mail;

    private final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    public void initViews() {
        name = findViewById(R.id.settingsName);
        mail = findViewById(R.id.settingsMail);

        refUsers.child(FBRef.uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.getValue(User.class).getUsername());
                mail.setText(refAuth.getCurrentUser().getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

    public void changeImage(View view) {
    }

    public void changeName(View view) {

    }

    public void changeMail(View view) {

    }

    public void changePassword(View view) {

    }

    public void deleteAccount(View view) {

    }
}