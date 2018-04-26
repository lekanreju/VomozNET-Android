package com.vomozsystems.apps.android.vomoznet.utility;


import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;

import java.text.DateFormatSymbols;
import java.util.Currency;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leksrej on 8/31/16.
 */
public class ApplicationUtils {

    public static final String APP_ID = "";
    public static final String BLANK_DATE = "Not Selected";
    public static final int CURRENT_REALM_VERSION = 1;
    static final String candidateChars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";

    public static String getMonthName(int monthNumber) {
        String[] months = new DateFormatSymbols().getShortMonths();
        int n = monthNumber - 1;
        return (n >= 0 && n <= 11) ? months[n] : "wrong number";
    }

    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.getCurrentFocus().getWindowToken(), 0);
    }

//    public static boolean isValidPhoneNumber(String phoneNumber) {
//        phoneNumber = phoneNumber.replace(" ", "").replace("(", "").replace(")", "").replace("-", "").replace("+", "");
//        boolean isInternational = (null != phoneNumber && phoneNumber.length() > 10 && (phoneNumber.startsWith("1") || (phoneNumber.startsWith("011"))));
//        boolean isLocal = (null != phoneNumber && phoneNumber.length() == 10);
//        return (isInternational || isLocal);
//    }

    public static String getCurrencySymbol(String strCode) {
        String symbol =  Currency.getInstance(strCode).getSymbol();
        if (!symbol.equalsIgnoreCase(strCode))
            return symbol;
        else
            return "";
    }

    public final static boolean isValidPhoneNumber(CharSequence target) {
        String phoneNumber = target.toString();
        phoneNumber = phoneNumber.replace(" ", "").replace("(", "").replace(")", "").replace("-", "").replace("+", "");
        if (phoneNumber == null) {
            return false;
        } else {
            if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {
                return false;
            } else {
                boolean value = android.util.Patterns.PHONE.matcher(phoneNumber).matches();
                return value;
            }
        }
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidName(String name) {
        if (null == name) return false;
        boolean valid = (name.length() > 2);
        String regx = "^[\\p{L} .'-]+$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regx);
        java.util.regex.Matcher m = p.matcher(name);
        return m.matches() && valid;
    }

    public static String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber != null)
            return phoneNumber.replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+", "");
        else
            return null;
    }

    public static boolean isBlank(String value) {
        return (value == null || value.equals("") || value.equals("null") || value.trim().equals(""));
    }

    public static String generateRandomChars(int length) { //"MMddYY"
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }
        return sb.toString();
    }

    public static boolean isNumeric(String value) {
        boolean ret = false;
        if (!isBlank(value)) {
            ret = value.matches("^[0-9]+$");
        }
        return ret;
    }

    public static String validatePassword(String pass) {
        String passwordRules = "Your password must :\n\n * Be between 8 and 15 digits/chars long\n* Contain at least one digit\n* Contain at least one uppercase char\n* Contain at least one lowercase char\n* Contain at least one special char (in the list '!@#$%^&*+=?-'";
        if (pass.length() < 8 || pass.length() > 15) {
            return "Password too short or too long.\n" + passwordRules;
        }
        if (!pass.matches(".*\\d.*")) {
            return "Password contains no digit or number.\n" + passwordRules;
        }
//        if (!pass.matches(".*[A-Z].*")) {
//            return "Password contains no uppercase letter.\n" + passwordRules;
//        }
//        if (!pass.matches(".*[a-z].*")) {
//            return "Password contains no lowercase letter\n" + passwordRules;
//        }
////        if (!pass.matches(".*[!@#$%^&*+=?-].*")) {
//            return "Password contains no special chars.\n" + passwordRules;
//        }
//        if (containsPartOf(pass,email)) {
//            System.out.println("password contains substring of email");
//            return false;
//        }
        return null;
    }

    private static boolean containsPartOf(String pass, String username) {
        int requiredMin = 3;
        for (int i = 0; (i + requiredMin) < username.length(); i++) {
            if (pass.contains(username.substring(i, i + requiredMin))) {
                return true;
            }
        }
        return false;
    }

    public static String generateRandomNumbers(int length) {
        String numbers = "1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return sb.toString();
    }


    public static MakeDonationInterface getDonationInterface() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
                .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
                .build();

        final String SERVER_URL = MakeDonationInterface.SERVER_URL; //"https://apps1.vomozsystems.com/vomoz/";
        Retrofit retrofit = null;
        Gson gson = new GsonBuilder().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            MakeDonationInterface makeDonationInterface = retrofit.create(MakeDonationInterface.class);
            return makeDonationInterface;
        }
        return null;
    }
}
