package com.example.campusfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Data List : wadah data untuk tiap kategori
    private ArrayList<CampusLocation> listFakultas = new ArrayList<>();
    private ArrayList<CampusLocation> listFasilitas = new ArrayList<>();
    private ArrayList<CampusLocation> listOlahraga = new ArrayList<>();
    private ArrayList<CampusLocation> listSaved = new ArrayList<>();

    // Adapter
    private LocationAdapter adapterFakultas;
    private LocationAdapter adapterFasilitas;
    private LocationAdapter adapterOlahraga;
    private LocationAdapter adapterSaved;


    private androidx.appcompat.widget.SearchView searchView; // Komponen Search
    private android.widget.LinearLayout layoutSavedSection; // Untuk menyembunyikan judul jika kosong
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi Database
        dbHelper = new DatabaseHelper(this);
        layoutSavedSection = findViewById(R.id.layout_saved_section);

        // 2. Cek & Isi Data Awal
        seedInitialData();

        // RecyclerView & Simpan Adapter ke variabel global
        adapterFakultas = setupRecyclerView(R.id.rv_fakultas, listFakultas);
        adapterFasilitas = setupRecyclerView(R.id.rv_fasilitas_mahasiswa, listFasilitas);
        adapterOlahraga = setupRecyclerView(R.id.rv_fasilitas_olahraga, listOlahraga);
        adapterSaved = setupRecyclerView(R.id.rv_saved, listSaved);

        // Search
        setupSearchLogic();
    }

    // --- UPDATE DATA ---
    @Override
    protected void onResume() {
        super.onResume();
        refreshAllData();
    }

    private void refreshAllData() {
        // 1. Load data kategori
        loadDataFromDB();

        // 2. Load data favorit
        listSaved.clear();
        listSaved.addAll(dbHelper.getFavoriteLocations());

        // 3. Cek: Ada yang disimpan gak?
        if (listSaved.isEmpty()) {
            // Kalau kosong, sembunyikan judul & list "Lokasi Favorit"
            layoutSavedSection.setVisibility(View.GONE);
        } else {
            // Kalau ada isinya, ditampilkan
            layoutSavedSection.setVisibility(View.VISIBLE);
        }

        // 4. Update adapter
        adapterFakultas.notifyDataSetChanged();
        adapterFasilitas.notifyDataSetChanged();
        adapterOlahraga.notifyDataSetChanged();
        adapterSaved.notifyDataSetChanged();
    }

    // --- FUNGSI ISI DATA AWAL (SEEDING) ---
    private void seedInitialData() {
        if (!dbHelper.isDatabaseEmpty()) {
            return;
        }

        // Masukkan Fakultas
        dbHelper.addLocation(
                "FT - Fakultas Teknik",
                "Fakultas Teknik. Pusat pendidikan teknik dan kejuruan. Terletak di sayap utara kampus, memiliki berbagai bengkel dan laboratorium canggih.",
                R.drawable.img_ft, -7.768992339155265, 110.38818237340764, "fakultas");
        dbHelper.addLocation(
                "FMIPA - Fakultas Matematika dan Ilmu Pengetahuan Alam",
                "Fakultas Matematika dan Ilmu Pengetahuan Alam. Gedung identik dengan warna biru, pusat riset sains.",
                R.drawable.img_fmipa, -7.774374524760435, 110.38553959459105, "fakultas");
        dbHelper.addLocation(
                "FEB - Fakultas Ekonomi dan Bisnis",
                "Fakultas Ekonomi dan Bisnis. Pusat studi ekonomi, manajemen, dan akuntansi. Gedung modern di area barat kampus.",
                R.drawable.img_feb, -7.7730439764589985, 110.38657527981984, "fakultas");
        dbHelper.addLocation(
                "FISIPOL - Fakultas Ilmu Sosial dan Ilmu Politik",
                "Fakultas Ilmu Sosial dan Ilmu Politik. Pusat ilmu sosial dan politik...",
                R.drawable.img_fisip, -7.7770, 110.3855, "fakultas");
        dbHelper.addLocation(
                "FIKK - Fakultas Ilmu Keolahragaan dan Kesehatan",
                "Fakultas Ilmu Olahraga. Pusat ilmu olahraga...",
                R.drawable.img_fikk, -7.776382896649196, 110.38301086632883, "fakultas");
        dbHelper.addLocation(
                "FK - Fakultas Kedokteran",
                "Fakultas Kedokteran. Pusat ilmu Kedokteran...",
                R.drawable.img_fk, -7.775812395176926, 110.38816178636004, "fakultas");
        dbHelper.addLocation(
                "FIP - Fakultas Ilmu Pendidikan",
                "Fakultas Ilmu Pendidikan. Pusat ilmu Pendidikan",
                R.drawable.img_fipp, -7.774499012377017, 110.38749596470463, "fakultas");
        dbHelper.addLocation(
                "FP - Fakultas Psikologi",
                "Fakultas Psikologi. Pusat ilmu Psikologi",
                R.drawable.img_fipp, -7.77508715573885, 110.38808095680074, "fakultas");
        dbHelper.addLocation(
                "FH - Fakultas Hukum",
                "Fakultas Hukum. Pusat ilmu hukum",
                R.drawable.img_fh, -7.772993855434534, 110.38812942556771, "fakultas");
        dbHelper.addLocation(
                "FBSB - Fakultas Budaya, Seni, dan Bahasa",
                "Fakultas Budaya Seni dan Bahasa. Pusat ilmu budaya, seni, dan bahasa",
                R.drawable.img_fbsb, -7.772809281253626, 110.3834177331949, "fakultas");
        dbHelper.addLocation(
                "FV - Fakultas Vokasi",
                "Fakultas Vokasi Wates",
                R.drawable.img_fv_wts, -7.847955814344222, 110.16318319489572, "fakultas");

        // Masukkan Fasilitas
        dbHelper.addLocation(
                "Rektorat UNY",
                "Gedung pusat administrasi universitas. Ikon utama UNY dengan arsitektur megah.",
                R.drawable.img_rektorat, -7.77577, 110.38759, "fasilitas");
        dbHelper.addLocation(
                "Digital Library",
                "Gedung Digital Library UNY. Menyediakan ribuan koleksi buku fisik dan digital, serta ruang belajar nyaman.",
                R.drawable.img_digilab, -7.77529, 110.38706, "fasilitas");
        dbHelper.addLocation(
                "Masjid Al-Mujahidin",
                "Masjid kampus utama yang luas dan nyaman untuk beribadah.",
                R.drawable.img_masjid, -7.774285, 110.385166, "fasilitas");
        dbHelper.addLocation(
                "Koperasi Mahasiswa (KOPMA)",
                "Koperasi Mahasiswa.",
                R.drawable.img_kopma, -7.774285, 110.385166, "fasilitas");
        dbHelper.addLocation(
                "Food Court",
                "Pusat makanan dan minuman.",
                R.drawable.img_foodcourt, -7.774285, 110.385166, "fasilitas");
        dbHelper.addLocation(
                "Limuny",
                "Tempat internetan.",
                R.drawable.img_limuny, -7.774285, 110.385166, "fasilitas");
        dbHelper.addLocation(
                "Museum Pendidikan",
                "Museum sejarah pendidikan.",
                R.drawable.img_museum, -7.774285, 110.385166, "fasilitas");
        dbHelper.addLocation(
                "Layanan Bimbingan Konseling",
                "Unit Layanan Bimbingan dan Konseling Peduli UNY.",
                R.drawable.img_ulbk, -7.7753440376607434, 110.38871400680179, "fasilitas");
        dbHelper.addLocation(
                "Plaza UNY",
                "Menyediakan berbagai kebutuhan mulai dari alat elektronik, kebutuhan mahasiswa, hingga foodcourt.",
                R.drawable.img_plaza, -7.774621129515383, 110.38892350434026, "fasilitas");
        dbHelper.addLocation(
                "Unit Percetakan UNY",
                "Melayani percetakan buku, souvenir, dan lain-lain dengan harga bersahabat.",
                R.drawable.img_pres, -7.769257189976874, 110.38869256935389, "fasilitas");


        // Masukkan Olahraga
        dbHelper.addLocation(
                "GOR UNY",
                "Gedung Olahraga serbaguna standar internasional. Sering digunakan untuk konser dan wisuda.",
                R.drawable.img_gor, -7.77668, 110.38407, "olahraga");
        dbHelper.addLocation(
                "Stadion Atletik",
                "Lapangan sepak bola dan lintasan lari standar nasional.",
                R.drawable.img_stadion, -7.776556, 110.385306, "olahraga");
        dbHelper.addLocation(
                "Kolam Renang",
                "Fasilitas kolam renang standar olimpiade di area Wates/Kampus pusat.",
                R.drawable.img_kolamrenang, -7.775303, 110.382416, "olahraga");
        dbHelper.addLocation(
                "Lapangan Tenis",
                "Lapangan tenis outdoor dan indoor sesuai standar.",
                R.drawable.img_tenis, -7.775031404201957, 110.38517319206177, "olahraga");

    }

    // --- FUNGSI MEMUAT DATA DARI DB KE LIST ---
    private void loadDataFromDB() {
        listFakultas.clear();
        listFasilitas.clear();
        listOlahraga.clear();

        listFakultas.addAll(dbHelper.getLocationsByCategory("fakultas"));
        listFasilitas.addAll(dbHelper.getLocationsByCategory("fasilitas"));
        listOlahraga.addAll(dbHelper.getLocationsByCategory("olahraga"));
    }

    // --- FUNGSI PENGATURAN RECYCLERVIEW ---
    private LocationAdapter setupRecyclerView(int recyclerViewId, ArrayList<CampusLocation> dataList) {
        RecyclerView rv = findViewById(recyclerViewId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);

        LocationAdapter adapter = new LocationAdapter(dataList);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CampusLocation item) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("EXTRA_ID", item.getId());
                intent.putExtra("EXTRA_NAME", item.getName());
                intent.putExtra("EXTRA_DESC", item.getDescription());
                intent.putExtra("EXTRA_IMG", item.getImageResourceId());
                intent.putExtra("EXTRA_LAT", item.getLatitude());
                intent.putExtra("EXTRA_LONG", item.getLongitude());

                startActivity(intent);
            }
        });

        return adapter;
    }

    private void setupSearchLogic() {
        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter setiap user mengetik
                filterAllLists(newText);
                return true;
            }
        });
    }

    private void filterAllLists(String text) {
        ArrayList<CampusLocation> filterFakultas = new ArrayList<>();
        for (CampusLocation item : listFakultas) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filterFakultas.add(item);
            }
        }

        if (filterFakultas.isEmpty()) {

        }
        adapterFakultas.setFilteredList(filterFakultas);

        ArrayList<CampusLocation> filterFasilitas = new ArrayList<>();
        for (CampusLocation item : listFasilitas) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filterFasilitas.add(item);
            }
        }
        adapterFasilitas.setFilteredList(filterFasilitas);

        ArrayList<CampusLocation> filterOlahraga = new ArrayList<>();
        for (CampusLocation item : listOlahraga) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filterOlahraga.add(item);
            }
        }
        adapterOlahraga.setFilteredList(filterOlahraga);

        ArrayList<CampusLocation> filterSaved = new ArrayList<>();
        for (CampusLocation item : listSaved) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filterSaved.add(item);
            }
        }
        adapterSaved.setFilteredList(filterSaved);
    }
}
