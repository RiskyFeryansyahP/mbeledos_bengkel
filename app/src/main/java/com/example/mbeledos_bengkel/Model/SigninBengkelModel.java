package com.example.mbeledos_bengkel.Model;

public class SigninBengkelModel {

    private double latitude;

    private double longitude;

    private String phonenumber;

    private String nama_bengkel;

    public SigninBengkelModel(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getNama_bengkel() {
        return nama_bengkel;
    }
}
