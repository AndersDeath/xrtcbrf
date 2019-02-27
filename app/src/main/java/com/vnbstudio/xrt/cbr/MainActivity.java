package com.vnbstudio.xrt.cbr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    final int PERMISSION_INTERNET_CODE = 1;
    String tagPermission = "123";
    String currentDate;
    Calendar dateAndTime = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();
    IResult mResultCallback = null;
    VolleyService mVolleyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maxDate.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        maxDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        maxDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        firstConnect();
        setInitialDateTime();
    }

    public void refreshRateHandler(View view) {
        refreshRate();
        Toast.makeText(this, getResources().getString(R.string.refresh), Toast.LENGTH_LONG).show();
    }

    public void firstConnect() {
        initVolleyCallback();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(tagPermission, "Permission is granted");
                connect("today");
            } else {
                Log.d(tagPermission, "Permission is revoked");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET_CODE );
            }
        } else {
            connect("today");
        }
    }

    public void refreshRate() {
        initVolleyCallback();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(tagPermission, "Permission is granted");
                connect(currentDate);
            } else {
                Log.d(tagPermission, "Permission is revoked");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET_CODE );
            }
        } else {
            connect(currentDate);
        }
    }

    void initVolleyCallback(){
        final String[] list = {"USD", "EUR", "GBP", "CHF"};
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,JSONObject response) {
                Log.d("TEST", "Volley requester " + requestType);
                Log.d("TEST", "Volley JSON post" + response);
                for (int i = 0; i < list.length; i++){
                    try {
                        JSONObject check = response.optJSONObject("response").optJSONObject("result").optJSONObject(list[i]);
                        if (check != null) {
                            Log.d("Success", check.toString());
                            if(list[i] == "USD") {
                                TextView mainField = findViewById(R.id.textUSD);
                                mainField.setText(response.optJSONObject("response").optJSONObject("result").optJSONObject("USD").optString("val"));
                            }
                            if(list[i] == "EUR") {
                                TextView mainField = findViewById(R.id.textEUR);
                                mainField.setText(response.optJSONObject("response").optJSONObject("result").optJSONObject("EUR").optString("val"));
                            }
                            if(list[i] == "GBP") {
                                TextView mainField = findViewById(R.id.textGBP);
                                mainField.setText(response.optJSONObject("response").optJSONObject("result").optJSONObject("GBP").optString("val"));
                            }
                            if(list[i] == "CHF") {
                                TextView mainField = findViewById(R.id.textCHF);
                                mainField.setText(response.optJSONObject("response").optJSONObject("result").optJSONObject("CHF").optString("val"));
                            }
                        }
                    } catch (Throwable t) { Log.d("Error", t.toString()); }
                }
            }

            @Override
            public void notifyError(String requestType,VolleyError error) {
                Log.d("TEST", "Volley requester " + requestType);
                Log.d("TEST", "Volley JSON post" + "That didn't work!");
            }
        };
    }

    public void connect(String date) {
        currentDate = date;
        mVolleyService = new VolleyService(mResultCallback,this);
        mVolleyService.getDataVolley("GETCALL","url" + date);
    }

    public void setDate(View v) {
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(Long.valueOf("946674000000"));
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        dialog.show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Log.i("OnDateSetListener", "Update");
            String date = setInitialDateTime();
            Log.i("OnDateSetListener", date);
            connect(date);
        }
    };

    private String setInitialDateTime() {
        TextView date = findViewById(R.id.date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateAndTime.getTimeInMillis());
        SpannableString content = new SpannableString(dateString);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        date.setText(content);
        return dateString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.i("asdsad", String.format("value = %d", requestCode));
        switch (requestCode){

            case PERMISSION_INTERNET_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
