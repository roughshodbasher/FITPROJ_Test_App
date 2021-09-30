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

import org.json.JSONException;

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

        //populate list
        List<String> aList = new ArrayList<String>();
        /*for (int i = 0; i < 5; i++){
            aList.add("a");
        }*/
        aList.add("Trip 1   1/1/2021    24km    3168g/KM");
        aList.add("Trip 2   2/1/2021    18km    2376g/KM");
        aList.add("Trip 3   3/1/2021    10km    1320g/KM");
        aList.add("Trip 4   4/1/2021    33km    4356g/KM");
        aList.add("Trip 5   5/1/2021    22km    2904g/KM");
        ArrayAdapter<String> aListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, aList);
        binding.tripsList.setAdapter(aListAdapter);

        //graph
        List<Entry> bList = new ArrayList<Entry>();
        for (int i = 0; i < 5; i++){
            bList.add(new Entry(i, i));
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