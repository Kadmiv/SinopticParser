package com.gaijin.sinopticparser.views.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaijin.sinopticparser.R;
import com.gaijin.sinopticparser.parsers_classes.DaySite;

import java.util.ArrayList;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by Kachulyak Ivan.
 */
public class DayPagerAdapter extends FragmentPagerAdapter {

    static ArrayList<DaySite> data;

    public DayPagerAdapter(FragmentManager fm, ArrayList<DaySite> data) {
        super(fm);
        this.data = data;
    }

    public void reloadData(ArrayList<DaySite> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return ViewPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }


    @SuppressLint("ValidFragment")
    static class ViewPageFragment extends Fragment {

        static final String PAGER_KEY = "page_number";

        int pageNumber;

        static ViewPageFragment newInstance(int page) {
            ViewPageFragment pageFragment = new ViewPageFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(PAGER_KEY, page);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pageNumber = getArguments().getInt(PAGER_KEY);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.page_fragment, null);

            // Create recycler view for page, which contain all information for day weather
            RecyclerView dayView = (RecyclerView) view.findViewById(R.id.day_view);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext(), VERTICAL, false);
            dayView.setLayoutManager(manager);
            DayAdapter adapter = new DayAdapter(view.getContext(), data.get(pageNumber).getWeatherOnDay());
            dayView.setAdapter(adapter);
            return view;
        }
    }
}
