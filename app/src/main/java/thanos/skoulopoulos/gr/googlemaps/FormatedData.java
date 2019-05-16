package thanos.skoulopoulos.gr.googlemaps;

import com.google.gson.annotations.SerializedName;

public class FormatedData
{
    @SerializedName("rs")
    private String rs;
@SerializedName("rm")
    private String rm;
@SerializedName("results")
    private Results[] results;

    public FormatedData(String rs,String rm, Results[] results) {
        this.rm=rm;
        this.rs=rs;
        this.results=results;

    }

    public String getRs ()
    {
        return rs;
    }

    public void setRs (String rs)
    {
        this.rs = rs;
    }

    public String getRm ()
    {
        return rm;
    }

    public void setRm (String rm)
    {
        this.rm = rm;
    }

    public Results[] getResults ()
    {
        return results;
    }

    public void setResults (Results[] results)
    {
        this.results = results;
    }

    @Override
    public String toString()
    {
        return "Stores Link: [rs = "+rs+", rm = "+rm+", results = "+results+"]";
    }
}