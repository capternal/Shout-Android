package com.shoutin.CustomClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoutin.R;


/**
 * Created by jupitor on 20/10/16.
 */

public class CustomSnackBarLayout extends RelativeLayout {

    private TextView objTextMessage;

    public CustomSnackBarLayout(Context context) {
        super(context);

        init();
    }

    public CustomSnackBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSnackBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.custom_snackbar_layout, this);
        this.objTextMessage = (TextView) findViewById(R.id.txt_snackbar_message);
//        this.objTextMessage.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // DO NOTHING
//            }
//        });
    }
}
