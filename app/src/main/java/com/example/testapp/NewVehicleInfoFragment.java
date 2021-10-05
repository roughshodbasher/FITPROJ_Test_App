package com.example.testapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testapp.databinding.FragmentNewVehicleInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class NewVehicleInfoFragment extends Fragment {

    private FragmentNewVehicleInfoBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentNewVehicleInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addVehicleButton.setOnClickListener(this::onClick);

    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.addVehicleButton:
                JSONObject json = new JSONObject();
                JSONObject json_data = new JSONObject();
                try {
                    json_data.put("VIN", binding.vinEditText.getText().toString());
                    json_data.put("Make", binding.makeEditText.getText().toString());
                    json_data.put("Registration", binding.registrationEditText.getText().toString());
                    json_data.put("Year", binding.yearEditText.getText().toString());
                    json_data.put("Fuel Consumption", binding.fuelConsumptionEditText.getText().toString());
                    json_data.put("Kilometers", binding.kilometersEditText.getText().toString());
                    json_data.put("Engine", binding.engineEditText.getText().toString());
                    json.put("data", json_data);
                    json.put("type", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //send new vehicle to server
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}