package com.vomozsystems.apps.android.vomoznet.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.ServiceGenerator;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;
import com.vomozsystems.apps.android.vomoznet.utility.FilePath;
import com.vomozsystems.apps.android.vomoznet.utility.ImageUtils;
import com.vomozsystems.apps.android.vomoznet.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by leksrej on 3/4/18.
 */

public class BaseFragment extends Fragment {

    private static final int TIMEOUT_VALUE = 150000;
    private Dialog dialog;
    protected View view;
    protected ProgressDialog progressDialog;
    protected int maxBufferSize = 1 * 1024 * 1024;
    protected String userChoosenTask;
    protected final int SELECT_FILE = 0;
    protected final int REQUEST_CAMERA = 1;
    protected final int CAMERA_REQUEST_CODE = 100;
    private int serverResponseCode = 0;
    private String serverResponseMessage;
    private ImageView targetImageView;
    private String targetImageType;

    //protected CircularImageView imgPerson;
    //private ImageView targetImageView;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission was not granted to perform this task", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (null != userChoosenTask) {
                        if (userChoosenTask.equals("Take Photo"))
                            cameraIntent();
                        else if (userChoosenTask.equals("Choose from Library"))
                            galleryIntent();
                    }
                } else {
                    //code for deny
                }
                break;
        }
    }

    public static final String TARGET_IMAGE_TYPE = "image_type";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    protected void displayProfileImage() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_image_action);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
        dialog.getWindow().setLayout(width, height);

        final ImageView imgProfile = (ImageView) dialog.findViewById(R.id.img_full_profile);
        final Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        if (null != user && null != user.getTexterCardId()) {
            try {
                if (null != user.getProfileImage() && !user.getProfileImage().equals("0")) {
                    final ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progress);
                    progressBar.setVisibility(View.VISIBLE);
                    String url = ServiceGenerator.PROFILE_PICS_BASE_URL + "/" + user.getProfileImage().charAt(0) + "/" + user.getTexterCardId().charAt(0) + "/" + user.getProfileImage();
                    Picasso.with(getActivity())
                            .load(url)
                            //.fit()
                            .resize(width, height)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_person_img)
                            .into(imgProfile, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(GONE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(GONE);
                                    }
                                }
                            });

                } else {
                    Picasso.with(getActivity())
                            .load(R.mipmap.ic_person_img)
                            .fit()
                            //.centerCrop()
                            .into(imgProfile);
                }
            } catch (Exception e) {

            }
        }
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_profile_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView btnSetImage = (TextView) dialog.findViewById(R.id.btn_profile_set);
        btnSetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImage();
                displayProfileImage();
            }
        });

        dialog.show();
    }

    public void selectImage(ImageView targetImageView, String targetImageType) {
        this.targetImageType = targetImageType;
        this.targetImageView = targetImageView;
        if (dialog != null)
            dialog.dismiss();

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void cameraIntent() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

    }

    protected void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @SuppressWarnings("deprecation")
    protected void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                //bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri selectedFileUri = data.getData();
                String selectedFilePath = FilePath.getPath(getActivity(), selectedFileUri);

                File selectedFile = new File(selectedFilePath);
                if (selectedFile.length() > maxBufferSize) {
                    selectedFilePath = ImageUtils.compressImage(selectedFilePath);
                }

                if (selectedFilePath != null) {
                    final String filePath = selectedFilePath;
                    progressDialog = ProgressDialog.show(getActivity(), "", "Uploading File...", true);
                    Realm realm = Realm.getDefaultInstance();
                    DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                    User user = realm.where(User.class).findFirst();
                    final Map<String, String> params = new HashMap<String, String>();
                    params.put("xAppSysAuthToken", "35a6989dcdaf55e286851a11abe994a4");
                    params.put("xDonationCenterID", donationCenter.getCardId() + "");
                    params.put("xDonationCenterMerchantCode", donationCenter.getMerchantIdCode());
                    params.put("xTexterUniversalID", user.getTexterCardId());
                    params.put("xTexterUniversalAuthToken", user.getAuthToken());
                    params.put("xTexterPictureType", targetImageType);
                    params.put("xImageFileName", filePath);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFile(filePath, params, view);
                        }
                    }).start();
                } else {
                    Toast.makeText(getActivity(), "Please choose a File First", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                String msg = e.getMessage();
                e.printStackTrace();
            }
        }
    }

    protected void onCaptureImageResult(Intent data) {
        Realm realm = Realm.getDefaultInstance();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (null != donationCenter) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

                Uri selectedFileUri = data.getData();
                String selectedFilePath = FilePath.getPath(getActivity(), selectedFileUri);

                selectedFilePath = resizePic(targetImageView, selectedFilePath);
                final String selectedPath = selectedFilePath;
                progressDialog = ProgressDialog.show(getActivity(), "", "Uploading File...", true);
                realm = Realm.getDefaultInstance();
                User user = realm.where(User.class).findFirst();
                final Map<String, String> params = new HashMap<String, String>();
                params.put("xAppSysAuthToken", "35a6989dcdaf55e286851a11abe994a4");
                params.put("xDonationCenterID", donationCenter.getCardId() + "");
                params.put("xDonationCenterMerchantCode", donationCenter.getMerchantIdCode());
                params.put("xTexterUniversalID", user.getTexterCardId());
                params.put("xTexterUniversalAuthToken", user.getAuthToken());
                params.put("xTexterPictureType",  targetImageType);
                params.put("xImageFileName", selectedFilePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(selectedPath, params, view);
                    }
                }).start();

            } catch (FileNotFoundException e) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(getActivity(), "Profile picture cannot be uploaded ", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(getActivity(), "Profile picture cannot be uploaded ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(getActivity(), "Profile picture cannot be uploaded " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String resizePic(ImageView mImageView, String mCurrentPhotoPath) throws Exception {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        if (targetH > 0 && targetW > 0) {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            try {
                FileOutputStream out = new FileOutputStream(mCurrentPhotoPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mCurrentPhotoPath;
            //mImageView.setImageBitmap(bitmap);
        }
        throw new Exception("Image was not successfully resized. Please try again.");
    }

    public void uploadFile(final String selectedFilePath, Map<String, String> params, final View view) {
        String result = null;
        HttpsURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        File selectedFile = new File(selectedFilePath);

        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            progressDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "File does not exist !", Toast.LENGTH_SHORT).show();
                }
            });
            //return false;
        } else if (selectedFile.length() > maxBufferSize) {
            progressDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "File is too big. Please select a file less than 1MB !", Toast.LENGTH_SHORT).show();
                }
            });
            //return false;
        } else {
            try {
                progressDialog.setTitle("Sending file...");
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(ServiceGenerator.API_BASE_FILE_URL);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setConnectTimeout(TIMEOUT_VALUE);
                connection.setReadTimeout(TIMEOUT_VALUE);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"xImage\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
//                if(bytesAvailable < 1000000) {
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];
                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                if (null != params && params.size() > 0)
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd);
                        dataOutputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        dataOutputStream.writeBytes(lineEnd);
                        dataOutputStream.writeBytes(entry.getValue());
                        dataOutputStream.writeBytes(lineEnd);
                    }

                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                progressDialog.setTitle("Getting server response...");
                serverResponseCode = connection.getResponseCode();
                serverResponseMessage = connection.getResponseMessage();

                Log.i(getClass().getSimpleName(), "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                progressDialog.setTitle("Server response received - " + serverResponseCode);
                if (serverResponseCode == 200) {
                    progressDialog.dismiss();
                    final Realm realm = Realm.getDefaultInstance();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Picture profile uploaded successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    //return true;
                } else {
                    progressDialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "File was not uploaded successfully ", Toast.LENGTH_LONG).show();
                        }
                    });
                    //return false;
                }
            } catch (SocketTimeoutException e) {
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "5020 - File upload was not successful - More than 2.5 minutes elapsed", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (final FileNotFoundException e) {
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "5100 - File upload was not successful ", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (final IOException e) {
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "5105 - File upload was not successful " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (final Exception e) {
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "5107 - File upload was not successful " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
        //return false;
    }

    protected void finish() {

    }
}
