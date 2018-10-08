package com.gaijin.sinopticparser.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaijin.sinopticparser.R;
import com.gaijin.sinopticparser.views.fragments.SeparateTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.picasso.Picasso;


/**
 * Created by Kachulyak Ivan.
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private Context context;
    private List<SeparateTime> dailyWeather;

    public DayAdapter(Context context, List<SeparateTime> dailyWeather) {
        this.context = context;
        this.dailyWeather = dailyWeather;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SeparateTime separateTime = dailyWeather.get(position);
        holder.temp_1.setText(separateTime.getTemp());
        holder.temp_2.setText(separateTime.getTemp_2());
        holder.humidity.setText(separateTime.getHumidity());
        holder.precipitation.setText(separateTime.getPrecipitation());
        holder.wind.setText(separateTime.getWind());
        holder.pressure.setText(separateTime.getAtmoPressure());
        holder.description.setText(separateTime.getShortDescription());
        Picasso.with(context)
                .load(Uri.parse(dailyWeather.get(position).getImage()))
                //.resize(200, 200)
                .into(holder.weatherImage);
        holder.time.setText(separateTime.getTime());

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
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.weather_image)
        ImageView weatherImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}