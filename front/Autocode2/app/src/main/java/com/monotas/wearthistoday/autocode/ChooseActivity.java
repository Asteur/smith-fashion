package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChooseActivity extends AppCompatActivity {
    private Realm realm;
    public ListView mListView;
    ChooseActivity.ClothesDataAdapter adapter;
    ArrayList<ClotheChoose> arrayList;
    SparseBooleanArray sparseBooleanArray ;
    boolean[] checked;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<ClothesData> realmResults = realm.where(ClothesData.class).findAll();
        arrayList = new ArrayList<>();
        int j = 0;

        for(ClothesData obj : realmResults){
            ClotheChoose cc = new ClotheChoose();
            cc.setImage(obj.getImage());
            cc.setColorText(obj.getColorText());
            cc.setTypeText(obj.getTypeText());
            cc.setId(obj.getId());
            cc.setSelected(false);
            arrayList.add(cc);
            j++;
        }
        num = j;
        checked = new boolean[j];
        for(int i = 0; i < j; i++){
            checked[i] = false;
        }
        adapter = new ClothesDataAdapter(this,0,arrayList);
        mListView = (ListView)findViewById(R.id.chooselist);
        mListView.setAdapter(adapter);
    }

    public void submit(View v) throws JSONException {

        int i = 0;
        JSONArray array = new JSONArray();
        SharedPreferences prefs = getSharedPreferences("token",MODE_PRIVATE);
        String dataString = prefs.getString("token","null");
        JSONObject token = new JSONObject();
        token.put("token",dataString);
        array.put(dataString);
        while (i < arrayList.size()) {
            Log.d("Submit", String.valueOf(i));
            if (arrayList.get(i).isSelected()) {
                Log.d("True", String.valueOf(i));
                Log.d("sparseBooleanArray", String.valueOf(sparseBooleanArray));
                ClotheChoose cl = arrayList.get(i);
                JSONObject jo = new JSONObject();
                jo.put("id",cl.getId());
                jo.put("Color",cl.getColorText());
                jo.put("type",cl.getTypeText());
                jo.put("image",cl.getImage());
                array.put(jo);
            }

            i++ ;
        }
        Log.d("JSONArray", String.valueOf(array));
        token.put("data",array);
        String url = "http://wearthistoday.monotas.com/api/test/echo";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                token,
                new com.android.volley.Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                        Log.d("FormActivity",response.toString());
                    }
                },
                new com.android.volley.Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d("FormActivity",error.toString());
                    }
                }
        );

        RequestSingleton.getInstance(getApplicationContext()).addToReqeustQueue(jsonObjectRequest);

    }

    public class ClothesDataAdapter extends ArrayAdapter<ClotheChoose> {
        private LayoutInflater layoutInflater;


        public ClothesDataAdapter(Context context, int resource, List<ClotheChoose> objects) {
            super(context, resource, objects);
            this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.chooselist,parent,false);
            }
            final ClotheChoose clothesData = (ClotheChoose)getItem(position);
            ((TextView)convertView.findViewById(R.id.typeText2)).setText(clothesData.getTypeText());
            ((TextView)convertView.findViewById(R.id.colorText2)).setText(clothesData.getColorText());

            Bitmap bmp = null;
            String ims = clothesData.getImage();
            byte[] im = Base64.decode(ims,0);
            bmp = BitmapFactory.decodeByteArray(im,0,im.length);

            ((ImageView)convertView.findViewById(R.id.imageView2)).setImageBitmap(bmp);
            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    clothesData.setSelected(isChecked);
                    Log.d("checkBox", String.valueOf(isChecked));
                }
            });

            //(ImageView)convertView.findViewById(R.id.image).set
            return convertView;

        }


    }
}
