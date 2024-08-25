package com.example.consumirpaises.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Languages {
    @SerializedName("eng")
    @Expose
    private String eng;

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Languages.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("eng");
        sb.append('=');
        sb.append(((this.eng == null)?"<null>":this.eng));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
