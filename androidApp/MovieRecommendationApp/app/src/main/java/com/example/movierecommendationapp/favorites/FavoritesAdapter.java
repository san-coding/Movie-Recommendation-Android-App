package com.example.movierecommendationapp.favorites;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movierecommendationapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Card Adapter Class
    private Context context;
    private ArrayList<FavoritesDetails> movies;

    // Constructor
    public FavoritesAdapter(Context context, ArrayList<FavoritesDetails> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public FavoritesAdapter.FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        return new FavoritesAdapter.FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.FavoritesViewHolder holder, int position) {
        FavoritesDetails movie = movies.get(position);
        Glide.with(holder.movieImg).load(movies.get(position).getBackDropPath()).into(holder.movieImg);

        holder.setDetails(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder{
        private TextView movieName,movieRatings,movieDescription;
        private ImageView movieImg;
        private com.google.android.material.button.MaterialButton addToFavButton;

        FavoritesViewHolder(View itemView){
            super(itemView);
            movieImg=itemView.findViewById(R.id.movieImage);
            movieName=itemView.findViewById(R.id.movieName);
            movieRatings=itemView.findViewById(R.id.movieRatings);
            movieDescription=itemView.findViewById(R.id.movieDescription);
            addToFavButton = itemView.findViewById(R.id.addToFavButton);
        }

        void setDetails(FavoritesDetails details){
            movieName.setText(String.format(Locale.US,details.getMovieName()));
            movieRatings.setText(String.format(Locale.US,details.getRatings()));
            movieDescription.setText(String.format(Locale.US,details.getMovieDescription()));
            addToFavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences prefs = context.getSharedPreferences("LoggedIn",Context.MODE_PRIVATE);
                    String loggedInUser = prefs.getString("loggedin","NoUserLoggedIn");
                    Map<String,Object> newFavorite = new HashMap<>();
                    newFavorite.put("email",loggedInUser);
                    newFavorite.put("favMovieId",details.getMovieId());
                    db.collection("favoriteMovies").add(newFavorite).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Added "+details.getMovieName()+" as favorite", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Error DB", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}