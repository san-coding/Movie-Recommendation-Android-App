package com.example.movierecommendationapp.cardview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendationapp.MainActivity;
import com.example.movierecommendationapp.ProfileActivity;
import com.example.movierecommendationapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private ArrayList<CardMovieDetails> moviesArrayList;

    private RequestQueue mQueue;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_views);

        InitializeCardView();
    }

    private void InitializeCardView() {
        recyclerView=findViewById(R.id.recyclerViewCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesArrayList=new ArrayList<>();
        progressBar=(ProgressBar)findViewById(R.id.progress_bar) ;


        mQueue= Volley.newRequestQueue(this);
        progressBarVisible();
        jsonParse();
//        CreateDataForCards("Kabhi kushi kabhi gham","6.9","aaa aaa aaa aaa");
  //      CreateDataForCards("Kabhi kushi kabhi gham","6.9","aaa aaa aaa aaa");

        adapter=new CardViewAdapter(this,moviesArrayList);
        recyclerView.setAdapter(adapter);


    }
    public void progressBarVisible(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void progressBarInVisible(){
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    private void jsonParse() {
        String URL="https://movie-recommender-fastapi.herokuapp.com";
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray keys = response.names ();
                progressBarInVisible();
                for (int i = 0; i < keys.length(); i++) {
                    try {
                        String movie_name=(response.getJSONObject((String) keys.get(i)).getString("original_title"));
                        String description=(response.getJSONObject((String) keys.get(i)).getString("tagline"));
                        String ratings=(response.getJSONObject((String) keys.get(i)).getString("vote_average"));
                      //  String ratings=(response.getJSONObject((String) keys.get(i)).getString("vote_average"));


                        CreateDataForCards(movie_name,description,ratings);
                        adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

              //  Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void CreateDataForCards(String movieName, String ratings, String movieDescription) {
        CardMovieDetails movie= new CardMovieDetails(movieName,ratings,movieDescription);
        System.out.println(movieName+" "+ ratings+" "+movieDescription);
        moviesArrayList.add(movie);

    }

    //Header menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.five_movie_screen_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.myProfileItem:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.confirmNoItem:
                return true;
            case R.id.confirmYesItem:
                Intent logoutIntent = new Intent(this, MainActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}