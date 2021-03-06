package com.example.testapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    private String polyLine = "delfFstytZm@Q@MtChAb@~HcBhWwCtO_L|R}Q~\\}Wtf@ko@~lAyRdb@sCnRgBnWgAnIuEfJoPrMyFtNiP~r@Rg@qBv^wC|^}DfP{GpHgP|KoAjL@fCENO?EGsAOz@Ib@a@p@cMkCkCqi@cGuiBeSuEEmCpGwNlXqErXuFnKuNfHmOCIhAkFjDgGnLcAvRrAvWaClIgGrG{SpQsD|GsDjY}BpVh@tGpD|Su@xXzIxRbGfYeFt^{JtNcNvLyFlFeC~GoE~d@gBn@jObw@hDzV{ArUkHlx@}B|^p@d@lFdc@~BhBbC@dAmHqDeDqFFyOf@wNcDmd@sLqRuAcIVqXrBeMkEaT{KQEkVjAmTkAeLiCvAcj@cBwXgFiPa@{EgIaHc@yAwF{Ji@gB^eDZyIuBuAeM{@@jLvD@bJh@d@KFAJLCRHvGpFxIb@jB|IlH^|E~G~T@RWjNo@zShEtClSnEvRfAhVkBnLfCdJdGbKzExGZzXeCQh@dUdFx[rIhLArEeGgBqk@r@o^|M}lAs@gWoGy]uJah@gA{l@rCohAdKu]~JmWbCoNpG_c@yDiYQ_KfDeZzDgUbNsMtReSfAsQ}AeT@gFzDcMlH_HtOsBlOa@hHEfFyHbHg@lO_`GkSfGqIxQcIvy@aR~PNtZaMLeNvBsIb@sNeAq@hBwLbRac@dRe@bIsf@zKemAbUkm@rByHxDk@zCyl@tGgdAGiRzJeNk@gf@hf@iu@ph@yk@|x@ucApu@ocA``@ai@nJoRnBuJlGgHhLLvUzHp@lHd|P|WzS|PdHn\\pDnv@~Fzb@q@dr@zGlk@v@~lA|T|^fAn[eGd@uEdYh@pV~Dzo@~Jhy@rJ|jAlMvz@xKl\\hMvS|TrN|@xZ~sAfMv[r_@jk@dNfL~UjKv@vMbWhAVtFzb@vNzIp@HkBdZ}Oj|@ZpUbFrThHzJ|@xDw@fA}EH_GQqAm@m@[XoAtBaA~AA_BnAuBNSNEr@|@{AlGOlDCPAHJpAsCtAqNuBqMcEmY_Ht@@mKt@}F|CsIzFyJ~DgMcAc_@aMkRoFuVaAyNwBit@qMwVwOcQeReVea@}HuR{Rww@uNeo@qJ}SwU_V{NmGcl@KwuBkTqvAqRwPeDg@{AsXhCqYHsRbAwYwB{y@qO}a@sEqb@SqJaAu[{DcOGw@l@e_AyJgKqBoMsGi[yV}MwGue@uJwTyAwMtEcZvZet@l`Amx@riAuKfQkCdToJhyAmJzzAgD|c@yEO]Yn@@";
    private boolean fromPressed = false;

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
        Log.d(TAG, "onClickAuto: on click auto");
        autocompleteLatLng = null;
        startActivityForResult(autocompleteIntent, 100);
        switch (view.getId()) {
            case R.id.fromEditText:
                fromPressed = true;
                //startLatLng = autocompleteLatLng;
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
                if (binding.destinationsEditText.getText().toString() != "") {
                    destinationListStr.add(binding.destinationsEditText.getText().toString());
                    destinationListLatLng.add(autocompleteLatLng);
                    binding.destinationsEditText.setText("");
                    destinationListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "MISSING FIELDS", Toast.LENGTH_SHORT).show();
                }
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
                        " car rego: " + binding.carAutoCompleteText.getText().toString());
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
//                webJson wj = new webJson("https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=AIzaSyBw_I2vdr7dIyy67_eDLFjhvDwlstAJT5o");
//                Thread webThread = new Thread(wj);
//                webThread.start();
//                while (!wj.isComplete()) {
//                    continue;
//                }
//                Log.d(TAG,wj.strOutput);
/*
                asyncCommunication c = new asyncCommunication(ip,port,json,0);
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
//
                while (!c.finished()) {
                    continue;
                }
                Log.d(TAG, c.getServerResponse());

                try{
                    JSONObject response = new JSONObject(c.getServerResponse());
                    String[] raw = ((String) response.get("polyline")).split("-");
                    String poly = "";
                    for (Integer i = 0; i < raw.length; i++) {
                        int v = (Integer.valueOf(raw[i]));
                        char w = (char) v;
                        poly = poly + w;
                    }
                    polyLine = poly;
//                    Log.d(TAG, (String) response.get("polyline"));
//                    polyLine = (String) response.get("polyline");
//                    polyLine.replaceAll("\\p{C}", "");
//                    polyLine = "delfFstytZm@Q`@MtChAb@~HcBhWwCtO_L|R}Q~}Wtf@ko@~lAyRdb@sCnRgBnWgAnIuEfJoPrMyFtNiP~r@R`g@qBv^wC|^}DfP{GpHgP|KoAjL@fCENO?EGsAOz@Ib@a@p@cMkCkCqi@cGuiBeSuEEmCpGwNlXqErXuFnKuNfHmOC_IhAkFjDgGnLcAvRrAvWaClIgGrG{SpQsD|GsDjY}BpVh@tGpD|Su@xXzIxRbGfYeFt^{JtNcNvLyFlFeC~GoE~d@gB`n@jObw@hDzV{ArUkHlx@}B|^p@`d@lFdc@~BhBbC_@dAmHqDeDqFFyOf@wNcDmd@sLqRuAcIVqXrBeMkEaT{K_QEkVjAmTkAeLiCvAcj@cBwXgFiPa@{EgIaHc@yAwF{Ji@gB^eDZyIuBuAeM{@_@jLvD`@bJh@d@KFAJLCRHvGpFxIb@jB|IlH^|E~G~T`@`RWjNo@zShEtClSnEvRfAhVkBnLfCdJdGbKzExGZzXeC`Qh@dUdFx[rIhL`ArEeGgBqk@r@o^|M}lAs@gWoGy]uJah@gA{l@rCohAdKu]~JmWbCoNpG_c@yDiYQ_KfDeZzDgUbNsMtReSfAsQ}AeT`@gFzDcMlH_HtOsBlOa@hH_EfFyHbHg_@lO_`GkSfGqIxQcIvy@aR~P_NtZaM`LeNvBsIb@sNeAq`@hBwLbRac@dRe_@bIsf@zKemAbUkm@rByHxDk`@zCyl@tGgdA`GiRzJeN`k@gf@hf@iu@ph@yk@|x@ucApu@ocA``@ai@nJoRnBuJlGgHhLLvUzHp`@lHd|P|WzS|PdHnpDnv@~Fzb@q@dr@zGlk@v@~lA|T|^fAn[eGd_@uEdYh@pV~Dzo@~Jhy@rJ|jAlMvz@xKlhMvS|TrN|_@xZ~sAfMv[r_@jk@dNfL~UjK`v@vMbWhA`VtFzb@vNzIp@`HkBdZ}Oj|@ZpUbFrThHzJ|@xDw@fA}EH_GQqAm@m@[XoAtBaA~A`A_BnAuBNSNEr@|@{AlGOlDCPAHJpAsCtAqNuBqMcEmY_H_t@_@mKt@}F|CsIzFyJ~DgMcAc_@aMkRoFuVaAyNwBit@qMwVwOcQeReVea@}HuR{Rww@uNeo@qJ}SwU_V{NmGcl@_KwuBkTqvAqRwPeDg_@{AsXhCqY`HsRbAwYwB{y@qO}a@sEqb@SqJaAu[{DcOGw`@l@e_AyJgKqBoMsGi[yV}MwGue@uJwTyAwMtEcZvZet@l`Amx@riAuKfQkCdToJhyAmJzzAgD|c@yEO]Yn@@\n";
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
//                polyLine = "delfFstytZm@Q`@MtChAb@~HcBhWwCtO_L|R}Q~}Wtf@ko@~lAyRdb@sCnRgBnWgAnIuEfJoPrMyFtNiP~r@R`g@qBv^wC|^}DfP{GpHgP|KoAjL@fCENO?EGsAOz@Ib@a@p@cMkCkCqi@cGuiBeSuEEmCpGwNlXqErXuFnKuNfHmOC_IhAkFjDgGnLcAvRrAvWaClIgGrG{SpQsD|GsDjY}BpVh@tGpD|Su@xXzIxRbGfYeFt^{JtNcNvLyFlFeC~GoE~d@gB`n@jObw@hDzV{ArUkHlx@}B|^p@`d@lFdc@~BhBbC_@dAmHqDeDqFFyOf@wNcDmd@sLqRuAcIVqXrBeMkEaT{K_QEkVjAmTkAeLiCvAcj@cBwXgFiPa@{EgIaHc@yAwF{Ji@gB^eDZyIuBuAeM{@_@jLvD`@bJh@d@KFAJLCRHvGpFxIb@jB|IlH^|E~G~T`@`RWjNo@zShEtClSnEvRfAhVkBnLfCdJdGbKzExGZzXeC`Qh@dUdFx[rIhL`ArEeGgBqk@r@o^|M}lAs@gWoGy]uJah@gA{l@rCohAdKu]~JmWbCoNpG_c@yDiYQ_KfDeZzDgUbNsMtReSfAsQ}AeT`@gFzDcMlH_HtOsBlOa@hH_EfFyHbHg_@lO_`GkSfGqIxQcIvy@aR~P_NtZaM`LeNvBsIb@sNeAq`@hBwLbRac@dRe_@bIsf@zKemAbUkm@rByHxDk`@zCyl@tGgdA`GiRzJeN`k@gf@hf@iu@ph@yk@|x@ucApu@ocA``@ai@nJoRnBuJlGgHhLLvUzHp`@lHd|P|WzS|PdHnpDnv@~Fzb@q@dr@zGlk@v@~lA|T|^fAn[eGd_@uEdYh@pV~Dzo@~Jhy@rJ|jAlMvz@xKlhMvS|TrN|_@xZ~sAfMv[r_@jk@dNfL~UjK`v@vMbWhA`VtFzb@vNzIp@`HkBdZ}Oj|@ZpUbFrThHzJ|@xDw@fA}EH_GQqAm@m@[XoAtBaA~A`A_BnAuBNSNEr@|@{AlGOlDCPAHJpAsCtAqNuBqMcEmY_H_t@_@mKt@}F|CsIzFyJ~DgMcAc_@aMkRoFuVaAyNwBit@qMwVwOcQeReVea@}HuR{Rww@uNeo@qJ}SwU_V{NmGcl@_KwuBkTqvAqRwPeDg_@{AsXhCqY`HsRbAwYwB{y@qO}a@sEqb@SqJaAu[{DcOGw`@l@e_AyJgKqBoMsGi[yV}MwGue@uJwTyAwMtEcZvZet@l`Amx@riAuKfQkCdToJhyAmJzzAgD|c@yEO]Yn@@\n";
*/
                //get route from server
                //get polyline from reply
                //ping location
                //((MainActivity)getActivity()).startLocationService();
                Bundle bundle = new Bundle();
                Log.d(TAG, "onClick: poly" + polyLine);
                bundle.putString("polyline", polyLine);
                getParentFragmentManager().setFragmentResult("dataFromStart", bundle);
                NavHostFragment.findNavController(StartTripFragment.this)
                        .navigate(R.id.action_StartTripFragment_to_MapsFragment);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (fromPressed) {
                startLatLng = place.getLatLng();
                fromPressed = false;
            } else {
                autocompleteLatLng = place.getLatLng();
            }
            autocompleteEditText.setText(place.getAddress());
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
                    startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "getLocation" + geoPoint.toString());
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String address = addresses.get(0).getAddressLine(0);
                        binding.fromEditText.setText(address);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IOException eio) {
                        eio.printStackTrace();
                        System.out.println(eio.getMessage());
                    }
                } else {
                    Log.d(TAG, "get location FAILED");
                    Toast.makeText(getContext(), "get location FAILED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}