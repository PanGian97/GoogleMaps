package thanos.skoulopoulos.gr.googlemaps;

import com.google.gson.annotations.SerializedName;

public class Results
{
    public Results(Double distance,Integer id,String name,String address,String tel
    ,Integer street,String site,String lat,String lon,String image_url,Integer is_open,Integer is_fav){
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
    @SerializedName("site")
    private String site;
    @SerializedName("address")
    private String address;
    @SerializedName("distance")
    private Double distance;
    @SerializedName("is_open")
    private Integer is_open;
    @SerializedName("street")
    private Integer street;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("name")
    private String name;
    @SerializedName("is_fav")
    private Integer is_fav;
    @SerializedName("tel")
    private String tel;
    @SerializedName("lon")
    private String lon;
    @SerializedName("id")
    private Integer id;
    @SerializedName("lat")
    private String lat;

    public String getSite ()
    {
        return site;
    }

    public void setSite (String site)
    {
        this.site = site;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public Double getDistance ()
    {
        return distance;
    }

    public void setDistance (Double distance)
    {
        this.distance = distance;
    }

    public Integer getIs_open ()
    {
        return is_open;
    }

    public void setIs_open (Integer is_open)
    {
        this.is_open = is_open;
    }

    public Integer getStreet ()
    {
        return street;
    }

    public void setStreet (Integer street)
    {
        this.street = street;
    }

    public String getImage_url ()
    {
        return image_url;
    }

    public void setImage_url (String image_url)
    {
        this.image_url = image_url;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Integer getIs_fav ()
    {
        return is_fav;
    }

    public void setIs_fav (Integer is_fav)
    {
        this.is_fav = is_fav;
    }

    public String getTel ()
    {
        return tel;
    }

    public void setTel (String tel)
    {
        this.tel = tel;
    }

    public String getLon ()
    {
        return lon;
    }

    public void setLon (String lon)
    {
        this.lon = lon;
    }

    public Integer getId ()
    {
        return id;
    }

    public void setId (Integer id)
    {
        this.id = id;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [site = "+site+", address = "+address+", distance = "+distance+", is_open = "+is_open+", street = "+street+", image_url = "+image_url+", name = "+name+", is_fav = "+is_fav+", tel = "+tel+", lon = "+lon+", id = "+id+", lat = "+lat+"]";
    }
}
