package com.example.ctdemoapp.ui.FAQ;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ctdemoapp.R;

public class FAQFragment extends Fragment {

    private FAQViewModel FAQViewModel;
    private TextView answer1 = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FAQViewModel =
                ViewModelProviders.of(this).get(FAQViewModel.class);
        View root = inflater.inflate(R.layout.fragment_faq, container, false);
        answer1= root.findViewById(R.id.answer1);

//        final Animation slide_down = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_down);
//        final Animation slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
//
//        //EventListener for Switches
//        Switch switch1 = (Switch) root.findViewById(R.id.switch1);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                System.out.println("state of switch is "+ isChecked);
//                if(isChecked == true){
//                    //slide to position
//                    answer1.animate().translationY(200);
//                }
//                else{
//                    answer1.animate().translationY(0);
//                }
//            }
//        });

        return root;
    }


}