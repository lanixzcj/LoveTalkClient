package com.example.lovetalk.fragment;

import com.example.lovetalk.R;
import com.example.lovetalk.view.HeaderLayout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public class BaseFragment extends Fragment {
	HeaderLayout headerLayout;
	Context context;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		headerLayout = (HeaderLayout) getView().findViewById(R.id.headerLayout);
	}
}
