package ir.parsmobiledesign.quantum.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import javax.annotation.Nullable;

import ir.parsmobiledesign.quantum.R;
import ir.parsmobiledesign.quantum.Realm.Category;

public class SpinnerAdapter extends ArrayAdapter {
    List<Category> list;
    TextView textView;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_layout, parent, false);
        textView = row.findViewById(R.id.tv_spinnervalue);
        textView.setText(list.get(position).getTitle());
        return row;
    }
}
