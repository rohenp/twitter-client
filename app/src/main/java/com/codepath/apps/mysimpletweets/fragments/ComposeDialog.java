package com.codepath.apps.mysimpletweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;


public class ComposeDialog extends DialogFragment {

    private final int MAX_LENGTH = 140;
    private EditText etCompose;
    private TextView tvUsername;
    private TextView tvName;
    private TextView tvCharacterCount;
    private ImageView ivProfileImage;
    private Button btTweet;

    public ComposeDialog() {
    }

    public interface ComposeDialogListener {
        void onFinishComposeDialog(String body);
    }

    public static ComposeDialog newInstance(User user) {
        ComposeDialog frag = new ComposeDialog();
        frag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        Bundle args = new Bundle();
        args.putParcelable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        etCompose = (EditText) view.findViewById(R.id.etBody);
        tvCharacterCount = (TextView) view.findViewById(R.id.tvCharacterCount);
        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvName = (TextView) view.findViewById(R.id.tvName);
        btTweet = (Button) view.findViewById(R.id.btTweet);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int bodyLength = etCompose.getText().toString().length();
                tvCharacterCount.setText(String.valueOf(MAX_LENGTH - bodyLength));

                if (bodyLength > MAX_LENGTH) {
                    tvCharacterCount.setTextColor(Color.RED);
                } else {
                    tvCharacterCount.setTextColor(Color.parseColor("#8D9EAA"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCompose.getText().toString().length() <= MAX_LENGTH) {
                    ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                    listener.onFinishComposeDialog(etCompose.getText().toString());
                    dismiss();
                }
            }
        });

        User user = getArguments().getParcelable("user");

        ivProfileImage.setImageResource(Color.TRANSPARENT);
        tvUsername.setText(user.getScreenName());
        tvName.setText(user.getName());

        Picasso.with(getActivity()).load(user.getProfileImageUrl()).into(ivProfileImage);

        // Show soft keyboard automatically
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }
}