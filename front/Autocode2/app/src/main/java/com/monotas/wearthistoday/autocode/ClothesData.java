package com.monotas.wearthistoday.autocode;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by daiki on 2016/12/07.
 */

public class ClothesData extends RealmObject{


    private String colorText;
    private String typeText;
    private int id;
    private  String image;


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



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
