package org.ginryan.oemtab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ginryan.oemtab.databinding.FragmentFillBinding;

public class FillFragment extends Fragment {

    public FillFragment() {
    }

    public String tip;

    public FillFragment setTip(String tip) {
        this.tip = tip;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_fill, container, false);
        FragmentFillBinding bind = FragmentFillBinding.bind(inflate);
        bind.tip.setText(tip);
        return inflate;
    }
}