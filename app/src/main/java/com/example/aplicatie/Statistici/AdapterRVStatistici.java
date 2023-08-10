package com.example.aplicatie.Statistici;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AdapterRVStatistici extends RecyclerView.Adapter<AdapterRVStatistici.MyViewHolder> {
    private List<DataItem> dataList;

    public AdapterRVStatistici(List<DataItem> dataList) {
        this.dataList = dataList;
    }
    
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private PieChart pieChart;
        private TextView denumireGrafic;

        public MyViewHolder(@NonNull View view) {
            super(view);
            pieChart = view.findViewById(R.id.pieChart);
            denumireGrafic = view.findViewById(R.id.denumireGrafic);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_statistici, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataItem dataItem = dataList.get(position);
        PieData pieData = creeazaGrafic(dataItem, holder.pieChart);
        holder.pieChart.setData(pieData);
        holder.pieChart.setUsePercentValues(true);
        holder.pieChart.animateXY(2000, 2000);
        holder.pieChart.setDrawHoleEnabled(false);

        holder.denumireGrafic.setText(dataItem.getNumeGrafic());

        Legend legend = holder.pieChart.getLegend();
        legend.setTextSize(18f);
        legend.setWordWrapEnabled(true);

        holder.pieChart.invalidate();
    }

    private PieData creeazaGrafic(DataItem dataItem, PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(dataItem.getValoare1(), dataItem.getEticheta1()));
        entries.add(new PieEntry(dataItem.getValoare2(), dataItem.getEticheta2()));
        PieDataSet dataSet = new PieDataSet(entries, "");


        int[] colors = {Color.parseColor("#FF6384"), Color.parseColor("#36A2EB"), Color.parseColor("#FFCE56")};
        dataSet.setColors(colors);
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        return pieData;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
}
