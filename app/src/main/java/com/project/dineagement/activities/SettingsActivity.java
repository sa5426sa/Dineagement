package com.project.dineagement.activities;

import static com.project.dineagement.FBRef.refAuth;
import static com.project.dineagement.FBRef.refGallery;
import static com.project.dineagement.FBRef.refImages;
import static com.project.dineagement.FBRef.refUsers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.dineagement.FBRef;
import com.project.dineagement.R;
import com.project.dineagement.objects.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    private TextView name, mail;

    private final String TAG = "SettingsActivity";

    private ImageView imageView;
    private String lastImage, lastGallery;
    Bitmap imageBitmap;
    private String currentPath;
    private File localFile;
    DocumentReference imageReference;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102;
    private static final int REQUEST_FULL_IMAGE_CAPTURE = 202;
    private static final int REQUEST_PICK_IMAGE = 301;

    private static int option;

    private Button changeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    public void initViews() {
        name = findViewById(R.id.settingsName);
        mail = findViewById(R.id.settingsMail);
        imageView = findViewById(R.id.settingsImage);

        changeImage = findViewById(R.id.changeImage);
        registerForContextMenu(changeImage);

        refUsers.child(FBRef.uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.getValue(User.class).getUsername());
                mail.setText(refAuth.getCurrentUser().getEmail());
                if (!dataSnapshot.getValue(User.class).getImage().isEmpty()) {
                    byte[] bytes = Base64.decode(dataSnapshot.getValue(User.class).getImage(), Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION)
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION)
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Take image");
        menu.add("Pick image from gallery");

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String option = item.getTitle().toString();
        if (option.equals("Take image")) {
            takeImage();
            SettingsActivity.option = 0;
        } else if (option.equals("Pick image from gallery")) {
            openGallery();
            SettingsActivity.option = 1;
        }
        return super.onContextItemSelected(item);
    }

    public void takeImage() {
        String filename = "temp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(filename, ".jpg", storageDir);
            currentPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(SettingsActivity.this, "com.project.dineagement.fileProvider", imageFile);
            Intent takePicture = new Intent();
            takePicture.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (takePicture.resolveActivity(getPackageManager()) != null)
                startActivityForResult(takePicture, REQUEST_FULL_IMAGE_CAPTURE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary file: ", e);
        }
    }

    public void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            switch (requestCode) {
                case REQUEST_FULL_IMAGE_CAPTURE:
                    lastImage = dateFormat.format(date);
                    imageBitmap = BitmapFactory.decodeFile(currentPath);
                    addImage(imageBitmap, refImages, refUsers, lastImage);
                    break;
                case REQUEST_PICK_IMAGE:
                    Uri file = data.getData();
                    if (file != null) {
                        lastGallery = dateFormat.format(date);
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                            addImage(imageBitmap, refGallery, refUsers, lastGallery);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else Toast.makeText(this, "No image selected...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    public void readImage(int option) {
        switch (option) {
            case 0:
                imageReference = refImages.document(lastImage);
                try {
                    localFile = File.createTempFile(lastImage, ".jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 1:
                imageReference = refGallery.document(lastGallery);
                try {
                    localFile = File.createTempFile(lastGallery, ".jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }

        final ProgressDialog pd = ProgressDialog.show(this, "Image Download", "Downloading image...", true);
        imageReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Blob blob = (Blob) documentSnapshot.get("imageData");
                    byte[] bytes = blob.toBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                } else Log.e(TAG, "No such document exists");
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SettingsActivity.this, "Image download failed...", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Download failed: ", e);
            }
        });
    }

    public void addImage(Bitmap image, CollectionReference collectionReference, DatabaseReference databaseReference, String name) {
        ProgressDialog pd;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        int quality = 100;
        String imageString;
        if (imageBytes.length > 1040000) {
            pd = ProgressDialog.show(this, "Compress Image", "Image size is too large!\nCompressing...", true);
            while (imageBytes.length > 1040000) {
                quality -=5;
                byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                imageBytes = byteArrayOutputStream.toByteArray();
            }
            pd.dismiss();
        }

        image = Bitmap.createScaledBitmap(image, imageView.getWidth(), imageView.getHeight(), true);
        byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageBytes = byteArrayOutputStream.toByteArray();

        Blob blob = Blob.fromBytes(imageBytes);
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("imageName", name);
        imageMap.put("imageData", blob);

        imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        pd = ProgressDialog.show(this, "Upload Image", "Uploading...", true);
        collectionReference.document(name).set(imageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "DocumentSnapshot written successfully");
                readImage(option);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error writing document: ", e);
            }
        });
        pd.dismiss();
        databaseReference.child(FBRef.uid).child("image").setValue(imageString).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "image value updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error updating value: ", e);
            }
        });
    }

    public void onChangeImage(View view) {
        openContextMenu(view);
    }

    public void onChangeName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Name");
        builder.setMessage("Enter your new name:");
        builder.setIcon(R.drawable.baseline_edit_24);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = input.getText().toString();
                refUsers.child(FBRef.uid).child("username").setValue(value);
                name.setText(value);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onChangeMail(View view) {
        FirebaseUser user = refAuth.getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification");
        builder.setMessage("Please re-enter your password to continue.");
        builder.setIcon(R.drawable.baseline_verify_24);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Password");
        builder.setView(input);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), input.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
                        builder1.setTitle("Change Email");
                        builder1.setMessage("Enter your new email:");
                        builder1.setIcon(R.drawable.baseline_edit_24);

                        builder1.setView(R.layout.change_mail);

                        builder1.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText input = findViewById(R.id.inputMail),
                                        input1 = findViewById(R.id.inputMailConfirm);
                                String value = input.getText().toString(),
                                        value1 = input1.getText().toString();
                                if (value1.equals(value)) {
                                    user.updateEmail(value);
                                    Toast.makeText(SettingsActivity.this, "Email changed successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Emails do not match.", Toast.LENGTH_SHORT).show();
                                    builder1.show();
                                }
                            }
                        });
                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog dialog = builder1.create();
                        dialog.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    public void onChangePassword(View view) {
        FirebaseUser user = refAuth.getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Verification")
                .setMessage("Please enter your CURRENT password to continue.")
                .setIcon(R.drawable.baseline_verify_24);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Password");
        builder.setView(input);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), input.getText().toString());
                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Change Password")
                                .setMessage("Enter your new password:")
                                .setIcon(R.drawable.baseline_edit_24)
                                .setView(R.layout.change_password);

                        builder1.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final EditText input = findViewById(R.id.inputPassword),
                                                input1 = findViewById(R.id.inputPasswordConfirm);
                                        String value = input.getText().toString(),
                                                value1 = input1.getText().toString();
                                        if (value1.equals(value)) {
                                            user.updatePassword(value);
                                            Toast.makeText(SettingsActivity.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                                            builder1.show();
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        builder1.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public void onDeleteAccount(View view) {
        FirebaseUser user = refAuth.getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Account Deletion")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setIcon(R.drawable.baseline_delete_forever_24)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Warning")
                                .setMessage("You are about to delete your account forever." +
                                        "\nYour user will be PERMANENTLY DELETED from the server databases and will NOT be eligible for recovery." +
                                        "\nAll tasks associated with you will also be deleted." +
                                        "\nAre you CERTAIN you want to delete your account?")
                                .setIcon(R.drawable.baseline_warning_24)
                                .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(SettingsActivity.this)
                                                .setTitle("Verification")
                                                .setMessage("Please enter your password to continue.")
                                                .setIcon(R.drawable.baseline_verify_24);

                                        final EditText input = new EditText(SettingsActivity.this);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        input.setHint("Password");
                                        builder2.setView(input);

                                        builder2.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), input.getText().toString());
                                                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        ProgressDialog.show(SettingsActivity.this, "Deleting Account...", "Goodbye.", true);
                                                        refUsers.child(FBRef.uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                                                        startActivity(i);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e(TAG, "Error deleting user from auth database: ", e);
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG, "Error deleting user from database: ", e);
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SettingsActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                        builder2.show();
                                    }
                                }).setNegativeButton("No, Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        builder1.show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.show();
    }
}