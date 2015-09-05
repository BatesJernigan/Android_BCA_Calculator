package com.example.bac_calculator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private double r, A, W, num, deno, ounce, sliderDoubleValue, totalBAC;
    private int addDrinkClickCount = 0, saveClickCount;
    private boolean flag;

    private String gender_value, weigh_val, rvalue;
    private Button save_btn, add_drink, resetButton;
    private EditText weightValue;
    private RadioButton rb;
    private RadioGroup rg;
    private SeekBar sb;
    private Switch genderSwitch;
    private TextView statusTextView, bacTextView, alcoholTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save_btn = (Button) findViewById(R.id.Save_button);
        add_drink = (Button)findViewById(R.id.add_drink_button);
        resetButton = (Button) findViewById(R.id.reset_button);

        rb = (RadioButton) findViewById(R.id.radio_1oz);
        rg = (RadioGroup) findViewById(R.id.radio_group);

        sb = (SeekBar) findViewById(R.id.seekBar);

        genderSwitch = (Switch) findViewById(R.id.Gender_switch);

        weightValue = (EditText) findViewById(R.id.Weight_value);
        statusTextView =(TextView) findViewById(R.id.status_value);
        bacTextView = (TextView) findViewById(R.id.bac_value);
        alcoholTextView = (TextView) findViewById(R.id.alcohol_value);

        genderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flag = isChecked;
                if(flag){
                    genderSwitch.setTextOn("on text");
                } else {
                    genderSwitch.setTextOff("off text");
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClickCount = 1;
                weigh_val = weightValue.getText().toString();

                if(weigh_val.equals("")) {
                    statusTextView.setText(getString(R.string.weightLabelPostError));
                }  else {
                    totalBAC = calculateBAC();
                    changeFields();
                }
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton r = (RadioButton) findViewById(checkedId);
                rvalue = r.getText().toString();

                ounce = 1.0; // defaults to 1.0
                if(rvalue.equals(getString(R.string.radio5oz))) {
                    ounce = 5.0;
                } else if(rvalue.equals(getString(R.string.radio12oz))) {
                    ounce = 12.0;
                }
            }
        });

        //anonymous class
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setMax(25);
                alcoholTextView.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        add_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrinkClickCount ++;
                weigh_val = weightValue.getText().toString();

                if (weigh_val.equals("")) {
                    statusTextView.setError(getString(R.string.weightLabelPostError));
                    statusTextView.setText(getString(R.string.weightLabelPostError));
                } else {
                    if (addDrinkClickCount == 1 && saveClickCount == 0 ) {
                        totalBAC = calculateBAC();
                        changeFields();
                    } else if(saveClickCount > 0 || addDrinkClickCount > 2) {
                        totalBAC += calculateBAC();
                        changeFields();
                    }

                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weigh_val = "";
                gender_value = "";

                save_btn.setEnabled(true);
                add_drink.setEnabled(true);

                sb.setProgress(Integer.parseInt(getString(R.string.alcohol_value)));
                rb.setChecked(true);

                weightValue.setText("");
                genderSwitch.setChecked(false);

                statusTextView.setText(getString(R.string.statusSafe));
                bacTextView.setText(getString(R.string.initialBacValue));
                totalBAC = 0.00;
            }
        });
    }

    protected double calculateBAC() {
        gender_value = flag ? "Male" : "Female";
        Log.d("demo", "gender value: " + gender_value);


        r = (gender_value == "Male") ? 0.73 : 0.66;
        sliderDoubleValue = Double.parseDouble(alcoholTextView.getText().toString());

        A = ounce * sliderDoubleValue / 100;
        W = Integer.parseInt(weigh_val);

        num = A * 5.14;
        deno = W * r;
        return num/deno;
    }

    public void changeFields() {
        bacTextView.setText((String.format("%.2f", totalBAC)));
        statusTextView.setTextColor(Color.WHITE);

        if(totalBAC <= 0.08) {
            statusTextView.setText(getString(R.string.statusSafe));
            statusTextView.setBackgroundColor(Color.GREEN);
        } else if(totalBAC > 0.08 && totalBAC < 0.20) {
            statusTextView.setText(getString(R.string.statusWarning));
            statusTextView.setBackgroundColor(Color.YELLOW);
        } else if(totalBAC >= 0.20 && totalBAC < 0.25) {
            statusTextView.setText(getString(R.string.statusDanger));
            statusTextView.setBackgroundColor(Color.RED);
        } else {
            statusTextView.setText(getString(R.string.statusDanger));
            statusTextView.setBackgroundColor(Color.RED);
            save_btn.setEnabled(false);
            add_drink.setEnabled(false);

            Toast.makeText(getApplicationContext(), getString(R.string.toastMessage),
                    Toast.LENGTH_LONG).show();
        }
    }
}
