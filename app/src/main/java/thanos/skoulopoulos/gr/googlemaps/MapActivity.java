package thanos.skoulopoulos.gr.googlemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean locationPermsssionGranted = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    //private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        if (locationPermsssionGranted) {
            getDeviceLocation();
            //DEFAULT if statement that need for setMyLocationEnabled
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);//location spot on map
            map.getUiSettings().setMyLocationButtonEnabled(false);//disable the by default location button
            map.getUiSettings().setCompassEnabled(true);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }
    private void getLocationPermission(){
        String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                {locationPermsssionGranted=true;
                initMap();}
               else{ ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);}
            }else{ ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);}
        }
        private void getDeviceLocation(){
            Log.d(TAG, "getDeviceLocation: getting current location ");
          fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
          try { if(locationPermsssionGranted){
              final Task location = fusedLocationProviderClient.getLastLocation();
              location.addOnCompleteListener(new OnCompleteListener() {
                  @Override
                  public void onComplete(@NonNull Task task) {
                   if(task.isSuccessful()){
                       Log.d(TAG, "onComplete: Found current location!");
                       Location currentLocation =  (Location) task.getResult();
                       moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                       //find close shops
                       DataFromServer(currentLocation.getLatitude(),currentLocation.getLongitude());
                   }else{
                       Log.d(TAG, "onComplete: Current location is null");
                       Toast.makeText(MapActivity.this, "Unable to get curr location", Toast.LENGTH_SHORT).show();
                   }
                  }
              });
          }

          }catch (SecurityException e ){
              Log.d(TAG, "getDeviceLocation: SecurityException "+e.getMessage());
          }

        }

        private void moveCamera(LatLng latLng, float zoom){
            Log.d(TAG, "moveCamera: Moving the camera to lat "+latLng.latitude+
                    " lng "+latLng.longitude);
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        }
private void initMap() {
    Log.d(TAG, "initMap: Initializing map");
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);//preparing map
}



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermsssionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE :{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 locationPermsssionGranted=true;
                 initMap();
                }
            }

        }
    }

    public void DataFromServer(double lat,double lon){
        GetDataService service = DataClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Store>> callLatList = service.getNearbyStores(lat,lon);
        callLatList.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                generateMapPoints(response.body());
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Log.d(TAG, "onFailure: Something get wrong");
            }
        });

    }
    public void generateMapPoints(List<Store> storeList){
        Log.d(TAG, "generateMapPoints: MAP POINTS:---> "+storeList);

    }
}


