package com.appsbygreatness.ideasappformobiledevelopers;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.database.AppExecutors;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 99;
    EditText loginPassword;
    Button loginButton;
    SharedPreferences preferences;
    BiometricManager biometricManager;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    Vibrator vibrator;

    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView helloText = findViewById(R.id.helloText);
        loginPassword = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (preferences.getString("Username","").equals("")){

            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

            startActivity(intent);

        }else{

            String username = preferences.getString("Username", "");

            helloText.setText("Welcome " + username + ", please provide password below.");

        }

        loginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }

            });

        loginPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = preferences.getString("Password", "");

                if (loginPassword.getText().toString().equals(password)){


                    Intent intent = new Intent(getApplicationContext(), ViewIdeas.class);

                    startActivity(intent);

                }else{

                    if(Build.VERSION.SDK_INT >= 26){

                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else {

                        vibrator.vibrate(200);
                    }
                    Toast.makeText(getApplicationContext(), "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                }


            }
        });

        setUpBiometricsLogin();
    }

    public void setUpBiometricsLogin(){

        biometricManager = BiometricManager.from(this);

        checkBiometricStatus();
        Executor executor = AppExecutors.getInstance().getMainThread();

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication Error :" + errString,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(getApplicationContext(), ViewIdeas.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Ideas app")
                .setSubtitle("Login using your fingerprint")
                .setDescription("Place your finger on fingerprint scanner")
                .setNegativeButtonText("Use account password")
                .build();
    }

    public void onClick(View view){

        String password = loginPassword.getText().toString();

        if (view.getTag().equals("delete")){

            if ((password.length() > 0)) {

                String num = password.substring(0, password.length() - 1);
                loginPassword.setText(num);

            }
            return;
        }

        if(view.getTag().equals("fingerprint")){

            biometricPrompt.authenticate(promptInfo);
            return;
        }


        String num =  view.getTag().toString();

        password += num;

        loginPassword.setText(password);
    }

    public void checkBiometricStatus(){

        switch (biometricManager.canAuthenticate()){

            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.i(TAG, "Biometric success");
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.i(TAG, "Phone does not support Biometrics Auth");
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.i(TAG, "No fingerprint found");

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                break;
        }
    }

    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod{

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {

            return new PasswordCharSequence(source);
        }
    }

    private class PasswordCharSequence implements CharSequence{

        private CharSequence source;

        public PasswordCharSequence(CharSequence source){

            this.source = source;
        }


        @Override
        public int length() {
            return source.length();
        }

        @Override
        public char charAt(int i) {
            return '⬤';
        }

        @Override
        public CharSequence subSequence(int i, int i1) {
            return source.subSequence(i, i1);
        }
    }

}
