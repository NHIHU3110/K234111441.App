package com.example.k234111441app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void say_hello(View view) {
        Toast.makeText(this, "Hellu Nhi nè", Toast.LENGTH_LONG).show();
    }

    public void close_app(View view) {
        finishAffinity();
    }

    public void click_say_hello(View view) {
        String hello = getString(R.string.str_say_hello);
        Toast.makeText(this, hello + " K234112E", Toast.LENGTH_LONG).show();
    }

    public void openCalculatorApp(View view) {
        Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
        startActivity(intent);
    }

    public void openEmployeeAdvanced(View view) {
        Intent intent = new Intent(MainActivity.this, EmployeeAdvancedManagementActivity.class);
        startActivity(intent);
    }
}
