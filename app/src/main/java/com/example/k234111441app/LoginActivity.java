package com.example.k234111441app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    /*
    declare all view as variables
     **/
    EditText edtUserName;
    EditText edtPassword;
    TextView txtMessage;
    CheckBox chkSaveLogin;
    String name_share_pref = "LoginInfo";

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
        boolean isSaved = preferences.getBoolean("saved", false);

        // Cập nhật trạng thái CheckBox
        chkSaveLogin.setChecked(isSaved);

        if (isSaved) {
            // Điền thông tin đã lưu
            edtUserName.setText(preferences.getString("username", ""));
            edtPassword.setText(preferences.getString("password", ""));
        } else {
            // Nếu không lưu, đảm bảo xóa trắng hoặc để mặc định
            edtPassword.setText("");
        }
    }

    public void loginSystem(View view) {
        String username = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();
        if (username.equalsIgnoreCase("admin") &&
                password.equals("123")) {
            SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (chkSaveLogin.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("saved", true);
            } else {
                // Nếu không tích chọn, xóa thông tin cũ đã lưu
                editor.clear();
            }
            editor.apply();

            txtMessage.setText(getString(R.string.str_login_success));
            android.content.Intent intent = new android.content.Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Thêm dòng này để đóng hẳn màn hình Login
        } else {
            txtMessage.setText(getString(R.string.str_login_fail));
        }
    }

    public void exitSystem(View view) {
        finish();
    }
}
    