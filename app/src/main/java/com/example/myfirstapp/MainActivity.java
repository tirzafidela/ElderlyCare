package com.example.myfirstapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.support.design.widget.FloatingActionButton;

import com.example.MyApplication;
import com.gigamole.library.PulseView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView mValueView, fallhist, heartlist, mFall, mChart, stat, connect;
    PulseView pulseView;

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private String TAG;
    FloatingActionButton btnCall;

    NotificationCompat.Builder notif, notify;
    private static final int uniqueID = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pulseView = (PulseView) findViewById(R.id.pv);
        mValueView = (TextView) findViewById(R.id.bpm_value);
        btnCall = (FloatingActionButton) findViewById(R.id.fab);
        stat = (TextView) findViewById(R.id.detailstat);
        connect = (TextView)findViewById(R.id.statusbar);

        notif = new NotificationCompat.Builder(this);
        notif.setAutoCancel(true);

        notify = new NotificationCompat.Builder(MyApplication.getAppContext());
        notify.setAutoCancel(true);

        DatabaseReference healthstat = FirebaseDatabase.getInstance().getReference().child("danger");
        healthstat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class) == true) {
                    stat.setText(" Not Healthy");
                } else {
                    stat.setText(" Safe and Healthy");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference con = FirebaseDatabase.getInstance().getReference().child("connect");
        con.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class) == true) {
                    connect.setText(" Connected");
                } else {
                    connect.setText(" Offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref_bpm = FirebaseDatabase.getInstance().getReference().child("bpm");
        ref_bpm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String val = (ds.child("val").getValue(Double.class).toString());
                    mValueView.setText(val);
                    Log.d("TAG", val);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "LoadCancelled", databaseError.toException());
            }
        });

        pulseView.startPulse();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //HRDisorderNotification
        DatabaseReference ref_hr = FirebaseDatabase.getInstance().getReference().child("HRDisorder");
        ref_hr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sendHRDisorderNotif();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "LoadCancelled", databaseError.toException());
            }
        });

        //See details
        fallhist = (TextView) findViewById(R.id.fall_history);
        heartlist = (TextView) findViewById(R.id.heart_history);
        mChart = (TextView)findViewById(R.id.seegraph);

        fallhist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), FallHistory.class);
                startActivity(intent2);
            }
        });

        heartlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(v.getContext(), HeartDisorder.class);
                startActivity(intent3);
            }
        });

        mChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(v.getContext(), VisualChart.class);
                startActivity(intent4);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:082291671881")));
            }
        });

        //Retrieve Fall Data
        mFall = (TextView)findViewById(R.id.fallsum);
        DatabaseReference ref_fallsum = FirebaseDatabase.getInstance().getReference().child("fallsum");
        ref_fallsum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sumfall = dataSnapshot.getValue(Integer.class).toString();
                mFall.setText(sumfall);
                sendFallNotif();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendFallNotif() {
        notif.setSmallIcon(R.mipmap.ic_launcher);
        notif.setTicker("Danger situation detected.");
        notif.setWhen(System.currentTimeMillis());
        notif.setContentTitle("ALERT!");
        notif.setContentText("A Fall is detected");
        notif.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intentt = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setContentIntent(pendingIntent);

        NotificationManager nm1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm1.notify(uniqueID, notif.build());
    }

    public void sendHRDisorderNotif() {
        notify.setSmallIcon(R.mipmap.ic_launcher);
        notify.setTicker("Danger situation detected.");
        notify.setWhen(System.currentTimeMillis());
        notify.setContentTitle("ALERT!");
        notify.setContentText("Heart Rate is not in normal condition.");
        notify.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intentt = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentt, PendingIntent.FLAG_UPDATE_CURRENT);
        notify.setContentIntent(pendingIntent);

        NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm2.notify(123, notify.build());
    }
}
