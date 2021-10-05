package com.example.testapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentVehicleInfoBinding;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        binding.newVehicleButton.setOnClickListener(this::onClick);
        binding.changeVehicleButton.setOnClickListener(this::onClick);

        getParentFragmentManager().setFragmentResultListener("dataFromChange", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                carInfo = result.getStringArray("carInfo");
            }
        });

        if (carInfo[0] == null) {
            //get first car/some default car from server and put in carInfo array
            // use vehicle ABC123 as default car

            // get info from server about vehicle ABC123
            String ip = "194.193.148.240";
            Integer port = 1024;
            JSONObject json = new JSONObject();
            JSONObject json_data = new JSONObject();
            try {
                json_data.put("rego","ABC123");

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String response = "{\"message\":[{\"trucktype\":null,\"yr\":2020,\"registration\":\"ABC123\",\"vin\":\"1234abcd5678efghi\",\"veh_id\":14,\"model\":\"Ranger\",\"fuel_type\":\"PETROL\",\"veh_type_id\":14,\"make\":\"Ford\",\"fuel_cons\":100,\"emissions\":371,\"eng\":\"engine\"}],\"status\":200}";
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

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.newVehicleButton:
                NavHostFragment.findNavController(VehicleInfoFragment.this)
                        .navigate(R.id.action_VehicleInfoFragment_to_NewVehicleInfoFragment);
                break;
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