package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.GiveActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.WizardCallbacks;
import com.vomozsystems.apps.android.vomoznet.adapter.HowToContributeAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.BankAccount;
import com.vomozsystems.apps.android.vomoznet.entity.ColorWrapper;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Engine;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetContributionEnginesResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetCreditCardRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetCreditCardResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link WizardCallbacks}
 * interface.
 */
public class HowToGiveFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener mListener;
    private WizardCallbacks wizardCallbacks;
    private List<CreditCard> allCards = new ArrayList<CreditCard>();
    private PaymentInfo paymentInfo;
    private HowToContributeAdapter howToContributeAdapter;
    private RecyclerView howToContributeRecyclerView;
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    private CreditCard selectedCreditCard;
    private BankAccount selectedBankAccount;
    private AlertDialog alertDialog;
    private List<Map<String, String>> contributionEngines;
    private List<BankAccount> bankAccounts;
    private List<CreditCard> cards;
    private LinearLayout myCardsView;
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HowToGiveFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HowToGiveFragment newInstance(int columnCount) {
        HowToGiveFragment fragment = new HowToGiveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_how_to_give, container, false);
        myCardsView = view.findViewById(R.id.my_cards_view);
        wizardCallbacks.setTitle("Give - Payment Method");

        Button nextButton = (Button) view.findViewById(R.id.next_button);
        Button previousButton = (Button) view.findViewById(R.id.previous_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = simpleItemRecyclerViewAdapter.getSelectedPos()>0 ? simpleItemRecyclerViewAdapter.getSelectedPos()+1: simpleItemRecyclerViewAdapter.getSelectedPos();
                Map<String, String> engine = contributionEngines.get(pos);
                if(engine.get("type").equalsIgnoreCase("check") || engine.get("type").equalsIgnoreCase("card")) {
                    try {
                        if(null != howToContributeAdapter.getmValues() && howToContributeAdapter.getmValues().size()>0) {
                            CreditCard creditCard = howToContributeAdapter.getmValues().get(howToContributeAdapter.getSelectedPos());
                            paymentInfo.setCreditCard(creditCard);
                            paymentInfo.setPaymentEngine(engine);
                            wizardCallbacks.onNext(GiveActivity.PAYMENT_ENGINE_PAGE, paymentInfo);
                        }else {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.app_name))
                                    .setContentText("Please select or enter card or checking account details")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .setConfirmText("OK")
                                    .show();
                        }
                    }catch(Exception e) {

                    }
                }else {
                    paymentInfo.setPaymentEngine(engine);
                    wizardCallbacks.onNext(GiveActivity.PAYMENT_ENGINE_PAGE, paymentInfo);
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wizardCallbacks.onPrevious(GiveActivity.PAYMENT_ENGINE_PAGE, paymentInfo);
            }
        });

        howToContributeAdapter = new HowToContributeAdapter(getActivity(), allCards);
        howToContributeRecyclerView = (RecyclerView) view.findViewById(R.id.howto_contribute_recyclerview);
        LinearLayoutManager howToContributeLayoutManager = new LinearLayoutManager(getActivity());
        howToContributeRecyclerView.setLayoutManager(howToContributeLayoutManager);
        howToContributeRecyclerView.addItemDecoration(new com.vomozsystems.apps.android.vomoznet.utility.DividerItemDecoration(getContext(), 1));
        howToContributeRecyclerView.setAdapter(howToContributeAdapter);
        howToContributeAdapter.notifyDataSetChanged();

        try {
            setContributionEngines(view, paymentInfo.getColorWrapper());
        }catch (Exception e) {

        }
        return view;
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

    private void setContributionEngines(final View view, ColorWrapper colorWrapper) {
        MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.item_list_engines);
        contributionEngines = new ArrayList<Map<String,String>>();
        howToContributeAdapter.setmValues(allCards);
        howToContributeAdapter.notifyDataSetChanged();
        Call<GetContributionEnginesResponse> callm = makeDonationInterface.getContributionEnginesAdvanced(colorWrapper.getCardId(), colorWrapper.getMerchantIdCode(), "ListContributionEnginesForThisDonationCenterAdvance");
        callm.enqueue(new Callback<GetContributionEnginesResponse>() {
            @Override
            public void onResponse(Call<GetContributionEnginesResponse> call, Response<GetContributionEnginesResponse> response) {
                if (response.isSuccessful() && null != response.body().getStatus() && response.body().getStatus().equalsIgnoreCase("1")) {
                    contributionEngines = response.body().getContributionEngines();
                    final Realm realm = Realm.getDefaultInstance();
                    final Config config = realm.where(Config.class).findFirst();

                    Button addCard = (Button) view.findViewById(R.id.howto_contribute_addCard);
                    if(contributionEnginesContains("card")) {
                        addCard.setVisibility(VISIBLE);
                        addCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newCreditCard(new TextView(getActivity()), config);
                            }
                        });
                    }else {
                        addCard.setVisibility(GONE);
                    }

                    Button addCheck = (Button) view.findViewById(R.id.howto_contribute_addCheck);
                    if(contributionEnginesContains("check")) {
                        addCheck.setVisibility(VISIBLE);
                        addCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newBankAccount(new TextView(getActivity()));
                            }
                        });
                    }else {
                        addCheck.setVisibility(GONE);
                    }

                    List<Engine> items = new ArrayList<Engine>();
                    Engine item = new Engine();

                    if(contributionEnginesContains("card")) {
                        item.setTitle("Use\nCard");
                        item.setImage(R.mipmap.ic_card_color);
                        items.add(item);
                    }

                    if(contributionEnginesContains("paypal")) {
                        item = new Engine();
                        item.setTitle("Use\nPaypal");
                        item.setImage(R.mipmap.ic_paypal_color);
                        items.add(item);
                    }

                    if(contributionEnginesContains("paystack")) {
                        item = new Engine();
                        item.setTitle("Use\nPaystack");
                        item.setImage(R.mipmap.ic_paystack_color);
                        items.add(item);
                    }

                    if(contributionEnginesContains("rave")) {
                        item = new Engine();
                        item.setTitle("Use\nRave");
                        item.setImage(R.mipmap.ic_rave);
                        items.add(item);
                    }
                    LinearLayout cardsView = (LinearLayout) view.findViewById(R.id.my_cards_view);

                    simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(items);
                    recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
                    int numberOfColumns = items.size();
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
                    ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
                    recyclerView.addItemDecoration(itemDecoration);

                    setCreditCardAndBankAccounts();

                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.app_name))
                            .setContentText("Contribution engines cannot be retrieved for this organization")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmText("OK")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<GetContributionEnginesResponse> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText("Contribution engines cannot be retrieved for this organization")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmText("OK")
                        .show();
            }
        });
    }

    private boolean contributionEnginesContains(String engine) {
        for(Map<String, String> map: contributionEngines){
            if(map.get("type").equalsIgnoreCase(engine))
                return true;
        }
        return false;
    }

    private void setCreditCardAndBankAccounts() {
        Realm realm = Realm.getDefaultInstance();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        User user = realm.where(User.class).findFirst();
        if (null != user && null != donationCenter) {
            GetCreditCardRequest getCreditCardRequest = new GetCreditCardRequest();
            getCreditCardRequest.setDonationCenterCardId(donationCenter.getCardId());
            getCreditCardRequest.setEmail(user.getEmail());
            getCreditCardRequest.setPassword(user.getPassword());
            getCreditCardRequest.setTexterCardId(user.getTexterCardId());
            getCreditCardRequest.setCallerId(user.getMobilePhone());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            String authToken = "";//
            //PreferenceManager.getDefaultSharedPreferences(this).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
            //Call<GetCreditCardResponse> call = apiService.getCreditCards(getCreditCardRequest, authToken, ApplicationUtils.APP_ID);
            Config config = realm.where(Config.class).findFirst();
            Call<GetCreditCardResponse> call = apiService.getAllCreditCards(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), getCreditCardRequest, authToken, ApplicationUtils.APP_ID);
            call.enqueue(new Callback<GetCreditCardResponse>() {
                @Override
                public void onResponse(Call<GetCreditCardResponse> call, Response<GetCreditCardResponse> response) {
                    if (response.isSuccessful() && null != response.body().getResponseData() && response.body().getResponseData().size()>0) {
                        List<CreditCard> tempCards = new ArrayList<CreditCard>();
                        bankAccounts = new ArrayList<BankAccount>();
                        cards = new ArrayList<CreditCard>();
                        for(CreditCard card: allCards) {
                            if(card.getId() == null)
                                tempCards.add(card);
                        }
                        allCards = tempCards;
                        tempCards = new ArrayList<CreditCard>();
                        for (CreditCard card : response.body().getResponseData()) {
                            allCards.add(card);
                            if (null != card.getCcv() && card.getCcv().equals("check")) {
                                BankAccount bankAccount = new BankAccount();
                                bankAccount.setId(card.getId());
                                bankAccount.setCreditCardId(card.getCreditCardId());
                                bankAccount.setMerchantIdCode(card.getMerchantIdCode());
                                //bankAccount.setAccountNumber(card.getCreditCardNumber());
                                bankAccount.setLast4Digits(card.getLast4Digits());
                                bankAccounts.add(bankAccount);
                            } else if (null != card.getCreditCardNumber() && null != card.getLast4Digits()) {
                                cards.add(card);
                            }
                        }


                        for(CreditCard creditCard: allCards) {
                            if(contributionEnginesContains("CARD") &&
                                    !creditCard.getCcv().equalsIgnoreCase("check")){
                                tempCards.add(creditCard);
                            }else if(contributionEnginesContains("CHECK") && creditCard.getCcv().equalsIgnoreCase("check")) {
                                tempCards.add(creditCard);
                            }
                        }
                        howToContributeAdapter.setmValues(tempCards);
                        howToContributeAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<GetCreditCardResponse> call, Throwable t) {
                    Log.i(getClass().getSimpleName(), "");
                }
            });
        }
    }

    private void newCreditCard(final TextView textView, Config config) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.new_card_layout, null);
        dialogBuilder.setView(layout);

        TextView title = (TextView) layout.findViewById(R.id.dialog_title);
        title.setText("New Card");
        final CheckBox chkSaveCard = (CheckBox) layout.findViewById(R.id.chk_saveCard);
        chkSaveCard.setChecked(false);
        final EditText firstNameView = (EditText) layout.findViewById(R.id.edit_cc_first_name);
        final EditText lastNameView = (EditText) layout.findViewById(R.id.edit_cc_last_name);
        if(config != null) {
            firstNameView.setText(config.getFirstName());
            lastNameView.setText(config.getLastName());
        }
        final EditText cardNumberView = (EditText) layout.findViewById(R.id.edit_cc_number);
        final EditText ccvNumberView = (EditText) layout.findViewById(R.id.edit_cc_ccv);
        final Spinner spinnerExpMonth = (Spinner) layout.findViewById(R.id.spinner_month);
        List<String> list = new ArrayList<String>();
        list.add("01");
        list.add("02");
        list.add("03");
        list.add("04");
        list.add("05");
        list.add("06");
        list.add("07");
        list.add("08");
        list.add("09");
        list.add("10");
        list.add("11");
        list.add("12");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpMonth.setAdapter(dataAdapter);
//        spinnerExpMonth.setSelection(list.indexOf(creditCard.getExpiration().substring(0,2)));

        final Spinner spinnerExpYear = (Spinner) layout.findViewById(R.id.spinner_year);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        list = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            list.add(String.valueOf(year));
            year++;
        }
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpYear.setAdapter(dataAdapter);
//        spinnerExpYear.setSelection(list.indexOf(creditCard.getExpiration().substring(3)));

        Button btnSelect = (Button) layout.findViewById(R.id.btn_add);
        btnSelect.setText("Use This Card");
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card = cardNumberView.getText().toString();
                String firstName = firstNameView.getText().toString();
                String ccv = ccvNumberView.getText().toString();
                String lastName = lastNameView.getText().toString();
                if (firstName.length() == 0) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid firstname. Cannot be empty.")
                            .show();
                } else if (lastName.length() == 0) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid lastname. Cannot be empty.")
                            .show();
                } else if (card.startsWith("3") && card.length() != 15) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid card number. Must be 15 digits long.")
                            .show();
                } else if (!card.startsWith("3") && card.length() != 16) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid card number. Must be 16 digits long.")
                            .show();
                } else if (card.startsWith("3") && ccv.length() != 4) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid card ccv. Must be 4 digits long.")
                            .show();
                } else if (!card.startsWith("3") && ccv.length() != 3) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Invalid card ccv. Must be 3 digits long.")
                            .show();
                } else {
                    try {
                        String last2 = spinnerExpYear.getSelectedItem().toString().substring(spinnerExpYear.getSelectedItem().toString().length() - 2);
                        Boolean expired = validateCardExpiryDate(spinnerExpMonth.getSelectedItem().toString() + last2);//expiry.before(today);
                        if (null == expired) {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("This card could not be validated. Please add a new card !");
                            dialog1.show();
                        } else if (!expired) {
                            String ex = "****-" + card.substring(card.length() - 4);
                            String last4 = "";
                            if(card != null)
                                last4 = card.substring(Math.max(card.length() - 4, 0));
                            //ex = ex + ", Exp: " + spinnerExpMonth.getSelectedItem().toString() + last2;
                            selectedCreditCard = new CreditCard();
                            selectedCreditCard.setLast4Digits(last4);
                            selectedCreditCard.setFirstName(firstName);
                            selectedCreditCard.setLastName(lastName);
                            selectedCreditCard.setCreditCardNumber(card);
                            selectedCreditCard.setCcv(ccv);
                            selectedCreditCard.setExpiration(spinnerExpMonth.getSelectedItem().toString() + last2);
                            selectedCreditCard.setAddress("");
                            selectedCreditCard.setCity("");
                            selectedCreditCard.setState("");
                            selectedCreditCard.setCountry("");
                            selectedCreditCard.setZipCode("");
                            selectedCreditCard.setSaveCard(chkSaveCard.isChecked());
                            //cards.add(selectedCreditCard);
                            allCards.add(0, selectedCreditCard);
                            howToContributeAdapter.setmValues(allCards);
                            howToContributeAdapter.notifyDataSetChanged();
                            textView.setText(ex);
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            alertDialog.dismiss();

                                        }
                                    })
                                    .setContentText("Card selected...please proceed to submit your donation.");
                            dialog1.show();

                        } else {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("This card has expired. Please select or add a new card !");
                            dialog1.show();
                        }
                    } catch (Exception e) {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("The credit card supplied could not be added!");
                        dialog1.show();
                    }
                }
            }
        });
        Button btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //ApplicationUtils.hideSoftKeyboard(DonateActivity.this);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private Boolean validateCardExpiryDate(String input) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMyy");
            simpleDateFormat.setLenient(false);
            Date expiry = simpleDateFormat.parse(input);
            boolean expired = expiry.before(new Date());
            return expired;
        } catch (Exception e) {
            return null;
        }
    }

    private void newBankAccount(final TextView textView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.checking_account, null);
        dialogBuilder.setView(layout);

        TextView title = (TextView) layout.findViewById(R.id.dialog_title);
        title.setText("New Bank Account");

        final EditText accountNumberView = (EditText) layout.findViewById(R.id.edit_account_number);
        final EditText routingNumberView = (EditText) layout.findViewById(R.id.edit_routing_number);
        final CheckBox chkSaveCard = (CheckBox) layout.findViewById(R.id.chk_saveCard);
        Button btnSelect = (Button) layout.findViewById(R.id.btn_add);
        btnSelect.setText("Use This Account");
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumber = accountNumberView.getText().toString();
                String routingNumber = routingNumberView.getText().toString();
                if (routingNumber.length() == 0) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Routing number cannot be empty.")
                            .show();
                } else if (routingNumber.length() != 9) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Routing number is invalid. Must be 9 digits")
                            .show();
                } else if (accountNumber.length() == 0) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Account number cannot be empty.")
                            .show();
                } else if (accountNumber.length() < 7) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Account number is invalid. Must be at least 7 digits.")
                            .show();
                } else {
                    selectedBankAccount = new BankAccount();
                    String ex = "****-" + accountNumber.substring(accountNumber.length() - 4);
                    String last4 = "";
                    if(accountNumber != null)
                        last4 = accountNumber.substring(Math.max(accountNumber.length() - 4, 0));
                    selectedBankAccount.setAccountNumber(accountNumber);
                    selectedBankAccount.setLast4Digits(last4);
                    selectedBankAccount.setRoutingNumber(routingNumber);
                    selectedBankAccount.setSaveCard(chkSaveCard.isChecked());
                    //bankAccountEditText.setText(accountNumber);
//                    if (!bankAccounts.contains(selectedBankAccount))
//                        bankAccounts.add(selectedBankAccount);
                    textView.setText(ex);
                    CreditCard creditCard = new CreditCard();
                    creditCard.setCcv("check");
                    creditCard.setLast4Digits(selectedBankAccount.getLast4Digits());
                    creditCard.setAccountNumber(selectedBankAccount.getAccountNumber());
                    creditCard.setRoutingNumber(selectedBankAccount.getRoutingNumber());
                    allCards.add(0, creditCard);
                    howToContributeAdapter.setmValues(allCards);
                    howToContributeAdapter.notifyDataSetChanged();
                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    //imgEditBankAccount.setVisibility(View.VISIBLE);
                                    //ApplicationUtils.hideSoftKeyboard(DonateActivity.this);
                                }
                            })
                            .setContentText("Done");
                    dialog1.show();
                }
            }
        });
        Button btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //ApplicationUtils.hideSoftKeyboard(DonateActivity.this);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Engine> mValues;
        private int selectedPos = 0;


        public int getSelectedPos() {
            return selectedPos;
        }

        public void setSelectedPos(int selectedPos) {
            this.selectedPos = selectedPos;
        }

        public SimpleItemRecyclerViewAdapter(List<Engine> items) {
            mValues = items;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_engine_type, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {

            try {
                if (selectedPos == position) {
                    // Here I am just highlighting the background
                    holder.mLayout.setBackground(getResources().getDrawable(R.drawable.selector));
                } else {
                    holder.mLayout.setBackgroundColor(Color.TRANSPARENT);
                }

                holder.mItem = mValues.get(position);
                holder.mImageView.setImageResource(mValues.get(position).getImage());
                holder.mNameView.setText(mValues.get(position).getTitle());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(selectedPos);
                        selectedPos = position;
                        notifyItemChanged(selectedPos);
                        ApplicationUtils.hideSoftKeyboard(getActivity());
                        if(selectedPos == 0) {
                            myCardsView.setVisibility(VISIBLE);
                        }else {
                            myCardsView.setVisibility(GONE);
                        }
                    }
                });
            } catch (Exception e) {

            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mNameView;
            public final ImageView mImageView;
            public final LinearLayout mLayout;
            public Engine mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mNameView = (TextView) view.findViewById(R.id.txt_engine_type);
                mImageView = view.findViewById(R.id.img_engine);
                mLayout = view.findViewById(R.id.layout_engine_background);
            }

            @Override
            public String toString() {
                return null;//super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
