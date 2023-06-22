package com.example.voiceacademyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.voiceacademyapp.R;
import com.example.voiceacademyapp.adapter.OnItemClickListener;
import com.example.voiceacademyapp.adapter.PodcastAdapter;
import com.example.voiceacademyapp.api.responds.GenericResponse;
import com.example.voiceacademyapp.model.PodcastChannel;
import com.example.voiceacademyapp.service.services.PodcastService;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class PodcastsActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private PodcastAdapter adapter;
    private PodcastService podcastService;
    private int idUser;
    private List<PodcastChannel> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcasts);
        recyclerView= findViewById(R.id.recyclerViewPodcast);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        this.context = PodcastsActivity.this;
        podcastService = new PodcastService(this);

        dataList = searchPodcast();

        PodcastAdapter adapter = new PodcastAdapter(dataList, this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Aquí puedes realizar la acción deseada cuando se hace clic en un elemento
                // del RecyclerView, como abrir una nueva vista o actividad
                // Puedes obtener el objeto correspondiente al elemento clickeado utilizando 'position'

                // Ejemplo:
                PodcastChannel clickedItem = dataList.get(position);
                // Realizar la acción deseada con el objeto clickeado...
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public List<PodcastChannel> searchPodcast() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getInt("id", 0);
        List<PodcastChannel> dataList =  new ArrayList<>();
        PodcastService.GetPodcasts(new PodcastService.PodcastServiceCallback() {
            @Override
            public void onSuccess(GenericResponse<PodcastChannel> response) {
                int code = response.getCode();
                if (code == HttpURLConnection.HTTP_FORBIDDEN) {
                    Toast.makeText(context, R.string.expired_session_label, Toast.LENGTH_SHORT).show();
                } else if (code == HttpURLConnection.HTTP_NOT_FOUND || code == HttpURLConnection.HTTP_BAD_REQUEST){
                    Toast.makeText(context, R.string.invalid_data_label, Toast.LENGTH_SHORT).show();
                } else {
                    dataList.clear();
                    dataList.addAll(response.getList());
                    adapter = new PodcastAdapter(dataList, context);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context, R.string.expired_session_label, Toast.LENGTH_SHORT).show();
            }
        });
        return dataList;
    }


}