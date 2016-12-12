package com.monotas.wearthistoday.autocode;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by daiki on 2016/12/07.
 */

public class ClothesData extends RealmObject{


    private String colorText;
    private String typeText;
    private byte[] imageData;
    private int id;


    public String getColorText() {
        return colorText;
    }
    public void setColorText(String colorText) {
        this.colorText = colorText;
    }
    public String getTypeText(){
        return this.typeText;
    }
    public void setTypeText(String typeText){
        this.typeText = typeText;
    }
    public void setImageData(byte[] imageData){
        this.imageData = imageData;
    }
    public byte[] getImageData(){
        return this.imageData;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
