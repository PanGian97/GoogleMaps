package thanos.skoulopoulos.gr.googlemaps;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {
//    @GET("stores/nearby_test?token=test")
//    //user cordinates
//    Call<List<FormatedData>>

    @GET("nearby_test?lat=37.975620&lon=23.734529&token=test")
    //user cordinates
    Call<FormatedData>  getNearbyStores(@Query("lat") Double lat, @Query("lon") Double lon);


}
