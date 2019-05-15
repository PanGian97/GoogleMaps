package thanos.skoulopoulos.gr.googlemaps;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataClientInstance {


    private static Retrofit retrofit;
    private static final String BASE_URL = "http://www.anaxoft.com/goandwin_production/api/";

    public static Retrofit getRetrofitDataInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}