package com.jonalmeida.tabgroups;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ClusterHelper implements EmbedlyHelper.EmbedlyResponse {

    private static final String LOGTAG = ClusterHelper.class.getSimpleName();

    private EmbedlyHelper embedlyHelper;
    private AppCompatActivity activity;

    public ClusterHelper(AppCompatActivity activity) {
        embedlyHelper = new EmbedlyHelper(this);
        this.activity = activity;
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d(LOGTAG, "Getting a response from Embedly");
        try {
            JSONObject resp = new JSONObject(response.body().string());
            List<JSONObject> urlObjs = EmbedlyHelper.getUrlObjects(resp);
            for (JSONObject urlObj : urlObjs) {
                Tab tab = new Tab.Builder()
                        .title(EmbedlyHelper.parseTitle(urlObj))
                        .url(EmbedlyHelper.parseUrl(urlObj))
                        .keywords(EmbedlyHelper.parseKeywords(urlObj))
                        .rgbColours(EmbedlyHelper.parseColours(urlObj))
                        .colourWeight(EmbedlyHelper.parseColorWeight(urlObj))
                        .faviconUrl(EmbedlyHelper.parseFaviconUrl(urlObj))
                        .build();
                //System.out.println("highest ranked topKeyword: " +
                //        EmbedlyHelper.getHighestRankedKeyword(keywords));
                System.out.println("tab.title: " + tab.title());
                GroupData.getInstance().addTab(tab, activity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTab(String url) {
        embedlyHelper.request(url);
    }

    public void addTabs(String[] urls) {
        embedlyHelper.request(urls);
    }
}
