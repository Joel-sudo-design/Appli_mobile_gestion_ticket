package com.example.appli_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class adapter_filter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public adapter_filter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView textView= rowView.findViewById(R.id.text);
        ImageView imageView = rowView.findViewById(R.id.image);

        textView.setText(values[position]);

        if (values[position].equals("Plus récent")) {
            imageView.setImageResource(R.drawable.arrow_upward);
        }
        else {
            imageView.setImageResource(R.drawable.arrow_downward);
        }
        return rowView;
    }
}

