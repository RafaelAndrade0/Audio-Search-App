package com.example.rasilva.audioserch2.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rasilva.audioserch2.R;
import com.example.rasilva.audioserch2.api.IAudiosearchService;
import com.example.rasilva.audioserch2.api.JSONResponse;
import com.example.rasilva.audioserch2.model.Artist;
import com.lapism.searchview.Search;
import com.lapism.searchview.widget.SearchBar;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private SearchBar searchBar;
    private Retrofit retrofit;
    private ImageView artistThumb;
    private TextView artistTitle;
    private ImageView artistHeader;
    private ProgressBar progressBar;
    private TextView textDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        artistThumb = findViewById(R.id.imageThumb);
        artistTitle = findViewById(R.id.textTitulo);
        artistHeader = findViewById(R.id.imageHeader);
        textDescricao = findViewById(R.id.textDescricao);
        progressBar = findViewById(R.id.progressBar);

//        progressBar.setVisibility(View.GONE);

        // Configura retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchBar.setLogoIcon(R.drawable.ic_search_24dp);


        searchBar.setOnQueryTextListener(new Search.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(CharSequence query) {
                progressBar.setVisibility(View.VISIBLE);
                procurarArtista(query.toString());
                return true;
            }

            @Override
            public void onQueryTextChange(CharSequence newText) {

            }
        });
    }

    //Faz a busca na API pelo artista passado
    public void procurarArtista(String artista) {
        IAudiosearchService iAudiosearchService = retrofit.create(IAudiosearchService.class);
        Call<JSONResponse> listCall = iAudiosearchService.recuperarDadosArtista(artista);
        listCall.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                Artist[] artist = jsonResponse.getArtists();
                Log.d("Resposta", artist[0].getStrArtist());


                Picasso.get()
                        .load(artist[0].getStrArtistWideThumb())
                        .resize(400, 200)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder2)
                        .into(artistHeader);

                Picasso.get()
                        .load(artist[0].getStrArtistThumb())
                        .resize(100, 100)
                        .centerCrop()
                        .into(artistThumb, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) artistThumb.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                artistThumb.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                artistTitle.setText(artist[0].getStrArtist());
                textDescricao.setText(artist[0].getStrBiographyEN());

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Resposta", "Erro " + t.getMessage());
            }
        });
    }
}




