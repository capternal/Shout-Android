package com.shoutin.CustomClasses;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shoutin.R;

import static android.R.attr.centerColor;
import static android.R.attr.endColor;
import static android.R.attr.startColor;


/**
 * Created by student on 22/10/16.
 */

public class SegmentController extends LinearLayout {

    // SEGMENT CLICK LISTENER INTERFACE
    public static SegmentClickListerner objSegmentClickListerner;

    // VIEW PAGER INDICATOR.
    private TextView[] views;
    private Context context;
    private String TAG = SegmentController.class.getSimpleName();
    private GradientDrawable shape;

    private int unSelectedColor = 0x7f0e002e;
    private int selectedColor = 0x7f01009b;
    private int indicatorViewSize = 10;
    private int cornerRadius = 10;
    private int strock = 1;
    private String[] SegmentString = {"ON", "OFF"};

    // DEFAULT SEGMENTS DIRECTIONS
    private String left = "left";
    private String right = "right";
    private String middle = "middle";


    public String[] getSegmentString() {
        return SegmentString;
    }

    public void setSegmentString(String[] segmentString) {
        SegmentString = segmentString;
    }

    public int getStrock() {
        return strock;
    }

    public void setStrock(int strock) {
        this.strock = strock;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public int getIndicatorViewSize() {
        return indicatorViewSize;
    }

    public void setIndicatorViewSize(int indicatorViewSize) {
        this.indicatorViewSize = indicatorViewSize;
    }

    public int getUnSelectedColor() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(int unSelectedColor) {
        this.unSelectedColor = unSelectedColor;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public SegmentController(Context context) {
        super(context);
        this.context = context;
        this.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
    }

    public void build() {
        this.removeAllViews();
        backgroundStroke();
        setUpIndicator();
    }

    public static void setSegmentListener(SegmentClickListerner listener) {
        objSegmentClickListerner = listener;
    }

    public interface SegmentClickListerner {
        public void onSegmentClickListener(int position);
    }

    public SegmentController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SegmentController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SegmentController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }


    /**
     * Sets default background to the segments.
     */
    private void setUpIndicator() {
        initView();
        for (int index = 0; index < getSegmentString().length; index++) {
            if (index == 0) {
                makeRoundCorner(getUnSelectedColor(), views[index], left);
            } else if (index == getSegmentString().length - 1) {
                makeRoundCorner(getUnSelectedColor(), views[index], right);
            } else {
                makeRoundCorner(getUnSelectedColor(), views[index], middle);
            }
        }
        setCurrentSegment(0);
    }

    /**
     * SET UP THE SEGMENTS.
     */
    public void initView() {
        try {
            views = new TextView[getSegmentString().length];

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;

            for (int i = 0; i < views.length; i++) {
                views[i] = new TextView(context);
                views[i].setLayoutParams(layoutParams);
                views[i].setTextColor(getResources().getColor(R.color.colorWhite));
                views[i].setText(getSegmentString()[i]);
                views[i].setPadding(0, 10, 0, 10);
                views[i].setGravity(Gravity.CENTER);
                views[i].setSingleLine(true);
                views[i].setEllipsize(TextUtils.TruncateAt.END);
                views[i].setId(i);
                views[i].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCurrentSegment(view.getId());
                        objSegmentClickListerner.onSegmentClickListener(view.getId());
                    }
                });
                this.addView(views[i]);
            }
        } catch (NullPointerException n) {
            Log.d(TAG, "No of view for the view is zero / null.");
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *  Sets default rounded background to parent view. i.e LinearLayout
    * */

    public void backgroundStroke() {
        GradientDrawable gdDefault = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{startColor,
                centerColor, endColor});
        gdDefault.setColor(getUnSelectedColor());
        gdDefault.setStroke(getStrock(), getSelectedColor());
        float[] floats = new float[8];
        floats[0] = getCornerRadius();
        floats[1] = getCornerRadius();
        floats[2] = getCornerRadius();
        floats[3] = getCornerRadius();
        floats[4] = getCornerRadius();
        floats[5] = getCornerRadius();
        floats[6] = getCornerRadius();
        floats[7] = getCornerRadius();
        gdDefault.setCornerRadii(floats);
        this.setBackgroundDrawable(gdDefault);
    }

     /*
    *  Sets rounded background to child view. i.e TextView
    * */

    public void makeRoundCorner(int bgcolor, View v, String strDirection) {
        GradientDrawable gdDefault = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{startColor,
                centerColor, endColor});
        gdDefault.setColor(bgcolor);
        float[] floats = new float[8];
        switch (strDirection) {
            case "left":
                floats[0] = getCornerRadius();
                floats[1] = getCornerRadius();
                floats[2] = 0;
                floats[3] = 0;
                floats[4] = 0;
                floats[5] = 0;
                floats[6] = getCornerRadius();
                floats[7] = getCornerRadius();
                break;
            case "right":
                floats[0] = 0;
                floats[1] = 0;
                floats[2] = getCornerRadius();
                floats[3] = getCornerRadius();
                floats[4] = getCornerRadius();
                floats[5] = getCornerRadius();
                floats[6] = 0;
                floats[7] = 0;
                break;
            case "middle":
                floats[0] = 0;
                floats[1] = 0;
                floats[2] = 0;
                floats[3] = 0;
                floats[4] = 0;
                floats[5] = 0;
                floats[6] = 0;
                floats[7] = 0;
                break;
        }
        gdDefault.setCornerRadii(floats);
        gdDefault.setStroke(getStrock() / 3, getSelectedColor());
        v.setBackgroundDrawable(gdDefault);
    }

    /**
     * Shows current segment highlighted.
     */
    public void setCurrentSegment(@NonNull int position) {
        for (int i = 0; i < views.length; i++) {
            if (i != position) {
                System.out.println("PRASANNA PRINT : SHOW UNSELECTED");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (i == 0) {
                        makeRoundCorner(getUnSelectedColor(), views[i], left);
                    } else if (i == getSegmentString().length - 1) {
                        makeRoundCorner(getUnSelectedColor(), views[i], right);
                    } else {
                        makeRoundCorner(getUnSelectedColor(), views[i], middle);
                    }
                }
                views[i].setTextColor(getSelectedColor());
            } else {
                System.out.println("PRASANNA PRINT : SHOW SELECTED");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (position == 0) {
                        makeRoundCorner(getSelectedColor(), views[position], left);
                    } else if (position == getSegmentString().length - 1) {
                        makeRoundCorner(getSelectedColor(), views[position], right);
                    } else {
                        makeRoundCorner(getSelectedColor(), views[position], middle);
                    }
                    views[position].setTextColor(getUnSelectedColor());
                }
            }
        }
    }
}
