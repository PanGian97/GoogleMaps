package thanos.skoulopoulos.gr.googlemaps;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;


public class MarkerCallback implements Callback {
   private Marker marker=null;
   private Context context;

    MarkerCallback(Marker marker, Context context) {
        this.marker=marker;
        this.context=context;
    }

    @Override
    public void onError() {
        Log.e(getClass().getSimpleName(), "Error loading thumbnail!");

        Toast.makeText(context, "Failed to load image ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        if (marker != null && marker.isInfoWindowShown()) {//to refresh infoWindow
            marker.hideInfoWindow();
            marker.showInfoWindow();

        }
      //  Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
    }

}