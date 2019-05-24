package com.example.oaxacaos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oaxacaos.Models.UXMethods;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regBtn = findViewById(R.id.reg_btn);
        EditText passwordEt = findViewById(R.id.reg_password_et);

        // click listeners para los botones
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // registro al hacer click en enter
        UXMethods.performClickOnEnterPressed(passwordEt, regBtn);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ev = UXMethods.dispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }
}
