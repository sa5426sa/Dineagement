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
import androidx.annotation.StringRes;
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

    private EditText eTName, eTEmail, eTPass;
    private CheckBox rememberUser;
    private Button btn;
    private TextView tVLogReg, loginTitle;

    private boolean stayConnected, isRegistered;
    private SharedPreferences settings;
    private String name, uid;
    private User userDB;

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

    private void initViews() {
        loginTitle = findViewById(R.id.loginTitle);
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
            FBRef.getUser(refAuth.getCurrentUser());
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

    private void register() {
        SpannableString spannableString = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                loginTitle.setText(R.string.app_register);
                eTName.setVisibility(View.VISIBLE);
                btn.setText(R.string.app_register);
                isRegistered = false;
                login();
            }
        };
        spannableString.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVLogReg.setText(spannableString);
        tVLogReg.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void login() {
        SpannableString spannableString = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                loginTitle.setText(R.string.app_login);
                eTName.setVisibility(View.INVISIBLE);
                btn.setText(R.string.app_login);
                isRegistered = true;
                register();
            }
        };
        spannableString.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVLogReg.setText(spannableString);
        tVLogReg.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void logOrReg(View view) {
        String email, password;
        if (isRegistered) {
            email = eTEmail.getText().toString();
            password = eTPass.getText().toString();

            final ProgressDialog dialog = ProgressDialog.show(this, "Login", "Please wait...", true);
            refAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @SuppressLint("ApplySharedPref")
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
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnected", rememberUser.isChecked());
                        editor.commit();
                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(main);
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
}