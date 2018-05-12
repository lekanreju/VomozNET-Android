package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Address;
import com.vomozsystems.apps.android.vomoznet.entity.Career;
import com.vomozsystems.apps.android.vomoznet.entity.Child;
import com.vomozsystems.apps.android.vomoznet.entity.ChildAttendance;
import com.vomozsystems.apps.android.vomoznet.entity.Contact;
import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by leksrej on 5/10/16.
 */
public interface ApiInterface {

    /**
     * @param callerId
     * @return
     */
    @GET("donation-centers/{callerId}")
    Call<DonationCenterResponse> getMemberDonationCenters(@Path("callerId") String callerId, @Query("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param callerId
     * @return
     */
    @GET("users/vomoz-net/{callerId}")
    Call<GetPersonalInfoResponse> geVomozNetMemberDonationCenters(@Path("callerId") String callerId, @Query("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param callerId
     * @return
     */
    @GET("users/get-donation-centers/{callerId}")
    Call<DonationCenterResponse> getDonationCenters(@Path("callerId") String callerId, @Query("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param donationCenterCardId
     * @param type
     * @return
     */
    @GET("items/{donationCenterCardId}/{type}")
    Call<GetItemResponse> getDonationCenterItems(@Path("donationCenterCardId") Long donationCenterCardId, @Path("type") String type);

    /**
     * @param donationCenterCardId
     * @return
     */
    @GET("donation-centers/set-coords/{donationCenterCardId}")
    Call<DonationCenterResponse> setMemberDonationCenterCoords(@Path("donationCenterCardId") Long donationCenterCardId, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param orgFilter
     * @return
     */
    @GET("donation-centers/search/all")
    Call<DonationCenterResponse> getAll(@Query("org-filter") String orgFilter);

    /**
     *
     * @param orgFilter
     * @return
     */
    @GET("donation-centers/get-sliders/{org-filter}")
    Call<DonationCenterSlidersResponse> getSliders(@Path("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @GET("donation-centers/get-slider-banners/{donationCenterCardId}/{merchantIdCode}")
    Call<DonationCenterSlidersResponse> getSlidersNew(@Path("donationCenterCardId") Long donationCenterCardId, @Path("merchantIdCode") String merchantIdCode, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param callerId
     * @param centerAuthToken
     * @param appId
     * @return
     */
    @GET("users/vomoz-global/{callerId}")
    Call<GetGlobalInfoResponse> getGlobalInfo(@Path("callerId") String callerId, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/vomoz-global/login")
    Call<GetGlobalInfoResponse> vmzGlobalLogin(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param vomozGlobalInfo
     * @param centerAuthToken
     * @param appId
     * @return
     */
    @POST("users/vomoz-global/create")
    Call<BaseServiceResponse> createGlobalInfo(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/get-user-info")
    Call<UserLoginResponse> getUserInfo(@Body MemberInfoRequest memberInfoRequest, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("payments/log-payment")
    Call<BaseServiceResponse> logPayment(@Body LogPaymentRequest logPaymentRequest, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/vomoz-global/update")
    Call<BaseServiceResponse> updateGlobalInfo(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/vomoz-global/update-all")
    Call<BaseServiceResponse> updateAllGlobalInfo(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/vomoz-global/reset-password")
    Call<BaseServiceResponse> resetPasswordGlobalInfo(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @return
     */
    @GET("reference-data")
    Call<GetReferenceDataResponse> getReferenceData();

    /**
     * @param centerCardId
     * @param merchantId
     * @param appId
     * @return
     */
    @GET("donation-centers/donation-types/{centerCardId}/{merchantIdCode}")
    Call<GetDonationTypesResponse> getDonationTypes(@Path("centerCardId") Long centerCardId, @Path("merchantIdCode") String merchantId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

//    /**
//     *
//     * @param userLoginRequest
//     * @param authToken
//     * @param appId
//     * @return
//     */
//    @POST("users/login")
//    Call<UserLoginResponse> login(@Body UserLoginRequest userLoginRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param request
     * @param authToken
     * @param orgFilter
     * @param appId
     * @return
     */
    @POST("users/login")
    Call<UserLoginResponse> login(@Body MemberInfoRequest request, @Query("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param request
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/user-balance")
    Call<UserLoginResponse> getUserBalance(@Body MemberInfoRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param callerId
     * @param authToken
     * @param appId
     * @return
     */
    @GET("users/get-balance/all/{callerId}")
    Call<GetAllBalanceResponse> getUserAllBalances(@Path("callerId") String callerId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @GET("users/get-balance/all/{callerId}/{donationCenterCardId}")
    Call<GetAllBalanceResponse> getUserAllBalancesForOrg(@Path("callerId") String callerId, @Path("donationCenterCardId") Long donationCenterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @GET("users/get-org-balance/{callerId}")
    Call<GetAllBalanceResponse> getUserAllBalancesForOrgFilter(@Path("callerId") String callerId, @Query("org-filter") String orgFilter, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/send-email/{callerId}")
    Call<BaseServiceResponse> sendEmail(@Path("callerId") String callerId, @Body EmailMessage serviceMessage, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param donationCenterCardId
     * @param authToken
     * @param appId
     * @return
     */
    @GET("donation-centers/get-directory/{donationCenterCardId}")
    Call<GetPersonalInfoResponse> getDonationCenterDirectories(@Path("donationCenterCardId") Long donationCenterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param donationHistoryRequest
     * @param appId
     * @return
     */
    @POST("users/donations-history")
    Call<GetDonationHistoryResponse> getDonationsHistory(@Body GetDonationHistoryRequest donationHistoryRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param donationCenterCardId
     * @param donationType
     * @param texterCardId
     * @param startDate
     * @param endDate
     * @param appId
     * @return
     */
    @GET("users/get-pdf-statement")
    Call<byte[]> getDonationsHistoryPDF(@Path(value = "donationCenterCardId") Long donationCenterCardId,
                                        @Path(value = "donationType") String donationType,
                                        @Path(value = "texterCardId") Long texterCardId,
                                        @Path(value = "startDate") Date startDate,
                                        @Path(value = "endDate") Date endDate, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getMemberRequest
     * @param appId
     * @return
     */
    @POST("users/info")
    Call<GetMemberResponse> getMemberInfo(@Body GetMemberRequest getMemberRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getMemberRequest
     * @param appId
     * @return
     */
    @POST("users/info/personal")
    Call<GetMemberResponse> getMemberPersonalInfo(@Body GetMemberRequest getMemberRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/info/kids")
    Call<GetMemberChildrenResponse> getMemberChildren(@Body Personal personal, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/update/kids")
    Call<BaseServiceResponse> updateChild(@Body Child child, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getMemberRequest
     * @param appId
     * @return
     */
    @POST("users/info/contact")
    Call<GetMemberResponse> getMemberContactInfo(@Body GetMemberRequest getMemberRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getMemberRequest
     * @param appId
     * @return
     */
    @POST("users/info/career")
    Call<GetMemberResponse> getMemberCareerInfo(@Body GetMemberRequest getMemberRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getMemberRequest
     * @param appId
     * @return
     */
    @POST("users/info/address")
    Call<GetMemberResponse> getMemberAddressInfo(@Body GetMemberRequest getMemberRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);


    /**
     * @param personal
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/update/personal")
    Call<BaseServiceResponse> updatePersonalInfo(@Body Personal personal, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param searchText
     * @param appId
     * @return
     */
    @GET("donation-centers/search/{searchText}")
    Call<DonationCenterResponse> searchDonationCenter(@Path("searchText") String searchText, @Query("org-filter") String orgFilter, @Header("VmzPay-App-Id") String appId);

    /**
     * @param changeDefaultDonationCenterRequest
     * @param authToken
     * @param appId
     * @return
     */
    @POST("donation-centers/change-dcenter")
    Call<ChangeDefaultDonationCenterResponse> changeDefaultDonationCenter(@Body ChangeDefaultDonationCenterRequest changeDefaultDonationCenterRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param career
     * @param cardId
     * @param appId
     * @return
     */
    @POST("users/update/career")
    Call<BaseServiceResponse> updateCareerInfo(@Body Career career, @Header("VmzPay-Auth-Token") String cardId, @Header("VmzPay-App-Id") String appId);


    /**
     * @param contact
     * @param centerAuthToken
     * @param appId
     * @return
     */
    @POST("users/update/contact")
    Call<BaseServiceResponse> updateContactInfo(@Body Contact contact, @Header("VmzPay-Auth-Token") String centerAuthToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param address
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/update/address")
    Call<BaseServiceResponse> updateAddressInfo(@Body Address address, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);


    /**
     * @param getCreditCardRequest
     * @param appId
     * @return
     */
    @POST("cards")
    Call<GetCreditCardResponse> getCreditCards(@Body GetCreditCardRequest getCreditCardRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param getCreditCardRequest
     * @param appId
     * @return
     */
    @POST("cards/all/{callerId}")
    Call<GetCreditCardResponse> getAllCreditCards(@Path("callerId") String callerId, @Body GetCreditCardRequest getCreditCardRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param creditCard
     * @param appId
     * @return
     */
    @POST("cards/update")
    Call<BaseServiceResponse> updateCreditCard(@Body CreditCard creditCard, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param creditCard
     * @param appId
     * @return
     */
    @POST("cards/delete")
    Call<BaseServiceResponse> deleteCreditCard(@Body CreditCard creditCard, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param creditCard
     * @param appId
     * @return
     */
    @POST("cards/create")
    Call<CreateCreditCardResponse> createCreditCard(@Body CreditCard creditCard, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param request
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/fcm/register")
    Call<BaseServiceResponse> sendCloudMessagingRegistration(@Body RegistrationRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param request
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/signup")
    Call<DoSignUpResponse> signUp(@Body DoSignUpRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param changePasswordRequest
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/change-password")
    Call<BaseServiceResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param resetPasswordRequest
     * @param appId
     * @return
     */
    @POST("users/reset-password")
    Call<BaseServiceResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest, @Header("VmzPay-App-Id") String appId);

    /**
     * @param donationCenterId
     * @return
     */
    @GET("donation-centers/get-topics/{donationCenterId}")
    Call<GetDonationCenterTopicResponse> getDonationCenterTopics(@Path("donationCenterId") Long donationCenterId);

    /**
     *
     * @param childAttendance
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/child-checkin")
    Call<BaseServiceResponse> checkInChild(@Body ChildAttendance childAttendance, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     *
     * @param childAttendance
     * @param authToken
     * @param appId
     * @return
     */
    @POST("users/child-checkout")
    Call<BaseServiceResponse> checkOutChild(@Body ChildAttendance childAttendance, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param donationCenterId
     * @return
     */
    @GET("donation-centers/get-topics/{donationCenterId}/{texterCardId}")
    Call<GetDonationCenterTopicResponse> getDonationCenterTopicsForUser(@Path("donationCenterId") Long donationCenterId, @Path("texterCardId") Long texterCardId);

    /**
     * @param donationCenterCardId
     * @param authToken
     * @param appId
     * @return
     */
    @GET("events/{donationCenterCardId}/{texterCardId}")
    Call<GetDonationCenterEventsResponse> getAttendableDonationCenterEvents(@Path("donationCenterCardId") Long donationCenterCardId, @Path("texterCardId") Long texterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param donationCenterCardId
     * @param authToken
     * @param appId
     * @return
     */
    @GET("events/all/{donationCenterCardId}/{texterCardId}")
    Call<GetDonationCenterEventsResponse> getAllDonationCenterEvents(@Path("donationCenterCardId") Long donationCenterCardId, @Path("texterCardId") Long texterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param request
     * @param authToken
     * @param appId
     * @return
     */
    @POST("events/attend")
    Call<BaseServiceResponse> attendEvent(@Body AttendEventRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    //    @GET("paypal/confirm-payment/{paymentId}/{donationCenterCardId}")
    //    Call<PaypalConfirmationResponse> confirmPayment(@Path("paymentId") String paymentId, @Path("donationCenterCardId") Long donationCenterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("paypal/confirm-payment")
    Call<PaypalConfirmationResponse> updatePayment(@Body ConfirmPaymentRequest confirmPaymentRequest, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    /**
     * @param callerid
     * @param donationCenterId
     * @return
     */
    @GET("users/exists-in-dcenter/{callerid}/{donationCenterId}")
    Call<UserExistsInDonationCenterResponse> userExistsInDonationCenter(@Path("callerid") String callerid, @Path("donationCenterId") Long donationCenterId);

    /**
     * @param texterCardId
     * @param authToken
     * @param appId
     * @return
     */
    @GET("users/get-balance/{texterCardId}")
    Call<GetUserCurrentYearBalanceResponse> getUserCurrentYearBalance(@Path("texterCardId") Long texterCardId, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/fcm/register/global")
    Call<BaseServiceResponse> sendCloudMessagingRegistrationForGlobal(@Body RegistrationRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/update-reset-password-questions")
    Call<BaseServiceResponse> updateForgotPasswordQuestions(@Body VomozGlobalInfo vomozGlobalInfo, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("cards/copy-card")
    Call<BaseServiceResponse> copyCard(@Body CopyCardRequest request, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/create/kids")
    Call<BaseServiceResponse> createChild(@Body Child child, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

    @POST("users/delete/kids")
    Call<BaseServiceResponse> deleteChild(@Body Child child, @Header("VmzPay-Auth-Token") String authToken, @Header("VmzPay-App-Id") String appId);

}
