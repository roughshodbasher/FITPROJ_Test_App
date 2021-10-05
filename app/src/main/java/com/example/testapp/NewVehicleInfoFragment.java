package com.example.testapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testapp.databinding.FragmentNewVehicleInfoBinding;

import org.json.JSONArray;
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
                    // send vehicle info to server
                    json_data.put("vin", binding.vinEditText.getText().toString());
                    json_data.put("make", binding.makeEditText.getText().toString());
                    json_data.put("rego", binding.registrationEditText.getText().toString());
                    json_data.put("year", binding.yearEditText.getText().toString());
                    json_data.put("fuel_consumption", binding.fuelConsumptionEditText.getText().toString());
                    json_data.put("model", binding.modelEditText.getText().toString());
                    json_data.put("engine", binding.engineEditText.getText().toString());
                    json_data.put("fuel_type", binding.fuelEngineEditText.getText().toString());
                    json_data.put("emission", binding.emissionEditText.getText().toString());
                    json.put("data", json_data);


                    json.put("type", 1);



                    // response from server
                    String response = "{\"message\":\"Vehicle added successfully\",\"status\":200}";

                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");
                    Toast responseToast = Toast.makeText(this.getContext(),message, Toast.LENGTH_LONG);
                    responseToast.show();
                    binding.vinEditText.setText("");
                    binding.makeEditText.setText("");
                    binding.registrationEditText.setText("");
                    binding.yearEditText.setText("");
                    binding.fuelConsumptionEditText.setText("");
                    binding.modelEditText.setText("");
                    binding.engineEditText.setText("");
                    binding.fuelEngineEditText.setText("");
                    binding.emissionEditText.setText("");

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