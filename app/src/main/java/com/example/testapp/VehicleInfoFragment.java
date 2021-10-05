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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class VehicleInfoFragment extends Fragment {

    private FragmentVehicleInfoBinding binding;
    private JSONObject vehicleInfoJSon;
    private String displayString;
    private String[] carInfo = new String[7]; // rego, vin, make, year, fuelConsumption, kilometers, engine;

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
        displayString = "VIN:\nMake:\nRegistration:\nYear:\nFuel Consumption:\nKilometers:\nEngine:";
        binding.newVehicleButton.setOnClickListener(this::onClick);
        binding.changeVehicleButton.setOnClickListener(this::onClick);

        getParentFragmentManager().setFragmentResultListener("dataFromChange", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                carInfo = result.getStringArray("carInfo");
            }
        });

        if (carInfo == null) {
            //get first car/some default car from server and put in carInfo array
        }
        binding.categoryText.setText(String.format(
                // rego, vin, make, year, fuelConsumption, kilometers, engine;
                "VIN:%s\n" +
                "Make: %s\n" +
                "Registration: %s\n" +
                "Year: %s\n" +
                "Fuel Consumption: %s\n" +
                "Kilometers: %s\n" +
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