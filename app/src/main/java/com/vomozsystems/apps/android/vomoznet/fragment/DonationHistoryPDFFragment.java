package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyDonationHistoryRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leksrej on 8/21/16.
 */
public class DonationHistoryPDFFragment extends DialogFragment {
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    private View view;
    private PDFView pdfView;
    private SweetAlertDialog dialog;
    private RecyclerView recyclerView;
    private MyDonationHistoryRecyclerViewAdapter adapter;
    private List<DonationHistory> donationHistoryHistories;
    private Long donationCenterCardId;
    private String merchantIdCode;
    private String donationType;
    private String endDate;
    private String startDate;
    private Long texterCardId;
    private File file;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Long getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

    public List<DonationHistory> getDonationHistoryHistories() {
        return donationHistoryHistories;
    }

    public void setDonationHistoryHistories(List<DonationHistory> donationHistoryHistories) {
        this.donationHistoryHistories = donationHistoryHistories;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Donation History")
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Send to Email",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                User user = new User();
                                //String filename="contacts_sid.vcf";
                                //File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                                //Uri path = Uri.fromFile(file);
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                Uri apkURI = FileProvider.getUriForFile(
                                        getActivity(),
                                        getActivity().getApplicationContext()
                                                .getPackageName() + ".provider", file);
                                install.setDataAndType(apkURI, "application/pdf");
                                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                // set the type to 'email'
                                emailIntent.setType("vnd.android.cursor.dir/email");
                                String to[] = {user.getEmail()};
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                                // the attachment
                                emailIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                                // the mail subject
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "VomozPay Donation History");
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.pdf_donation_history, null);
        pdfView = (PDFView) view.findViewById(R.id.pdfView);
        builder.setView(view);
        Dialog dialog = builder.create();
        //new DownloadFile(donationCenterCardId, donationType, startDate, endDate, texterCardId).execute("");
        new DownloadFile().execute(url);
        return dialog;
    }

    private MakeDonationInterface getDonationInterface() {
        final String SERVER_URL = MakeDonationInterface.SERVER_URL;
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

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void refresh(Long donationCenterCardId, String donationType, String startDate, String endDate, Long texterCardId) {
        //new DownloadFile(donationCenterCardId, donationType, startDate, endDate, texterCardId).execute("");
    }

    public void downloadFile(String fileURL, File directory) {
        try {

            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            c.setRequestProperty("Accept", "*/*");
            c.setRequestMethod("GET");
            c.connect();

            int status = c.getResponseCode();
            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, File> {

        private String url;
        private Long donationCenterCardId;
        private String donationType;
        private Long texterCardId;
        private String startDate;
        private String endDate;

//        public DownloadFile(Long donationCenterCardId, String donationType, String startDate, String endDate, Long texterCardId) {
//            this.donationCenterCardId = donationCenterCardId;
//            this.donationType = donationType;
//            this.endDate = endDate;
//            this.startDate = startDate;
//            this.texterCardId = texterCardId;
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(String... params) {

            String filePath = Environment.getExternalStorageDirectory().getPath() + "/VmzPay-donation-history.pdf";
            file = new File(filePath);
            try {
                file.createNewFile();
                //http://192.168.1.2:8080/api/users/get-pdf-statement/2/All/696/08-01-2016/08-30-2016
                //downloadFile(ApiClient.BASE_URL + "users/get-pdf-statement/" + donationCenterCardId + "/" + donationType + "/" + texterCardId + "/" + startDate + "/" + endDate, file);
                downloadFile(params[0], file);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            try {
                pdfView.fromFile(file).load();
            } catch (Exception e) {

            }
        }
    }
}
