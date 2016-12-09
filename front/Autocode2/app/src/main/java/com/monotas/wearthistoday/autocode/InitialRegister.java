package com.monotas.wearthistoday.autocode;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class InitialRegister extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_register);

        EditText editText = (EditText)findViewById(R.id.editText);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        EditText editText2 = (EditText)findViewById(R.id.editText2);
        //表示
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String sex = sp.getString("Sex", null);

        editText.setText(sp.getString("UserName", null), TextView.BufferType.NORMAL);
        if("女性".equals(sex)) {
            radioGroup.check(R.id.female);
        } else{
            radioGroup.check(R.id.male);
        }
        editText2.setText(String.valueOf(sp.getInt("Age", 0)), TextView.BufferType.NORMAL);
    }


    public void Register(View v){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //値の取得
        EditText editText = (EditText)findViewById(R.id.editText);
        String name = editText.getText().toString();

        if(! "".equals(name)) {
            sp.edit().putString("UserName", name).apply();
        }

        RadioGroup rg = (RadioGroup)findViewById(R.id.radiogroup);
        int id = rg.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)findViewById(id);
        String sex = radioButton.getText().toString();

        sp.edit().putString("Sex", sex).apply();

        EditText et2 = (EditText)findViewById(R.id.editText2);

        if(! "".equals(et2.getText().toString())) {
            int age = Integer.parseInt(et2.getText().toString());
            if(age > 0) {
                sp.edit().putInt("Age", age).apply();
            }
        }

        Toast.makeText(this, "登録しました. ", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
