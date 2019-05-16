package thanos.skoulopoulos.gr.googlemaps;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormatedData
{
    @SerializedName("rs")
    private String rs;
@SerializedName("rm")
    private String rm;



    @SerializedName("results")
    private ArrayList <Results> results;

    public FormatedData(String rs,String rm, ArrayList<Results> results) {
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
    public ArrayList<Results> getResults() {
        return results;
    }

    public void setResults(ArrayList<Results> results) {
        this.results = results;
    }

    //@Override
    public String toString(int i)
    {
        return "Stores Link: [rs = "+rs+", rm = "+rm+", results = "+ results.get(i)+"]";
    }
}