package com.example.consumirpaises.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Name {

    @SerializedName("common")
    @Expose
    private String common;
    @SerializedName("official")
    @Expose
    private String official;

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Name.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("common");
        sb.append('=');
        sb.append(((this.common == null)?"<null>":this.common));
        sb.append(',');
        sb.append("official");
        sb.append('=');
        sb.append(((this.official == null)?"<null>":this.official));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
