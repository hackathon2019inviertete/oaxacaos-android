package com.example.oaxacaos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.oaxacaos.Models.UXMethods;

public class LoginOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        Button loginBtn = findViewById(R.id.login_button);
        EditText passwordEt = findViewById(R.id.login_password_et);
        LinearLayout regLl = findViewById(R.id.register_ll);

        // click listeners para los botones
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOptionsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        regLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOptionsActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // login al hacer click en enter
        UXMethods.performClickOnEnterPressed(passwordEt, loginBtn);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ev = UXMethods.dispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }
}
