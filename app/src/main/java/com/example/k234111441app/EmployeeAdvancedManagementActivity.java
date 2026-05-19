package com.example.k234111441app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.k234111441app.adapters.EmployeeAdapter;
import com.example.k234111441app.database.MyDatabaseHelper;
import com.example.k234111441app.models.Department;
import com.example.k234111441app.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvancedManagementActivity extends AppCompatActivity {
    Button btnAdd, btnUpdate, btnDelete, btnReset;
    ListView lvEmployee;
    ArrayList<Employee> listEmployee;
    ArrayList<Employee> filteredList;
    EmployeeAdapter adapterEmployee;
    EditText edtId, edtName, edtPhone, edtSearch;
    AutoCompleteTextView edtPOB;
    Spinner spDepartment, spFilterDept;
    TextView txtEmpty, btnExit;
    ArrayList<Department> listDepartment;
    ArrayList<Department> listFilterDept;
    ArrayAdapter<Department> adapterDepartment, adapterFilterDept;
    MyDatabaseHelper dbHelper;
    int selectedIndex = -1;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_advanced_management);
        
        // Ép các icon trên Status Bar (pin, sóng, giờ) thành màu TRẮNG để nổi bật trên nền đen
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        dbHelper = new MyDatabaseHelper(this);
        addViews();
        addEvents();
        lvEmployee.post(this::loadData);

        View layoutHeaderTitle = findViewById(R.id.layoutHeaderTitle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // "Kín luôn": Đẩy nội dung xuống nhưng giữ nền đen phủ tận mép trên (Status Bar)
            layoutHeaderTitle.setPadding(
                layoutHeaderTitle.getPaddingLeft(),
                systemBars.top + (int)(12 * getResources().getDisplayMetrics().density),
                layoutHeaderTitle.getPaddingRight(),
                layoutHeaderTitle.getPaddingBottom() // Giữ padding bottom từ XML (20dp)
            );
            
            // Phần Navigation Bar phía dưới vẫn giữ padding để không bị lẹm nút
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        btnExit = findViewById(R.id.btnExit);
        lvEmployee = findViewById(R.id.lvEmployee);
        
        // Inflate and add Header (Search + Form) to ListView
        LayoutInflater inflater = getLayoutInflater();
        headerView = inflater.inflate(R.layout.header_employee_management, lvEmployee, false);
        lvEmployee.addHeaderView(headerView);

        // Find views inside the Header
        btnAdd = headerView.findViewById(R.id.btnAdd);
        btnUpdate = headerView.findViewById(R.id.btnUpdate);
        btnDelete = headerView.findViewById(R.id.btnDelete);
        btnReset = headerView.findViewById(R.id.btnReset);
        edtId = headerView.findViewById(R.id.edtId);
        edtName = headerView.findViewById(R.id.edtName);
        edtPhone = headerView.findViewById(R.id.edtPhone);
        edtPOB = headerView.findViewById(R.id.edtPOB);
        
        // Cấu hình AutoComplete cho Quê quán (Tỉnh thành VN)
        String[] provinces = getResources().getStringArray(R.array.vietnam_provinces);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_1, provinces);
        edtPOB.setAdapter(provinceAdapter);

        edtSearch = headerView.findViewById(R.id.edtSearch);
        spDepartment = headerView.findViewById(R.id.spDepartment);
        spFilterDept = headerView.findViewById(R.id.spFilterDept);
        txtEmpty = headerView.findViewById(R.id.txtEmpty);

        listDepartment = new ArrayList<>();
        adapterDepartment = new ArrayAdapter<>(this, R.layout.spinner_item_swiss, listDepartment);
        adapterDepartment.setDropDownViewResource(R.layout.spinner_dropdown_item_swiss);
        spDepartment.setAdapter(adapterDepartment);

        listFilterDept = new ArrayList<>();
        adapterFilterDept = new ArrayAdapter<>(this, R.layout.spinner_item_swiss, listFilterDept);
        adapterFilterDept.setDropDownViewResource(R.layout.spinner_dropdown_item_swiss);
        spFilterDept.setAdapter(adapterFilterDept);

        listEmployee = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapterEmployee = new EmployeeAdapter(this, R.layout.item_customer_employee, filteredList);
        lvEmployee.setAdapter(adapterEmployee);
        registerForContextMenu(lvEmployee);
    }

    private void loadData() {
        ArrayList<Department> depts = dbHelper.getAllDepartments();
        
        listDepartment.clear();
        listDepartment.addAll(depts);
        adapterDepartment.notifyDataSetChanged();

        listFilterDept.clear();
        listFilterDept.add(new Department("ALL", getString(R.string.str_filter_all)));
        listFilterDept.addAll(depts);
        adapterFilterDept.notifyDataSetChanged();

        listEmployee.clear();
        listEmployee.addAll(dbHelper.getAllEmployees());
        updateFilteredList(edtSearch.getText().toString(), spFilterDept.getSelectedItemPosition());
    }

    private void addEvents() {
        btnExit.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> processAdd());
        btnUpdate.setOnClickListener(v -> processUpdate());
        btnDelete.setOnClickListener(v -> processDelete());
        btnReset.setOnClickListener(v -> clearInputs());

        // TextWatcher để Tự động lưu khi chỉnh sửa
        TextWatcher autoSaveWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (selectedIndex != -1) {
                    autoSaveCurrentEmployee();
                }
            }
        };

        edtName.addTextChangedListener(autoSaveWatcher);
        edtPhone.addTextChangedListener(autoSaveWatcher);
        edtPOB.addTextChangedListener(autoSaveWatcher);

        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedIndex != -1) {
                    autoSaveCurrentEmployee();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lvEmployee.setOnItemClickListener((parent, view, position, id) -> {
            int headerCount = lvEmployee.getHeaderViewsCount();
            int actualPosition = position - headerCount;
            
            if (actualPosition >= 0 && actualPosition < filteredList.size()) {
                Employee emp = filteredList.get(actualPosition);
                Intent intent = new Intent(EmployeeAdvancedManagementActivity.this, EmployeeDetailActivity.class);
                intent.putExtra("EMPLOYEE_OBJ", emp);
                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilteredList(s.toString(), spFilterDept.getSelectedItemPosition());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spFilterDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFilteredList(edtSearch.getText().toString(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        edtSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (edtSearch.getCompoundDrawables()[2] != null) {
                    if (event.getX() >= (edtSearch.getWidth() - edtSearch.getPaddingEnd() - edtSearch.getCompoundDrawables()[2].getBounds().width())) {
                        edtSearch.setText("");
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void fillForm(Employee emp) {
        edtId.setText(emp.getId());
        edtId.setEnabled(false);
        edtName.setText(emp.getName());
        edtPhone.setText(emp.getPhone());
        edtPOB.setText(emp.getPlaceOfBirth());
        
        if (emp.getDepartment() != null) {
            for (int i = 0; i < listDepartment.size(); i++) {
                if (listDepartment.get(i).getId().equals(emp.getDepartment().getId())) {
                    spDepartment.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateFilteredList(String query, int deptPosition) {
        filteredList.clear();
        selectedIndex = -1;
        
        String lowerQuery = query.toLowerCase();
        Department filterDept = null;
        if (deptPosition > 0) { // 0 là "ALL"
            filterDept = listFilterDept.get(deptPosition);
        }

        for (Employee emp : listEmployee) {
            boolean matchesSearch = lowerQuery.isEmpty() || 
                    emp.getName().toLowerCase().contains(lowerQuery) || 
                    emp.getId().toLowerCase().contains(lowerQuery);
            
            boolean matchesDept = (filterDept == null) || 
                    (emp.getDepartment() != null && emp.getDepartment().getId().equals(filterDept.getId()));

            if (matchesSearch && matchesDept) {
                filteredList.add(emp);
            }
        }

        if (filteredList.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            txtEmpty.setText(getString(R.string.str_no_emp_found));
        } else {
            txtEmpty.setVisibility(View.GONE);
        }
        
        adapterEmployee.notifyDataSetChanged();
    }

    private void processAdd() {
        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String pob = edtPOB.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || pob.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_input_full_info), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, getString(R.string.str_invalid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        for (Employee emp : listEmployee) {
            if (emp.getId().equalsIgnoreCase(id)) {
                Toast.makeText(this, getString(R.string.str_msg_id_exists), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Department selectedDept = (Department) spDepartment.getSelectedItem();
        Employee newEmp = new Employee(id, name, phone, pob, selectedDept);
        
        long result = dbHelper.addEmployee(newEmp);
        if (result > 0) {
            listEmployee.add(newEmp);
            updateFilteredList(edtSearch.getText().toString(), spFilterDept.getSelectedItemPosition());
            clearInputs();
            hideKeyboard();
            Toast.makeText(this, getString(R.string.str_msg_add_success), Toast.LENGTH_SHORT).show();
        }
    }

    private void processUpdate() {
        if (selectedIndex == -1) {
            Toast.makeText(this, getString(R.string.str_select_to_update), Toast.LENGTH_SHORT).show();
            return;
        }

        String id = edtId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String pob = edtPOB.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || pob.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_input_full_info), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, getString(R.string.str_invalid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        Employee originalEmp = filteredList.get(selectedIndex);
        originalEmp.setName(name);
        originalEmp.setPhone(phone);
        originalEmp.setPlaceOfBirth(pob);
        originalEmp.setDepartment((Department) spDepartment.getSelectedItem());
        
        int rows = dbHelper.updateEmployee(originalEmp);
        if (rows > 0) {
            updateFilteredList(edtSearch.getText().toString(), spFilterDept.getSelectedItemPosition());
            clearInputs();
            hideKeyboard();
            Toast.makeText(this, getString(R.string.str_msg_update_success), Toast.LENGTH_SHORT).show();
        }
    }

    private void processDelete() {
        if (selectedIndex == -1) {
            Toast.makeText(this, getString(R.string.str_msg_select_to_delete), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.str_confirm_delete))
            .setMessage(getString(R.string.str_msg_delete_confirm))
            .setPositiveButton(getString(R.string.str_yes), (dialog, which) -> {
                Employee empToDelete = filteredList.get(selectedIndex);
                int rows = dbHelper.deleteEmployee(empToDelete.getId());
                if (rows > 0) {
                    listEmployee.remove(empToDelete);
                    updateFilteredList(edtSearch.getText().toString(), spFilterDept.getSelectedItemPosition());
                    clearInputs();
                    hideKeyboard();
                    Toast.makeText(this, getString(R.string.str_msg_delete_success), Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(getString(R.string.str_no), null)
            .show();
    }

    private void clearInputs() {
        edtId.setText("");
        edtId.setEnabled(true);
        edtName.setText("");
        edtPhone.setText("");
        edtPOB.setText("");
        spDepartment.setSelection(0);
        selectedIndex = -1;
        edtId.requestFocus();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.employee_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int actualPosition = info.position - lvEmployee.getHeaderViewsCount();
        
        if (actualPosition >= 0) {
            selectedIndex = actualPosition;
            if (item.getItemId() == R.id.menu_edit) {
                fillForm(filteredList.get(selectedIndex));
                lvEmployee.smoothScrollToPosition(0);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                processDelete();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void autoSaveCurrentEmployee() {
        if (selectedIndex == -1) return;

        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String pob = edtPOB.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) return;

        Employee emp = filteredList.get(selectedIndex);
        emp.setName(name);
        emp.setPhone(phone);
        emp.setPlaceOfBirth(pob);
        emp.setDepartment((Department) spDepartment.getSelectedItem());
        
        dbHelper.updateEmployee(emp);
        adapterEmployee.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Quan trọng để cập nhật Intent mới chứa EDIT_ID
        handleIntentExtras(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntentExtras(getIntent());
    }

    private void handleIntentExtras(Intent intent) {
        if (intent != null && intent.hasExtra("EDIT_ID")) {
            String editId = intent.getStringExtra("EDIT_ID");
            // Cần đợi dữ liệu load xong mới tìm (nếu load từ DB là async)
            // Ở đây listEmployee đã có dữ liệu từ onCreate/loadData
            for (int i = 0; i < listEmployee.size(); i++) {
                if (listEmployee.get(i).getId().equals(editId)) {
                    final int indexInFullList = i;
                    lvEmployee.post(() -> {
                        // Tìm vị trí trong filteredList để highlight đúng
                        for (int j = 0; j < filteredList.size(); j++) {
                            if (filteredList.get(j).getId().equals(editId)) {
                                selectedIndex = j;
                                fillForm(filteredList.get(j));
                                lvEmployee.smoothScrollToPosition(0);
                                break;
                            }
                        }
                    });
                    break;
                }
            }
            intent.removeExtra("EDIT_ID");
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtName, InputMethodManager.SHOW_IMPLICIT);
    }
}
