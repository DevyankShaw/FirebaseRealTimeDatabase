package com.example.devyankshaw.firebaserealtimedatabase;

//This is the model class for the artists
public class Artist {

    String artistId;
    String artistName;
    String artistGenre;

    //This blank constructors is used while reading the values
    public Artist(){

    }

    public Artist(String artistId, String artistName, String artistGenre) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistGenre = artistGenre;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistGenre() {
        return artistGenre;
    }
}
