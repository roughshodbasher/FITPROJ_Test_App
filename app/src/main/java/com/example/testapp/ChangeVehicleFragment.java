package com.example.testapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testapp.databinding.FragmentChangeVehicleBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ChangeVehicleFragment extends Fragment {

    private FragmentChangeVehicleBinding binding;
    private String carJSON;
    private JSONObject carRegoJSON;
    private List<String> carRegoList = new ArrayList<String>();
    private String[] carInfo = new String[6]; // rego, vin, make, year, fuelConsumption, kilometers, engine;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentChangeVehicleBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.changeVehicleButton.setOnClickListener(this::onClick);
        //get all regos from server
        String allRegos = "{\"status\": 200, \"message\": [{\"registration\": \"ABC123\"}, {\"registration\": \"ABC234\"}, {\"registration\": \"ABC345\"}, {\"registration\": \"EFG897\"}, {\"registration\": \"EFO197\"}, {\"registration\": \"27MUGF\"}]}";
        try {
            JSONObject tempJSON = new JSONObject(allRegos);
            JSONArray message = tempJSON.getJSONArray("message");
            for (int i = 0; i < message.length(); i++) {
                carRegoList.add(message.getJSONObject(i)
                        .getString("registration"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //car rego autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, carRegoList);
        AutoCompleteTextView autoCompleteTextView = binding.regoAutoCompleteText;
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        binding.regoAutoCompleteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    //ask server for car's info
                    //put in variables (array)
                binding.regoText.setText(String.format(
                        "VIN:%s\n" +
                        "Make:%s\n" +
                        "Year:%s\n" +
                        "Fuel Consumption:%s\n" +
                        "Kilometers:%s\n" +
                        "Engine:%s",
                        carInfo[1], carInfo[2], carInfo[3], carInfo[4], carInfo[5], carInfo[6]
                ));
                    Log.d(TAG, "onTextChanged: CHANGED");
                }
            }
        });
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeVehicleButton:
                Bundle bundle = new Bundle();
                bundle.putStringArray("carInfo", carInfo);
                getParentFragmentManager().setFragmentResult("dataFromChange", bundle);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}