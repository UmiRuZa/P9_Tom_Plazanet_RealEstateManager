package com.openclassrooms.realestatemanager.UI.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.ItemDetailHostActivity;

public class SimulatorFragment extends Fragment {

    public SimulatorFragment() {
        // Required empty public constructor
    }

    String currency;

    int mLoanAmount;
    TextView amountTextView;
    SeekBar seekBarAmount;

    int mDuration;
    TextView durationTextView;
    SeekBar seekBarDuration;

    TextView interestTextView;

    TextView monthlyFeeTextView;

    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulator, container, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPref.getBoolean("Convert", false)) {
            currency = " €";
        } else {
            currency = " $";
        }

        ((ItemDetailHostActivity)getActivity()).configureToolbar();

        amountTextView = view.findViewById(R.id.simulator_amount_text);
        seekBarAmount = view.findViewById(R.id.simulator_amount);
        seekBarAmount.setOnSeekBarChangeListener(seekBarAmountChangeListener);

        durationTextView = view.findViewById(R.id.simulator_duration_text);
        seekBarDuration = view.findViewById(R.id.simulator_duration);
        seekBarDuration.setOnSeekBarChangeListener(seekBarDurationChangeListener);

        interestTextView = view.findViewById(R.id.simulator_interest_text_view);

        monthlyFeeTextView = view.findViewById(R.id.simulator_monthly_fee_text);


        this.setInterestTexView();
        this.setSummaryTextView();

        return view;
    }

    // ------------------
    //  AMOUNT SEEKBAR
    // ------------------

    SeekBar.OnSeekBarChangeListener seekBarAmountChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mLoanAmount = progressFormat(progress);

            amountTextView.setText("Montant de votre prêt : " + mLoanAmount + currency);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setSecondaryProgress(seekBar.getProgress());
            setSummaryTextView();
        }
    };

    public static int progressFormat(int progress) {
        int loanFormat = progress * 10000;

        return loanFormat;
    }

    // ------------------
    //  DURATION SEEKBAR
    // ------------------

    SeekBar.OnSeekBarChangeListener seekBarDurationChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mDuration = progress;
            setInterestTexView();
            durationTextView.setText("Durée de votre prêt : " + progress + " ans");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setSecondaryProgress(seekBar.getProgress());
            setSummaryTextView();
        }
    };

    // ------------------
    //  INTEREST
    // ------------------

    static double mInterest;

    private void setInterestTexView() {
        getInterestCost(mDuration, mLoanAmount);

        interestTextView.setText(String.format("%.2f", mInterest * 100) + " %");
    }

    public static int getInterestCost(int duration, int loanAmount) {
        int interestCost;

        if (duration >= 2 && duration < 10) {
            mInterest = 0.012;
        } else if (duration >= 10 && duration < 12) {
            mInterest = 0.0122;
        } else if (duration >= 12 && duration < 15) {
            mInterest = 0.0133;
        } else if (duration >= 15 && duration < 20) {
            mInterest = 0.0142;
        } else if (duration >= 20 && duration < 25) {
            mInterest = 0.0154;
        } else if (duration >= 25 && duration <= 30) {
            mInterest = 0.0168;
        } else {
            mInterest = 0.0;
        }

        if (loanAmount == 0) {
            interestCost = 0;
        } else {
            interestCost = (int) Math.round(loanAmount * mInterest);
        }
        return interestCost;
    }

    // ------------------
    //  SIMULATION SUMMARY
    // ------------------

    private void setSummaryTextView() {
        monthlyFeeTextView.setText(getMonthlyFee(mDuration, mLoanAmount, mInterest) + currency + "/mois");
    }

    public static int getMonthlyFee(int duration, int loanAmount, double interest) {
        int monthlyFee;

        if (duration == 0) {
            monthlyFee = 0;
        } else {
            monthlyFee = (int) (((loanAmount* interest) / 12) / (1 - Math.pow((1 + (interest / 12)), -(duration * 12))));
        }
        return monthlyFee;
    }
}