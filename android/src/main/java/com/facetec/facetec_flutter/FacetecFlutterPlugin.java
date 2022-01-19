package com.facetec.facetec_flutter;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.facetec.sdk.*;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;


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

    //    public static String DeviceKeyIdentifier = "dDw1XqV2CVDIgXdXkqDdjjUnMDhr2U3h";
    public static String DeviceKeyIdentifier = "";
    public static String BaseURL = "https://api.facetec.com/api/v3.1/biometrics";
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
            result.success("Work in progress");
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
