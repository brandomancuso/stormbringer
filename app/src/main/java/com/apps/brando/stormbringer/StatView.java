package com.apps.brando.stormbringer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatView extends RelativeLayout{
    String tag;
    int value;
    boolean checked;
    boolean editMode = false;
    TextView name, val;
    Button incr, decr;
    CheckBox checkbox;

    public StatView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatView,
                0, 0);
        tag = a.getString(R.styleable.StatView_tag);
        value = a.getInt(R.styleable.StatView_value, 10);
        checked = a.getBoolean(R.styleable.StatView_checked, false);
        init();

    }

    public StatView(Context context, String name, int value, boolean checked){
        super(context);
        tag = name;
        this.value = value;
        this.checked = checked;
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.stat_view_layout, this);
        checkbox = findViewById(R.id.stat_check_box);
        checkbox.setActivated(checked);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
            }
        });
        name = findViewById(R.id.tag);
        name.setText(tag);
        val = findViewById(R.id.value);
        val.setText(String.valueOf(value));
        incr = findViewById(R.id.incr);
        incr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value += 1;
                val.setText(String.valueOf(value));
            }
        });
        decr = findViewById(R.id.decr);
        decr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                value -= 1;
                val.setText(String.valueOf(value));
            }
        });
    }

    public int getValue(){
        return value;
    }

    public void setValue(int val){
        value = val;
        this.val.setText(String.valueOf(value));
    }

    public boolean enableEditMode(){
        incr.setVisibility(VISIBLE);
        decr.setVisibility(VISIBLE);
        return editMode = true;
    }

    public boolean disableEditMode(){
        incr.setVisibility(INVISIBLE);
        decr.setVisibility(INVISIBLE);
        return editMode = false;
    }
}
