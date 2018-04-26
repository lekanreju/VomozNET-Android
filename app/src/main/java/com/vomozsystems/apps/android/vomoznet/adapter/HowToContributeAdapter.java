package com.vomozsystems.apps.android.vomoznet.adapter;

/**
 * Created by leksrej on 1/8/18.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class HowToContributeAdapter extends RecyclerView.Adapter<HowToContributeAdapter.ViewHolder> {

    private List<CreditCard> mValues;
    private Activity activity;
    private int selectedPos = 0;
    private AlertDialog alertDialog;

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public List<CreditCard> getmValues() {
        return mValues;
    }

    public void setmValues(List<CreditCard> mValues) {
        this.mValues = mValues;
    }

    public HowToContributeAdapter(Activity activity, List<CreditCard> items) {
        mValues = items;
       this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.howto_contribute_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HowToContributeAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if(holder.mItem.getCcv().equalsIgnoreCase("check")) {
            holder.mTitleView.setText("****-" + holder.mItem.getLast4Digits());
            holder.mSubTitleView.setText(" - Check");
            holder.mImageView.setImageResource(R.mipmap.ic_bankaccount);
        }else if(holder.mItem.getCcv().equalsIgnoreCase("paypal")) {
            holder.mTitleView.setText("Paypal");
            holder.mSubTitleView.setText("");
            holder.mImageView.setImageResource(R.mipmap.ic_paypal);
        }else {
            if(null != holder.mItem.getLast4Digits() && holder.mItem.getLast4Digits().length()>1)
                holder.mTitleView.setText("****-" + holder.mItem.getLast4Digits());
            else if(null != holder.mItem.getCreditCardNumber() && holder.mItem.getCreditCardNumber().length()>=4){
                String substring = holder.mItem.getCreditCardNumber().substring(Math.max(holder.mItem.getCreditCardNumber().length() - 4, 0));
                holder.mTitleView.setText("****-" + substring);
            }else {
                holder.mTitleView.setText("Unknown");
            }
            holder.mSubTitleView.setText(" - Card");
            holder.mImageView.setImageResource(R.mipmap.ic_card);
        }

        if (selectedPos == position) {
            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.selected));
            holder.mCheckBox.setChecked(true);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.mCheckBox.setChecked(false);
        }

        if(holder.mItem.getCcv().equalsIgnoreCase("paypal")) {
            holder.mEditImageView.setImageResource(R.mipmap.ic_edit_disabled);
        }else if(holder.mItem.getId() != null) {
            holder.mEditImageView.setImageResource(R.mipmap.ic_edit_disabled);
        }else {
            holder.mEditImageView.setImageResource(R.mipmap.ic_edit);
        }

        holder.mEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mItem.getCcv().equalsIgnoreCase("paypal")) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Paypal cannot be edited or deleted. Please choose a card/checking account")
                            .show();
                }
                else if(holder.mItem.getId() != null){
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("This card/check cannot be edited or deleted")
                            .show();
                }else if(holder.mItem.getCcv().equalsIgnoreCase("check")) {
                    editBankAccount(holder.mItem);
                } else {
                    editCreditCard(holder.mItem);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
                ApplicationUtils.hideSoftKeyboard(activity);

            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
                ApplicationUtils.hideSoftKeyboard(activity);
            }
        });
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

    private void editBankAccount(final CreditCard selectedBankAccount) {
        final int indexPos = mValues.indexOf(selectedBankAccount);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_checking_account, null);
        dialogBuilder.setView(layout);

        TextView title = (TextView) layout.findViewById(R.id.dialog_title);
        title.setText("Edit Bank Account");

        final EditText accountNumberView = (EditText) layout.findViewById(R.id.edit_account_number);
        final EditText routingNumberView = (EditText) layout.findViewById(R.id.edit_routing_number);
        accountNumberView.setText(selectedBankAccount.getAccountNumber());
        routingNumberView.setText(selectedBankAccount.getRoutingNumber());
        final CheckBox chkSaveCard = (CheckBox) layout.findViewById(R.id.chk_saveCard);
        Button btnSave = (Button) layout.findViewById(R.id.btn_save);
        btnSave.setText("Save");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumber = accountNumberView.getText().toString();
                String routingNumber = routingNumberView.getText().toString();
                if (routingNumber.length() == 0) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Routing number cannot be empty.")
                            .show();
                } else if (routingNumber.length() != 9) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Routing number is invalid. Must be 9 digits")
                            .show();
                } else if (accountNumber.length() == 0) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Account number cannot be empty.")
                            .show();
                } else if (accountNumber.length() < 7) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Account number is invalid. Must be at least 7 digits.")
                            .show();
                } else {
                    String last4 = "";

                    if(accountNumber != null)
                        last4 = accountNumber.substring(Math.max(accountNumber.length() - 4, 0));
                    selectedBankAccount.setAccountNumber(accountNumber);
                    selectedBankAccount.setCreditCardNumber(accountNumber);
                    selectedBankAccount.setLast4Digits(last4);
                    selectedBankAccount.setRoutingNumber(routingNumber);
                    selectedBankAccount.setSaveCard(chkSaveCard.isChecked());

                    CreditCard creditCard = new CreditCard();
                    creditCard.setCcv("check");
                    creditCard.setAccountNumber(selectedBankAccount.getAccountNumber());
                    creditCard.setRoutingNumber(selectedBankAccount.getRoutingNumber());
                    creditCard.setCreditCardNumber(selectedBankAccount.getAccountNumber());
                    mValues.set(indexPos, selectedBankAccount);
                    notifyDataSetChanged();
                    SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    alertDialog.dismiss();
                                }
                            })
                            .setContentText("Done");
                    dialog1.show();
                }
            }
        });
        Button btnClose = (Button) layout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //ApplicationUtils.hideSoftKeyboard(DonateActivity.this);
            }
        });

        Button btnDelete = (Button) layout.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("VomozPay")
                        .setContentText("Are you sure you want to delete this card ? \n\nAccount Number: " + selectedBankAccount.getAccountNumber() + "\n\nRouting: " + selectedBankAccount.getRoutingNumber())
                        .setConfirmText("Delete")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                mValues.remove(selectedBankAccount);
                                notifyDataSetChanged();
                                sDialog.setTitleText("Deleted!")
                                        .setContentText("The selected card has been deleted")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                alertDialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void editCreditCard(final CreditCard selectedCreditCard) {
        final int indexPos = mValues.indexOf(selectedCreditCard);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_card_layout, null);
        dialogBuilder.setView(layout);

        TextView title = (TextView) layout.findViewById(R.id.dialog_title);
        title.setText("Edit Card");
        final CheckBox chkSaveCard = (CheckBox) layout.findViewById(R.id.chk_save);
        //chkSaveCard.setChecked(false);
        final EditText firstNameView = (EditText) layout.findViewById(R.id.edit_cc_first_name);
        final EditText lastNameView = (EditText) layout.findViewById(R.id.edit_cc_last_name);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpMonth.setAdapter(dataAdapter);
        String month = selectedCreditCard.getExpiration().substring(0,2);
        spinnerExpMonth.setSelection(list.indexOf(month));
        final Spinner spinnerExpYear = (Spinner) layout.findViewById(R.id.spinner_year);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        list = new ArrayList<String>();
        String myear = selectedCreditCard.getExpiration().substring(Math.max(selectedCreditCard.getExpiration().length() - 2, 0));
        int pos = 0;
        for (int i = 0; i < 15; i++) {
            String tyear = String.valueOf(year);
            list.add(tyear);
            year++;
            if(tyear.endsWith(myear)) pos = i;
        }
        dataAdapter = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpYear.setAdapter(dataAdapter);
        spinnerExpYear.setSelection(pos);
        firstNameView.setText(selectedCreditCard.getFirstName());
        lastNameView.setText(selectedCreditCard.getLastName());
        cardNumberView.setText(selectedCreditCard.getCreditCardNumber());
        ccvNumberView.setText(selectedCreditCard.getCcv());

        Button btnDelete = (Button) layout.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("VomozPay")
                        .setContentText("Are you sure you want to delete this card ? \n\nCard Number: " + selectedCreditCard.getCreditCardNumber() + "\n\nExp: " + selectedCreditCard.getExpiration())
                        .setConfirmText("Delete")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                mValues.remove(selectedCreditCard);
                                notifyDataSetChanged();
                                sDialog.setTitleText("Deleted!")
                                        .setContentText("The selected card has been deleted")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                alertDialog.dismiss();
                            }
                        })
                        .show();

            }
        });
        Button btnEdit = (Button) layout.findViewById(R.id.btn_edit);
        btnEdit.setText("Save");
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String card = cardNumberView.getText().toString();
                String firstName = firstNameView.getText().toString();
                String ccv = ccvNumberView.getText().toString();
                String lastName = lastNameView.getText().toString();
                if (firstName.length() == 0) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid firstname. Cannot be empty.")
                            .show();
                } else if (lastName.length() == 0) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid lastname. Cannot be empty.")
                            .show();
                } else if (card.startsWith("3") && card.length() != 15) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid card number. Must be 15 digits long.")
                            .show();
                } else if (!card.startsWith("3") && card.length() != 16) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid card number. Must be 16 digits long.")
                            .show();
                } else if (card.startsWith("3") && ccv.length() != 4) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid card ccv. Must be 4 digits long.")
                            .show();
                } else if (!card.startsWith("3") && ccv.length() != 3) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(activity.getResources().getString(R.string.app_name))
                            .setContentText("Invalid card ccv. Must be 3 digits long.")
                            .show();
                } else {
                    try {
                        String last2 = spinnerExpYear.getSelectedItem().toString().substring(spinnerExpYear.getSelectedItem().toString().length() - 2);
                        Boolean expired = validateCardExpiryDate(spinnerExpMonth.getSelectedItem().toString() + last2);//expiry.before(today);
                        if (null == expired) {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(activity.getResources().getString(R.string.app_name))
                                    .setContentText("This card could not be validated. Please add a new card !");
                            dialog1.show();
                        } else if (!expired) {
                            String ex = "****-" + card.substring(card.length() - 4);
                            String last4 = "";
                            if(card != null)
                                last4 = card.substring(Math.max(card.length() - 4, 0));
                            //ex = ex + ", Exp: " + spinnerExpMonth.getSelectedItem().toString() + last2;
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

                            mValues.set(indexPos, selectedCreditCard);
                            notifyDataSetChanged();
                            SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(activity.getResources().getString(R.string.app_name))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            alertDialog.dismiss();
                                        }
                                    })
                                    .setContentText("Card saved/added...please proceed to submit your donation.");
                            dialog1.show();

                        } else {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(activity.getResources().getString(R.string.app_name))
                                    .setContentText("This card has expired. Please select or add a new card !");
                            dialog1.show();
                        }
                    } catch (Exception e) {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(activity.getResources().getString(R.string.app_name))
                                .setContentText("The credit card supplied could not be added!");
                        dialog1.show();
                    }
                }
            }
        });
        Button btnCancel = (Button) layout.findViewById(R.id.btn_close);
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

    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mSubTitleView;
        public final CheckBox mCheckBox;
        public final ImageView mImageView;
        public final ImageView mEditImageView;
        public CreditCard mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.howto_contribute_title);
            mSubTitleView = (TextView) view.findViewById(R.id.howto_contribute_subtitle);
            mCheckBox = (CheckBox) view.findViewById(R.id.howto_contribute_checkbox);
            mImageView = (ImageView) view.findViewById(R.id.howto_contribute_image);
            mEditImageView = (ImageView) view.findViewById(R.id.howto_contribute_edit);
        }
    }
}
