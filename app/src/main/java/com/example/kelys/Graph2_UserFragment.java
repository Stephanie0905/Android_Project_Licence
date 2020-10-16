package com.example.kelys;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Graph2_UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Graph2_UserFragment extends Fragment {

    BarChart barChart;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Graph2_UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Graph2_UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Graph2_UserFragment newInstance(String param1, String param2) {
        Graph2_UserFragment fragment = new Graph2_UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_graph2__user, container, false);

        barChart = view.findViewById(R.id.bar_chart);

        ArrayList<BarEntry> myBarList = new ArrayList<>();
        myBarList.add(new BarEntry(4,5));
        myBarList.add(new BarEntry(5,10));
        myBarList.add(new BarEntry(6,12));
        myBarList.add(new BarEntry(7,13));
        myBarList.add(new BarEntry(8,15));

        BarDataSet barDataSet = new BarDataSet(myBarList,"Mon Graphe bar");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.setBackgroundColor(Color.TRANSPARENT);


        barChart.getDescription().setEnabled(false);
        barChart.animateY(3000);


        return view;

    }
}