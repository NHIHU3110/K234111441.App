package com.example.k234111441app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.k234111441app.models.Department;
import com.example.k234111441app.models.Employee;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "employee_mgmt.db";
    private static final int DATABASE_VERSION = 11;

    // Table Department
    public static final String TABLE_DEPT = "department";
    public static final String COLUMN_DEPT_ID = "id";
    public static final String COLUMN_DEPT_NAME = "name";

    // Table Employee
    public static final String TABLE_EMP = "employee";
    public static final String COLUMN_EMP_ID = "id";
    public static final String COLUMN_EMP_NAME = "name";
    public static final String COLUMN_EMP_PHONE = "phone";
    public static final String COLUMN_EMP_POB = "place_of_birth";
    public static final String COLUMN_EMP_DEPT_ID = "dept_id";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableDept = "CREATE TABLE " + TABLE_DEPT + " (" +
                COLUMN_DEPT_ID + " TEXT PRIMARY KEY, " +
                COLUMN_DEPT_NAME + " TEXT)";
        db.execSQL(createTableDept);

        String createTableEmp = "CREATE TABLE " + TABLE_EMP + " (" +
                COLUMN_EMP_ID + " PRIMARY KEY, " +
                COLUMN_EMP_NAME + " TEXT, " +
                COLUMN_EMP_PHONE + " TEXT, " +
                COLUMN_EMP_POB + " TEXT, " +
                COLUMN_EMP_DEPT_ID + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_EMP_DEPT_ID + ") REFERENCES " + TABLE_DEPT + "(" + COLUMN_DEPT_ID + "))";
        db.execSQL(createTableEmp);
        
        // Seed data: Professional Departments with unique IDs
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D1', 'IT')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D2', 'HR')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D3', 'Marketing')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D4', 'Finance')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D5', 'Sales')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D6', 'R&D')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D7', 'Customer Support')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D8', 'Legal')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D9', 'Production')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D10', 'Logistics')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D11', 'Quality Control')");
        db.execSQL("INSERT INTO " + TABLE_DEPT + " VALUES ('D12', 'Administration')");

        // Seed more Employee data with POB
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E001', 'ALBERTO MULLER', '0912345678', 'Berlin', 'D1')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E002', 'BRUNO SCHMIDT', '0922345678', 'Munich', 'D2')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E003', 'CARLA WEBER', '0932345678', 'Vienna', 'D1')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E004', 'DIETER KURZ', '0942345678', 'Hà Nội', 'D3')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E005', 'ERIK HOFFMANN', '0952345678', 'TP Hồ Chí Minh', 'D4')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E006', 'FRANZISKA GRAF', '0962345678', 'Đà Nẵng', 'D5')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E007', 'GÜNTHER KRÜGER', '0972345678', 'Cần Thơ', 'D6')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E008', 'HELMUT VOGT', '0982345678', 'Hải Phòng', 'D7')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E009', 'INGRID KOCH', '0992345678', 'Huế', 'D8')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E100', 'XAVER KOCH', '0912345754', 'Bình Dương', 'D3')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E101', 'YASMIN MEYER', '0912345755', 'Long An', 'D4')");
        db.execSQL("INSERT INTO " + TABLE_EMP + " VALUES ('E102', 'ZENO SCHMIDT', '0912345756', 'Tiền Giang', 'D5')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPT);
        onCreate(db);
    }

    // CRUD for Department
    public ArrayList<Department> getAllDepartments() {
        ArrayList<Department> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DEPT, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Department(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // CRUD for Employee
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, d.name as dept_name FROM " + TABLE_EMP + " e " +
                "LEFT JOIN " + TABLE_DEPT + " d ON e." + COLUMN_EMP_DEPT_ID + " = d." + COLUMN_DEPT_ID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Department dept = new Department(cursor.getString(4), cursor.getString(5));
                list.add(new Employee(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), dept));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long addEmployee(Employee emp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMP_ID, emp.getId());
        values.put(COLUMN_EMP_NAME, emp.getName());
        values.put(COLUMN_EMP_PHONE, emp.getPhone());
        values.put(COLUMN_EMP_POB, emp.getPlaceOfBirth());
        values.put(COLUMN_EMP_DEPT_ID, emp.getDepartment() != null ? emp.getDepartment().getId() : null);
        return db.insert(TABLE_EMP, null, values);
    }

    public int updateEmployee(Employee emp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMP_NAME, emp.getName());
        values.put(COLUMN_EMP_PHONE, emp.getPhone());
        values.put(COLUMN_EMP_POB, emp.getPlaceOfBirth());
        values.put(COLUMN_EMP_DEPT_ID, emp.getDepartment() != null ? emp.getDepartment().getId() : null);
        return db.update(TABLE_EMP, values, COLUMN_EMP_ID + " = ?", new String[]{emp.getId()});
    }

    public int deleteEmployee(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EMP, COLUMN_EMP_ID + " = ?", new String[]{id});
    }
}
