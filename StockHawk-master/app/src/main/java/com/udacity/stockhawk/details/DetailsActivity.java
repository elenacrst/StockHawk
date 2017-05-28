package com.udacity.stockhawk.details;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

public class DetailsActivity extends AppCompatActivity {

    public TextView symbolTextView;
    LineChart chart;

    public static final String EXTRA_SYMBOL = "symbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        symbolTextView = (TextView) findViewById(R.id.history_title);
        chart = (LineChart) findViewById(R.id.chart);

        if(getIntent().hasExtra(getString(R.string.extraSymbol))){
            String symbol = getIntent().getStringExtra(getString(R.string.extraSymbol));

            symbolTextView.setText(getString(R.string.price_history,symbol));
            symbolTextView.setContentDescription(getString(R.string.price_history,symbol));

            getHistory(symbol);
        }

    }

    private void getHistory(String symbol) {

        String history = getHistoryString(symbol);
        List<String[]> lines = getLines(history);


        ArrayList<Entry> entries = new ArrayList<>(lines.size());

        final ArrayList<Long> xAxisValues = new ArrayList<>();
        int xAxisPosition = 0;

        for(int i = lines.size()-1; i>=0; i--){
            String[] line = lines.get(i);
            xAxisValues.add(Long.valueOf(line[0]));
            xAxisPosition++;
            entries.add(
                    new Entry(xAxisPosition//time
                            ,Float.valueOf(line[1]))//value
            );
        }
        setupChart(symbol, entries, xAxisValues);


    }

    private List<String[]> getLines(String history) {
        CSVReader reader = new CSVReader(new StringReader(history));
        List<String[]> lines = null;
        try{
           lines = reader.readAll();

        }catch(IOException e){
            e.printStackTrace();
        }
        return lines;
    }

    private String getHistoryString(String symbol){
        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);
        String history = "";
        if(cursor.moveToFirst()){
            history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            cursor.close();
        }
        return history;
    }

    private void setupChart(String symbol, ArrayList<Entry> entries, final ArrayList<Long> xAxisValues){
        LineData lineData = new LineData(new LineDataSet(entries, symbol));
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Long dateLong = xAxisValues.get((int)value);
                Date date = new Date(dateLong);
                return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date).toString();
            }
        });
    }
}
