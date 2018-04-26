package com.vomozsystems.apps.android.vomoznet.service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by leksrej on 9/2/16.
 */
public interface FileUploadService {

    /**
     * 'xAppSysAuthToken' => $vgAppSysAuthToken,
     * 'xDonationCenterID' => $vgDonationCenterID,
     * 'xDonationCenterMerchantCode' => $vgDonationCenterMerchantCode,
     * 'xTexterUniversalID' => $vgTexterUniversalID,
     * 'xTexterUniversalAuthToken' => $vgGiverUniversalAuthToken,
     * 'xTexterPictureType' => $vgTexterPictureType,
     * 'xImageFileName' =>$vgImageFileName,
     * 'xImage' =>"@$vgImage");
     */

    @Multipart
    @POST("vzMediaUploadViaApp/")
    Call<ResponseBody> upload(@Part("xAppSysAuthToken") RequestBody sysToken,
                              @Part("xDonationCenterID") RequestBody donationCenter,
                              @Part("xDonationCenterMerchantCode") RequestBody merchantCode,
                              @Part("xTexterUniversalID") RequestBody universalId,
                              @Part("xTexterUniversalAuthToken") RequestBody authToken,
                              @Part("xTexterPictureType") RequestBody pictureType,
                              @Part("xImageFileName") RequestBody fileName,
                              @Part MultipartBody.Part file);
}
