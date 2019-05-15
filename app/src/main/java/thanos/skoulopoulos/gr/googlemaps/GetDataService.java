package thanos.skoulopoulos.gr.googlemaps;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {
//    @GET("stores/nearby_test?token=test")
//    //user cordinates
//    Call<List<DataObject>> getNearbyStores(@Query("lat") Double lat, @Query("lon") Double lon);

        @GET("/photos")
        Call<List<RetroPhoto>> getAllPhotos();

}
