package com.example.campusfinder;

public class CampusLocation {

    // 1. Variabel (Data yang disimpan)
    private int id;
    private boolean isFavorite;
    private String name;            // Nama lokasi
    private String description;     // Deskripsi singkat
    private int imageResourceId;    // ID Gambar di folder drawable
    private double latitude;        // Koordinat garis lintang
    private double longitude;       // Koordinat garis bujur

    // 2. Constructor untuk membuat object baru dari class
    public CampusLocation(int id, String name, String description, int imageResourceId, double lat, double lng, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.latitude = lat;
        this.longitude = lng;
        this.isFavorite = isFavorite;
    }

    // Constructor untuk seed data awal
    public CampusLocation(String name, String description, int imageResourceId, double lat, double lng) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.latitude = lat;
        this.longitude = lng;
        this.isFavorite = false;
    }

    // 3. Getter (mengambil data dari variabel privat di atas)
    public int getId() { return id; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getImageResourceId() {
        return imageResourceId;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}