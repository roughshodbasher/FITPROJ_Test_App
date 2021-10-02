package com.example.testapp;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    final Calendar cal = Calendar.getInstance();
    private int year = cal.get(Calendar.YEAR);
    private int month = cal.get(Calendar.MONTH);
    private int date = cal.get(Calendar.DAY_OF_MONTH);
    private List<String> aList = new ArrayList<String>();
    private List<Entry> bList = new ArrayList<Entry>();
    private ArrayList<String[]> tripData =  new ArrayList<>();
    private ArrayAdapter<String> aListAdapter;



    private int totalEmission = 0;
    private int totalkm = 0;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
        //binding.dateSet.setOnClickListener(this::onClick);
        binding.startInput.setOnClickListener(this::onClick);
        binding.endInput.setOnClickListener(this::onClick);
        binding.getBtn.setOnClickListener(this::onClick);
        aListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, aList);
        binding.tripsList.setAdapter(aListAdapter);
    }

    private void onClick(View view) {
        switch (view.getId()) {
            //case R.id.dateSet:
                //btnMonthYear(view);
                //break;
            case R.id.startInput:
                btnDate(view, "Select start date", binding.startInput);
                break;
            case R.id.endInput:
                btnDate(view, "Select end date", binding.endInput);
                break;
            case R.id.getBtn:
                getTripInfo();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void btnDate(View view, String title, TextView textView){
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                cal.set(selectedYear, selectedMonth, selectedDay);
                textView.setText(dateFormat.format(cal.getTime()));
            }
        }, year, month, date);
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    private void getTripInfo(){
        // check if user has entered both start and end dates, if not, show a toast and return
        String startDate = (String) binding.startInput.getText();
        String endDate = (String) binding.endInput.getText();
        try {
            dateFormat.parse(startDate);
            dateFormat.parse(endDate);
        } catch (ParseException e) {
            Toast errorToast = Toast.makeText(this.getContext(),"Please enter start and end dates", Toast.LENGTH_SHORT);
            errorToast.show();
            return;
        }

        // send request to server for trips between two dates

//        String ip = "194.193.148.240";
//        Integer port = 1024;
//        JSONObject json = new JSONObject();
//        JSONObject json_data = new JSONObject();
//        try {
//            json_data.put("type", 0);
//            json_data.put("start", "2000-01-01");
//            json_data.put("end","3000-12-31");
//            json.put("data", json_data);
//            json.put("type", 1);
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//        asyncCommunication c = new asyncCommunication(ip,port,json);
//        Thread thread = new Thread(c);
//        thread.start();
//        while (!c.finished()) {
//            continue;
//        }
//        Log.d(TAG, c.getServerResponse());
        // receive response from server, display it on UI
//        String response = "{\"message\": [{\"user_id\": 2, \"trip_id\": 1, \"veh_reg\": \"111KLM\", \"end\": \"CBD\", \"total_emi\": 64400, \"start\": \"Monash Clayton\", \"dist\": 23, \"date\": \"2021-09-20\"}, \n" +
//                "{\"user_id\": 1, \"trip_id\": 2, \"veh_reg\": \"ABC123\", \"end\": \"Monash University Caulfied\", \"total_emi\": 23, \"start\": \"Monash University Clayton\", \"dist\": 9, \"date\": \"2021-09-28\"}], \"status\": 200}";
//        String response = c.getServerResponse();
        String response = "{\"status\": 200, \"message\": [{\"trip_id\": 19, \"end\": \"Brunswick\", \"user_id\": 2, \"veh_reg\": \"7WNUVQ\", \"total_emi\": 31404, \"dist\": 12, \"start\": \"East Melbourne\", \"date\": \"2021-08-01\"}, {\"trip_id\": 25, \"end\": \"St Kilda\", \"user_id\": 2, \"veh_reg\": \"89R57P\", \"total_emi\": 7740, \"dist\": 30, \"start\": \"Carlton\", \"date\": \"2021-08-07\"}, {\"trip_id\": 3, \"end\": \"Carlton\", \"user_id\": 2, \"veh_reg\": \"HBTTH4\", \"total_emi\": 6615, \"dist\": 21, \"start\": \"East Melbourne\", \"date\": \"2021-08-12\"}, {\"trip_id\": 6, \"end\": \"South Melbourne\", \"user_id\": 1, \"veh_reg\": \"XLJ2O2\", \"total_emi\": 5566, \"dist\": 22, \"start\": \"Carlton\", \"date\": \"2021-08-17\"}, {\"trip_id\": 12, \"end\": \"Brunswick\", \"user_id\": 1, \"veh_reg\": \"UZ2714\", \"total_emi\": 35412, \"dist\": 13, \"start\": \"Clayton\", \"date\": \"2021-08-19\"}, {\"trip_id\": 10, \"end\": \"Richmond\", \"user_id\": 2, \"veh_reg\": \"XLJ2O2\", \"total_emi\": 3289, \"dist\": 13, \"start\": \"Footscray\", \"date\": \"2021-08-20\"}, {\"trip_id\": 9, \"end\": \"South Yarra\", \"user_id\": 1, \"veh_reg\": \"1FVHHR\", \"total_emi\": 75300, \"dist\": 30, \"start\": \"East Melbourne\", \"date\": \"2021-08-21\"}, {\"trip_id\": 23, \"end\": \"South Melbourne\", \"user_id\": 2, \"veh_reg\": \"ELJTNZ\", \"total_emi\": 42200, \"dist\": 10, \"start\": \"Clayton\", \"date\": \"2021-08-21\"}, {\"trip_id\": 30, \"end\": \"Richmond\", \"user_id\": 2, \"veh_reg\": \"9FHQM9\", \"total_emi\": 10528, \"dist\": 28, \"start\": \"East Melbourne\", \"date\": \"2021-08-22\"}, {\"trip_id\": 31, \"end\": \"South Yarra\", \"user_id\": 1, \"veh_reg\": \"8X89BV\", \"total_emi\": 12030, \"dist\": 30, \"start\": \"Carneigie\", \"date\": \"2021-08-25\"}, {\"trip_id\": 18, \"end\": \"South Melbourne\", \"user_id\": 1, \"veh_reg\": \"LU2KK2\", \"total_emi\": 124152, \"dist\": 28, \"start\": \"South Melbourne\", \"date\": \"2021-08-26\"}, {\"trip_id\": 16, \"end\": \"Brunswick\", \"user_id\": 1, \"veh_reg\": \"WCE3SB\", \"total_emi\": 44232, \"dist\": 12, \"start\": \"Clayton\", \"date\": \"2021-08-27\"}, {\"trip_id\": 29, \"end\": \"South Melbourne\", \"user_id\": 2, \"veh_reg\": \"EFG123\", \"total_emi\": 64400, \"dist\": 23, \"start\": \"St Kilda\", \"date\": \"2021-08-27\"}, {\"trip_id\": 27, \"end\": \"South Melbourne\", \"user_id\": 1, \"veh_reg\": \"JIC1QZ\", \"total_emi\": 106416, \"dist\": 24, \"start\": \"Fitzroy\", \"date\": \"2021-08-30\"}, {\"trip_id\": 20, \"end\": \"Brunswick\", \"user_id\": 1, \"veh_reg\": \"9NLNHB\", \"total_emi\": 42730, \"dist\": 10, \"start\": \"St Kilda\", \"date\": \"2021-09-05\"}, {\"trip_id\": 7, \"end\": \"Carlton\", \"user_id\": 1, \"veh_reg\": \"0IGE14\", \"total_emi\": 118260, \"dist\": 27, \"start\": \"Footscray\", \"date\": \"2021-09-08\"}, {\"trip_id\": 15, \"end\": \"Footscray\", \"user_id\": 1, \"veh_reg\": \"BCCS9B\", \"total_emi\": 6475, \"dist\": 25, \"start\": \"Carlton\", \"date\": \"2021-09-09\"}, {\"trip_id\": 26, \"end\": \"Fitzroy\", \"user_id\": 2, \"veh_reg\": \"RQDSEO\", \"total_emi\": 75378, \"dist\": 17, \"start\": \"Richmond\", \"date\": \"2021-09-13\"}, {\"trip_id\": 13, \"end\": \"St Kilda\", \"user_id\": 2, \"veh_reg\": \"YDFXYV\", \"total_emi\": 42306, \"dist\": 11, \"start\": \"Brunswick\", \"date\": \"2021-09-14\"}, {\"trip_id\": 1, \"end\": \"CBD\", \"user_id\": 2, \"veh_reg\": \"111KLM\", \"total_emi\": 64400, \"dist\": 23, \"start\": \"Monash Clayton\", \"date\": \"2021-09-20\"}, {\"trip_id\": 11, \"end\": \"South Yarra\", \"user_id\": 1, \"veh_reg\": \"SXW75T\", \"total_emi\": 5355, \"dist\": 17, \"start\": \"South Yarra\", \"date\": \"2021-09-21\"}, {\"trip_id\": 14, \"end\": \"East Melbourne\", \"user_id\": 2, \"veh_reg\": \"HAG2G4\", \"total_emi\": 3173, \"dist\": 19, \"start\": \"Clayton\", \"date\": \"2021-09-21\"}, {\"trip_id\": 24, \"end\": \"Carneigie\", \"user_id\": 2, \"veh_reg\": \"6MM95B\", \"total_emi\": 8820, \"dist\": 28, \"start\": \"Brunswick\", \"date\": \"2021-09-22\"}, {\"trip_id\": 2, \"end\": \"Monash University Caulfied\", \"user_id\": 1, \"veh_reg\": \"ABC123\", \"total_emi\": 18036, \"dist\": 9, \"start\": \"Monash University Clayton\", \"date\": \"2021-09-28\"}, {\"trip_id\": 28, \"end\": \"Richmond\", \"user_id\": 2, \"veh_reg\": \"OZQSWV\", \"total_emi\": 76384, \"dist\": 22, \"start\": \"St Kilda\", \"date\": \"2021-09-30\"}, {\"trip_id\": 4, \"end\": \"South Yarra\", \"user_id\": 1, \"veh_reg\": \"QSF8OH\", \"total_emi\": 9768, \"dist\": 24, \"start\": \"South Melbourne\", \"date\": \"2021-10-04\"}, {\"trip_id\": 22, \"end\": \"St Kilda\", \"user_id\": 2, \"veh_reg\": \"QB7J95\", \"total_emi\": 5670, \"dist\": 18, \"start\": \"Footscray\", \"date\": \"2021-10-04\"}, {\"trip_id\": 17, \"end\": \"South Yarra\", \"user_id\": 2, \"veh_reg\": \"PIT28I\", \"total_emi\": 48072, \"dist\": 12, \"start\": \"Richmond\", \"date\": \"2021-10-10\"}, {\"trip_id\": 21, \"end\": \"Footscray\", \"user_id\": 1, \"veh_reg\": \"GXKIJ0\", \"total_emi\": 3757, \"dist\": 13, \"start\": \"Fitzroy\", \"date\": \"2021-10-11\"}, {\"trip_id\": 8, \"end\": \"East Melbourne\", \"user_id\": 2, \"veh_reg\": \"889WH5\", \"total_emi\": 5934, \"dist\": 23, \"start\": \"St Kilda\", \"date\": \"2021-10-22\"}, {\"trip_id\": 32, \"end\": \"Footscray\", \"user_id\": 1, \"veh_reg\": \"2WA6SC\", \"total_emi\": 113880, \"dist\": 26, \"start\": \"Fitzroy\", \"date\": \"2021-10-25\"}, {\"trip_id\": 5, \"end\": \"Footscray\", \"user_id\": 2, \"veh_reg\": \"7LKP4J\", \"total_emi\": 80120, \"dist\": 20, \"start\": \"Fitzroy\", \"date\": \"2021-10-28\"}]}";

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray message = jsonResponse.getJSONArray("message");
            tripData.clear();
            for (int i = 0; i < message.length(); i++) {
                JSONObject element = message.getJSONObject(i);
                String [] trip = new String[4];
                trip[0] = String.valueOf(element.getInt("trip_id"));
                trip[1] = element.getString("date");
                int dist = element.getInt("dist");
                int emission = element.getInt("total_emi");
                totalEmission += emission;
                totalkm += dist;
                trip[2] = String.valueOf(dist);
                trip[3] = String.valueOf(emission);
                tripData.add(trip);
            }
            aList.clear();

            //populate list to display trips on the screen
            for (int i = 0; i < tripData.size(); i++){
                String[] trip = tripData.get(i);
                aList.add("Trip " + trip[0] + "  " + trip[1] + "  " + trip[2]+ "km " + trip[3] +"g/KM");
            }
            aListAdapter.notifyDataSetChanged();

            //graph, display emission on the graph
            // clear bList first, then add in the new emission
            bList.clear();
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

            float average;
            if (totalkm == 0) {
                average = 0;
            }else{
                average = totalEmission/totalkm;
            }
            String averageEmission = "A total of " + totalkm + " km travelled, and " + totalEmission + " emission between " +
                    startDate + " and " + endDate +
                    ". Average emission per km is " + average + ".";
            binding.analysisId.setText(averageEmission);

        } catch (JSONException e) {
            e.printStackTrace();
        }

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