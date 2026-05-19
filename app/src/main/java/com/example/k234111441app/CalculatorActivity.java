package com.example.k234111441app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorActivity extends AppCompatActivity {

    EditText edtFormula;
    Button btnDel, btnEqual, btnBack;
    TextView txtMC, txtMR, txtMplus, txtMminus, txtMS, txtM;

    private String name_share_pref = "CalcData";
    private double memoryValue = 0;
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // LUÔN TỰ ĐỘNG TẢI: Không cần kiểm tra biến saved
        android.content.SharedPreferences pref = getSharedPreferences(name_share_pref, MODE_PRIVATE);
        String lastFormula = pref.getString("last_formula", "");
        
        if (!lastFormula.isEmpty()) {
            edtFormula.setText(lastFormula);
            isResultDisplayed = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LUÔN TỰ ĐỘNG LƯU: Ngay khi rời màn hình là lưu lại ngay
        android.content.SharedPreferences pref = getSharedPreferences(name_share_pref, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref.edit();
        editor.putString("last_formula", edtFormula.getText().toString());
        editor.apply();
    }

    private void addViews() {
        edtFormula = findViewById(R.id.editFomula);
        btnDel = findViewById(R.id.btnDel);
        btnEqual = findViewById(R.id.btnEqual);
        btnBack = findViewById(R.id.btnBack);

        // Ánh xạ các nút nhớ
        txtMC = findViewById(R.id.txtMC);
        txtMR = findViewById(R.id.txtMR);
        txtMplus = findViewById(R.id.txtMplus);
        txtMminus = findViewById(R.id.txtMminus);
        txtMS = findViewById(R.id.txtMS);
        txtM = findViewById(R.id.txtM);

        if (txtM != null) {
            txtM.setVisibility(View.INVISIBLE);
        }
    }

    private void addEvents() {
        // Sự kiện quay về MainActivity
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Chỉ cần finish() để quay lại màn hình trước đó trong stack
                finish();
            });
        }

        // Sự kiện cho nút Xóa (Del) - Xóa 1 ký tự cuối
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentData = edtFormula.getText().toString();
                if (isResultDisplayed) {
                    edtFormula.setText("");
                    isResultDisplayed = false;
                } else if (!currentData.isEmpty()) {
                    currentData = currentData.substring(0, currentData.length() - 1);
                    edtFormula.setText(currentData);
                }
            }
        });

        // Sự kiện cho nút Bằng (=) - Xử lý tính toán
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateResult();
            }
        });

        // Xử lý các nút Memory
        View.OnClickListener memoryListener = v -> {
            String formula = edtFormula.getText().toString();
            double currentVal = 0;
            try {
                if (!formula.isEmpty()) {
                    currentVal = evaluate(formula);
                }
            } catch (Exception ignored) {}

            int id = v.getId();
            if (id == R.id.txtMC) {
                memoryValue = 0;
                Toast.makeText(this, getString(R.string.str_mem_cleared), Toast.LENGTH_SHORT).show();
            } else if (id == R.id.txtMR) {
                displayResult(memoryValue);
                isResultDisplayed = true;
            } else if (id == R.id.txtMplus) {
                memoryValue += currentVal;
            } else if (id == R.id.txtMminus) {
                memoryValue -= currentVal;
            } else if (id == R.id.txtMS) {
                memoryValue = currentVal;
                Toast.makeText(this, getString(R.string.str_mem_stored), Toast.LENGTH_SHORT).show();
            }
            updateMemoryLabel();
        };

        txtMC.setOnClickListener(memoryListener);
        txtMR.setOnClickListener(memoryListener);
        txtMplus.setOnClickListener(memoryListener);
        txtMminus.setOnClickListener(memoryListener);
        txtMS.setOnClickListener(memoryListener);
    }

    private void updateMemoryLabel() {
        if (txtM != null) {
            txtM.setVisibility(memoryValue != 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void calculateResult() {
        String formula = edtFormula.getText().toString();
        if (formula.isEmpty()) return;
        try {
            double result = evaluate(formula);
            displayResult(result);
            isResultDisplayed = true;

            // LUÔN TỰ ĐỘNG LƯU KHI TÍNH XONG
            android.content.SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = preferences.edit();
            editor.putString("last_formula", edtFormula.getText().toString());
            editor.apply();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.str_expression_error), Toast.LENGTH_SHORT).show();
        }
    }

    private double evaluate(String formula) {
        // Chuyển đổi ký tự hiển thị sang ký tự toán học cho exp4j
        String expressionStr = formula.replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-");
        Expression expression = new ExpressionBuilder(expressionStr).build();
        return expression.evaluate();
    }

    private void displayResult(double result) {
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            edtFormula.setText(getString(R.string.str_error));
            return;
        }
        if (result == (long) result) {
            edtFormula.setText(String.valueOf((long) result));
        } else {
            edtFormula.setText(String.valueOf(result));
        }
    }

    public void processInputData(View view) {
        Button btnClicked = (Button) view;
        String inputValue = btnClicked.getText().toString();
        String oldValue = edtFormula.getText().toString();

        // Xử lý nút xóa hết
        if (inputValue.equals("C") || inputValue.equals("CE")) {
            edtFormula.setText("");
            isResultDisplayed = false;
            return;
        }

        // Xử lý các phép tính nhanh (x², √x, 1/x, %, +/-)
        if (inputValue.equals("x²") || inputValue.equals("√x") || inputValue.equals("1/x") || inputValue.equals("%") || inputValue.equals("+/−")) {
            if (oldValue.isEmpty()) return;
            try {
                double val = evaluate(oldValue);
                double res = 0;
                if (inputValue.equals("x²")) res = val * val;
                else if (inputValue.equals("√x")) {
                    if (val < 0) {
                        Toast.makeText(this, getString(R.string.str_error_negative), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    res = Math.sqrt(val);
                } else if (inputValue.equals("1/x")) {
                    if (val == 0) {
                        Toast.makeText(this, getString(R.string.str_error_div_zero), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    res = 1 / val;
                } else if (inputValue.equals("%")) {
                    res = val / 100;
                } else if (inputValue.equals("+/−")) {
                    res = -val;
                }
                displayResult(res);
                isResultDisplayed = true;
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.str_error_generic), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Nếu vừa tính xong mà nhấn số -> Bắt đầu biểu thức mới
        if (isResultDisplayed) {
            if (Character.isDigit(inputValue.charAt(0)) || inputValue.equals(".")) {
                oldValue = "";
            }
            isResultDisplayed = false;
        }

        // Ngăn chặn nhập nhiều dấu chấm trong một số
        if (inputValue.equals(".")) {
            String[] tokens = oldValue.split("[+−×÷\\-]");
            String lastToken = tokens.length > 0 ? tokens[tokens.length - 1] : "";
            if (lastToken.contains(".")) return;
            if (lastToken.isEmpty() || isOperator(String.valueOf(oldValue.charAt(oldValue.length()-1)))) {
                edtFormula.setText(oldValue + "0.");
                return;
            }
        }

        // Ngăn chặn nhập nhiều toán tử liên tiếp
        if (isOperator(inputValue) && !oldValue.isEmpty()) {
            char lastChar = oldValue.charAt(oldValue.length() - 1);
            if (isOperator(String.valueOf(lastChar))) {
                edtFormula.setText(oldValue.substring(0, oldValue.length() - 1) + inputValue);
                return;
            }
        } else if (isOperator(inputValue) && oldValue.isEmpty()) {
            if (inputValue.equals("−") || inputValue.equals("-")) {
                // Cho phép nhập dấu âm ở đầu
            } else {
                return;
            }
        }

        edtFormula.setText(oldValue + inputValue);
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("−") || s.equals("×") || s.equals("÷") || s.equals("-");
    }
}
