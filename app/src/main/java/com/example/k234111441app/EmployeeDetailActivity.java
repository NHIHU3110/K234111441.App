package com.example.k234111441app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.k234111441app.models.Employee;
import com.google.android.material.button.MaterialButton;

public class EmployeeDetailActivity extends AppCompatActivity {

    TextView txtId, txtName, txtPhone, txtPOB, txtDept;
    MaterialButton btnBack, btnCall, btnEdit;
    Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_detail);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        addViews();
        getData();
        addEvents();

        View layoutHeaderTitle = findViewById(R.id.layoutHeaderTitle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            layoutHeaderTitle.setPadding(
                    layoutHeaderTitle.getPaddingLeft(),
                    systemBars.top + (int) (12 * getResources().getDisplayMetrics().density),
                    layoutHeaderTitle.getPaddingRight(),
                    layoutHeaderTitle.getPaddingBottom()
            );
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtId = findViewById(R.id.txtDetailId);
        txtName = findViewById(R.id.txtDetailName);
        txtPhone = findViewById(R.id.txtDetailPhone);
        txtPOB = findViewById(R.id.txtDetailPOB);
        txtDept = findViewById(R.id.txtDetailDept);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEditDetail);
        btnCall = findViewById(R.id.btnCall);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EMPLOYEE_OBJ")) {
            employee = (Employee) intent.getSerializableExtra("EMPLOYEE_OBJ");
            if (employee != null) {
                txtId.setText(employee.getId());
                txtName.setText(employee.getName().toUpperCase());
                txtPhone.setText(employee.getPhone());
                txtPOB.setText(employee.getPlaceOfBirth().toUpperCase());
                if (employee.getDepartment() != null) {
                    txtDept.setText(employee.getDepartment().getName().toUpperCase());
                } else {
                    txtDept.setText("N/A");
                }
            }
        }
    }

    private void addEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeDetailActivity.this, EmployeeAdvancedManagementActivity.class);
            intent.putExtra("EDIT_ID", employee.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        btnCall.setOnClickListener(v -> {
            if (employee != null && employee.getPhone() != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + employee.getPhone()));
                startActivity(callIntent);
            }
        });
    }
}
