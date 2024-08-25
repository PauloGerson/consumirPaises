package com.example.consumirpaises.Pojo;

import androidx.room.Entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Pais {

    @SerializedName("name")
    @Expose
    private Name name;

    @SerializedName("capital")
    @Expose
    private List<String> capital;

    @SerializedName("languages")
    @Expose
    private Languages languages;

    @SerializedName("population")
    @Expose
    private Integer population;

    @SerializedName("region")
    @Expose
    private String region;

    @SerializedName("flags")
    @Expose
    private Flags flags;  // Novo campo para a bandeira

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<String> getCapital() {
        return capital;
    }

    public void setCapital(List<String> capital) {
        this.capital = capital;
    }

    public Languages getLanguages() {
        return languages;
    }

    public void setLanguages(Languages languages) {
        this.languages = languages;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Pais.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("capital");
        sb.append('=');
        sb.append(((this.capital == null) ? "<null>" : this.capital));
        sb.append(',');
        sb.append("languages");
        sb.append('=');
        sb.append(((this.languages == null) ? "<null>" : this.languages));
        sb.append(',');
        sb.append("population");
        sb.append('=');
        sb.append(((this.population == null) ? "<null>" : this.population));
        sb.append(',');
        sb.append("region");
        sb.append('=');
        sb.append(((this.region == null) ? "<null>" : this.region));
        sb.append(',');
        sb.append("flags");
        sb.append('=');
        sb.append(((this.flags == null) ? "<null>" : this.flags));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
