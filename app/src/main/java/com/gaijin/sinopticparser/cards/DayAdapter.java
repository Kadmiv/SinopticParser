package com.gaijin.sinopticparser.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaijin.sinopticparser.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.picasso.Picasso;


/**
 * Created by Kachulyak Ivan.
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private Context context;
    private List<WeatherView> dailyWeather;

    public DayAdapter(Context context, List<WeatherView> dailyWeather) {
        this.context = context;
        this.dailyWeather = dailyWeather;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherView weatherView = dailyWeather.get(position);
        holder.temp_1.setText(weatherView.getTemp());
        holder.temp_2.setText(weatherView.getTemp_2());
        holder.humidity.setText(weatherView.getHumidity());
        holder.precipitation.setText(weatherView.getPrecipitation());
        holder.wind.setText(weatherView.getWind());
        holder.pressure.setText(weatherView.getAtmoPressure());
        holder.description.setText(weatherView.getShortDescription());
        Picasso.with(context)
                .load(R.drawable.cup_pic)
                //.resize(200, 200)
                .into(holder.weatherImage);

    }

    @Override
    public int getItemCount() {
        return dailyWeather.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.temp_1)
        TextView temp_1;
        @BindView(R.id.temp_2)
        TextView temp_2;
        @BindView(R.id.pressure)
        TextView pressure;
        @BindView(R.id.wind)
        TextView wind;
        @BindView(R.id.precipitation)
        TextView precipitation;
        @BindView(R.id.humidity)
        TextView humidity;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.weather_image)
        ImageView weatherImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
