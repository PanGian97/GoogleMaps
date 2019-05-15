package thanos.skoulopoulos.gr.googlemaps;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

public class Store {
    @SerializedName("distance")
    private Double distance;
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("tel")
    private Integer tel;
    @SerializedName("street")
    private Integer street;
    @SerializedName("site")
    private String site;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lon")
    private Double lon;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("is_open")
    private Boolean is_open;
    @SerializedName("is_fav")
    private Boolean is_fav;

public Store(Double distance, Integer id, String name, String address, Integer tel, Integer street, String site, Double lat, Double lon, String image_url, Boolean is_open, Boolean is_fav){
this.distance = distance;
this.id = id;
this.name = name;
this.address = address;
this.tel=tel;
this.street = street;
this.site=site;
this.lat=lat;
this.lon=lon;
this.image_url=image_url;
this.is_open=is_open;
this.is_fav=is_fav;
}


    //getter setter
    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTel() {
        return tel;
    }

    public void setTel(Integer tel) {
        this.tel = tel;
    }

    public Integer getStreet() {
        return street;
    }

    public void setStreet(Integer street) {
        this.street = street;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getIs_open() {
        return is_open;
    }

    public void setIs_open(Boolean is_open) {
        this.is_open = is_open;
    }

    public Boolean getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(Boolean is_fav) {
        this.is_fav = is_fav;
    }
}
