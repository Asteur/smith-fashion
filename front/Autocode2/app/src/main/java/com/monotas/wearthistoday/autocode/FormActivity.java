package com.monotas.wearthistoday.autocode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import javax.net.ssl.HttpsURLConnection;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Response;


public class FormActivity extends AppCompatActivity {
    /*ここでView関連のオブジェクトを宣言*/
    Spinner colorSpinner;
    Spinner typeSpinner;
    ImageView imageView;

    /**/

    Bitmap clothesImage;
    Realm realm;
    public ListView mListView;
    FormActivity.ClothesDataAdapter adapter;
    static final int REQUEST_CAPTURE_IMAGE = 100;
    static final int fromGallery = 810;
    static final int fromLibrary =1002;
    ArrayList<Bitmap> arrayList;
    ArrayList<String> setArray;
    settingAdapter sa;
    final CharSequence[] cs= {"かっこよさ", "フォーマルさ","かわいさ","カジュアルさ"};
    final CharSequence[] ss= {"Camera", "Gallery","Library"};
    ListView setlist;
    ArrayList<Integer> score;
    SharedPreferences prefs;

    Switch mSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form);
        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();
        Bundle p = intent.getExtras();
        clothesImage = (Bitmap) p.get("Image");
        mSwitch = (Switch)findViewById(R.id.switch1);
        arrayList = new ArrayList<>();
        arrayList.add(clothesImage);
        adapter = new FormActivity.ClothesDataAdapter(this,0,arrayList);
        mListView = (ListView)findViewById(R.id.imageList);
        mListView.setAdapter(adapter);
        setArray = new ArrayList<>();
        sa = new settingAdapter(this,0,setArray);
        setlist = (ListView)findViewById(R.id.setlist);
        setlist.setAdapter(sa);
        score = new ArrayList<>();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        clothesImage.compress(Bitmap.CompressFormat.JPEG,50,baos);
        final byte[] mImageData = baos.toByteArray();
        realm.beginTransaction();
        ClothesData clothesData = realm.createObject(ClothesData.class);
        clothesData.setImage(Base64.encodeToString(mImageData,Base64.NO_WRAP));
        realm.commitTransaction();
    }

    public void add(View v){
        AlertDialog.Builder selector = new AlertDialog.Builder(this);
        selector.setTitle("以下から選択");
        selector.setItems(
                ss,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case 0:
                                Intent intent = new Intent();
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                                startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
                                break;
                            case 1:
                                Intent gallery = new Intent(getApplicationContext(),GalleryActivity.class);
                                startActivityForResult(gallery,fromGallery);
                                break;
                            case 2:
                                Intent library = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                library.addCategory(Intent.CATEGORY_OPENABLE);
                                library.setType("image/*");
                                startActivityForResult(library,fromLibrary);

                        }
                    }
                }
        );
        selector.create().show();

    }
    public void addset(View v){
        AlertDialog.Builder listDlg = new AlertDialog.Builder(this);
        listDlg.setTitle("次のものを追加");
        listDlg.setItems(
                cs,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("dialog", String.valueOf(cs[i]));
                        setArray.add(String.valueOf(cs[i]));
                        sa.notifyDataSetChanged();
                    }
                }
        );
        listDlg.create().show();
        score.add(4);
    }
    public void clear(View v){
        setArray.clear();
        sa.notifyDataSetChanged();
        score.clear();
    }
    public void register(View v) throws JSONException {
        Intent intent = new Intent(this,ResultActivity.class);
        JSONObject ja = new JSONObject();
        JSONObject st = new JSONObject();
        JSONArray im = new JSONArray();
        for(Bitmap bm:arrayList){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes= stream.toByteArray();
            im.put(Base64.encodeToString(bytes, Base64.NO_WRAP));

        }
        st.put("images",im);

        int prg = 0;
        boolean m = mSwitch.isChecked();
        if(m){
            prg = 1;
        }
        Log.d("progress", String.valueOf(prg));
        st.put("gender",prg);
        for(int i = 0; i < setArray.size();i++){
            JSONObject jo = new JSONObject();
            jo.put("rank",i+1);
            jo.put("level",score.get(i)+1);
            String ans;
            if(setArray.get(i) == "フォーマルさ"){
                ans = "formal";
            }
            else if(setArray.get(i) == "かわいさ"){
                ans = "kawaii";
            }
            else if(setArray.get(i) == "カジュアルさ"){
                ans = "casual";
            }
            else{
                ans = "kakkoii";
            }
            ja.put(ans,jo);
        }
        st.put("priority",ja);

        Log.d("JSON", String.valueOf(st));
        intent.putExtra("json",String.valueOf(st));
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");
            arrayList.add(capturedImage);
            adapter.notifyDataSetChanged();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.JPEG,50,baos);
            final byte[] mImageData = baos.toByteArray();
            realm.beginTransaction();
            ClothesData clothesData = realm.createObject(ClothesData.class);
            clothesData.setImage(Base64.encodeToString(mImageData,Base64.NO_WRAP));
            realm.commitTransaction();
        }
        else if(requestCode == fromGallery && resultCode == Activity.RESULT_OK){
            String captured = (String)data.getExtras().get("image");
            Bitmap bmp = null;
            byte[] im = Base64.decode(captured,0);
            bmp = BitmapFactory.decodeByteArray(im,0,im.length);
            arrayList.add(bmp);
            adapter.notifyDataSetChanged();
        }
        else if(requestCode == fromLibrary && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            uri =data.getData();
            try {
                Bitmap img = getBitmapFromUri(uri);
                arrayList.add(img);
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) throws IOException{
        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri,"r");
        FileDescriptor fd = pfd.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fd);
        pfd.close();
        return image;
    }

    public class ClothesDataAdapter extends ArrayAdapter<Bitmap> {
        private LayoutInflater layoutInflater;

        public ClothesDataAdapter(Context context, int resource, List<Bitmap> objects) {
            super(context, resource, objects);
            this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.imagelistview,parent,false);
            }
            Bitmap im = (Bitmap)getItem(position);

            ((ImageView)convertView.findViewById(R.id.imageView)).setImageBitmap(im);

            //(ImageView)convertView.findViewById(R.id.image).set
            return convertView;

        }


    }

    public class settingAdapter extends ArrayAdapter<String>{
        private LayoutInflater layoutInflater;
        public settingAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.listsettter,parent,false);
            }
            final int pos = position;
            ((TextView)convertView.findViewById(R.id.titleset)).setText((String)getItem(position));
            SeekBar sb = (SeekBar) convertView.findViewById(R.id.listseek);
            sb.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            score.set(pos,i);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    }
            );
            return convertView;
        }
    }
}
