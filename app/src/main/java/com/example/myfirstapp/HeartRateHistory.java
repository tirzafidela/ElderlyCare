package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeartRateHistory extends AppCompatActivity {

    private ArrayList<String> hrhistory = new ArrayList<>();
    private ArrayList<DateFormat> dateList = new ArrayList<>();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_heart_disorder);


        /*mListView = (ListView)findViewById(R.id.hrlist);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hrhistory);
        mListView.setAdapter(arrayAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("HRDisorder");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String val = dataSnapshot.getValue(String.class);
                hrhistory.add(val);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                arrayAdapter.notifyDataSetChanged();
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
        }); */

        //Retrieve Low Data
        ListView lolist = (ListView) findViewById(R.id.lowlist);
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("HRDisorder").child("Low");

        final List<HashMap<String, String>> listItems1 = new ArrayList<>();
        SimpleAdapter adapter1 = new SimpleAdapter(this, listItems1, R.layout.list_detail,
                new String[] {"First Line", "Second Line"},
                new int[] {R.id.text1, R.id.text2});

        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final HashMap<String, String> details = new HashMap<>();
                String val = (dataSnapshot.child("val").getValue(Long.class).toString());
                String time = (dataSnapshot.child("time").getValue(String.class));
                details.put("First Line", val);
                details.put("Second Line", time);
                listItems1.add(details);
                Log.d("TAG", val);
                //arrayDetail.add(val);
                //arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        lolist.setAdapter(adapter1);

    }
}
