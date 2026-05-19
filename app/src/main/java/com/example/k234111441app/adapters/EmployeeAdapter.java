package com.example.k234111441app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.k234111441app.R;
import com.example.k234111441app.models.Employee;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private final Activity context;
    private final int resource;

    public EmployeeAdapter(@NonNull Activity context, int resource, @NonNull List<Employee> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            convertView = inflater.inflate(this.resource, parent, false);
            holder = new ViewHolder();
            holder.txtId = convertView.findViewById(R.id.txtId);
            holder.txtName = convertView.findViewById(R.id.txtName);
            holder.txtPhone = convertView.findViewById(R.id.txtPhone);
            holder.txtDept = convertView.findViewById(R.id.txtDept);
            holder.imgCall = convertView.findViewById(R.id.imgCall);
            holder.imgSms = convertView.findViewById(R.id.imgSms);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Employee emp = getItem(position);
        if (emp != null) {
            holder.txtId.setText(emp.getId());
            holder.txtName.setText(emp.getName());
            holder.txtPhone.setText(emp.getPhone());
            if (emp.getDepartment() != null) {
                holder.txtDept.setText(emp.getDepartment().getName());
            } else {
                holder.txtDept.setText("");
            }

            // Đồng bộ màu nền nếu dòng này đang được chọn để sửa (Tùy chọn thẩm mỹ)
            // if (context instanceof EmployeeAdvancedManagementActivity) { ... }

            holder.imgCall.setOnClickListener(view -> {
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:" + emp.getPhone());
                intentCall.setData(uri);
                context.startActivity(intentCall);
            });

            holder.imgSms.setOnClickListener(view -> {
                Intent intentSms = new Intent(Intent.ACTION_SENDTO);
                Uri uriSms = Uri.parse("sms:" + emp.getPhone());
                intentSms.setData(uriSms);
                context.startActivity(intentSms);
            });
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtId;
        TextView txtName;
        TextView txtPhone;
        TextView txtDept;
        ImageView imgCall;
        ImageView imgSms;
    }
}
