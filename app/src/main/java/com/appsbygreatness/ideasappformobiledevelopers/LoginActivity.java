package com.appsbygreatness.ideasappformobiledevelopers;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 99;
    EditText loginPassword;
    Button loginButton;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView helloText = findViewById(R.id.helloText);
        loginPassword = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);


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

                    Toast.makeText(getApplicationContext(), "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                }


            }
        });


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



        String num =  view.getTag().toString();

        password += num;

        loginPassword.setText(password);
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
