package com.facetec.facetec_flutter;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;

import Processors.Config;
import Processors.Processor;
import Processors.NetworkingHelpers;
import Processors.PhotoIDMatchProcessor;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.facetec.sdk.*;

import android.content.Intent;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;

import java.io.IOException;

/**
 * FacetecFlutterPlugin
 */

public class FacetecFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private Context context;
    private Activity activity;

    public static String DeviceKeyIdentifier = "dDw1XqV2CVDIgXdXkqDdjjUnMDhr2U3h";
    public static String BaseURL = "https://api.facetec.com/api/v3.1/biometrics";

    public FaceTecSessionResult latestSessionResult;
    public Processor latestProcessor;
    String latestExternalDatabaseRefID = "";
    public FaceTecIDScanResult latestIDScanResult;
    private boolean isSessionPreparingToLaunch = false;

    static String PublicFaceScanEncryptionKey =
            "-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5PxZ3DLj+zP6T6HFgzzk\n" +
                    "M77LdzP3fojBoLasw7EfzvLMnJNUlyRb5m8e5QyyJxI+wRjsALHvFgLzGwxM8ehz\n" +
                    "DqqBZed+f4w33GgQXFZOS4AOvyPbALgCYoLehigLAbbCNTkeY5RDcmmSI/sbp+s6\n" +
                    "mAiAKKvCdIqe17bltZ/rfEoL3gPKEfLXeN549LTj3XBp0hvG4loQ6eC1E1tRzSkf\n" +
                    "GJD4GIVvR+j12gXAaftj3ahfYxioBH7F7HQxzmWkwDyn3bqU54eaiB7f0ftsPpWM\n" +
                    "ceUaqkL2DZUvgN0efEJjnWy5y1/Gkq5GGWCROI9XG/SwXJ30BbVUehTbVcD70+ZF\n" +
                    "8QIDAQAB\n" +
                    "-----END PUBLIC KEY-----";

    private static Result pendingCallbackContext = null;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "facetec_flutter");
        context = flutterPluginBinding.getApplicationContext();
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("initialize")) {
            pendingCallbackContext = new MethodResultWrapper(result);
            try {
                initialize(call.arguments);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (call.method.equals("idCheck")) {
            try {
                idCheck(call.arguments);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
    }


    private void initialize(Object args) throws JSONException {
        Object json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONTokener(args.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            jsonArray = (JSONArray) json;
        }
        assert jsonArray != null;
        final String appToken = jsonArray.getString(0);
        Log.d(null, appToken);
        DeviceKeyIdentifier = appToken;
        new Handler(Looper.getMainLooper()).post(() -> {
            // Initialize FaceTec SDK
            FaceTecSDK.initializeInDevelopmentMode(context, DeviceKeyIdentifier, PublicFaceScanEncryptionKey,
                    new FaceTecSDK.InitializeCallback() {
                        @Override
                        public void onCompletion(final boolean successful) {
                            if (successful) {
                                Log.d("FaceTecSDKSampleApp", "Initialization Successful.");
                                pendingCallbackContext.success("SDK initialized");
                            } else {
                                String status = FaceTecSDK.getStatus(context).toString();
                                pendingCallbackContext.error("FaceTec Issue", status, null);
                            }
                        }
                    });
        });
    }

    private void idCheck(Object args) throws JSONException {

        new Handler(Looper.getMainLooper()).post(() -> {
            // Facetec id check
            getSessionToken(new SessionTokenCallback() {
                @Override
                public void onSessionTokenReceived(String sessionToken) {
                    isSessionPreparingToLaunch = false;
                    latestExternalDatabaseRefID = "android_sample_app_" + randomUUID();
                    Log.d("Session token fetch", sessionToken);
                    latestProcessor = new PhotoIDMatchProcessor(sessionToken, activity,FacetecFlutterPlugin.this);
                }
            });
        });
    }

    interface SessionTokenCallback {
        void onSessionTokenReceived(String sessionToken);
    }

    public void getSessionToken(final SessionTokenCallback sessionTokenCallback) {
//        utils.showSessionTokenConnectionText();
        Log.d("FaceTecSDK", "creating session token");

        // Do the network call and handle result
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("X-Device-Key", Config.DeviceKeyIdentifier)
                .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
                .url(Config.BaseURL + "/session-token")
                .get()
                .build();

        NetworkingHelpers.getApiClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("FaceTecSDK", "Exception raised while attempting HTTPS call.");

                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
                if (!e.getMessage().equals(NetworkingHelpers.OK_HTTP_RESPONSE_CANCELED)) {
                    Log.d("FaceTecSDK", "Session could not be started due to an unexpected issue during the network request.");
//                    utils.handleErrorGettingServerSessionToken();
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseJSON = new JSONObject(responseString);
                    if (responseJSON.has("sessionToken")) {
//                        utils.hideSessionTokenConnectionText();
                        Log.d("FaceTecSDK", "created session token");
                        sessionTokenCallback.onSessionTokenReceived(responseJSON.getString("sessionToken"));
                    } else {
//                        utils.handleErrorGettingServerSessionToken();
                        Log.d("FaceTecSDK", "Session could not be started due to an unexpected issue during the network request.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("FaceTecSDK", "Exception raised while attempting to parse JSON result.");
//                    utils.handleErrorGettingServerSessionToken();
                }
            }
        });
    }

    public void setLatestSessionResult(FaceTecSessionResult sessionResult) {
        this.latestSessionResult = sessionResult;
    }

    public String getLatestExternalDatabaseRefID() {
        return latestExternalDatabaseRefID;
    }

    public void setLatestIDScanResult(FaceTecIDScanResult idScanResult) {
        this.latestIDScanResult = idScanResult;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (latestProcessor == null) {
            return;
        }

//        utils.fadeInMainUI();

        // At this point, you have already handled all results in your Processor code.
        if (this.latestProcessor.isSuccess()) {
            Log.d("FaceTecSDK", "Success");
//            utils.displayStatus("Success");

        } else {
            Log.d("FaceTecSDK", "Session exited early, see logs for more details.");
//            utils.displayStatus("Session exited early, see logs for more details.");

            // Reset the enrollment identifier.
            latestExternalDatabaseRefID = "";
        }
    }

    private static class MethodResultWrapper implements MethodChannel.Result {
        private final MethodChannel.Result methodResult;
        private final Handler handler;

        MethodResultWrapper(final MethodChannel.Result result) {
            this.methodResult = result;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object result) {
            this.handler.post(
                    () -> MethodResultWrapper.this.methodResult.success(result));
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            this.handler.post(
                    () -> MethodResultWrapper.this.methodResult.error(errorCode, errorMessage, errorDetails));
        }

        @Override
        public void notImplemented() {
            this.handler.post(
                    MethodResultWrapper.this.methodResult::notImplemented);
        }
    }
}
