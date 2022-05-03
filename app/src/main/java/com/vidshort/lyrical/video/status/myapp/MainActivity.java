package com.vidshort.lyrical.video.status.myapp;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.vidshort.lyrical.video.status.myapp.Adapters.AppsAdapter;
import com.vidshort.lyrical.video.status.myapp.model.Apps;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvAppList;
    private Skeleton skeleton;

    String jsonString;
    ArrayList appList = new ArrayList();
    ArrayList<Apps> appsArrayList = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String responseFileName = "apps.js";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("timeSP", MODE_PRIVATE);
        editor = sp.edit();
        Log.e("log", "time " + sp.getLong("time", 0));
        if (sp.getLong("time", 0) == 0) {
            jsonString = getJsonFromAsset();
            saveTextToFile(MainActivity.this, responseFileName, jsonString);
            editor.putLong("time", new Date().getTime());
            editor.apply();
        }

        rvAppList = findViewById(R.id.rvAppList);
        skeleton = findViewById(R.id.skeletonLayout);
        skeleton = SkeletonLayoutUtils.applySkeleton(rvAppList, R.layout.item_app_list);
        skeleton.showSkeleton();

        Log.e("log","new request time :"+(sp.getLong("time", 0) + 300000));
        if (new Date().getTime() > (sp.getLong("time", 0) + 300000)) {
            Log.e("log","inside if");
            jsonString = getJsonFromAsset();
            saveTextToFile(MainActivity.this, responseFileName, jsonString);
            editor.putLong("time", new Date().getTime());
            editor.apply();
        } else {
            Log.e("log","inside else");
            File file = new File(MainActivity.this.getFilesDir(), responseFileName);
            jsonString = read_file(MainActivity.this, file.getPath());
        }


        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                appList.add(jsonArray.getJSONObject(i).getString("appLink"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new MyAsync().execute();
        rvAppList.setLayoutManager(new LinearLayoutManager(this));
    }

    String getJsonFromAsset() {
        try {
            InputStream file = getAssets().open("apps.json");
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            return new String(formArray);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void saveTextToFile(Context context, String filename, String content) {
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read_file(Context context, String path) {
        try {
            FileInputStream fis = context.openFileInput(responseFileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    class MyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("log", "doInBackground");
            try {
                for (int i = 0; i < appList.size(); i++) {
                    Log.e("log", "appList : " + appList.get(i).toString());
                    Document document = Jsoup.connect(appList.get(i).toString()).get();
                    String appImage = document.getElementsByClass("xSyT2c").select("img").attr("src");
                    String appName = document.getElementsByClass("AHFaub").select("span").text();
                    appsArrayList.add(new Apps(appName, appImage, appList.get(i).toString()));
                    Log.e("log", "App name : " + appName);
                }
            } catch (IOException e) {
                Log.e("log", "error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            AppsAdapter appsAdapter = new AppsAdapter(MainActivity.this, appsArrayList);
            rvAppList.setAdapter(appsAdapter);
            Log.e("log", "complete");
        }
    }
}