package com.example.devyankshaw.firebaserealtimedatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {
    TextView textViewArtistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    ListView listViewTracks;
    Button buttonAddTrack;

    DatabaseReference databaseTracks;

    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        textViewArtistName = findViewById(R.id.textViewArtistName);
        editTextTrackName = findViewById(R.id.editTextTrackName);
        seekBarRating = findViewById(R.id.seekBarRating);
        buttonAddTrack = findViewById(R.id.buttonAddTrack);

        listViewTracks = findViewById(R.id.listViewTracks);

        //Below code is used to receive the data i.e artist id and name from the MainActivity
        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        tracks = new ArrayList<>();

        textViewArtistName.setText(name);

        //Create reference to the database with root node as tracks and inner root node as the id of the artist that is clicked
        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //The below code is used to retrieve all the tracks for a particular artist and display them on the listView
        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tracks.clear();

                for(DataSnapshot trackSnapshot : dataSnapshot.getChildren()){
                    Track track = trackSnapshot.getValue(Track.class);
                    tracks.add(track);

                }
                TrackList trackListAdapter = new TrackList(AddTrackActivity.this, tracks);
                listViewTracks.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if(!TextUtils.isEmpty(trackName)){
            //Generates a unique id
            String id = databaseTracks.push().getKey();

            Track track = new Track(id, trackName, rating);

            databaseTracks.child(id).setValue(track);

            Toast.makeText(this, "Track saved successfully", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Track name should not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
