package thanos.skoulopoulos.gr.googlemaps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "CustomInfoWindowAdapter";
private final View window;
    private final ArrayList<Results> storeList;
    private Context context;
    String storeAddress;
    String storeImageUrl;


    public CustomInfoWindowAdapter(Context context, ArrayList<Results> storeList) {//passing object and context
        this.window= LayoutInflater.from(context).inflate(R.layout.info_window_layout,null);
        this.context=context;
        this.storeList = storeList;

    }

    private void renderWindowText(Marker marker,View view){
        String title = marker.getTitle();
        TextView storeTitle = (TextView)view.findViewById(R.id.store_title);
        storeTitle.setText(title);


        Gson gson = new Gson();

        Results storeObject = gson.fromJson(marker.getSnippet(),Results.class);
//        String markerIdToString = marker.getSnippet();
//        int markerId =Integer.parseInt(markerIdToString);
//        for(Results listId:storeList){
//            if(listId.getId()==markerId){
//
//            }
//        }

          storeAddress = storeObject.getAddress() ;
          storeImageUrl = storeObject.getImage_url();
        TextView storeAdress = (TextView)view.findViewById(R.id.store_address);
        storeAdress.setText(storeAddress);

        ImageView storeImg = (ImageView)view.findViewById(R.id.store_img);

//       Picasso.with(this.context)
//               .load(storeImageUrl)
//               .resize(150,75)
//               .error(context.getResources().getDrawable(R.drawable.store))
//               .into(storeImg);
        Picasso.with(this.context)
                .load(storeImageUrl)
                .resize(150,75)
                .placeholder(R.drawable.store)
                .error(R.drawable.store)
                .resize(150, 75)
                .into(storeImg, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: **********Image load succeed");
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "onError: ************Image load failed");
                            }
                        });

                        Log.d(TAG, "renderWindowText: image url======== " + storeImageUrl);

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
}
