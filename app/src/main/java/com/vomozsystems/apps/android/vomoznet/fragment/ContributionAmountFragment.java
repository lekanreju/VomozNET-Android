package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.GiveActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.WizardCallbacks;
import com.vomozsystems.apps.android.vomoznet.adapter.ContributionTypeAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.ColorWrapper;
import com.vomozsystems.apps.android.vomoznet.entity.ContributionType;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationTypesResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContributionAmountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContributionAmountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Map<String, String> contributionEngineMap;
    private PaymentInfo paymentInfo;
    private WizardCallbacks wizardCallbacks;

    public ContributionAmountFragment() {
        // Required empty public constructor
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContributionAmountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContributionAmountFragment newInstance() {
        ContributionAmountFragment fragment = new ContributionAmountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WizardCallbacks) {
            wizardCallbacks = (WizardCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WizardCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        wizardCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contribution_amount, container, false);
        contributionEngineMap = paymentInfo.getPaymentEngine();
        TextView receiverNameTextView = (TextView) view.findViewById(R.id.receiver_name_textview);
        TextView paymentMthodTextView = (TextView) view.findViewById(R.id.payment_method_textview);

        wizardCallbacks.setTitle("Give - Amounts");

        if(null != paymentInfo) {
            if(null != paymentInfo.getColorWrapper()) {
                downloadOrganizationDonationTypes(paymentInfo.getColorWrapper());
                selectContributionType(view);
                receiverNameTextView.setText(paymentInfo.getColorWrapper().getName());
                if (paymentInfo.getCreditCard() != null) {
                    paymentMthodTextView.setText(paymentInfo.getPaymentEngine().get("type") + ": ****-" + paymentInfo.getCreditCard().getLast4Digits());
                }else {
                    paymentMthodTextView.setText(paymentInfo.getPaymentEngine().get("type"));
                }
            }
        }

        Button previousButton = (Button) view.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wizardCallbacks.onPrevious(GiveActivity.AMOUNTS_PAGE, paymentInfo);
            }
        });

        Button nextButton = (Button) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                List<ContributionType> contributionTypes = realm.where(ContributionType.class).findAll();
                boolean valid = true;
                String message = null;
                int index = 0;
                for (ContributionType contributionType : contributionTypes) {
                    if (null != contributionType.getAmount() && contributionType.getAmount() > 0 && null != contributionType.getTypeId()) {
                        valid = true;
                    } else {
                        valid = false;
                        message = "Invalid amount or contribution type for contribution #" + (index + 1);
                        break;
                    }
                    index++;
                }
                if (!valid || message != null) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmText("OK")
                            .setContentText(message)
                            .show();
                }else {
                    wizardCallbacks.onNext(GiveActivity.AMOUNTS_PAGE, paymentInfo);
                }
            }
        });
        return view;
    }

    private void downloadOrganizationDonationTypes(ColorWrapper colorWrapper) {
        final Realm realm = Realm.getDefaultInstance();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetDonationTypesResponse> call = apiInterface.getDonationTypes(colorWrapper.getCardId(), colorWrapper.getMerchantIdCode(), "", "");
        call.enqueue(new Callback<GetDonationTypesResponse>() {
            @Override
            public void onResponse(Call<GetDonationTypesResponse> call, Response<GetDonationTypesResponse> response) {
                if (response.isSuccessful()) {
                    GetDonationTypesResponse getDonationTypesResponse = response.body();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(getDonationTypesResponse.getResponseData());
                    realm.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<GetDonationTypesResponse> call, Throwable t) {

            }
        });
    }

    private void selectContributionType(View view) {
        String engineType = contributionEngineMap.get("type");
        if(contributionEngineMap != null) {
            String currencyCode = "USD";
            String country = "US";
            if (engineType.equalsIgnoreCase("PAYPAL")) {
                currencyCode = contributionEngineMap.get("paypal_currency");
                country = contributionEngineMap.get("paypal_country");
            } else if (engineType.equalsIgnoreCase("PAYSTACK")) {
                currencyCode = contributionEngineMap.get("paystack_currency");
            } else if (engineType.equalsIgnoreCase("RAVE")) {
                currencyCode = contributionEngineMap.get("rave_currency");
            }
            final Realm realm = Realm.getDefaultInstance();
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setGroupingUsed(true);

            final List<ContributionType> contributionTypes = realm.where(ContributionType.class).findAll();
            final TextView totalAmountToContribute = (TextView) view.findViewById(R.id.contribution_total_amount);

            if (contributionTypes != null && contributionTypes.size() > 0) {
                Double total = 0D;
                for (ContributionType contributionType : contributionTypes) {
                    total += contributionType.getAmount();
                }
                totalAmountToContribute.setText(ApplicationUtils.getCurrencySymbol(currencyCode) + numberFormat.format(total) + " " + currencyCode);
            } else {
                List<ContributionType> types = realm.where(ContributionType.class).findAll();
                ContributionType contributionType = new ContributionType();
                contributionType.setId(types.size() + 1);
                contributionType.setAmount(0D);
                contributionType.setDescription("Choose Type");
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(contributionType);
                realm.commitTransaction();
                totalAmountToContribute.setText(ApplicationUtils.getCurrencySymbol(currencyCode) + numberFormat.format(0D) + " " + currencyCode);
            }

            Button addContributionTypeButton = (Button) view.findViewById(R.id.contribution_type_add_button);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.contribution_type_recyclerview);
            final ContributionTypeAdapter contributionTypeAdapter = new ContributionTypeAdapter(getActivity(), totalAmountToContribute, paymentInfo);
            recyclerView.setAdapter(contributionTypeAdapter);
            contributionTypeAdapter.notifyDataSetChanged();

            addContributionTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ContributionType> types = realm.where(ContributionType.class).findAll();
                    if (types.size() < 3) {
                        ContributionType contributionType = new ContributionType();
                        contributionType.setId(types.size() + 1);
                        contributionType.setAmount(0D);
                        contributionType.setDescription("Choose Type");
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(contributionType);
                        realm.commitTransaction();
                        types = realm.where(ContributionType.class).findAll();
                        contributionTypeAdapter.setmValues(types);
                        contributionTypeAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

}
