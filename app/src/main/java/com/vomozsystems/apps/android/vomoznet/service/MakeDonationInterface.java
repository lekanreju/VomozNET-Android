package com.vomozsystems.apps.android.vomoznet.service;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by leksrej on 8/23/16.
 */
public interface MakeDonationInterface {

    String SUCCESS_CODE = "1";
    String FAILURE_CODE = "2";
    Long connectTimeOut = 30L;
    Long readTimeOut = 30L;
    String SERVER_URL = "https://apps1.vomozsystems.com/vomoz/";

    //SendAuthSMSAndEmailToThisGiver

//    $vgProcessorTransactionID = $_POST['processor_transaction_id'];//’VQV2e49Wq1azbt8899'; //Got from PAYSTACK, RAVE, PAYPAL, INTERSWITCH, NIBBS for each successful transaction.
//    $vgProcessorTotalAmountContributed = $_POST['processor_total_amount'];//’75.00';  // please note that this must equate the total contributions made by this member.
//    $vgProcessorName = $_POST['processor_name'];//PAYSTACK';  // Others are RAVE, PAYPAL, INTERSWITCH, NIBBS, etc
//    $vgProcessorCurrency3LetterCode = $_POST['processor_currency_code'];//‘NGN'; // Others are USD, GBP, CAD, EUR, GHC, etc


    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<ConfirmPaymentResponse> confirmPayment(@Field("univ_auth_token") String universalAuthToken,
                                                @Field("processor_transaction_id") String processerTransactionId,
                                                @Field("processor_total_amount") String processerTotalAmountContributed,
                                                @Field("processor_name") String processerName,
                                                @Field("processor_currency_code") String processorCurrencyCode,
                                                @Field("default_donation_center_id") Long defaultDonationCenterId,
                                                @Field("default_merchant_id_code") String defaultMerchantIdCode,
                                                @Field("receiving_donation_center_id") Long receivingDonationCenterId,
                                                @Field("receiving_merchant_id_code") String receivingMerchantIdCode,
                                                @Field("default_cc_unique_id") Long defaultUniqueCCId,
                                                @Field("default_is_receiving") String defaultIsReceiving,
                                                @Field("giver_phone_number") String giverPhoneNumber,
                                                @Field("giver_first_name") String giverFirstName,
                                                @Field("giver_last_name") String giverLastName,
                                                @Field("giver_email") String giverEmail,
                                                @Field("amount_1") Double amount1,
                                                @Field("amount_2") Double amount2,
                                                @Field("amount_3") Double amount3,
                                                @Field("contribution_type_1") String contributionType1,
                                                @Field("contribution_type_2") String contributionType2,
                                                @Field("contribution_type_3") String contributionType3,
                                                @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<ChangeDonationCenterResponse> changeOrganization(@Field("current_universal_auth_token") String universalAuthToken,
                                                @Field("current_donation_center_id") Long currentDonationCenterCardId,
                                                @Field("current_merchant_id_code") String currentMerchantIdCode,
                                                @Field("new_donation_center_id") Long newDonationCenterCardId,
                                                @Field("new_merchant_id_code") String newMerchantIdCode,
                                                @Field("giver_phone_number") String giverPhoneNumber,
                                                @Field("giver_first_name") String giverFirstName,
                                                @Field("giver_last_name") String giverLastName,
                                                @Field("giver_email") String giverEmail,
                                                @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<ConfirmPaymentResponse> confirmPaymentOld(@Field("univ_auth_token") String universalAuthToken,
                                                @Field("paypal_transaction_id") String paypalTransactionId,
                                                @Field("total_amount_contributed") Double totalAmount,
                                                @Field("default_donation_center_id") Long defaultDonationCenterId,
                                                @Field("default_merchant_id_code") String defaultMerchantIdCode,
                                                @Field("receiving_donation_center_id") Long receivingDonationCenterId,
                                                @Field("receiving_merchant_id_code") String receivingMerchantIdCode,
                                                @Field("default_cc_unique_id") Long defaultUniqueCCId,
                                                @Field("default_is_receiving") String defaultIsReceiving,
                                                @Field("giver_phone_number") String giverPhoneNumber,
                                                @Field("giver_first_name") String giverFirstName,
                                                @Field("giver_last_name") String giverLastName,
                                                @Field("giver_email") String giverEmail,
                                                @Field("amount_1") Double amount1,
                                                @Field("amount_2") Double amount2,
                                                @Field("amount_3") Double amount3,
                                                @Field("contribution_type_1") String contributionType1,
                                                @Field("contribution_type_2") String contributionType2,
                                                @Field("contribution_type_3") String contributionType3,
                                                @Field("service_name") String serviceName);
    /**
     * @param universalAuthToken
     * @param defaultDonationCenterId
     * @param defaultMerchantIdCode
     * @param receivingDonationCenterId
     * @param receivingMerchantIdCode
     * @param defaultIsReceiving
     * @param giverPhoneNumber
     * @param giverFirstName
     * @param giverLastName
     * @param giverEmail
     * @param cardFirstName
     * @param cardLastName
     * @param cardNumber
     * @param cardExp
     * @param cardCcv
     * @param cardSave
     * @param amount1
     * @param amount2
     * @param amount3
     * @param contributionType1
     * @param contributionType2
     * @param contributionType3
     * @param serviceName
     * @return
     */
    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<MakeDonationResponse> makeDonationWithNewCard(@Field("univ_auth_token") String universalAuthToken,
                                                       @Field("default_donation_center_id") Long defaultDonationCenterId,
                                                       @Field("default_merchant_id_code") String defaultMerchantIdCode,
                                                       @Field("receiving_donation_center_id") Long receivingDonationCenterId,
                                                       @Field("receiving_merchant_id_code") String receivingMerchantIdCode,
                                                       @Field("default_is_receiving") String defaultIsReceiving,
                                                       @Field("giver_phone_number") String giverPhoneNumber,
                                                       @Field("giver_first_name") String giverFirstName,
                                                       @Field("giver_last_name") String giverLastName,
                                                       @Field("giver_email") String giverEmail,
                                                       @Field("card_first_name") String cardFirstName,
                                                       @Field("card_last_name") String cardLastName,
                                                       @Field("card_number") String cardNumber,
                                                       @Field("card_exp") String cardExp,
                                                       @Field("card_ccv") String cardCcv,
                                                       @Field("card_save") String cardSave,
                                                       @Field("amount_1") Double amount1,
                                                       @Field("amount_2") Double amount2,
                                                       @Field("amount_3") Double amount3,
                                                       @Field("contribution_type_1") String contributionType1,
                                                       @Field("contribution_type_2") String contributionType2,
                                                       @Field("contribution_type_3") String contributionType3,
                                                       @Field("service_name") String serviceName);

    /**
     *
     * @param universalAuthToken
     * @param defaultDonationCenterId
     * @param defaultMerchantIdCode
     * @param receivingDonationCenterId
     * @param receivingMerchantIdCode
     * @param defaultUniqueCCId
     * @param defaultIsReceiving
     * @param giverPhoneNumber
     * @param giverFirstName
     * @param giverLastName
     * @param giverEmail
     * @param amount1
     * @param amount2
     * @param amount3
     * @param contributionType1
     * @param contributionType2
     * @param contributionType3
     * @param cardSourceUniversalAuthToken
     * @param cardSourceDonationCenterId
     * @param cardSourceMerchantIdCode
     * @param cardSourceUniqueId
     * @param serviceName
     * @return
     */
    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<MakeDonationResponse> makeDonationWithExistingCard(@Field("univ_auth_token") String universalAuthToken,
                                                            @Field("default_donation_center_id") Long defaultDonationCenterId,
                                                            @Field("default_merchant_id_code") String defaultMerchantIdCode,
                                                            @Field("receiving_donation_center_id") Long receivingDonationCenterId,
                                                            @Field("receiving_merchant_id_code") String receivingMerchantIdCode,
                                                            @Field("default_cc_unique_id") Long defaultUniqueCCId,
                                                            @Field("default_is_receiving") String defaultIsReceiving,
                                                            @Field("giver_phone_number") String giverPhoneNumber,
                                                            @Field("giver_first_name") String giverFirstName,
                                                            @Field("giver_last_name") String giverLastName,
                                                            @Field("giver_email") String giverEmail,
                                                            @Field("amount_1") Double amount1,
                                                            @Field("amount_2") Double amount2,
                                                            @Field("amount_3") Double amount3,
                                                            @Field("contribution_type_1") String contributionType1,
                                                            @Field("contribution_type_2") String contributionType2,
                                                            @Field("contribution_type_3") String contributionType3,
                                                            @Field("card_source_univ_auth_token") String cardSourceUniversalAuthToken,
                                                            @Field("card_source_donation_center_id") String cardSourceDonationCenterId,
                                                            @Field("card_source_donation_center_merchant_code") String cardSourceMerchantIdCode,
                                                            @Field("card_source_cc_unique_id") String cardSourceUniqueId,
                                                            @Field("service_name") String serviceName);



    /**
     * @param donationCenterId
     * @param merchantIdCode
     * @param phoneNumber
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     * @param serviceName
     * @return
     */
    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<SignUpResponse> signUpMemberToDonationCenter(@Field("donation_center_id") Long donationCenterId,
                                                      @Field("merchant_id_code") String merchantIdCode,
                                                      @Field("new_phone_number") String phoneNumber,
                                                      @Field("new_email") String email,
                                                      @Field("new_password") String password,
                                                      @Field("new_first_name") String firstName,
                                                      @Field("new_last_name") String lastName,
                                                      @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<GetContributionEnginesResponse> getContributionEngines(@Field("donation_center_id") Long donationCenterId,
                                                                @Field("merchant_id_code") String merchantIdCode,
                                                                @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<GetContributionEnginesResponse> getContributionEnginesAdvanced(@Field("donation_center_id") Long donationCenterId,
                                                                @Field("merchant_id_code") String merchantIdCode,
                                                                @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<GetCountryStatesResponse> getCountryStates(@Field("country") String country,
                                                    @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<GetDonationStatementResponse> getPDFStatement(@Field("univ_auth_token") String universalAuthToken,
                                                       @Field("donation_center_id") Long defaultDonationCenterId,
                                                       @Field("merchant_id_code") String defaultMerchantIdCode,
                                                       @Field("donation_type") String donationType,
                                                       @Field("start_date") String startDate,
                                                       @Field("end_date") String endDate,
                                                       @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<MakeDonationResponse> makeDonationWithChecking(@Field("univ_auth_token") String universalAuthToken,
                                                        @Field("default_donation_center_id") Long defaultDonationCenterId,
                                                        @Field("default_merchant_id_code") String defaultMerchantIdCode,
                                                        @Field("receiving_donation_center_id") Long receivingDonationCenterId,
                                                        @Field("receiving_merchant_id_code") String receivingMerchantIdCode,
                                                        @Field("default_is_receiving") String defaultIsReceiving,
                                                        @Field("giver_phone_number") String giverPhoneNumber,
                                                        @Field("giver_first_name") String giverFirstName,
                                                        @Field("giver_last_name") String giverLastName,
                                                        @Field("giver_email") String giverEmail,
                                                        @Field("card_save") String cardSave,
                                                        @Field("amount_1") Double amount1,
                                                        @Field("amount_2") Double amount2,
                                                        @Field("amount_3") Double amount3,
                                                        @Field("contribution_type_1") String contributionType1,
                                                        @Field("contribution_type_2") String contributionType2,
                                                        @Field("contribution_type_3") String contributionType3,
                                                        @Field("routing_number") String routingNumber,
                                                        @Field("account_number") String accountNumber,
                                                        @Field("service_name") String serviceName);


    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<ResetPasswordConfirmationCodeResponse> resetPassword(@Field("donation_center_id") Long donationCenterId,
                                                              @Field("merchant_id_code") String merchantIdCode,
                                                              @Field("giver_phone_number") String giverPhoneNumber,
                                                              @Field("service_name") String serviceName);

    @FormUrlEncoded
    @POST("index-vmznet-test.php")
    Call<SendAuthCodeResponse> sendAuthCode(@Field("giver_phone_number") String customerPhoneNumber,
                                            @Field("giver_email") String customerEmail,
                                            @Field("giver_auth_code") String authCode,
                                                              @Field("service_name") String serviceName);
}
