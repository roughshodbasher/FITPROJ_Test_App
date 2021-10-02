package com.example.testapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentVehicleInfoBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleInfoFragment extends Fragment {

    private FragmentVehicleInfoBinding binding;
    private JSONObject vehicleInfoJSon;

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
        binding.newVehicleButton.setOnClickListener(this::onClick);
        binding.changeVehicleButton.setOnClickListener(this::onClick);
        String vehicleInfo = "{'data': {'method': 'get', 'command': 'all_vehicle_info', 'rego': 'ABC123'}, 'name': 'Database', 'type': 1}";
        try {
            Toast.makeText(getContext(), "made JSON", Toast.LENGTH_SHORT).show();
            JSONObject tempJSON = new JSONObject(vehicleInfo);
            vehicleInfoJSon = new JSONObject(tempJSON.getString("data"));
        } catch (JSONException e) {
            Toast.makeText(getContext(), "no make", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.newVehicleButton:
                NavHostFragment.findNavController(VehicleInfoFragment.this)
                        .navigate(R.id.action_VehicleInfoFragment_to_NewVehicleInfoFragment);
                break;
            case R.id.changeVehicleButton:
                try {
                    String text = vehicleInfoJSon.getString("rego");
                    binding.categoryText.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}