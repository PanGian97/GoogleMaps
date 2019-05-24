package thanos.skoulopoulos.gr.googlemaps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private MapActivity mapActivity;
    private static final String TAG = "CustomInfoWindowAdapter";
private final View window;
    private final ArrayList<Results> storeList;
    private Context context;
    private String storeAddress;
    private String storeImageUrl;
    private String storeWebsiteUrl;
    private LatLng markerPosition;


    public CustomInfoWindowAdapter(MapActivity mapActivity,Context context, ArrayList<Results> storeList) {//passing object and context
        this.mapActivity=mapActivity;
        this.window= LayoutInflater.from(context).inflate(R.layout.info_window_layout,null);
        this.context=context;
        this.storeList = storeList;

    }

    private void renderWindowText(final Marker marker, View view){
        LatLng position = marker.getPosition();
         markerPosition = position;
        String title = marker.getTitle();
        TextView storeTitle = (TextView)view.findViewById(R.id.store_title);
        storeTitle.setText(title);


        String markerIdToString = marker.getSnippet();
        int markerId =Integer.parseInt(markerIdToString);
        for(Results res:storeList){
            if(res.getId().equals(markerId)){
               storeAddress = res.getAddress();
               storeImageUrl = res.getCompleteImage_url();
               storeWebsiteUrl = res.getWebsite_url();

            }
        }

        TextView storeAdress = (TextView)view.findViewById(R.id.store_address);
        ImageView storeImg = (ImageView)view.findViewById(R.id.store_img);

        storeAdress.setText(storeAddress);
        if(storeImageUrl!=null) {
            Picasso.with(this.context)
                    .load(storeImageUrl)
                    .resize(150, 75)
                    .placeholder(R.drawable.store)
                    .error(R.drawable.store)
                    .resize(150, 75)
                    .into(storeImg, new MarkerCallback(marker,context));

        }

        mapActivity.showInfoButtons(storeWebsiteUrl,marker);

    }



    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,window);

        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,window);
        return window;
    }

    public GoogleMap.OnInfoWindowCloseListener onInfoWindowCloseListener = new GoogleMap.OnInfoWindowCloseListener() {
        @Override
        public void onInfoWindowClose(Marker marker) {
            Toast.makeText(mapActivity, "WINDOW CLOSED", Toast.LENGTH_SHORT).show();
            mapActivity.hideInfo();
        }
    };

   GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
       @Override
       public void onInfoWindowClick(Marker marker) {
           Log.d(TAG, "onInfoWindowClick: Info window clicked");



       }
   };

    }
//
//    public void onInfoWindowClick(Marker marker) {
//        Log.d(TAG, "onInfoWindowClick: Info window clicked");
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Find route to this store?")
//                .setCancelable(true)
//                .setPositiveButton("Go there!", (dialog, id) -> {
//                    mapActivity.calculateDirections(marker);
//                    dialog.dismiss();
//                })
//                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());