package com.monotas.wearthistoday.autocode;

/**
 * Created by daiki on 2016/12/12.
 */

public class ClotheChoose {

    private String colorText;
    private String typeText;
    private int id;
    private  String image;
    private boolean selected;


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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
