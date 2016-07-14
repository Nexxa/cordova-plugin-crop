package com.jeduan.crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private static final String ERROR_NULL_IMG_MSG        = "The image cannot be cropped";
    private static final String ERROR_NULL_IMG_CODE       = "nullImage";
    private static final String ERROR_CROPPING_MSG        = "Error on cropping";
    private static final String ERROR_USER_CANCELLED_MSG  = "User cancelled";
    private static final String ERROR_USER_CANCELLED_CODE = "userCancelled";
    private static final String FILE_PATH_PREFIX          = "file://";
    private static final String RESULT_FILE_NAME_SUFFIX   = "-cropped.jpg";

    private CallbackContext callbackContext;
    private Uri inputUri;
    private Uri outputUri;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("cropImage")) {
            String imagePath = args.getString(0);
            JSONObject options = args.optJSONObject(1);
            int toSize = this.MAX_SIZE;

            this.callbackContext = callbackContext;

            if (imagePath.equals("null")) {
                sendError(ERROR_NULL_IMG_MSG, ERROR_NULL_IMG_CODE);

                return false;
            }

            if (!imagePath.startsWith(FILE_PATH_PREFIX)) {
                imagePath = FILE_PATH_PREFIX.concat(imagePath);
            }

            if (options != null) {
                toSize = options.optInt("toSize", MAX_SIZE);
            }

            this.inputUri = Uri.parse(imagePath);
            this.outputUri = Uri.fromFile(new File(getTempDirectoryPath() + "/" + System.currentTimeMillis() + RESULT_FILE_NAME_SUFFIX));

            PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
            pr.setKeepCallback(true);
            callbackContext.sendPluginResult(pr);

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
                String resultFilePath = FILE_PATH_PREFIX + imageUri.getPath() + "?" + System.currentTimeMillis();

                this.callbackContext.success(resultFilePath);
                this.callbackContext = null;

            } else if (resultCode == Crop.RESULT_ERROR) {
                sendError(ERROR_CROPPING_MSG, String.valueOf(resultCode));

            } else if (resultCode == Activity.RESULT_CANCELED) {
                sendError(ERROR_USER_CANCELLED_MSG, ERROR_USER_CANCELLED_CODE);
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void sendError(String message, String code) {
        try {
            JSONObject err = new JSONObject();

            err.put("message", message);
            err.put("code", code);

            this.callbackContext.error(err);
            this.callbackContext = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public Bundle onSaveInstanceState() {
        Bundle state = new Bundle();

        if (this.inputUri != null) {
            state.putString("inputUri", this.inputUri.toString());
        }

        if (this.outputUri != null) {
            state.putString("outputUri", this.outputUri.toString());
        }

        return state;
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {

        if (state.containsKey("inputUri")) {
            this.inputUri = Uri.parse(state.getString("inputUri"));
        }

        if (state.containsKey("outputUri")) {
            this.inputUri = Uri.parse(state.getString("outputUri"));
        }

        this.callbackContext = callbackContext;
    }
}
