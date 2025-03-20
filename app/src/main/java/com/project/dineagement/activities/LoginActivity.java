package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.project.dineagement.FBRef;
import com.project.dineagement.objects.User;
import com.project.dineagement.R;

public class LoginActivity extends AppCompatActivity {

    EditText eTName, eTEmail, eTPass;
    CheckBox rememberUser;
    Button btn;
    TextView tVLogReg;

    boolean stayConnected, isRegistered;
    SharedPreferences settings;
    String name, email, password, uid;
    int REQUEST_CODE = 100;
    User userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        initViews();
        stayConnected = false;
        isRegistered = true;
        register();
    }

    public void initViews() {
        eTName = findViewById(R.id.eTName);
        eTEmail = findViewById(R.id.eTEmail);
        eTPass = findViewById(R.id.eTPass);
        rememberUser = findViewById(R.id.rememberUser);
        btn = findViewById(R.id.btn);
        tVLogReg = findViewById(R.id.tVLogReg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isChecked = settings.getBoolean("stayConnected", false);
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        if (refAuth.getCurrentUser() != null && isChecked) {
            refAuth.getCurrentUser();
            stayConnected = true;
            main.putExtra("isNewUser", false);
            startActivity(main);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnected) finish();
    }

    public void register() {
        SpannableString spannableString = new SpannableString("Don't have an account? Register here!");
        ClickableSpan span = new ClickableSpan() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(@NonNull View view) {
                setTitle("Register");
                eTName.setVisibility(View.VISIBLE);
                btn.setText("Register");
                isRegistered = false;
                login();
            }
        };
        spannableString.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVLogReg.setText(spannableString);
        tVLogReg.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void login() {
        SpannableString spannableString = new SpannableString("Already have an account? Login here!");
        ClickableSpan span = new ClickableSpan() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(@NonNull View view) {
                setTitle("Login");
                eTName.setVisibility(View.INVISIBLE);
                btn.setText("login");
                isRegistered = true;
                register();
            }
        };
        spannableString.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVLogReg.setText(spannableString);
        tVLogReg.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void logOrReg() {
        if (isRegistered) {
            email = eTEmail.getText().toString();
            password = eTPass.getText().toString();

            ProgressDialog dialog = ProgressDialog.show(this, "Login", "Please wait...", true);
            refAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = refAuth.getCurrentUser();
                        assert user != null;
                        FBRef.getUser(user);
                        Log.d("LoginActivity", "loginEmailPass:success");
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
                    } else {
                        Log.d("LoginActivity", "loginEmailPass:fail");
                        Toast.makeText(LoginActivity.this, "Incorrect e-mail and/or password!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            name = eTName.getText().toString();
            email = eTEmail.getText().toString();
            password = eTPass.getText().toString();

            ProgressDialog dialog = ProgressDialog.show(this, "Register", "Please wait...", true);
            refAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = refAuth.getCurrentUser();
                        assert user != null;
                        FBRef.getUser(user);
                        Log.d("LoginActivity", "createUser:success");
                        uid = user.getUid();
                        userDB = new User(uid, name);
                        refUsers.child(uid).setValue(userDB);
                        Toast.makeText(LoginActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(LoginActivity.this, "E-mail is already registered! Try logging in instead.", Toast.LENGTH_SHORT).show();
                        else {
                            Log.w("LoginActivity", "createUser:fail", task.getException());
                            Toast.makeText(LoginActivity.this, "User creation failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("stayConnected", rememberUser.isChecked());
            editor.commit();
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
        }
    }
}