package com.gaijin.sinopticparser.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaijin.sinopticparser.R;
import com.gaijin.sinopticparser.views.fragments.SeparateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.picasso.Picasso;


/**
 * Created by Kachulyak Ivan.
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    /*Metrics */
    private final List<String> METRICS_1 = Arrays.asList("C", "м/сек", "%", "мм");

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
        // Bind temperature view
        String temp1 = separateTime.getTemp_1();
        String temp2 = separateTime.getTemp_2();
        String temperatures = String.format("%s/%s %s", temp1, temp2, METRICS_1.get(0));
        holder.temp_1.setText(temperatures);

        //bind wind view
        String wind = separateTime.getWind();
        wind = String.format("%s %s", wind, METRICS_1.get(1));
        holder.wind.setText(wind);
        String windDirection = separateTime.getWindDirection();
        holder.setWindIcon(windDirection);

        //bind precipitation view
        String precipitation = separateTime.getPrecipitation();
        precipitation = String.format("%s %s", precipitation, METRICS_1.get(2));
        holder.precipitation.setText(precipitation);

        //bind humidity view
        String humidity = separateTime.getHumidity();
        humidity = String.format("%s %s", humidity, METRICS_1.get(2));
        holder.humidity.setText(humidity);

        //bind pressure view
        String pressure = separateTime.getAtmoPressure();
        pressure = String.format("%s %s", pressure, METRICS_1.get(3));
        holder.pressure.setText(pressure);

        //bind short description view
        //holder.shortDescription.setText(separateTime.getShortDescription());

        //bind image of weather view

        Picasso.with(context)
                .load(Uri.parse(dailyWeather.get(position).getImage()))
                //.resize(200, 200)
                .into(holder.weatherImage);


        //bind time view
        holder.time.setText(separateTime.getTime());

    }

    @Override
    public int getItemCount() {
        return dailyWeather.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.temp_1)
        TextView temp_1;
        @BindView(R.id.pressure)
        TextView pressure;
        @BindView(R.id.wind)
        TextView wind;
        @BindView(R.id.precipitation)
        TextView precipitation;
        @BindView(R.id.humidity)
        TextView humidity;
        @BindView(R.id.time)
        TextView time;
//        @BindView(R.id.short_description)
//        TextView shortDescription;
        @BindView(R.id.weather_image)
        ImageView weatherImage;
        @BindView(R.id.wind_icon)
        ImageView windIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * In this function we analyzed direction of wind, and change wind icon
         * @param windDirection - wind direction code s
         */
        public void setWindIcon(String windDirection) {

            int resId = 0;
            switch (windDirection.toLowerCase()) {
                case "n":
                    resId = R.drawable.wind_icon_n;
                    break;
                case "nw":
                    resId = R.drawable.wind_icon_nw;
                    break;
                case "ne":
                    resId = R.drawable.wind_icon_ne;
                    break;
                case "w":
                    resId = R.drawable.wind_icon_w;
                    break;
                case "sw":
                    resId = R.drawable.wind_icon_sw;
                    break;
                case "s":
                    resId = R.drawable.wind_icon_s;
                    break;
                case "se":
                    resId = R.drawable.wind_icon_se;
                    break;
                case "e":
                    resId = R.drawable.wind_icon_e;
                    break;
                default:
                    resId = R.drawable.wind_icon_e;
            }

            windIcon.setBackgroundResource(resId);
        }
    }
}
