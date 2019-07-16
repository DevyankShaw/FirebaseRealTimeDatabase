package com.example.devyankshaw.firebaserealtimedatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";

    EditText editTextName;
    Button buttonAdd;
    Spinner spinnerGenres;
    AlertDialog alertDialog;

    DatabaseReference databaseArtists;

    ListView listViewArtists;
    List<Artist> artistList, artistListReverse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //By this we create a reference to the database and giving artists as the root node for the json
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        editTextName = findViewById(R.id.editTextName);
        buttonAdd = findViewById(R.id.buttonAddArtist);
        spinnerGenres = findViewById(R.id.spinnerGenres);

        listViewArtists = findViewById(R.id.listViewArtist);

        artistList = new ArrayList<>();
        artistListReverse = new ArrayList<>();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistList.get(position);

                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);
                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());
                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = artistList.get(position);

                showUpdateDialog(artist.artistId, artist.artistName);

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //The below code is used to retrieve all the artists and display them on the listView
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //This onDataChange() is executed everytime whenever we change anything inside the database
                artistList.clear();
                //artistListReverse.clear();

                //By this for loop we fetch the data or artist objects from the firebase database
                for (DataSnapshot artistSnapShot: dataSnapshot.getChildren()){
                    Artist artist = artistSnapShot.getValue(Artist.class);

                    artistList.add(artist);
                }

                //Here artistListReverse stores the latest artist to oldest artist
                /*for(int i=artistList.size()-1; i>=0; i--){
                    artistListReverse.add(artistList.get(i));
                }*/

                ArtistList adapter = new ArtistList(MainActivity.this, artistList);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //This onCalled() is executed when there is some error
            }
        });
    }

    private void showUpdateDialog(final String artistId, String artistName){

        //Creating an object of AlertDialog builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        //Inflating the layout for alert dialog and setting it to the alert dialog
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        //Getting textView,editText and button from the layout i.e creating the references
        final Spinner spinnerGenres = dialogView.findViewById(R.id.spinnerUpdateGenre);
        final EditText editTextName = dialogView.findViewById(R.id.editTextUpdateName);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if(TextUtils.isEmpty(name)){
                    editTextName.setError("Name required");
                    return;
                }

                updateArtist(artistId, name, genre);
                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistId);
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setTitle("Updating Artist: " + artistName);

        //Creating alert dialog using AlertDialog Builder and then displaying it
        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }
    private void deleteArtist(String artistId){
        //Getting the references of the artists and its tracks in order to delete them
        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artists").child(artistId);
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        //Removing the data of artists and its tracks
        drArtist.removeValue();
        drTracks.removeValue();

        Toast.makeText(this, "Artist Deleted", Toast.LENGTH_SHORT).show();
    }


    private boolean updateArtist(String id, String name, String genre){

        //Getting the reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);

        //Passing the values to the Artist model in order to update
        Artist artist = new Artist(id, name, genre);

        //Setting the updated artist object to the database
        databaseReference.setValue(artist);

        Toast.makeText(this, "Artist Updated Successfully", Toast.LENGTH_LONG).show();

        return true;
    }

    public void addArtist(){
        String name = editTextName.getText().toString();
        String genre = spinnerGenres.getSelectedItem().toString();

        //If name is not empty then if blocks execute i.e we store the artist details to the database
        if(!TextUtils.isEmpty(name)){
            //By this we create a unique id using push() and storing the the unique id to the id using getkey()
           String id = databaseArtists.push().getKey();

           //Create the artist
           Artist artist = new Artist(id, name, genre);

           //In order to store the value to the firebase database we use setValue() inside that particular id
           databaseArtists.child(id).setValue(artist);

           Toast.makeText(this, "Artist added", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_SHORT).show();
        }
    }
}
