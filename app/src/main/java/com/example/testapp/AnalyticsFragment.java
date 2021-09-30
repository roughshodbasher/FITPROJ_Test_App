package com.example.testapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentAnalyticsBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.dateSet.setOnClickListener(this::onClick);
        List<String> aList = new ArrayList<String>();

        // TODO: send request to server for trips between two dates

        // receive response from server, display it on UI
        String response = "{\"message\": [{\"user_id\": 2, \"trip_id\": 1, \"veh_reg\": \"111KLM\", \"end\": \"CBD\", \"total_emi\": 64400, \"start\": \"Monash Clayton\", \"dist\": 23, \"date\": \"2021-09-20\"}, \n" +
                "{\"user_id\": 1, \"trip_id\": 2, \"veh_reg\": \"ABC123\", \"end\": \"Monash University Caulfied\", \"total_emi\": 23, \"start\": \"Monash University Clayton\", \"dist\": 9, \"date\": \"2021-09-28\"}], \"status\": 200}";
        ArrayList<String[]> tripData =  new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray message = jsonResponse.getJSONArray("message");
            for (int i = 0; i < message.length(); i++) {
                JSONObject element = message.getJSONObject(i);
                String [] trip = new String[4];
                trip[0] = String.valueOf(element.getInt("trip_id"));
                trip[1] = element.getString("date");
                trip[2] = String.valueOf(element.getInt("dist"));
                trip[3] = String.valueOf(element.getInt("total_emi"));
                tripData.add(trip);
            }
            //populate list to display trips on the screen
            for (int i = 0; i < tripData.size(); i++){
                String[] trip = tripData.get(i);
                aList.add("Trip " + trip[0] + "  " + trip[1] + "  " + trip[2]+ "km " + trip[3] +"g/KM");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        ArrayAdapter<String> aListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, aList);
        binding.tripsList.setAdapter(aListAdapter);

        //graph, display emission on the graph
        List<Entry> bList = new ArrayList<Entry>();
        for (int i = 0; i < tripData.size(); i++){
            bList.add(new Entry(i, Integer.parseInt(tripData.get(i)[3])));
        }

        LineDataSet setList = new LineDataSet(bList, "Emissions");
        setList.setAxisDependency(YAxis.AxisDependency.LEFT);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setList);
        LineData data = new LineData(dataSets);
        binding.emissionsGraph.setData(data);
        binding.emissionsGraph.invalidate();

    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateSet:
                //btnMonthYear(view);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
/*
    //https://www.youtube.com/watch?v=U95yWPKe5UY
    public void btnMonthYear(View view) {
        final Calendar calendar = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        int i = 1;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        builder.setActivatedMonth(calendar.get(calendar.MONTH))
                .setMinYear(1990)
                .setActivatedYear(calendar.get(calendar.YEAR))
                .setMaxYear(2030)
                .setMinMonth(Calendar.FEBRUARY)
                .setTitle("Select trading month")
                .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                .build()
                .show();
    }*/

}