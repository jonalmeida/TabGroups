package com.jonalmeida.tabgroups;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EmbedlyHelper {
    public interface EmbedlyResponse extends Callback {}

    private static final String LOGTAG = EmbedlyHelper.class.getSimpleName();

    private static final String EMBEDLY_ENDPOINT =
            "https://embedly-proxy.services.mozilla.com/v2/extract"; // Production endpoint
//    private static final String EMBEDLY_ENDPOINT =
//            "https://embedly-proxy.stage.mozaws.net/v2/extract";

    private static final String ATTR_URLS = "urls";
    private static final String ATTR_KEYWORDS = "keywords";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_SCORE = "score";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_FAVICON_COLOURS = "favicon_colors";
    private static final String ATTR_COLOR = "color";
    private static final String ATTR_COLOR_WEIGHT = "weight";
    private static final String ATTR_ORIGINAL_URL = "original_url";
    private static final String ATTR_FAVICON_URL = "favicon_url";

    public static final MediaType JSON = MediaType.parse("application/json");

    private OkHttpClient mClient;
    private EmbedlyResponse mCallback;

    public EmbedlyHelper(EmbedlyResponse callback) {
        // We need to add a NetworkInterceptor because of a bug in the embedly proxy.
        // See: https://github.com/mozilla/embedly-proxy/pull/123
        mClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request req = chain.request();
                        Request newReq = req.newBuilder()
                                .header("Content-Type", "application/json")
                                .build();
                        Log.d(LOGTAG, "req.headers: " + newReq.headers().toString());
                        return chain.proceed(newReq);
                    }
                }).build();
        mCallback = callback;
    }

    public void request(String url) {
        request(new String[] {url});
    }

    public void request(String[] urls) {
        JSONArray urlArray = new JSONArray();
        for (String url : urls) {
            urlArray.put(url.replace("\\/", "/"));
        }
        JSONObject reqObject = new JSONObject();
        try {
            reqObject = reqObject.put(ATTR_URLS, urlArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "Failed at serializing urls to request. Barfing out..");
            return;
        }
        Log.d(LOGTAG, "Json request obj: " + String.valueOf(reqObject));
        RequestBody body = RequestBody.create(JSON, String.valueOf(reqObject));
        Request mRequest = new Request.Builder()
                .url(EMBEDLY_ENDPOINT)
                .post(body)
                .build();
        //Log.d(LOGTAG, "Making request now.. " + mRequest.headers().toString());
        mClient.newCall(mRequest).enqueue(mCallback);
    }

    public static List<JSONObject> getUrlObjects(JSONObject resp) {
        List<JSONObject> respObjects = new LinkedList<>();
        try {
            JSONObject respJsonObject = resp.getJSONObject(ATTR_URLS);
            Iterator<String> urlKeyIterator = respJsonObject.keys();
            while (urlKeyIterator.hasNext()) {
                String key = urlKeyIterator.next();
                System.out.println("urlKeys: " + key);
                JSONObject urlObj = respJsonObject.getJSONObject(key);
                respObjects.add(urlObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respObjects;
    }

    public static @Nullable String parseTitle(JSONObject urlObj) {
        String url = null;
        try {
            url = urlObj.getString(ATTR_TITLE);
        } catch (JSONException e) {
            Log.e(LOGTAG, "Parsing page title failed.");
            e.printStackTrace();
        }
        return url;
    }

    private static HashMap<String, Integer> parseKeywords(String responseString) throws
            JSONException {
        return parseKeywords(new JSONObject(responseString));
    }

    public static HashMap<String, Integer> parseKeywords(JSONObject urlObj) {
        final HashMap<String, Integer> rankedKeywords = new HashMap<>();
        try {
            JSONArray keywords = urlObj.getJSONArray(ATTR_KEYWORDS);
            int len = keywords.length();
            for (int i = 0; i < len; i++) {
                JSONObject keyword = keywords.getJSONObject(i);
                rankedKeywords.put(
                        (String) keyword.get(ATTR_NAME),
                        (Integer) keyword.get(ATTR_SCORE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rankedKeywords;
    }

    public static int[] parseColours(JSONObject urlObj) {
        int[] coloursArr = new int[3];
        try {
            JSONArray coloursObj = urlObj.getJSONArray(ATTR_FAVICON_COLOURS);
            if (coloursObj.length() > 0) {
                // We're just looking at the first colour object for simplicity
                JSONArray colours = coloursObj.getJSONObject(0).getJSONArray(ATTR_COLOR);
                coloursArr[0] = colours.getInt(0);
                coloursArr[1] = colours.getInt(1);
                coloursArr[2] = colours.getInt(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coloursArr;
    }

    public static long parseColorWeight(JSONObject urlObj) {
        long weight = 0l;
        try {
            JSONArray coloursObj = urlObj.getJSONArray(ATTR_FAVICON_COLOURS);
            if (coloursObj.length() > 0) {
                // We're just looking at the first colour object for simplicity
                weight = coloursObj.getJSONObject(0).getLong(ATTR_COLOR_WEIGHT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weight;
    }

    public static @Nullable String parseUrl(JSONObject urlObj) {
        String url = null;
        try {
            url = urlObj.getString(ATTR_ORIGINAL_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static @Nullable String parseFaviconUrl(JSONObject urlObj) {
        String url = null;
        try {
            url = urlObj.getString(ATTR_FAVICON_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }
}
