package com.monotas.wearthistoday.autocode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class SettingActivity extends AppCompatActivity {
    CheckBox Casual;
    CheckBox formal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        Casual = (CheckBox)findViewById(R.id.casualCheck);
        formal = (CheckBox)findViewById(R.id.formalCheck);
        if(Casual == null){
            Log.d("Casual","null");
        }
    }
    public void ok(View v){
        Intent intent = new Intent(this,RecommendActivity.class);
        if(Casual.isChecked()){
            intent.putExtra("casual",1);
        }
        else{
            intent.putExtra("casual",0);
        }
        if(formal.isChecked()){
            intent.putExtra("formal",1);
        }
        else{
            intent.putExtra("formal",0);
        }
        startActivity(intent);
        finish();
    }
}
