package com.example.covid19tracking.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.example.covid19tracking.R;
import com.example.covid19tracking.api.ApiClient;
import com.example.covid19tracking.api.ApiService;
import com.example.covid19tracking.api.ContinentResult;
import com.example.covid19tracking.databinding.ActivityContinentDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContinentDetailActivity extends AppCompatActivity {

    private ActivityContinentDetailBinding binding;

    private ApiService apiService;

    private String continent;

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
        Call<ContinentResult> call = apiService.getContinentsDetail(continent, yesterday, twoDaysAgo,
                strict, allowNull);
        call.enqueue(new Callback<ContinentResult>() {
            @Override
            public void onResponse(@NonNull Call<ContinentResult> call, @NonNull Response<ContinentResult> response) {
                if(response.body() != null){
                    binding.toolbar.setTitle(response.body().getContinent() + " Continent Detail");

                    setTitleText(binding.textContinentName, response.body().getContinent());

                    long timeStamp = response.body().getUpdated();
                    Date date = new Date(timeStamp);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    String mDate = dateFormat.format(date);
                    binding.textContinentUpdatedTime.setText(mDate);

                    int population = response.body().getPopulation();
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContinentResult> call, @NonNull Throwable t) {

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
}