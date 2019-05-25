package thanos.skoulopoulos.gr.googlemaps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreDetailsFrag extends Fragment {

    private View view;
    TextView titleTxt;
    TextView addressTxt;
    ImageView imgTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_store_details_frag,container,false);
        titleTxt =(TextView)view.findViewById(R.id.store_title_dtl);
        addressTxt =(TextView)view.findViewById(R.id.store_desc_dtl);
        imgTxt = (ImageView)view.findViewById(R.id.store_img_dtl);

        return view;
    }
    public View assignDataToFragment(Context context,Marker marker, ArrayList<Results> storeList){

        String storeAddress="" ;
        String storeImageUrl="";
        String storeWebsiteUrl="" ;

        String markerIdToString = marker.getSnippet();
        int markerId =Integer.parseInt(markerIdToString);

        for(Results res:storeList){
            if(res.getId().equals(markerId)){
               storeAddress = res.getAddress();
                storeImageUrl = res.getCompleteImage_url();
                 storeWebsiteUrl = res.getWebsite_url();

            }
        }
        titleTxt.setText(marker.getTitle());
        addressTxt.setText(storeAddress);
        if(storeImageUrl!=null) {
            Picasso.with(context)
                    .load(storeImageUrl)
                    .resize(150, 75)
                    .placeholder(R.drawable.store)
                    .error(R.drawable.store)
                    .resize(150, 75)
                    .into(imgTxt, new MarkerCallback(marker,context));

        } return view;
    }
}
