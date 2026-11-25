package com.example.stylemate;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SectionFragment extends Fragment {

    public static SectionFragment newInstance(String title) {
        SectionFragment fragment = new SectionFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull android.view.LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        TextView textView = new TextView(requireContext());

        String title = getArguments() != null
                ? getArguments().getString("title")
                : "StyleMate";

        textView.setText(title);
        textView.setTextSize(24);
        textView.setTextColor(
                getResources().getColor(R.color.text_black)
        );
        textView.setGravity(Gravity.CENTER);

        return textView;
    }
}