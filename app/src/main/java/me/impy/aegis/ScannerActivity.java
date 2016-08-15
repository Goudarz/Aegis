package me.impy.aegis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.impy.aegis.crypto.KeyInfo;
import me.impy.aegis.crypto.TOTP;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setFormats(getSupportedFormats());

        ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();

        try {
            KeyInfo info = KeyInfo.FromURL(rawResult.getText());

            //String nowTimeString = (Long.toHexString(System.currentTimeMillis() / 1000 / info.getPeriod()));
            //Toast.makeText(this, TOTP.generateTOTP(info.getSecret(), nowTimeString, info.getDigits(), info.getAlgo()), Toast.LENGTH_LONG).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("Keyinfo", info);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                    mScannerView.startCamera();          // Start camera on resume
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ScannerActivity.this, "Permission denied to get access to the camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private List<BarcodeFormat> getSupportedFormats() {
        ArrayList<BarcodeFormat> supportedFormats = new ArrayList<>();
        supportedFormats.add(BarcodeFormat.QR_CODE);

        return supportedFormats;
    }
}