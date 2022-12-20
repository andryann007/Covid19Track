package com.example.covid19tracking.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid19tracking.R;
import com.example.covid19tracking.adapter.CountryFlagAdapter;
import com.example.covid19tracking.api.ApiClient;
import com.example.covid19tracking.api.ApiService;
import com.example.covid19tracking.api.ContinentResult;
import com.example.covid19tracking.api.CountryResult;
import com.example.covid19tracking.databinding.ActivityContinentDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContinentDetailActivity extends AppCompatActivity {

    private ActivityContinentDetailBinding binding;

    private ApiService apiService;

    private String continent;

    private String searchType = null;

    private String sortType = null;

    private String sortBy = null;

    private ArrayList<CountryResult> globalDataResults;

    private CountryFlagAdapter countryFlagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContinentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        continent = getIntent().getStringExtra("continent_name");

        binding.toolbar.setOnClickListener(v-> onBackPressed());

        setContinentDetail();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void setContinentDetail(){
        String twoDaysAgo = "false";
        String yesterday = "false";
        String allowNull = "false";
        String strict = "false";
        binding.loadingDetails.setVisibility(View.VISIBLE);

        Call<ContinentResult> call = apiService.getContinentsDetail(continent, yesterday, twoDaysAgo,
                strict, allowNull);
        call.enqueue(new Callback<ContinentResult>() {
            @Override
            public void onResponse(@NonNull Call<ContinentResult> call, @NonNull Response<ContinentResult> response) {
                if(response.body() != null){
                    binding.loadingDetails.setVisibility(View.GONE);
                    binding.toolbar.setTitle(response.body().getContinent() + " Continent Detail");

                    setTitleText(binding.textContinentName, response.body().getContinent());

                    long timeStamp = response.body().getUpdated();
                    Date date = new Date(timeStamp);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    String mDate = dateFormat.format(date);
                    binding.textContinentUpdatedTime.setText(mDate);

                    long population = response.body().getPopulation();
                    String mPopulation = String.format(Locale.US, "%,d",population).replace(',','.');
                    setPopulationText(binding.textContinentPopulation, mPopulation);

                    int totalCase = response.body().getContinentCases();
                    String mTotalCase = String.format(Locale.US, "%,d",totalCase).replace(',','.');
                    setCaseText(binding.textContinentTotalCase, mTotalCase);

                    int todayCase = response.body().getTodayCases();
                    String mTodayCase = String.format(Locale.US, "%,d",todayCase).replace(',','.');
                    setCaseText(binding.textContinentTodayCase, mTodayCase);

                    int recoveredCase = response.body().getRecovered();
                    String mRecoveredCase = String.format(Locale.US, "%,d",recoveredCase).replace(',','.');
                    setCaseText(binding.textContinentRecovered, mRecoveredCase);

                    int todayRecovered = response.body().getTodayRecovered();
                    String mTodayRecovered = String.format(Locale.US, "%,d",todayRecovered).replace(',','.');
                    setCaseText(binding.textContinentTodayRecovered, mTodayRecovered);

                    int deathCase = response.body().getDeaths();
                    String mDeathCase = String.format(Locale.US, "%,d",deathCase).replace(',','.');
                    setCaseText(binding.textContinentDeath, mDeathCase);

                    int todayDeaths = response.body().getTodayDeaths();
                    String mTodayDeaths = String.format(Locale.US, "%,d",todayDeaths).replace(',','.');
                    setCaseText(binding.textContinentTodayDeath, mTodayDeaths);

                    int activeCase = response.body().getActive();
                    String mActiveCase = String.format(Locale.US, "%,d",activeCase).replace(',','.');
                    setCaseText(binding.textContinentActiveCase, mActiveCase);

                    String[] countryName = response.body().getCountries();
                    String mCountryName = Arrays.toString(countryName).replace("[", "")
                            .replace("]", "");
                    setCountryNameText(binding.textCountriesName, mCountryName);
                    binding.titleContinentCountriesList.setVisibility(View.GONE);
                    binding.textCountriesName.setVisibility(View.GONE);

                    binding.titlePercentageContinent.setText(String.format("%s COVID-19 Percentage", response.body().getContinent()));

                    double percentActiveCase = ((double) activeCase / (double) totalCase) * 100;
                    double percentDeathCase = ((double) deathCase / (double) totalCase) * 100;
                    double percentRecoveredCase = ((double) recoveredCase / (double) totalCase) * 100;

                    String mPercentActiveCase = String.format(Locale.US, "%.2f", percentActiveCase);
                    String mPercentDeathCase = String.format(Locale.US, "%.2f", percentDeathCase);
                    String mPercentRecoveredCase = String.format(Locale.US, "%.2f", percentRecoveredCase);

                    setPercentText(binding.percentContinentActiveCases, mPercentActiveCase);
                    setPercentText(binding.percentContinentDeath, mPercentDeathCase);
                    setPercentText(binding.percentContinentRecovered, mPercentRecoveredCase);

                    getCountriesFlags(mCountryName);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContinentResult> call, @NonNull Throwable t) {

            }
        });
    }

    private void getCountriesFlags(String queryCountryList){
        String twoDaysAgo = "false";
        String yesterday = "false";
        String allowNull = "false";

        RecyclerView rvCountryFlag = findViewById(R.id.rvCountriesFlag);

        binding.loadingDetails.setVisibility(View.VISIBLE);

        Call<ArrayList<CountryResult>> call = apiService.getCountriesFlag(queryCountryList, yesterday, twoDaysAgo, allowNull);
        call.enqueue(new Callback<ArrayList<CountryResult>>(){

            @Override
            public void onResponse(@NonNull Call<ArrayList<CountryResult>> call, @NonNull Response<ArrayList<CountryResult>> response) {
                if(response.isSuccessful()){
                    binding.loadingDetails.setVisibility(View.GONE);
                    globalDataResults = response.body();

                    for(int i = 0; i < Objects.requireNonNull(globalDataResults).size(); i++){
                        countryFlagAdapter = new CountryFlagAdapter(globalDataResults, ContinentDetailActivity.this);
                        rvCountryFlag.setAdapter(countryFlagAdapter);
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<ArrayList<CountryResult>> call, @NonNull Throwable t) {
                Toast.makeText(ContinentDetailActivity.this, "Fail to Fetch https://disease.sh/v3/covid-19/countries data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCaseText(TextView tv, String textValue){
        tv.setText(HtmlCompat.fromHtml("<b>" + textValue + " case</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

   private void setPopulationText(TextView tv, String textValue){
       tv.setText(HtmlCompat.fromHtml("<b>" + textValue + " people</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
   }

    private void setTitleText(TextView tv, String textValue){
        tv.setText(HtmlCompat.fromHtml("<b>" + textValue + " Continent </b>COVID-19 Case", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setCountryNameText(TextView tv, String textValue){
        tv.setText(HtmlCompat.fromHtml("<b>" + textValue + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void setPercentText(TextView tv, String textValue){
        tv.setText(HtmlCompat.fromHtml("<b>" + textValue + "</b> %", HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void dialogSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_search, null);

        EditText inputSearch = v.findViewById(R.id.textSearchBy);
        Button btnSearch = v.findViewById(R.id.btnSearch);
        RadioGroup radioSearchType = v.findViewById(R.id.radioGroupSearchType);
        RadioButton radioSearchContinents = v.findViewById(R.id.radioSearchContinents);
        RadioButton radioSearchCountries = v.findViewById(R.id.radioSearchCountries);

        builder.setView(v);

        AlertDialog dialogSearch = builder.create();
        if (dialogSearch.getWindow() != null) {
            dialogSearch.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            radioSearchType.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioSearchContinents){
                    searchType = radioSearchContinents.getText().toString();
                } else if (checkedId == R.id.radioSearchCountries){
                    searchType = radioSearchCountries.getText().toString();
                }
            });

            btnSearch.setOnClickListener(view -> doSearch(inputSearch.getText().toString()));

            inputSearch.setOnEditorActionListener((v1, actionid, event) -> {
                if (actionid == EditorInfo.IME_ACTION_GO) {
                    doSearch(inputSearch.getText().toString());
                }
                return false;
            });
            dialogSearch.show();
        }
    }

    private void doSearch(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"Tidak ada inputan!!!", Toast.LENGTH_SHORT).show();
        }

        if(searchType.isEmpty()){
            Toast.makeText(getApplicationContext(),"Harap pilih tipe search!!!", Toast.LENGTH_SHORT).show();
        }

        else if (searchType.equals("Continents")){
            Intent i = new Intent(ContinentDetailActivity.this, SearchActivity.class);
            i.putExtra("searchType", "continents");
            i.putExtra("searchQuery", query);
            startActivity(i);
        }

        else if (searchType.equals("Countries")){
            Intent i = new Intent(ContinentDetailActivity.this, SearchActivity.class);
            i.putExtra("searchType", "countries");
            i.putExtra("searchQuery", query);
            startActivity(i);
        }
    }

    private void dialogSort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_sort, null);

        Button btnSearch = v.findViewById(R.id.btnSort);
        RadioGroup radioSortGroupType = v.findViewById(R.id.radioGroupSortType);
        RadioButton radioSortContinents = v.findViewById(R.id.radioButtonSortContinents);
        RadioButton radioSortCountries = v.findViewById(R.id.radioButtonSortCountries);

        RadioGroup radioSortGroupBy1 = v.findViewById(R.id.radioGroup1SortBy);
        RadioButton radioSortByTotal = v.findViewById(R.id.radioButtonSortTotal);
        RadioButton radioSortByActive = v.findViewById(R.id.radioButtonSortActive);

        RadioGroup radioSortGroupBy2 = v.findViewById(R.id.radioGroup2SortBy);
        RadioButton radioSortByRecovered = v.findViewById(R.id.radioButtonSortRecovered);
        RadioButton radioSortByDeath = v.findViewById(R.id.radioButtonSortDeaths);

        builder.setView(v);

        AlertDialog dialogSort = builder.create();
        if (dialogSort.getWindow() != null) {
            dialogSort.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            radioSortGroupType.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonSortContinents){
                    sortType = radioSortContinents.getText().toString().toLowerCase();
                } else if (checkedId == R.id.radioButtonSortCountries){
                    sortType = radioSortCountries.getText().toString().toLowerCase();
                }
            });

            radioSortGroupBy1.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioButtonSortTotal){
                    radioSortGroupBy2.setOnCheckedChangeListener(null);
                    sortBy = radioSortByTotal.getText().toString().toLowerCase().replace("total case", "cases");
                } else if (checkedId == R.id.radioButtonSortActive) {
                    radioSortGroupBy2.setOnCheckedChangeListener(null);
                    sortBy = radioSortByActive.getText().toString().toLowerCase().replace("active case", "actives");
                }
            });

            radioSortGroupBy2.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radioButtonSortRecovered){
                    radioSortGroupBy1.setOnCheckedChangeListener(null);
                    sortBy = radioSortByRecovered.getText().toString().toLowerCase();
                } else if (checkedId == R.id.radioButtonSortDeaths){
                    radioSortGroupBy1.setOnCheckedChangeListener(null);
                    sortBy = radioSortByDeath.getText().toString().toLowerCase().replace("death", "deaths");
                }
            });

            btnSearch.setOnClickListener(view -> doSort(sortBy));
            dialogSort.show();
        }
    }

    private void doSort(String query) {
        if(query.isEmpty()){
            Toast.makeText(getApplicationContext(),"Harap pilih tipe continents / countries!!!", Toast.LENGTH_SHORT).show();
        }
        if(sortType.isEmpty()){
            Toast.makeText(getApplicationContext(),"Harap pilih jenis sorting data!!!", Toast.LENGTH_SHORT).show();
        } else if (sortType.equals("continents")){
            switch (query) {
                case "cases":
                    Intent sortContinentsByCase = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortContinentsByCase.putExtra("sortType", "continents");
                    sortContinentsByCase.putExtra("sortBy", "cases");
                    startActivity(sortContinentsByCase);
                    break;
                case "actives":
                    Intent sortContinentsByActive = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortContinentsByActive.putExtra("sortType", "continents");
                    sortContinentsByActive.putExtra("sortBy", "actives");
                    startActivity(sortContinentsByActive);
                    break;
                case "recovered":
                    Intent sortContinentsByRecovered = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortContinentsByRecovered.putExtra("sortType", "continents");
                    sortContinentsByRecovered.putExtra("sortBy", "recovered");
                    startActivity(sortContinentsByRecovered);
                    break;
                case "deaths":
                    Intent sortContinentsByDeath = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortContinentsByDeath.putExtra("sortType", "continents");
                    sortContinentsByDeath.putExtra("sortBy", "deaths");
                    startActivity(sortContinentsByDeath);
                    break;
            }

        } else if (sortType.equals("countries")){
            switch (query) {
                case "cases":
                    Intent sortCountryByCase = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortCountryByCase.putExtra("sortType", "countries");
                    sortCountryByCase.putExtra("sortBy", "cases");
                    startActivity(sortCountryByCase);
                    break;
                case "actives":
                    Intent sortCountryByActive = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortCountryByActive.putExtra("sortType", "countries");
                    sortCountryByActive.putExtra("sortBy", "actives");
                    startActivity(sortCountryByActive);
                    break;
                case "recovered":
                    Intent sortCountryByRecovered = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortCountryByRecovered.putExtra("sortType", "countries");
                    sortCountryByRecovered.putExtra("sortBy", "recovered");
                    startActivity(sortCountryByRecovered);
                    break;
                case "deaths":
                    Intent sortCountryByDeath = new Intent(ContinentDetailActivity.this, SortActivity.class);
                    sortCountryByDeath.putExtra("sortType", "countries");
                    sortCountryByDeath.putExtra("sortBy", "deaths");
                    startActivity(sortCountryByDeath);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.nav_search) {
            dialogSearch();
        } else if (item.getItemId() == R.id.nav_sort){
            dialogSort();
        }
        return super.onOptionsItemSelected(item);
    }
}