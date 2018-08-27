package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class VisualChart extends AppCompatActivity {

    public LineChartView chart;
    private LineChartData data;
    private ImageView back_btn;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;
    private TextView min, max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_chart);
        back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });
        chart = (LineChartView) findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        generateValues();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        min = (TextView) findViewById(R.id.min_value);
        max = (TextView)findViewById(R.id.max_value);

        DatabaseReference minbpm = FirebaseDatabase.getInstance().getReference().child("minbpm");
        minbpm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(Float.class).toString();
                min.setText(val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference maxbpm = FirebaseDatabase.getInstance().getReference().child("maxbpm");
        maxbpm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(Float.class).toString();
                max.setText(val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void generateValues() {
        DatabaseReference ref_bpm = FirebaseDatabase.getInstance().getReference().child("bpm");
        final ArrayList values = new ArrayList<PointValue>();
        final List<Line> lines = new ArrayList<Line>();

        List<AxisValue> timeAxis = new ArrayList<>();
        List<AxisValue> valAxis = new ArrayList<>();

        ref_bpm.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                double val = dataSnapshot.child("val").getValue(Double.class);
                float eval = (float) val;
                //valAxis.add(new AxisValue(eval));
                long time = dataSnapshot.child("timestamp").getValue(Long.class);
                float etime = (float) time;
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy. HH:mm");
                String strDate = df.format(time);
                //timeAxis.add(new AxisValue(etime).setLabel(strDate));
                values.add(new PointValue(etime, eval).setLabel(strDate));

                Line line = new Line(values);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
                line.setPointColor(ChartUtils.COLOR_BLUE);
                line.setColor(ChartUtils.COLOR_BLUE);
                lines.add(line);

                data = new LineChartData(lines);

                if (hasAxes) {
                    Axis axisX = new Axis().setMaxLabelChars(5);
                    ;
                    Axis axisY = new Axis().setHasLines(true);
                    //axisX.setValues(timeAxis);
                    //axisY.setValues(valAxis);
                    if (hasAxesNames) {
                        axisX.setName("Time");
                        axisY.setName("BPM");
                    }
                    data.setAxisXBottom(axisX);
                    data.setAxisYLeft(axisY);
                } else {
                    data.setAxisXBottom(null);
                    data.setAxisYLeft(null);
                }

                data.setBaseValue(Float.NEGATIVE_INFINITY);
                chart.setLineChartData(data);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                double val = dataSnapshot.child("val").getValue(Double.class);
                float eval = (float) val;
                long time = dataSnapshot.child("timestamp").getValue(Long.class);
                float etime = (float) time;
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy. HH:mm");
                String strDate = df.format(time);
                values.add(new PointValue(etime, eval).setLabel(strDate));

                Line line = new Line(values);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
                line.setPointColor(ChartUtils.COLOR_BLUE);
                lines.add(line);

                data = new LineChartData(lines);

                if (hasAxes) {
                    Axis axisX = new Axis();
                    Axis axisY = new Axis().setHasLines(true);
                    if (hasAxesNames) {
                        axisX.setName("Time");
                        axisY.setName("BPM");
                    }
                    data.setAxisXBottom(axisX);
                    data.setAxisYLeft(axisY);
                } else {
                    data.setAxisXBottom(null);
                    data.setAxisYLeft(null);
                }

                data.setBaseValue(Float.NEGATIVE_INFINITY);
                chart.setLineChartData(data);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(VisualChart.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    };

}

