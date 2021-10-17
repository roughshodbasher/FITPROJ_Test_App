package com.example.testapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentVehicleInfoBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VehicleInfoFragment extends Fragment {

    private FragmentVehicleInfoBinding binding;
    private JSONObject vehicleInfoJSon;
    private String displayString;
    private String[] carInfo = new String[7]; // rego, vin, make, year, fuelConsumption, model, engine;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentVehicleInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayString = "VIN:\nMake:\nRegistration:\nYear:\nFuel Consumption:\nModel:\nEngine:";
        // set onclick listener for new vehicle and change vehicle button
        binding.newVehicleButton.setOnClickListener(this::onClick);
        binding.changeVehicleButton.setOnClickListener(this::onClick);

        // set a listener for the fragment, if the user decides to change the vehicle, then the data for
        // the new vehicle will be sent back in the bundle, listens for such change, and update UI
        // when new data arrives
        getParentFragmentManager().setFragmentResultListener("dataFromChange", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                // get the information for the new vehicle
                carInfo = result.getStringArray("carInfo");
                updateUI(); // update UI
            }
        });
        // if nothing is in carInfo, get some default car and display it
        if (carInfo[0] == null) {
            //get some default car from server and put in carInfo array
            // use vehicle ABCDEF as default car
            // get info from server about vehicle ABCDEF
            String ip = "194.193.148.240";
            Integer port = 1024;
            JSONObject json = new JSONObject();
            JSONObject json_data = new JSONObject();
            try {
                json_data.put("rego","ABCDEF");
                json.put("type", 2);
                json.put("data",json_data);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            asyncCommunication c = new asyncCommunication(ip,port,json,0);
            Thread thread = new Thread(c);
            thread.start();
            while (!c.finished()) {
                continue;
            }

            // parse the information and store it in carInfo
            try {
//                String response = "{\"message\":[{\"trucktype\":null,\"yr\":2020,\"registration\":\"ABC123\",\"vin\":\"1234abcd5678efghi\",\"veh_id\":14,\"model\":\"Ranger\",\"fuel_type\":\"PETROL\",\"veh_type_id\":14,\"make\":\"Ford\",\"fuel_cons\":100,\"emissions\":371,\"eng\":\"engine\"}],\"status\":200}";
                String response = c.getServerResponse();
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray message = jsonResponse.getJSONArray("message");
                JSONObject vehicleInfo = message.getJSONObject(0);
                carInfo[0] = vehicleInfo.getString("registration");
                carInfo[1] = vehicleInfo.getString("vin");
                carInfo[2] = vehicleInfo.getString("make");
                carInfo[3] = String.valueOf(vehicleInfo.getInt("yr"));
                carInfo[4] = String.valueOf(vehicleInfo.getInt("fuel_cons"));
                carInfo[5] = vehicleInfo.getString("model");
                carInfo[6] = vehicleInfo.getString("eng");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // updateUI
        updateUI();

    }

    // display the car info on the UI
    private void updateUI(){
        binding.categoryText.setText(String.format(
                // rego, vin, make, year, fuelConsumption, kilometers, engine;
                "VIN:%s\n" +
                        "Make: %s\n" +
                        "Registration: %s\n" +
                        "Year: %s\n" +
                        "Fuel Consumption: %s\n" +
                        "Model: %s\n" +
                        "Engine: %s",
                carInfo[1], carInfo[2], carInfo[0], carInfo[3], carInfo[4], carInfo[5], carInfo[6]
        ));
    }


    // on click listener for the view
    private void onClick(View view) {
        switch (view.getId()) {
            // new vehicle button will take the user to a new fragment that allows them to add in
            // new vehicles
            case R.id.newVehicleButton:
                NavHostFragment.findNavController(VehicleInfoFragment.this)
                        .navigate(R.id.action_VehicleInfoFragment_to_NewVehicleInfoFragment);
                break;
            // change vehicle, allows the user to change the vehicle that is to display in this fragment
            case R.id.changeVehicleButton:
                NavHostFragment.findNavController(VehicleInfoFragment.this)
                        .navigate(R.id.action_VehicleInfoFragment_to_ChangeVehicleFragment);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}