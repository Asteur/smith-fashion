package com.monotas.wearthistoday.autocode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_CAPTURE_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void register(View v){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    public void closet(View v){
        Intent intent = new Intent(this,ListActivity.class);
        startActivity(intent);

    }

    public void initial_register(View v){
        Intent intent = new Intent(this, InitialRegister.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");
            Intent formIntent = new Intent(this,FormActivity.class);
            formIntent.putExtra("Image",capturedImage);
            startActivity(formIntent);
        }
    }
}
