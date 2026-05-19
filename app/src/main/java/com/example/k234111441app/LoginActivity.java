package com.example.k234111441app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    TextView txtMessage;
    CheckBox chkSaveLogin;
    String name_share_pref = "LoginInfo";
    RadioButton radAdmin, radUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        txtMessage = findViewById(R.id.txtMessage);
        chkSaveLogin = findViewById(R.id.chkSaveLogin);
        radAdmin = findViewById(R.id.radAdmin);
        radUser = findViewById(R.id.radUser);
    }

    public void loginSystem(View view) {
        String username = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();
        
        if (username.equalsIgnoreCase("admin") && password.equals("123")) {
            boolean saved = chkSaveLogin.isChecked();
            SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("UserName", username);
            editor.putString("Password", password);
            editor.putBoolean("Saved", saved);
            editor.apply();

            txtMessage.setText("Chào mừng bạn quay trở lại!");
            Intent intent;
            if (radAdmin.isChecked()) {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, EmployeeAdvancedManagementActivity.class);
            }
            startActivity(intent);
        } else {
            txtMessage.setText(getString(R.string.str_login_fail));
        }
    }

    public void exitSystem(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
        String username = preferences.getString("UserName", "");
        String password = preferences.getString("Password", "");
        boolean saved = preferences.getBoolean("Saved", false);
        if (saved) {
            edtUserName.setText(username);
            edtPassword.setText(password);
            chkSaveLogin.setChecked(saved);
        }
    }
}
