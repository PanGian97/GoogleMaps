package thanos.skoulopoulos.gr.googlemaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.SphericalUtil;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int STORE_RADIUS=1000;
    private static final int MAX_STORE_RADIUS = 300000;
    private int storeAbjRadius = 1000;
    private Boolean locationPermsssionGranted = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<Results> stores;
    private ArrayList<Marker> markerList;
    private ArrayList<Marker> closeMarkerList;
    private ArrayList<PolylineData> polylineData = new ArrayList<>();
   private String websiteUrl;
    String selectedMarkerIdHolder=" ";
   private GeoApiContext geoApiContext;
    SeekBar borderBar;

    Circle circle;
    CircleOptions circleOptions;
    EditText seachEditText;
    ImageView userLocationimage;
    TextView txtRouteMode;
    Button btnWebsite;
    Button btnCancelRoute;
    Button btnFindRoute;
    LatLngBounds.Builder latLngBuilder;
    LatLng userMarkerLocation;
    Location currentLocation;
    private ArrayList<Results> storeList;
    private static final int COLOR_R_NO_MARKERS = 22;
    private static final int COLOR_R_MARKERS = 201;
  CustomInfoWindowAdapter customInfoWindowAdapter;
    Polyline polyline;
    private FragmentTabHost fragHost;

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
            abjustBorderBar();



        }
    }
public void showInfoButtons(final String websiteUrl,Marker marker){
        this.websiteUrl = websiteUrl;

btnWebsite.setVisibility(View.VISIBLE);
btnFindRoute.setVisibility(View.VISIBLE);
    btnWebsite.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            startActivity(browserIntent);
        }
    });
    btnFindRoute.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

           if(!selectedMarkerIdHolder.equals(marker.getId())) calculateDirections(marker);//check if it already pressed
            selectedMarkerIdHolder= marker.getId();
            marker.hideInfoWindow();
            FragmentManager manager =getSupportFragmentManager();
             StoreDetailsFrag fragment= (StoreDetailsFrag) manager.findFragmentById(R.id.store_dtl_frag);//
            manager.beginTransaction().replace(R.id.store_dtl_frag,new StoreDetailsFrag()).commit();
            fragment.assignDataToFragment(MapActivity.this,marker,storeList);


//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            Fragment fragment = new Fragment();
//            ft.replace(R.id.store_dtl_frag, fragment);
//            ft.commit();




        }
    });
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
         btnWebsite = (Button)findViewById(R.id.button_website);
         btnFindRoute = (Button)findViewById(R.id.button_find_route);
         btnCancelRoute = (Button)findViewById(R.id.button_route_mode);
         btnWebsite.setVisibility(View.GONE);
         btnFindRoute.setVisibility(View.GONE);
         txtRouteMode  =(TextView)findViewById(R.id.txt_route_mode);

        fragHost = (FragmentTabHost) findViewById(R.id.frag_holder);
        fragHost.setup(this, getSupportFragmentManager(), R.id.store_dtl_frag);
        fragHost.addTab(fragHost.newTabSpec("Info")
                .setIndicator("Info"), StoreDetailsFrag.class, null);

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
                       currentLocation =  (Location) task.getResult();

                        userMarkerLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                     // circle.remove();// remove the previous user position circle radius
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
    if(geoApiContext == null){
        geoApiContext = new GeoApiContext.Builder()// use it to calculate directions
                .apiKey(getString(R.string.api_key_string))
                .build();
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
            markerList = new ArrayList<>();
        GetDataService service = DataClientInstance.getRetrofitDataInstance().create(GetDataService.class);
        Call<FormatedData> callList = service.getNearbyStores(lat,lon);
        if(isNetworkConnected()) {
            callList.enqueue(new Callback<FormatedData>() {//Asynchronous
                @Override
                public void onResponse(Call<FormatedData> call, Response<FormatedData> response) {
                    // generateMapPoints(response.body());
                    FormatedData formatedData = response.body();

                    map.clear();//clear previus all markers

                    stores = formatedData.getResults();
                    Log.d(TAG, "onResponse: MAP_POINTS------->  " + stores.get(0).toString());

                    setStoreList(stores);

                    for (Results store : stores) {
//

                       LatLng markerLocation = new LatLng(store.getLatToDouble(), store.getLonToDouble());


                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(markerLocation)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_icon_small))
                                .title(store.getName())
                               .snippet(store.getId().toString())
                            //    .snippet(markerStoreInfoString)
                                .visible(false)//they will be visible on a specific radius
                        );
                       userMapBounds(userMarkerLocation,marker);


                    }
                    map.setInfoWindowAdapter(customInfoWindowAdapter = new CustomInfoWindowAdapter(MapActivity.this,MapActivity.this, getStoreList()));
                    map.setOnInfoWindowClickListener(customInfoWindowAdapter.onInfoWindowClickListener);
                    map.setOnInfoWindowCloseListener(customInfoWindowAdapter.onInfoWindowCloseListener);
                    map.setOnPolylineClickListener(MapActivity.this::onPolylineClick);
                    createMarkersOnBounds(userMarkerLocation,markerList);


                }

                @Override
                public void onFailure(Call<FormatedData> call, Throwable t) {
                    Log.d(TAG, "onFailure: @@@@@@Something get wrong");
                }
            });
        }
    }

    public void userMapBounds(LatLng userPos, Marker marker) {
        markerList.add(marker);

    }
    public void abjustBorderBar(){
        borderBar = (SeekBar)findViewById(R.id.seekBar);
        borderBar.setProgress(STORE_RADIUS);
        borderBar.incrementProgressBy(1000);
        borderBar.setMax(MAX_STORE_RADIUS);

        borderBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                storeAbjRadius = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                dataFromServer(currentLocation.getLatitude(),currentLocation.getLongitude());

            }
        });


    }

    public void createMarkersOnBounds(LatLng userPos, ArrayList<Marker> marker){

        closeMarkerList = new ArrayList<>();

        for (Marker m:markerList) {

            if (SphericalUtil.computeDistanceBetween(userPos,m.getPosition()) < storeAbjRadius) {
                closeMarkerList.add(m);
            }

            }

if(closeMarkerList.size()>0) {
   if(circle !=null) circle.remove();//delete previus circle radius

    Toast.makeText(this, "We found: "+closeMarkerList.size()+" stores close to your location", Toast.LENGTH_SHORT).show();


  circleCreator(COLOR_R_MARKERS);

    for (Marker m:closeMarkerList) {
        m.setVisible(true);


    }

}else{
    Toast.makeText(this, "No store is close to your location", Toast.LENGTH_SHORT).show();
 if(circle!=null) circle.remove(); //delete previus circle radius

      circleCreator(COLOR_R_NO_MARKERS);


}

    }

public void circleCreator(int rColor){
    circle = map.addCircle(new CircleOptions()
            .center(userMarkerLocation)
            .radius(storeAbjRadius)
            .strokeColor(Color.rgb(rColor, 24, 6))
            .fillColor(Color.argb( 100,216, 203, 212)));
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            circle.getCenter(), getZoomLevel(circle)));
}


    public float getZoomLevel(Circle circle) {
       float zoomLevel = DEFAULT_ZOOM;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()
                ));
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {//triggered when request is completed
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.d(TAG, "onFailure: Failed to calculate directions"+e);
            }
        });
    }
    private void addPolylinesToMap(final DirectionsResult result){//called automatically from calculateDirections
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                if (polylineData.size() > 0) {//reset everything from other routes
                    for(PolylineData polData:polylineData){
                        polData.getPolyline().remove();
                    }polylineData.clear();
                }
                 polylineData = new ArrayList<>();

                double minDuration=999999;
                for(DirectionsRoute route: result.routes){




                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                     polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapActivity.this, R.color.colorPrimaryDark));
                    polyline.setClickable(true);
                    polylineData.add(new PolylineData(polyline,route.legs[0]));

                    double routeDuration = route.legs[0].duration.inSeconds;
                    if(routeDuration<minDuration){minDuration = routeDuration;
                        onPolylineClick(polyline);}// it clicks all the polylines but lst the one with the least trip duration
                        zoomRoute(polyline.getPoints());//zoom around the bounds of polyline points
                       routeMode();
                }
            }
        });
    }

    private void routeMode() {
        //during route mode no store can be selected and the store radious is static;
        borderBar.setEnabled(false);
            btnCancelRoute.setVisibility(View.VISIBLE);
            txtRouteMode.setVisibility(View.VISIBLE);

//        FragmentManager manager =getSupportFragmentManager();
//       Fragment storeDetailFrag = manager.findFragmentById(R.id.store_dtl_frag);//instantiate fragment into view
         // manager.beginTransaction().replace(R.id.store_dtl_frag,new StoreDetailsFrag()).commit();

            btnCancelRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borderBar.setEnabled(true);
                    btnCancelRoute.setVisibility(View.GONE);
                    txtRouteMode.setVisibility(View.GONE);
                   // manager.beginTransaction().remove(storeDetailFrag).commit();//removing fragment from view
                    dataFromServer(currentLocation.getLatitude(),currentLocation.getLongitude());//reset the map

                }
            });

    }


//    private void routeStoreDetails(Marker marker) {
//
//            StoreDetailsFrag storeDetailsFrag = new StoreDetailsFrag();
//            storeDetailsFrag.assignDataToFragment(MapActivity.this,marker,getStoreList());
//
//        }



    public void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public ArrayList<Results> getStoreList() {
        return storeList;
    }

    public void setStoreList(ArrayList<Results> storeList) {
        this.storeList = storeList;
    }

    public void hideInfo() {
        btnWebsite.setVisibility(View.GONE);
        btnFindRoute.setVisibility(View.GONE);


    }


public void zoomRoute(List<LatLng> latLngRouteList){
//case there is no route
        if(latLngRouteList==null)return;
//creating bounds
    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    for (LatLng latLngPoint : latLngRouteList)
        boundsBuilder.include(latLngPoint);
//creating the view
    int routePadding = 120;
    LatLngBounds latLngRouteBounds = boundsBuilder.build();

    map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(latLngRouteBounds, routePadding),
            600,
            null
    );
}

    @Override
    public void onPolylineClick(Polyline polyline) {

        for(PolylineData polylineRoute: polylineData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineRoute.toString());

            if(polyline.getId().equals(polylineRoute.getPolyline().getId())){
                polylineRoute.getPolyline().setColor(ContextCompat.getColor(MapActivity.this, R.color.colorAccent));
                polylineRoute.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineRoute.getLeg().endLocation.lat,polylineRoute.getLeg().endLocation.lng
                );
                Marker destMarker = map.addMarker(new MarkerOptions()

                .position(endLocation)
                        .title("Selected store")
                        .snippet("Duration: "+polylineRoute.getLeg().duration.toString())
                );
                    map.setInfoWindowAdapter(null);//disable to custom adapter
                  destMarker.showInfoWindow();

            }
            else{
                polylineRoute.getPolyline().setColor(ContextCompat.getColor(MapActivity.this, R.color.colorPrimary));
                polylineRoute.getPolyline().setZIndex(0);
            }
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(!selectedMarkerIdHolder.equals(marker.getId()))
        {polyline.remove();}

        return false;
    }
}


