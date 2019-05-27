package thanos.skoulopoulos.gr.googlemaps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class StoreDetailsFrag extends Fragment {

    private View view;
    TextView titleTxt;
    TextView addressTxt;
    ImageView imgTxt;
    private Results store;
    private Marker marker;
    private Button closeBtn;
    EventListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_store_details_frag,container,false);
        titleTxt =(TextView)view.findViewById(R.id.store_title_dtl);
        addressTxt =(TextView)view.findViewById(R.id.store_desc_dtl);
        imgTxt = (ImageView)view.findViewById(R.id.store_img_dtl);
        closeBtn = (Button)view.findViewById(R.id.close_dtl_btn);
        init();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detachFragemnt();

            }
        });
        return view;
    }

    private void init() {
        if (store != null) {
            titleTxt.setText(marker.getTitle());
            addressTxt.setText(store.getAddress());
            if (store.getImage_url() != null) {
                Picasso.with(getContext())
                        .load(store.getImage_url())
                        .resize(150, 75)
                        .placeholder(R.drawable.store)
                        .error(R.drawable.store)
                        .resize(150, 75)
                        .into(imgTxt, new MarkerCallback(marker, getContext()));

            }
        }
    }
public void detachFragemnt(){
    FragmentTransaction fragment_trans = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();//Objects.requireNonNull--just so it cant be null
    fragment_trans.detach(StoreDetailsFrag.this);
    fragment_trans.commit();
    ((MapActivity)getActivity()).attachInfoButton();
//?
}
    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listener.onFragmentRemove();
    }

    @Override
        public void onAttach (Context context){
            super.onAttach(context);

            listener = (EventListener)context;

            marker = ((MapActivity) context).getMarker();
            String markerIdToString = marker.getSnippet();

            int markerId = Integer.parseInt(markerIdToString);

            for (Results res : ((MapActivity) context).getStoreList()) {
                if (res.getId() == markerId) {
                    store = res;
                }
            }

        }
    }
