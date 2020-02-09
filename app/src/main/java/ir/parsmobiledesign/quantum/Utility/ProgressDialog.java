package ir.parsmobiledesign.quantum.Utility;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import javax.annotation.Nullable;

import ir.parsmobiledesign.quantum.R;


public class ProgressDialog extends DialogFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.progress_dialog, container, false);
        return view;
    }
}
