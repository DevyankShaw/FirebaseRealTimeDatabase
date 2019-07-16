package com.example.devyankshaw.firebaserealtimedatabase;

//This is the model class for tracks
public class Track {

    private String trackId;
    private String trackName;
    private int trackRating;

    //This blank constructor is used to retrive the tracks
    public Track(){

    }

    //This constructors intialise all the instance variables
    public Track(String trackId, String trackName, int trackRating) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackRating = trackRating;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public int getTrackRating() {
        return trackRating;
    }
}
