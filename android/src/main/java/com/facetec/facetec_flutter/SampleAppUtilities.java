package com.facetec.facetec_flutter;
import android.media.MediaPlayer;
import android.os.Handler;

import Processors.Config;

public class SampleAppUtilities {
    enum VocalGuidanceMode {
        OFF,
        MINIMAL,
        FULL
    }
    private MediaPlayer vocalGuidanceOnPlayer;
    private MediaPlayer vocalGuidanceOffPlayer;
    static VocalGuidanceMode vocalGuidanceMode = VocalGuidanceMode.MINIMAL;

    private FacetecFlutterPlugin sampleAppActivity;
    public String currentTheme = Config.wasSDKConfiguredWithConfigWizard ? "Config Wizard Theme" : "FaceTec Theme";
    private Handler themeTransitionTextHandler;

    public SampleAppUtilities(FacetecFlutterPlugin activity) {
        sampleAppActivity = activity;
    }

//    public void showSessionTokenConnectionText() {
//        themeTransitionTextHandler = new Handler();
//        themeTransitionTextHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sampleAppActivity.activityMainBinding.themeTransitionText.animate().alpha(1f).setDuration(600);
//            }
//        }, 3000);
//    }
//
//    public void hideSessionTokenConnectionText() {
//        themeTransitionTextHandler.removeCallbacksAndMessages(null);
//        themeTransitionTextHandler = null;
//        sampleAppActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sampleAppActivity.activityMainBinding.themeTransitionText.animate().alpha(0f).setDuration(600);
//            }
//        });
//    }
//
//    public void displayStatus(final String statusString) {
//        Log.d("FaceTecSDKSampleApp", statusString);
//        sampleAppActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sampleAppActivity.activityMainBinding.statusLabel.setText(statusString);
//            }
//        });
//    }
//
//    public void handleErrorGettingServerSessionToken() {
//        hideSessionTokenConnectionText();
//        displayStatus("Session could not be started due to an unexpected issue during the network request.");
//        fadeInMainUI();
//    }
}
