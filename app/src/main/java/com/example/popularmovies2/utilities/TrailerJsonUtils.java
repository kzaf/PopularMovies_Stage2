package com.example.popularmovies2.utilities;

import com.example.popularmovies2.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrailerJsonUtils {

    private static final String RESULTS = "results";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String KEY = "key";

    public static Trailer[] getTrailersStringsFromJson(String json) throws JSONException {

        JSONObject trailersJson = new JSONObject(json);
        JSONArray trailersJsonArray = trailersJson.optJSONArray(RESULTS);

        Trailer[] trailersArray = new Trailer[trailersJsonArray.length()];

        for(int i = 0; i < trailersJsonArray.length(); i++){

            String name = trailersJsonArray.getJSONObject(i).optString(NAME);
            String type = trailersJsonArray.getJSONObject(i).optString(TYPE);
            String key = trailersJsonArray.getJSONObject(i).optString(KEY);

            trailersArray[i] = new Trailer(name, type, key);
        }

        return trailersArray;

    }


}
