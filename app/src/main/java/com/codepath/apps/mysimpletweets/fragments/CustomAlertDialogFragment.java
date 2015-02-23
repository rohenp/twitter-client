package com.codepath.apps.mysimpletweets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CustomAlertDialogFragment extends DialogFragment {
    public interface CustomAlertListener {
        void onOKButton();
    }

    private CustomAlertListener listener;

    public CustomAlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static CustomAlertDialogFragment newInstance(String title, String message) {
        CustomAlertDialogFragment frag = new CustomAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        listener = (CustomAlertListener) getActivity();
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onOKButton();
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}