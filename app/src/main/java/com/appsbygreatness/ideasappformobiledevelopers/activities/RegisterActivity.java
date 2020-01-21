package com.appsbygreatness.ideasappformobiledevelopers.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.R;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, confirmPassword;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.regUsernameET);
        password = findViewById(R.id.regPasswordET);
        confirmPassword = findViewById(R.id.regConfirmPasswordET);
        Button registerButton = findViewById(R.id.regButton);
        TextView cancelRegister = findViewById(R.id.cancelRegister);

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null){

            SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            String name = preferences.getString("Username", "");

            registerButton.setText("Save");
            username.setText(name);
        }




        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (username.getText().toString().equals("") || password.getText().toString().equals("")
                        || confirmPassword.getText().toString().equals("")){

                    Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();

                }else if (!password.getText().toString().equals(confirmPassword.getText().toString())){

                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();

                }else{

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Username", username.getText().toString()).apply();
                    editor.putString("Password", password.getText().toString()).apply();

                    Intent intent = new Intent(getApplicationContext(), ViewIdeas.class);
                    startActivity(intent);
                }
            }
        });




        cancelRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
