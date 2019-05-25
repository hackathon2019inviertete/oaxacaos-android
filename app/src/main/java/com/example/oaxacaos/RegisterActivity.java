package com.example.oaxacaos;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.oaxacaos.Api.PostMethod;
import com.example.oaxacaos.Models.UXMethods;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regBtn = findViewById(R.id.reg_btn);
        final EditText emailEt = findViewById(R.id.reg_email_et);
        final EditText passwordEt = findViewById(R.id.reg_password_et);

        // click listeners para los botones
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if (checkFields(email, password)) {
                    tryRegister(email, password);
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Formato de correo incorrecto y/o contraseña vacia")
                            .setPositiveButton("Revisar", null)
                            .show();
                }
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

    public boolean checkFields(String email, String password){
        return validate(email) && !password.equals("");
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public void tryRegister(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonRegister = new JSONObject();
                    jsonRegister.put("email", email);
                    jsonRegister.put("password", password);

                    PostMethod postMethod = new PostMethod();
                    JSONObject jsonResponse = postMethod.makeRequest(getString(R.string.sign_up_url), jsonRegister, RegisterActivity.this, false);

                    if (jsonResponse != null) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Error")
                                        .setMessage("Ya hay un usuario registrado con ese email.")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
