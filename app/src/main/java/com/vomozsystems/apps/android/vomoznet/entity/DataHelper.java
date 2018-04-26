package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static List<ColorWrapper> sColorWrappers = new ArrayList<>();
    private static List<ColorSuggestion> sColorSuggestions =
            new ArrayList<>();

    public static void buildSuggestions(Context context) {
        sColorSuggestions = new ArrayList<ColorSuggestion>();
        String commaSep = PreferenceManager.getDefaultSharedPreferences(context).getString("SUGGESTIONS", "");
        List<String> items = Arrays.asList(commaSep.split("\\s*,\\s*"));
        for(String item: items) {
            if(null!=item && item.length()>0) {
                ColorSuggestion colorSuggestion = new ColorSuggestion(item);
                colorSuggestion.setIsHistory(true);
                sColorSuggestions.add(colorSuggestion);
            }
        }
//        for(ColorWrapper colorWrapper: sColorWrappers) {
//            ColorSuggestion colorSuggestion = new ColorSuggestion(colorWrapper.getName());
//            sColorSuggestions.add(colorSuggestion);
//            List<String> tags = Arrays.asList(colorWrapper.getTags().split(","));
//            for(String string: tags) {
//                if(!TextUtils.isEmpty(string) && !sColorSuggestions.contains(string)) {
//                    colorSuggestion = new ColorSuggestion(string);
//                    sColorSuggestions.add(colorSuggestion);
//                }
//            }
//        }
    }
    public interface OnFindColorsListener {
        void onResults(List<ColorWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<ColorSuggestion> results);
    }

    public static List<ColorSuggestion> getHistory(Context context, int count) {

        List<ColorSuggestion> suggestionList = new ArrayList<>();
        ColorSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (ColorSuggestion colorSuggestion : sColorSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                DataHelper.resetSuggestionsHistory();
                List<ColorSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorSuggestion suggestion : sColorSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<ColorSuggestion>() {
                    @Override
                    public int compare(ColorSuggestion lhs, ColorSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null && null!=results.values) {
                    listener.onResults((List<ColorSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static void findColors(final Context context, String query, final OnFindColorsListener listener) {

        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ColorWrapper> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {
                    for (ColorWrapper color : sColorWrappers) {
                        if (color.getName().toUpperCase()
                                .contains(constraint.toString().toUpperCase())) {
                            if(!suggestionList.contains(color)) suggestionList.add(color);
                            ColorSuggestion colorSuggestion = new ColorSuggestion();
                            colorSuggestion.setIsHistory(true);
                            colorSuggestion.setmColorName(constraint.toString());
                            if(!sColorSuggestions.contains(colorSuggestion)) sColorSuggestions.add(colorSuggestion);
                            String csv = convertToCommaSeparated(sColorSuggestions);
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SUGGESTIONS", csv).apply();

                        } else if(color.getTags().contains(constraint)){
                            if(!suggestionList.contains(color)) suggestionList.add(color);
                            ColorSuggestion colorSuggestion = new ColorSuggestion();
                            colorSuggestion.setIsHistory(true);
                            colorSuggestion.setmColorName(constraint.toString());
                            if(!sColorSuggestions.contains(colorSuggestion)) sColorSuggestions.add(colorSuggestion);
                            String csv = convertToCommaSeparated(sColorSuggestions);
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SUGGESTIONS", csv).apply();

                        } else if(color.getAddress().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            if(!suggestionList.contains(color)) suggestionList.add(color);
                            ColorSuggestion colorSuggestion = new ColorSuggestion();
                            colorSuggestion.setIsHistory(true);
                            colorSuggestion.setmColorName(constraint.toString());
                            if(!sColorSuggestions.contains(colorSuggestion)) sColorSuggestions.add(colorSuggestion);
                            String csv = convertToCommaSeparated(sColorSuggestions);
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SUGGESTIONS", csv).apply();
                        } else if(color.getFullName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            if(!suggestionList.contains(color)) suggestionList.add(color);
                            ColorSuggestion colorSuggestion = new ColorSuggestion();
                            colorSuggestion.setIsHistory(true);
                            colorSuggestion.setmColorName(constraint.toString());
                            if(!sColorSuggestions.contains(colorSuggestion)) sColorSuggestions.add(colorSuggestion);
                            String csv = convertToCommaSeparated(sColorSuggestions);
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SUGGESTIONS", csv).apply();
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null) {
                    listener.onResults((List<ColorWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static String convertToCommaSeparated(List<ColorSuggestion> colorSuggestions) {

        StringBuilder csvBuilder = new StringBuilder();

        for(ColorSuggestion colorSuggestion : colorSuggestions){
            csvBuilder.append(colorSuggestion.getmColorName());
            csvBuilder.append(",");
        }
        String csv = csvBuilder.toString();
        csv = csv.substring(0, csv.length() - ",".length());

        return csv;
    }

    public static void initColorWrapperList(final Context context) {

        if (sColorWrappers.isEmpty()) {
            sColorWrappers = new ArrayList<ColorWrapper>();
            Realm realm = Realm.getDefaultInstance();
            Config config = realm.where(Config.class).findFirst();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<DonationCenterResponse> call = apiInterface.getAll(context.getResources().getString(R.string.org_filter));
            call.enqueue(new Callback<DonationCenterResponse>() {
                @Override
                public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                    if(response.isSuccessful()) {
                        for (DonationCenter donationCenter : response.body().getResponseData()) {
                            ColorWrapper colorWrapper = new ColorWrapper();
                            colorWrapper.setName(donationCenter.getShortName());
                            colorWrapper.setFullName(donationCenter.getName());
                            colorWrapper.setMerchantIdCode(donationCenter.getMerchantIdCode());
                            colorWrapper.setCardId(donationCenter.getCardId());
                            colorWrapper.setId(donationCenter.getCardId());
                            colorWrapper.setPhone(donationCenter.getTelephoneNumber());
                            colorWrapper.setEmail(donationCenter.getEmailAddress());
                            colorWrapper.setLogoUrl(donationCenter.getLogoName());
                            colorWrapper.setUrl(donationCenter.getWebUrl());
                            colorWrapper.setAddress(donationCenter.getAddress());
                            colorWrapper.setTags(donationCenter.getShortName());
                            String[] items = donationCenter.getAddress().replace(",", " ").split(" ");
                            for(String string: items) {
                                colorWrapper.setTags(colorWrapper.getTags() + "," + string);
                            }
                            colorWrapper.setTags(colorWrapper.getTags().substring(1));
                            sColorWrappers.add(colorWrapper);
                        }
                        buildSuggestions(context);
                    }
                }

                @Override
                public void onFailure(Call<DonationCenterResponse> call, Throwable t) {

                }
            });

        }
    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static List<ColorWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<ColorWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}