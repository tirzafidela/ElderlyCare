package com.example.myfirstapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.MyApplication;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LowList extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_heart_disorder, container, false);
        //Retrieve Low Data
        ListView lolist = (ListView) v.findViewById(R.id.lowlist);
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("HRDisorder").child("Low");

        final List<HashMap<String, String>> listItems1 = new ArrayList<>();
        SimpleAdapter adapter1 = new SimpleAdapter(getActivity(), listItems1, R.layout.list_detail,
                new String[] {"First Line", "Second Line"},
                new int[] {R.id.text1, R.id.text2});

        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final HashMap<String, String> details = new HashMap<>();
                String val = (dataSnapshot.child("val").getValue(Long.class).toString());
                Long time = (dataSnapshot.child("timestamp").getValue(Long.class));
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy. HH:mm");
                String strDate = df.format(time);
                details.put("First Line", val);
                details.put("Second Line", strDate);
                listItems1.add(details);
                Log.d("TAG", val);
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
        return v;
    }

}
