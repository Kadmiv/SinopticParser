package com.gaijin.sinopticparser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaijin.sinopticparser.cards.City;
import com.gaijin.sinopticparser.cards.Searcher;
import com.gaijin.sinopticparser.components.Variables;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.view.KeyEvent.KEYCODE_ENTER;

/**
 * Created by Kachulyak Ivan.
 */
class SearchCityActivity extends AppCompatActivity implements TextWatcher,AdapterView.OnItemClickListener, Variables {


    @BindView(R.id.cityName)
    EditText cityName;
    @BindView(R.id.city_variants)
    ListView cityVariants;

    ArrayList<String> dataForReturn = null;


    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_city);
        ButterKnife.bind(this);
        cityName.addTextChangedListener(this);
        cityVariants.setOnItemClickListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Observable.just(cityName.getText().toString())
                //.map(string->JsoupParser.getTodayClass(string))
                .map(new Function<String, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(String cityName) throws Exception {
                        Searcher searcher = new Searcher();
                        return searcher.getSearchingResult(cityName);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> loadDataToView(data));

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void loadDataToView(ArrayList<String> data) {
        dataForReturn = (ArrayList<String>) data.clone();
        for (int i = 0; i < data.size(); i++) {
            City city = new City(data.get(i).split("\\|"));
            String information = String.format("%s %s", city.getCityName(), city.getCityRegion());
            data.set(i, information);
        }


        if (data != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
            cityVariants.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MyLog","City is  "+dataForReturn.get(position));
        Intent returnIntent = new Intent();
        returnIntent.putExtra("cityInfo",dataForReturn.get(position));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
