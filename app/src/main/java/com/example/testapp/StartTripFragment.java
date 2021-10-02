package com.example.testapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentStartTripBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class StartTripFragment extends Fragment {

    private FragmentStartTripBinding binding;
    private String[] carVIN = {"ABC123", "DEF456", "JKL789", "HIJ012", "LMN345"};
    private List<String> destinationListStr = new ArrayList<String>();
    private LatLng startLatLng, autocompleteLatLng;
    private List<LatLng> destinationListLatLng = new ArrayList<LatLng>();
    private ArrayAdapter<String> destinationListAdapter;
    private EditText autocompleteEditText;
    private Intent autocompleteIntent;
    private LocationManager locationManager;
    private String currentAddress;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private String polyLine = "knkmEh|vnUuAuCq@q@eBe@{Cg@eBCuEb@_GvA{Ar@gG`BiEz@oBbAgH~FsBnByCfBgOpOuDvDsBrCkHhKm@`@s@Hy@?AhBi@rG]rITlO?nIBly@Enr@@~A{CBqOJsD@gDRcE@a@?{AQaHF_JAmEe@gDCkET{HZuDH{DBaBQ}OB{BR{EBoC?qBBwBLkF?}KFs@??|@?v@@lFBbOBlV@vBc@?gABqABiRD_H@?mBjB?zIA?jB`MCpACfACb@??jA?z@@lAAfIBrM@jLD~\\@lCBnB[pAKb@[dBHjDDrEe@rEOr@@~@}@hC_DjHeFlJ{IjP}Uvi@yJjTuIxRaDpGu@hAeFvGkFjH_CvDoBpDu@~@w@?mAzBeEbKcA`DwEdNuDlJ_IrQgBdE}DjJiDnGuDdHsCvG}AzDwDvHeEtJyUvk@wBrFaK|TkG`MwM`XqNp[uFxL}DhHsG|JoDpE}JfKmTrTeBvByCrEiDxGoE|JyDrGiEjG_DrD{FlFqQnLs_@xUu[vRgO`KoBfAmKlH}ExCuB|@eIdD_Ch@iRdEgBPmAIwBm@MEa@HaCiAgGiC}E{B}NoHiLsFy_@kRcFyByJyCsSoGoWiI_I}CsOaHm@g@iCgAeFmBqHqBaFyAi@]]cA@YJo@nIxBHT|Bj@^LW`Bs@vCK\\}@vAc@Xy@Ic@q@@eA^k@r@K~Dz@vGlBxIpDtIlDpCjAj@CjFjBbGnBrEtAzQtFbQnFhHlCj\\hPrd@bUta@|Pn`@lP`T|ItD`BvJnDbPvDhKzA|Jx@rUf@jb@n@d_@f@|\\d@lHKr]w@l}@kBtBCbTm@vLy@lHmAlSuEfW}Gfh@yM~XsHzG{AxJyAbIkAzR{C~O_CdKkBfDiAtJgE~JwE|GkDlW{Ll\\}OnSkJrKeDtMsCxHcApJs@nQWbR@lT@~R?vLBpNI`C@fF^dDr@jGjCfDbCbDfDhBpBtDbDnC`BtF~Bf[bInLzCxRpHfI`D~EfAvG`@~BIjEg@~Aa@zDcBxMqHxAgAt@cApCsDjCwF|AkG^_I?qd@FaoAJ}eA?w]H_E?mIJwHOqG[iDw@sIC{ELmD`@_HP_b@@y`@D_e@Fw[@cGUuHBeBG{FByFr@aAPGr@@?{D?_EAqJEiJG_EAiGCoB?]h@?vA?wA?i@??eB?yAA}AAeJA_N@uMeUFqVHksAZ}\\Pmg@F}YJAgHIma@WakAQe_AMab@Gck@Cys@AgbAAmNEaEsK@?uN@cHA_@W?mD@s@AiBk@_BiBGM";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentStartTripBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        //destination list adapter
        destinationListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, destinationListStr);
        binding.destinationsList.setAdapter(destinationListAdapter);

        //car VIN autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, carVIN);
        AutoCompleteTextView autoCompleteTextView = binding.carAutoCompleteText;
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        // places intent builder
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
        autocompleteIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                .setLocationBias(RectangularBounds.newInstance(
                        //SW Corner
                        new LatLng(-39, 143.5),
                        //NE Corner
                        new LatLng(-37, 146)
                ))
                .setCountry("AU")
                .build(getContext());

        //places autocomplete
        binding.fromEditText.setFocusable(false);
        binding.destinationsEditText.setFocusable(false);
        Places.initialize(getContext(), "AIzaSyANO9QSl-t37uZDHAvJyFONA7Mty3TL9Y0");
        binding.fromEditText.setOnClickListener(this::onClickAuto);
        binding.destinationsEditText.setOnClickListener(this::onClickAuto);

        //location manager
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        binding.currentLocationButton.setOnClickListener(this::onClick);
        binding.addDestinationButton.setOnClickListener(this::onClick);
        binding.startButton.setOnClickListener(this::onClick);

    }

    public void onClickAuto(View view) {
        startActivityForResult(autocompleteIntent, 100);
        switch (view.getId()) {
            case R.id.fromEditText:
                startLatLng = autocompleteLatLng;
                autocompleteEditText = binding.fromEditText;
                break;
            case R.id.destinationsEditText:
                autocompleteEditText = binding.destinationsEditText;
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addDestinationButton:
                destinationListStr.add(binding.destinationsEditText.getText().toString());
                destinationListLatLng.add(autocompleteLatLng);
                binding.destinationsEditText.setText("");
                destinationListAdapter.notifyDataSetChanged();
                break;
            case R.id.currentLocationButton:
                ((MainActivity)getActivity()).getLocation();
                currentAddress = ((MainActivity)getActivity()).getCurrentAddress();
                binding.fromEditText.setText(currentAddress);
                getLastKnownLocation();
                break;
            case R.id.startButton:
                //send destination to server
                Log.d(TAG, "start location:" + startLatLng +
                        " destinations:" + destinationListLatLng.toString() +
                        " car rego: " + binding.carAutoCompleteText.getText());
                String ip = "194.193.148.240";
//                ip = "localhost";

                Integer port = 1024;
                JSONObject json = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("start", startLatLng);
                    data.put("destinations", destinationListLatLng);
                    data.put("vehicle",binding.carAutoCompleteText.getText());
                    json.put("name", "directions");
                    json.put("type", 0);
                    json.put("data", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                asyncCommunication c = new asyncCommunication(ip,port,json);
                Thread thread = new Thread(c);
                thread.start();
                //c.doInBackground(null);
                //Boolean connected = c.connect(ip,port);
                //Log.d(TAG, connected.toString());
                //c.sendMessage("test");
                //send destination to server
                //get route from server
                //get polyline from reply
                Log.d(TAG, "start button pressed");
                while (!c.finished()) {
                    continue;
                }
                Log.d(TAG, c.getServerResponse());
                //MapsFragment.getInstance().addPolylinesToMap(polyLine);
                MapsFragment.getInstance().readyRoute(c.output);
                //MapsFragment.getInstance().setPolyLine(polyLine);
                //get route from server
                //get polyline from reply
                //ping location
                //((MainActivity)getActivity()).startLocationService();
                NavHostFragment.findNavController(StartTripFragment.this)
                        .navigate(R.id.action_StartTripFragment_to_MapsFragment);
                break;
        }
    }

    private boolean isConnected(StartTripFragment startTripFragment) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            autocompleteEditText.setText(place.getAddress());
            autocompleteLatLng = place.getLatLng();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "getLocation" + geoPoint.toString());
                    Toast.makeText(getContext(), location.getLatitude() + "-" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "get location FAILED");
                    Toast.makeText(getContext(), "get location FAILED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}