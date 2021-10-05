package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.testapp.databinding.FragmentMapsBinding;
import com.example.testapp.databinding.FragmentStartTripBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.internal.PolylineEncoding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MapsFragment extends Fragment {

    private GoogleMap mGoogleMap;
    private static MapsFragment instance = null;
    //private String polyLine = "knkmEh|vnUuAuCq@q@eBe@{Cg@eBCuEb@_GvA{Ar@gG`BiEz@oBbAgH~FsBnByCfBgOpOuDvDsBrCkHhKm@`@s@Hy@?AhBi@rG]rITlO?nIBly@Enr@@~A{CBqOJsD@gDRcE@a@?{AQaHF_JAmEe@gDCkET{HZuDH{DBaBQ}OB{BR{EBoC?qBBwBLkF?}KFs@??|@?v@@lFBbOBlV@vBc@?gABqABiRD_H@?mBjB?zIA?jB`MCpACfACb@??jA?z@@lAAfIBrM@jLD~\\@lCBnB[pAKb@[dBHjDDrEe@rEOr@@~@}@hC_DjHeFlJ{IjP}Uvi@yJjTuIxRaDpGu@hAeFvGkFjH_CvDoBpDu@~@w@?mAzBeEbKcA`DwEdNuDlJ_IrQgBdE}DjJiDnGuDdHsCvG}AzDwDvHeEtJyUvk@wBrFaK|TkG`MwM`XqNp[uFxL}DhHsG|JoDpE}JfKmTrTeBvByCrEiDxGoE|JyDrGiEjG_DrD{FlFqQnLs_@xUu[vRgO`KoBfAmKlH}ExCuB|@eIdD_Ch@iRdEgBPmAIwBm@MEa@HaCiAgGiC}E{B}NoHiLsFy_@kRcFyByJyCsSoGoWiI_I}CsOaHm@g@iCgAeFmBqHqBaFyAi@]]cA@YJo@nIxBHT|Bj@^LW`Bs@vCK\\}@vAc@Xy@Ic@q@@eA^k@r@K~Dz@vGlBxIpDtIlDpCjAj@CjFjBbGnBrEtAzQtFbQnFhHlCj\\hPrd@bUta@|Pn`@lP`T|ItD`BvJnDbPvDhKzA|Jx@rUf@jb@n@d_@f@|\\d@lHKr]w@l}@kBtBCbTm@vLy@lHmAlSuEfW}Gfh@yM~XsHzG{AxJyAbIkAzR{C~O_CdKkBfDiAtJgE~JwE|GkDlW{Ll\\}OnSkJrKeDtMsCxHcApJs@nQWbR@lT@~R?vLBpNI`C@fF^dDr@jGjCfDbCbDfDhBpBtDbDnC`BtF~Bf[bInLzCxRpHfI`D~EfAvG`@~BIjEg@~Aa@zDcBxMqHxAgAt@cApCsDjCwF|AkG^_I?qd@FaoAJ}eA?w]H_E?mIJwHOqG[iDw@sIC{ELmD`@_HP_b@@y`@D_e@Fw[@cGUuHBeBG{FByFr@aAPGr@@?{D?_EAqJEiJG_EAiGCoB?]h@?vA?wA?i@??eB?yAA}AAeJA_N@uMeUFqVHksAZ}\\Pmg@F}YJAgHIma@WakAQe_AMab@Gck@Cys@AgbAAmNEaEsK@?uN@cHA_@W?mD@s@AiBk@_BiBGM";
    private String polyLine;
    private LatLng currentLatLng;
    private LatLng prevLatLng;
    private Polyline greyPolyLine;
    private Polyline tripPolyLine;
    private List<LatLng> greyPolyList = new ArrayList<LatLng>();
    private List<LatLng> tripPolyList = new ArrayList<LatLng>();
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            startLocationUpdates();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            if (polyLine != null) {
                Toast.makeText(getContext(), "should display route", Toast.LENGTH_SHORT).show();
                addPolylinesToMap(polyLine);
            } else {
                Toast.makeText(getContext(), "map ready", Toast.LENGTH_SHORT).show();
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                LatLng monash = new LatLng(-37.9144, 145.1300);
                googleMap.addMarker(new MarkerOptions().position(monash).title("Monash University"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(monash));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        getParentFragmentManager().setFragmentResultListener("dataFromStart", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                polyLine = result.getString("polyLine");
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        instance = this;
    }

    LocationCallback locationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            Location location = locationResult.getLastLocation();
            //send location to server
            //receive info from server
            LatLng tempLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            //if server says on track
            if (currentLatLng != null && currentLatLng != tempLatLng) {
                prevLatLng = currentLatLng;
                currentLatLng = tempLatLng;
                Range rangeLat, rangeLng;
                if (tripPolyLine != null){
                    tripPolyLine.remove();
                }
                if (currentLatLng.latitude < prevLatLng.latitude) {
                    rangeLat = new Range(currentLatLng.latitude-0.00001, prevLatLng.latitude+0.00001);
                } else {
                    rangeLat = new Range(prevLatLng.latitude-0.00001, currentLatLng.latitude+0.00001);
                }
                if (currentLatLng.longitude < prevLatLng.longitude) {
                    rangeLng = new Range(currentLatLng.longitude-0.00001, prevLatLng.longitude+0.00001);
                } else {
                    rangeLng = new Range(prevLatLng.longitude-0.00001, currentLatLng.longitude+0.00001);
                }
                List<LatLng> toRemove = new ArrayList<LatLng>();
                for(LatLng latLng: tripPolyList){
                    if (rangeLat.contains(latLng.latitude) && rangeLng.contains(latLng.longitude)) {
                        toRemove.add(latLng);
                    }
                }
                tripPolyList.removeAll(toRemove);
                tripPolyLine = mGoogleMap.addPolyline(new PolylineOptions().addAll(tripPolyList));
                tripPolyLine.setColor(ContextCompat.getColor(getActivity(), R.color.quantum_googblue));
                tripPolyLine.setClickable(true);
                if (!greyPolyList.contains(currentLatLng)) {
                    greyPolyList.add(currentLatLng);
                }
                if (greyPolyLine != null){
                    greyPolyLine.remove();
                }
                greyPolyLine = mGoogleMap.addPolyline(new PolylineOptions().addAll(greyPolyList));
                greyPolyLine.setColor(ContextCompat.getColor(getActivity(), R.color.quantum_grey));
            } else {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            //if server says recalculating route
//            tripPolyLine.remove();
            //set polyLine to new polyline from server
//            addPolylinesToMap(polyLine);
            //if server says trip done
            //display dialogue box with trip details
//            NavHostFragment.findNavController(MapsFragment.this) //back to menu
//                    .navigate(R.id.action_MapsFragment_to_FirstFragment);
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    public void addPolylinesToMap(final String polyLine){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(polyLine);
                List<LatLng> newDecodedPath = new ArrayList<>();
                for(com.google.maps.model.LatLng latLng: decodedPath) {
                    LatLng point = new LatLng(latLng.lat, latLng.lng);
                    newDecodedPath.add(point);
                    tripPolyList.add(point);
                    Log.d(TAG, latLng.lat + "-" + latLng.lng);
                }
                tripPolyLine = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                tripPolyLine.setColor(ContextCompat.getColor(getActivity(), R.color.quantum_googblue));
                tripPolyLine.setClickable(true);
                zoomRoute(newDecodedPath);
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    public static MapsFragment getInstance() {
        return instance;
    }
}