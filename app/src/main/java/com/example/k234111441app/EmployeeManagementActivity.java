package com.example.k234111441app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EmployeeManagementActivity extends AppCompatActivity {
    Button btnExit, btnAdd, btnUpdate, btnDelete;
    ListView lvEmployee;
    ArrayList<Employee> listEmployee;
    ArrayAdapter<Employee> adapterEmployee;
    EditText edtId, edtName, edtPhone;
    int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_management);
        addViews();
        addEvents();
        loadData();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadData() {
        listEmployee.add(new Employee("e1", "Bi", "1234567891"));
        listEmployee.add(new Employee("e2", "Bun", "2345678912"));
        listEmployee.add(new Employee("e3", "Bin", "3456789123"));
        listEmployee.add(new Employee("e4", "Cun", "4567891234"));
        listEmployee.add(new Employee("e5", "Teo", "5678912345"));
        adapterEmployee.notifyDataSetChanged();
    }

    private void addEvents() {
        btnExit.setOnClickListener(view -> processExit());
        btnAdd.setOnClickListener(view -> processAdd());
        btnUpdate.setOnClickListener(view -> processUpdate());
        btnDelete.setOnClickListener(view -> processDelete());
        
        lvEmployee.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedIndex = i;
            displayEmployeeInfor(i);
        });
    }

    private void processDelete() {
        if (selectedIndex != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.str_delete_confirm_title));
            builder.setMessage(getString(R.string.str_delete_confirm_msg));
            builder.setPositiveButton(getString(R.string.str_yes), (dialogInterface, i) -> {
                listEmployee.remove(selectedIndex);
                adapterEmployee.notifyDataSetChanged();
                clearInputs();
                selectedIndex = -1;
                Toast.makeText(EmployeeManagementActivity.this, getString(R.string.str_delete_success), Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton(getString(R.string.str_no), (dialogInterface, i) -> dialogInterface.dismiss());
            builder.create().show();
        } else {
            Toast.makeText(this, getString(R.string.str_select_to_delete), Toast.LENGTH_SHORT).show();
        }
    }

    private void processUpdate() {
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_input_full_info), Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedIndex != -1) {
            boolean isExisted = false;
            for (int i = 0; i < listEmployee.size(); i++) {
                if (i == selectedIndex) continue;
                if (listEmployee.get(i).getId().equalsIgnoreCase(id)) {
                    isExisted = true;
                    break;
                }
            }

            if (isExisted) {
                Toast.makeText(this, getString(R.string.str_id_exists_update), Toast.LENGTH_SHORT).show();
            } else {
                Employee emp = listEmployee.get(selectedIndex);
                emp.setId(id);
                emp.setName(name);
                emp.setPhone(phone);
                adapterEmployee.notifyDataSetChanged();
                Toast.makeText(this, getString(R.string.str_update_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.str_select_to_update), Toast.LENGTH_SHORT).show();
        }
    }

    private void processAdd() {
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_input_full_info), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isExisted = false;
        for (Employee emp : listEmployee) {
            if (emp.getId().equalsIgnoreCase(id)) {
                isExisted = true;
                break;
            }
        }

        if (isExisted) {
            Toast.makeText(this, getString(R.string.str_id_exists), Toast.LENGTH_SHORT).show();
        } else {
            Employee newEmployee = new Employee(id, name, phone);
            listEmployee.add(newEmployee);
            adapterEmployee.notifyDataSetChanged();
            clearInputs();
            Toast.makeText(this, getString(R.string.str_add_success), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        edtId.setText("");
        edtName.setText("");
        edtPhone.setText("");
        edtId.requestFocus();
    }

    private void displayEmployeeInfor(int i) {
        Employee emp = listEmployee.get(i);
        edtId.setText(emp.getId());
        edtName.setText(emp.getName());
        edtPhone.setText(emp.getPhone());
    }

    private void processExit() {
        Dialog custom = new Dialog(EmployeeManagementActivity.this);
        custom.setContentView(R.layout.custom_dialog);
        ImageView imgYes = custom.findViewById(R.id.imgYes);
        ImageView imgCancel = custom.findViewById(R.id.imgCancel);

        imgYes.setOnClickListener(v -> finish());
        imgCancel.setOnClickListener(v -> custom.dismiss());
        custom.show();
    }

    private void addViews() {
        btnExit = findViewById(R.id.btn_exit);
        btnAdd = findViewById(R.id.btn_save);
        btnUpdate = findViewById(R.id.btn_save); // Trong layout cũ bạn dùng chung nút Save để Update
        btnDelete = findViewById(R.id.btn_delete);
        lvEmployee = findViewById(R.id.lv_employees);
        edtId = findViewById(R.id.edt_id);
        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);

        listEmployee = new ArrayList<>();
        adapterEmployee = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listEmployee);
        lvEmployee.setAdapter(adapterEmployee);
    }
}
