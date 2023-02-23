package com.jkutkut.proyectoaadt2_jorgere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jkutkut.proyectoaadt2_jorgere.R;
import com.jkutkut.proyectoaadt2_jorgere.dialog.model.QueryFilters;

import java.util.Objects;

public class FilterDialog extends DialogFragment {

    private CheckBox chkMagnitude;
    private Spinner spnMagnitude;
    private EditText etxtMagnitude;

    private CheckBox chkCountry;
    private Spinner spnCountry;

    private FilterDialogListener listener;
    private QueryFilters filters;

    // TODO add okay button

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = requireActivity().getLayoutInflater()
                .inflate(R.layout.filter_dialog, null);

        chkMagnitude = v.findViewById(R.id.chkMagnitude);
        spnMagnitude = v.findViewById(R.id.spnMagnitude);
        etxtMagnitude = v.findViewById(R.id.etxtMagnitude);
        chkCountry = v.findViewById(R.id.chkCountry);
        spnCountry = v.findViewById(R.id.spnCountry);

        chkMagnitude.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filters.setFilterByMagnitude(isChecked);
            updateUI();
        });
        chkCountry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filters.setFilterByCountry(isChecked);
            updateUI();
        });

        spnCountry.setAdapter(
            new android.widget.ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                filters.getCountries()
            )
        );

        updateUI();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof FilterDialogListener))
            throw new RuntimeException("Context must implement FilterDialogListener");
        listener = (FilterDialogListener) context;
        filters = listener.onDialogStart();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filters.setFilterByMagnitude(chkMagnitude.isChecked());
        filters.setMagnitudeOperator(
            Objects.requireNonNull(spnMagnitude.getSelectedItem()).toString()
        );
        filters.setMagnitudeValue(
            Float.parseFloat(etxtMagnitude.getText().toString())
        );
        filters.setFilterByCountry(chkCountry.isChecked());
        filters.setCountry(
            Objects.requireNonNull(spnCountry.getSelectedItem()).toString()
        );
        listener.onDialogEnd();
    }


    public void updateUI() {
        chkMagnitude.setChecked(filters.isFilterByMagnitude());
        chkCountry.setChecked(filters.isFilterByCountry());

        spnMagnitude.setEnabled(filters.isFilterByMagnitude());
        etxtMagnitude.setEnabled(filters.isFilterByMagnitude());
        spnCountry.setEnabled(filters.isFilterByCountry());
        setMagnitudeOperationUI();
        setMagnitudeValueUI();
        setCountryUI();
    }

    private void setMagnitudeOperationUI() {
        int pos = stringSearch(
            filters.getMagnitudeOperator(),
            getResources().getStringArray(R.array.magnitude_operators)
        );
        if (pos != -1)
            spnMagnitude.setSelection(pos);
        else
            spnMagnitude.setSelection(0);
    }

    private void setMagnitudeValueUI() {
        if (filters.getMagnitudeValue() != null)
            etxtMagnitude.setText(filters.getMagnitudeValue().toString());
        else
            etxtMagnitude.setText(getString(R.string.filter_dialog_default_magnitude));
    }

    private void setCountryUI() {
        int pos = stringSearch(
            filters.getCountry(),
            filters.getCountries()
        );
        if (pos != -1)
            spnCountry.setSelection(pos);
        else
            spnCountry.setSelection(0);
    }

    private int stringSearch(String needle, String[] haystack) {
        if (needle == null || haystack == null) return -1;
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i].equals(needle))
                return i;
        }
        return -1;
    }
}