package com.sivaram.populartvseries.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sivaram.populartvseries.R;
import com.sivaram.populartvseries.model.Cast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastHolder> {

    private Context context;
    private List<Cast> castList;

    public CastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_layout, parent, false);
        return new CastHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastHolder holder, int position) {
        if(castList.get(position).getPicUrlPath()!=null)
        {
            Picasso.get().load(castList.get(position).getPicUrlPath()).into(holder.profileImg);
        }
        else {
            holder.profileImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.user));
            holder.profileImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        holder.name.setText(castList.get(position).getName());
        holder.characterName.setText(castList.get(position).getCharacterName());
        holder.episodes.setText(castList.get(position).getEpisodes());
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class CastHolder extends RecyclerView.ViewHolder{

        private TextView name, characterName, episodes;
        private ImageView profileImg;
        public CastHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.original_name);
            characterName = itemView.findViewById(R.id.character_name);
            episodes = itemView.findViewById(R.id.episodes);
            profileImg = itemView.findViewById(R.id.cast_image);
        }
    }
}
