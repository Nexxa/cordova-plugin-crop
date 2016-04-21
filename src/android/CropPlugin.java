package com.jeduan.crop;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.soundcloud.android.crop.Crop;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CropPlugin extends CordovaPlugin {

    private static final int MAX_SIZE                     = 1080;
    private static final String ANDROID_DATA_PATH         = "/Android/data/";
    private static final String CACHE_PATH                = "/cache/";
    private static final String ERROR_CROPPING_MSG        = "Error on cropping";
    private static final String ERROR_USER_CANCELLED_MSG  = "User cancelled";
    private static final String ERROR_USER_CANCELLED_CODE = "userCancelled";
    private static final String FILE_PATH_PREFIX          = "file://";
    private static final String RESULT_FILE_NAME          = "/cropped.jpg";

    private CallbackContext callbackContext;
    private Uri inputUri;
    private Uri outputUri;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("cropImage")) {
            String imagePath = args.getString(0);
            JSONObject options = args.optJSONObject(1);
            int toSize = this.MAX_SIZE;

            if (options != null) {
                toSize = options.optInt("toSize", MAX_SIZE);
            }

            this.inputUri = Uri.parse(imagePath);
            this.outputUri = Uri.fromFile(new File(getTempDirectoryPath() + RESULT_FILE_NAME));

            PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
            pr.setKeepCallback(true);
            callbackContext.sendPluginResult(pr);
            this.callbackContext = callbackContext;

            cordova.setActivityResultCallback(this);

            Crop.of(this.inputUri, this.outputUri)
                .asSquare()
                .withMaxSize(toSize, toSize)
                .start(cordova.getActivity());

            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = Crop.getOutput(intent);
                String resultFilePath = FILE_PATH_PREFIX + imageUri.getPath()
                                            + "?" + System.currentTimeMillis();

                this.callbackContext.success(resultFilePath);
                this.callbackContext = null;

            } else if (resultCode == Crop.RESULT_ERROR) {
                try {
                    JSONObject err = new JSONObject();

                    err.put("message", ERROR_CROPPING_MSG);
                    err.put("code", String.valueOf(resultCode));

                    this.callbackContext.error(err);
                    this.callbackContext = null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                try {
                    JSONObject err = new JSONObject();

                    err.put("message", ERROR_USER_CANCELLED_MSG);
                    err.put("code", ERROR_USER_CANCELLED_CODE);

                    this.callbackContext.error(err);
                    this.callbackContext = null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                ANDROID_DATA_PATH + cordova.getActivity().getPackageName() + CACHE_PATH);
        }
        // Use internal storage
        else {
            cache = cordova.getActivity().getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();

        return cache.getAbsolutePath();
    }
}
