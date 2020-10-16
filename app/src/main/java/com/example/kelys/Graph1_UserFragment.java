package com.example.kelys;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Graph1_UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Graph1_UserFragment extends Fragment {

    PieChart pieChart;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Graph1_UserFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Graph1_UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Graph1_UserFragment newInstance(String param1, String param2) {
        Graph1_UserFragment fragment = new Graph1_UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_graph1__user, container, false);

        pieChart = view.findViewById(R.id.pie_chart);

        ArrayList<PieEntry> myPieData = new ArrayList<>();
        myPieData.add(new PieEntry(34,"A"));
        myPieData.add(new PieEntry(40,"B"));
        myPieData.add(new PieEntry(45,"C"));
        myPieData.add(new PieEntry(70,"D"));

        PieDataSet pieDataSet = new PieDataSet(myPieData,"Mon Graphe Camember");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(25f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setBackgroundColor(Color.TRANSPARENT);

        pieChart.setDrawSlicesUnderHole(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateXY(2000,2000);

        return view;
    }

}