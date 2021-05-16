package com.example.fiverrproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;


import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ConsentForm form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getconcentStatus();
    }

    private void getconcentStatus(){
        ConsentInformation.getInstance(MainActivity.this).addTestDevice("F3C32EA7CB114B04A5A7E4F756BFF356");
        ConsentInformation.getInstance(MainActivity.this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {"pub-5201796050186600"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                if(consentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()){
                    switch (consentStatus){
                        case UNKNOWN:
                            displayconsentform();
                            break;
                        case PERSONALIZED:
                            loadbannerads(true);
                            break;
                        case NON_PERSONALIZED:
                            loadbannerads(false);
                            break;
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void displayconsentform(){
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL("https://www.your.com/privacyurl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        switch (consentStatus){
                            case PERSONALIZED:
                                loadbannerads(true);
                                break;

                            case NON_PERSONALIZED:
                                loadbannerads(false);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        form.load();
    }

    private void loadbannerads(boolean isPersonlized){
        AdView adView= findViewById(R.id.adView);
        AdRequest adRequest;
        if(isPersonlized){
            adRequest= new AdRequest.Builder().build();
        }
        else {
            Bundle bundle= new Bundle();
            bundle.putString("npa","1");
            adRequest= new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class,bundle).build();
        }

        adView.loadAd(adRequest);

    }
}