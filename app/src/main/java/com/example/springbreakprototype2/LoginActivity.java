package com.example.springbreakprototype2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText emailTextField, passwordTextField, passwordVerificationTextField;
    TextView verifyPassword;
    Button signIn, createAccount, createNewAccount, back;
    Intent intent;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailTextField = findViewById(R.id.loginUsernameEdit);
        passwordTextField = findViewById(R.id.loginPasswordEdit);
        passwordVerificationTextField = findViewById(R.id.loginPasswordVerificationEdit);

        verifyPassword = findViewById(R.id.loginPasswordVerification);

        signIn = findViewById(R.id.button6);
        createAccount = findViewById(R.id.loginCreateAccountSubmit);
        createNewAccount = findViewById(R.id.button7);
        back = findViewById(R.id.button9);

        intent = new Intent(this, HomeActivity.class);
        extras = new Bundle();
        extras.putString("PREVIOUS", "MAIN");
        extras.putDouble("LOWER_PRICE", -1);
        extras.putDouble("UPPER_PRICE", -1);
        extras.putString("CATEGORIES", "All items");
        extras.putString("SORT_BY", "Recent First");
        extras.putString("GOOD_SERVICE", "good");
    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void createAccount (View view) {
        signIn.setVisibility(View.INVISIBLE);
        createNewAccount.setVisibility(View.INVISIBLE);
        verifyPassword.setVisibility(View.VISIBLE);
        passwordVerificationTextField.setVisibility(View.VISIBLE);
        createAccount.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
    }

    public void submitNewAccount (View view) {
        String email = emailTextField.getText().toString();
        String password = passwordVerificationTextField.getText().toString();
        String verificationPassword = passwordTextField.getText().toString();
        if (email.equals("") || password.equals("") || verificationPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Make sure all fields are filled out", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(verificationPassword)) {
            Toast.makeText(getApplicationContext(), "Make sure passwords are the same", Toast.LENGTH_SHORT).show();
        } else if (!(email.substring(email.length() - 8).equals("@umd.edu")) && !(email.substring(email.length() - 17).equals("@terpmail.umd.edu"))) {
            Toast.makeText(getApplicationContext(), "Enter your @umd.edu email or @terpmail.umd.edu address: ", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Email verification sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                //Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                                //Can tell them why (like email already exists, malformed email, etc)

                                try
                                {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword)
                                {
                                    Toast.makeText(getApplicationContext(), "Weak password",
                                            Toast.LENGTH_SHORT).show();

                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Toast.makeText(getApplicationContext(), "Malformed email",
                                            Toast.LENGTH_SHORT).show();

                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Toast.makeText(getApplicationContext(), "Email already exists",
                                            Toast.LENGTH_SHORT).show();

                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            // ...
                        }
                    });
        }
    }

    public void backToSignIn (View view) {
        signIn.setVisibility(View.VISIBLE);
        createNewAccount.setVisibility(View.VISIBLE);
        verifyPassword.setVisibility(View.INVISIBLE);
        passwordVerificationTextField.setVisibility(View.INVISIBLE);
        createAccount.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
    }

    private void successfulSignIn(FirebaseUser user) {
        //Need to pass in user as part of intent?
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void signIn (View view) {
        String email = emailTextField.getText().toString();
        String password = passwordTextField.getText().toString();
        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Make sure to fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    successfulSignIn(user);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Verify account in email", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                                //Can display email doesnt exist or wrong password, or not, idk which is better
                            }
                        }
                    });
        }

    }





}
