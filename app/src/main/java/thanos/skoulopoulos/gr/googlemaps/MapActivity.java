package thanos.skoulopoulos.gr.googlemaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final int STORE_RADIUS=6500;
    private Boolean locationPermsssionGranted = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<Results> stores;
    private ArrayList<String> storeUrlList;
    private String storePictureUrl;
    private Boolean isConnected;
    EditText seachEditText;
    ImageView userLocationimage;
    LatLngBounds.Builder latLngBuilder;
    LatLng userMarkerLocation;
    CameraUpdate cameraUpdate;
    private ArrayList<Results> storeList;


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
            userLocationimage = (ImageView)findViewById(R.id.imageView);
            initUserLocation();
            initSeachBar();


        }
    }

    private void initUserLocation() {
        userLocationimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              getDeviceLocation();
            }
        });

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


    private Boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo() != null){
            Log.d(TAG, "isNetworkConnected: INTERNET");
        }else{
            Toast.makeText(this, "NO INTERNET ACCESS!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "isNetworkConnected: NO INTERNET ACCESS");
        }
        return cm.getActiveNetworkInfo()!=null;
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

                        userMarkerLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());


                       moveCamera(userMarkerLocation,DEFAULT_ZOOM);
                       //find close shops
                       dataFromServer(currentLocation.getLatitude(),currentLocation.getLongitude());
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
    if(isNetworkConnected()) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//preparing map
    }
    else{
        Log.d(TAG, "initMap: No internet access");
    }
}
private void initSeachBar(){
    Log.d(TAG, "initSeachBar: Seach bar is initializing");
        seachEditText = (EditText)findViewById(R.id.seach_editText);
       seachEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
      if(i== EditorInfo.IME_ACTION_DONE
              || i==EditorInfo.IME_ACTION_SEARCH
              || keyEvent.getAction()== KeyEvent.ACTION_DOWN
              || keyEvent.getAction()== KeyEvent.KEYCODE_ENTER) {

          if(isNetworkConnected()) {geolocate();}
          else{
              Toast.makeText(MapActivity.this, "NO INTERNTE ACCESS", Toast.LENGTH_SHORT).show();}
      }
               return false;
           }
       });

       hideSoftKeyboard();
}

private void geolocate(){
    Log.d(TAG, "geolocate: Geolocating");
    String seachString = seachEditText.getText().toString();
    Geocoder geocoder = new Geocoder(MapActivity.this);
    List<Address> list = new ArrayList<>();
    try{
        list = geocoder.getFromLocationName(seachString,1);
    }catch (IOException e ){
        Log.d(TAG, "geolocate: IOException"+e.getMessage());
    }
    if(list.size()>0){
        Address address = list.get(0);

        Log.d(TAG, "geolocate: "+address.toString());
        moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM);
    }
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

    public void dataFromServer(double lat, double lon){

        GetDataService service = DataClientInstance.getRetrofitDataInstance().create(GetDataService.class);
        Call<FormatedData> callList = service.getNearbyStores(lat,lon);
        if(isNetworkConnected()) {
            callList.enqueue(new Callback<FormatedData>() {//Asynchronous
                @Override
                public void onResponse(Call<FormatedData> call, Response<FormatedData> response) {
                    // generateMapPoints(response.body());
                    FormatedData formatedData = response.body();
                    stores = formatedData.getResults();
                    Log.d(TAG, "onResponse: MAP_POINTS------->  " + stores.get(0).toString());

                    setStoreList(stores);

                    for (Results store : stores) {
//                        Gson gson = new Gson();
//                        String markerStoreInfoString = gson.toJson(store);

                       LatLng markerLocation = new LatLng(store.getLatToDouble(), store.getLonToDouble());


                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(markerLocation)
                                .title(store.getName())
                                .snippet(store.getId().toString())
                                .visible(false)//they will be visible on a specific radius
                        );
                       mapBounds(userMarkerLocation,marker);

                        storePictureUrl = DataClientInstance.getImageBaseUrl() + store.getId().toString() + store.getImage_url();

                    }
                    map.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this, getStoreList()));

                }

                @Override
                public void onFailure(Call<FormatedData> call, Throwable t) {
                    Log.d(TAG, "onFailure: @@@@@@Something get wrong");
                }
            });
        }
    }

    public void mapBounds(LatLng userPos,Marker marker) {
        //Draw your circle
        Circle circle = map.addCircle(new CircleOptions()
                .center(userPos)
                .radius(STORE_RADIUS)
                .strokeColor(Color.rgb(226, 27, 96))
                .fillColor(Color.argb(22, 225,224 , 215)));

        if (SphericalUtil.computeDistanceBetween(userPos,marker.getPosition()) < STORE_RADIUS) {
            marker.setVisible(true);
        }
    }
    public void generateMapPoints(List<FormatedData> storeList){
        Log.d(TAG, "generateMapPoints: MAP POINTS:---> "+storeList.toString());

    }
    public void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public ArrayList<Results> getStoreList() {
        return storeList;
    }

    public void setStoreList(ArrayList<Results> storeList) {
        this.storeList = storeList;
    }
}


