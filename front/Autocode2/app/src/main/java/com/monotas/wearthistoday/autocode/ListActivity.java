package com.monotas.wearthistoday.autocode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class ListActivity extends AppCompatActivity {

    private Realm realm;
    public ListView mListView;
    ClothesDataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<ClothesData> realmResults = realm.where(ClothesData.class).findAll();
        ClothesData[] list = (ClothesData[]) realmResults.toArray();
        ArrayList<ClothesData> arrayList = new ArrayList<>();
        for(ClothesData obj : list){
            arrayList.add(obj);
        }
        adapter = new ClothesDataAdapter(this,0,arrayList);
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(adapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView lv = (ListView)adapterView;
                ClothesData clothesData = (ClothesData)lv.getItemAtPosition(i);
                realm.beginTransaction();
                final String color = clothesData.getColorText();
                final String type = clothesData.getTypeText();
                final byte[] imageData = clothesData.getImageData();

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("Color",color);
                    obj.put("type",type);
                    obj.put("Image",imageData);
                    String url = "http://wearthistoday.monotas.com/api/echo";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            obj,
                            new com.android.volley.Response.Listener<JSONObject>(){
                                @Override
                                public void onResponse(JSONObject response){
                                    Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                                    Log.d("FormActivity",response.toString());
                                    finish();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                clothesData.deleteFromRealm();
                realm.commitTransaction();
                Toast.makeText(getApplicationContext(),"データを削除しました。",Toast.LENGTH_SHORT).show();
                adapter.remove(clothesData);
                adapter.notifyDataSetChanged();
                return false;
            }
        });



    }
    public class ClothesDataAdapter extends ArrayAdapter<ClothesData> {
        private LayoutInflater layoutInflater;

        public ClothesDataAdapter(Context context, int resource, List<ClothesData> objects) {
            super(context, resource, objects);
            this.layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.clotheslist,parent,false);
            }
            ClothesData clothesData = (ClothesData)getItem(position);
            ((TextView)convertView.findViewById(R.id.type)).setText(clothesData.getTypeText());
            ((TextView)convertView.findViewById(R.id.color)).setText(clothesData.getColorText());

            Bitmap bmp = null;
            bmp = BitmapFactory.decodeByteArray(clothesData.getImageData(),0,clothesData.getImageData().length);

            ((ImageView)convertView.findViewById(R.id.image)).setImageBitmap(bmp);

            //(ImageView)convertView.findViewById(R.id.image).set
            return convertView;

        }


    }


}
