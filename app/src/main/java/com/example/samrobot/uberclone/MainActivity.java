package com.example.samrobot.uberclone;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.samrobot.uberclone.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

//    press Crl+O
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // before setContentView
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //init View
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegiterDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });
    }
        private  void showLoginDialog(){
            final AlertDialog.Builder  dialog = new AlertDialog.Builder(this);
            dialog.setTitle("SIGN In");
            dialog.setMessage("Please use Email to sin in");

            LayoutInflater inflater = LayoutInflater.from(this);
            View signin_layout = inflater.inflate(R.layout.layout_login,null);

            final MaterialEditText edtEmail = signin_layout.findViewById(R.id.edtEmail);
            final MaterialEditText edtPassword = signin_layout.findViewById(R.id.edtPassword);


            dialog.setView(signin_layout);

            //Set Button
            dialog.setPositiveButton("SIGNIN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            //cehck validation
                            if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                                Snackbar.make(rootLayout, "Plase enter email address", Snackbar.LENGTH_SHORT)
                                        .show();
                                return;
                            }


                            if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                                Snackbar.make(rootLayout, "Plase enter Password", Snackbar.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            // Login
                            auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            startActivity(new Intent(MainActivity.this, Welcome.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        }
                    });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            dialog.show();
        }


        private void showRegiterDialog() {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("REGISTER");
            dialog.setMessage("Please use Email to register");

            LayoutInflater inflater = LayoutInflater.from(this);
            View register_layout = inflater.inflate(R.layout.layout_register,null);

            final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
            final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
            final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
            final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

            dialog.setView(register_layout);

            //Set Button
            dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    //cehck validation
                    if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                        Snackbar.make(rootLayout, "Plase enter email address", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }


                    if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                        Snackbar.make(rootLayout, "Plase enter Password", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    if (edtPassword.getText().toString().length() < 6) {
                        Snackbar.make(rootLayout, "Password too short!", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    if (TextUtils.isEmpty(edtName.getText().toString())) {
                        Snackbar.make(rootLayout, "Plase enter Full name", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                        Snackbar.make(rootLayout, "Plase enter Phone Number", Snackbar.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    // Register New User
                    auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //Insert User to DB
                                    User user = new User();
                                    user.setEmail(edtEmail.getText().toString());
                                    user.setPassword(edtPassword.getText().toString());
                                    user.setName(edtName.getText().toString());
                                    user.setPhone(edtPhone.getText().toString());

                                    // User email to key
                                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Snackbar.make(rootLayout, "Register Success", Snackbar.LENGTH_SHORT)
                                                            .show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(rootLayout, "Fial Regisetr" + e.getMessage(), Snackbar.LENGTH_SHORT)
                                                            .show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout, "Fial Regisetr" + e.getMessage(), Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }
            });

            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }

            });
            dialog.show();

        }
    }
