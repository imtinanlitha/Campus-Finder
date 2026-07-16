package com.example.campusfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailActivity extends AppCompatActivity {

    // Variabel View
    private ImageView imgPhoto;
    private TextView tvName, tvDesc;
    private Button btnMaps;

    // Variabel data koordinat
    private double latitude;
    private double longitude;
    private String locationName;
    private android.widget.ImageButton btnBack;
    private FloatingActionButton btnFavorite;
    private DatabaseHelper dbHelper;
    private int locationId;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this); // Inisialisasi DB

        // 1. Menghubungkan variabel dengan ID di XML
        imgPhoto = findViewById(R.id.img_detail_photo);
        tvName = findViewById(R.id.tv_detail_name);
        tvDesc = findViewById(R.id.tv_detail_desc);
        btnMaps = findViewById(R.id.btn_open_maps);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnBack = findViewById(R.id.btn_back);

        // 2. Ambil data Intent
        getIncomingIntent();

        if (locationId != -1) {
            isFavorite = dbHelper.isLocationFavorite(locationId);
        }

        updateFavoriteIcon();

        // Klik Maps
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });

        // Klik like
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                dbHelper.updateFavoriteStatus(locationId, isFavorite);
                updateFavoriteIcon();

                String pesan = isFavorite ? "Disimpan ke Favorit" : "Dihapus dari Favorit";
                Toast.makeText(DetailActivity.this, pesan, Toast.LENGTH_SHORT).show();
            }
        });

        // Klik kembali
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // cara lain: onBackPressed();
            }
        });
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("EXTRA_ID")) {
            locationId = getIntent().getIntExtra("EXTRA_ID", -1);

            // Ambil data teks/gambar dari intent
            String name = getIntent().getStringExtra("EXTRA_NAME");
            String desc = getIntent().getStringExtra("EXTRA_DESC");
            int imgRes = getIntent().getIntExtra("EXTRA_IMG", 0);
            latitude = getIntent().getDoubleExtra("EXTRA_LAT", 0);
            longitude = getIntent().getDoubleExtra("EXTRA_LONG", 0);

            tvName.setText(name);
            tvDesc.setText(desc);
            imgPhoto.setImageResource(imgRes);

        } else {
            Toast.makeText(this, "Data lokasi tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_red);
            btnFavorite.setColorFilter(getResources().getColor(android.R.color.holo_red_light));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnFavorite.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        }
    }
    private void openGoogleMaps() {
        String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + locationName + ")";

        Uri mapUri = Uri.parse(geoUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

        // prioritas membuka aplikasi Google Maps
        mapIntent.setPackage("com.google.android.apps.maps");

        // Cek apakah user punya aplikasi maps
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // Jika tidak punya app Maps, buka lewat browser sebagai cadangan
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude));
            startActivity(webIntent);
        }
    }
}