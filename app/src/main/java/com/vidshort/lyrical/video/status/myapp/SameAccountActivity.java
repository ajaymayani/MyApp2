package com.vidshort.lyrical.video.status.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.vidshort.lyrical.video.status.myapp.Adapters.AppsAdapter;
import com.vidshort.lyrical.video.status.myapp.model.Apps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class SameAccountActivity extends AppCompatActivity {
    RecyclerView rvAppList;
    Skeleton skeleton;
    String jsonString;
    ArrayList appList = new ArrayList();
    ArrayList<Apps> appsArrayList = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String responseFileName = "accountLink.json";
    String accountData = "accountData.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_account);
        sp = getSharedPreferences("timeSP2", MODE_PRIVATE);
        editor = sp.edit();
        rvAppList = findViewById(R.id.rvAppList);
        skeleton = findViewById(R.id.skeletonLayout);
        skeleton = SkeletonLayoutUtils.applySkeleton(rvAppList, R.layout.item_app_list, 8);
        skeleton.showSkeleton();
        rvAppList.setLayoutManager(new LinearLayoutManager(this));


        Log.e("log", "time " + sp.getLong("time", 0));
        if (sp.getLong("time", 0) == 0) {
            newRequest();

        } else if (new Date().getTime() > (sp.getLong("time", 0) + 300000)) {
            Log.e("log", "inside if");
            newRequest();
        } else {
            Log.e("log", "inside else");
            fetchFromDevice();
        }
        Log.e("log", "new request time :" + (sp.getLong("time", 0) + 300000));
    }

    String getJsonFromAsset() {
        try {
            InputStream file = getAssets().open(responseFileName);
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            return new String(formArray);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    void newRequest() {
        jsonString = getJsonFromAsset();
        saveFileToDevice(SameAccountActivity.this, responseFileName, jsonString);
        editor.putLong("time", new Date().getTime());
        editor.apply();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                appList.add(jsonArray.getJSONObject(i).getString("appStoreLink"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new MyAsync().execute();
    }

    void fetchFromDevice() {
        File assetDir = getApplicationContext().getDir("asset", Context.MODE_PRIVATE);
        File fileWithinAssetDir = new File(assetDir, accountData);

        jsonString = readFileFromDevice(SameAccountActivity.this, fileWithinAssetDir.getPath());
        Log.e("log", "jsonString :" + jsonString);
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String appImage = jsonArray.getJSONObject(i).getString("appImage");
                String appName = jsonArray.getJSONObject(i).getString("appName");
                String appLink = jsonArray.getJSONObject(i).getString("appLink");
                appsArrayList.add(new Apps(appName, appImage, appLink));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AppsAdapter appsAdapter = new AppsAdapter(SameAccountActivity.this, appsArrayList);
        rvAppList.setAdapter(appsAdapter);
    }

    public void saveFileToDevice(Context context, String fileName, String content) {
        try {
            File assetDir = context.getDir("asset", Context.MODE_PRIVATE);
            File fileWithinAssetDir = new File(assetDir, fileName);

            FileOutputStream fos = new FileOutputStream(fileWithinAssetDir.getAbsolutePath());
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFileFromDevice(Context context, String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
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
                Document document = Jsoup.connect(appList.get(0).toString()).get();
                int count = document.getElementsByClass("ImZGtf mpg5gc").size();
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < count; i++) {
                    String appImage = document.getElementsByClass("ImZGtf").get(i).select(".kJ9uy").select("img").attr("data-src");
                    String appName = document.getElementsByClass("ImZGtf").get(i).select(".WsMG1c").text();
                    String appLink = document.getElementsByClass("ImZGtf").get(i).getElementsByTag("a").attr("href");

                    appsArrayList.add(new Apps(appName, appImage, "https://play.google.com" + appLink));

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("appImage", appImage);
                    jsonObject.put("appName", appName);
                    jsonObject.put("appLink", "https://play.google.com" + appLink);
                    jsonArray.put(jsonObject);
                }
                saveFileToDevice(getApplicationContext(), accountData, jsonArray.toString());
            } catch (IOException | JSONException e) {
                Log.e("log", "error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            AppsAdapter appsAdapter = new AppsAdapter(SameAccountActivity.this, appsArrayList);
            rvAppList.setAdapter(appsAdapter);
            Log.e("log", "complete");
        }
    }

}