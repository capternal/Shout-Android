package com.shoutin.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.shoutin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jupitor on 16/12/16.
 */

public class CustomListPopup extends LinearLayout {

    private ArrayList<CustomListModel> arrCustomListModel = new ArrayList<CustomListModel>();
    private ArrayList<CustomListModel> arrDuplicate = new ArrayList<CustomListModel>();
    private Activity activity;
    private EditText objEditTextSearch;
    private TextView objTextViewNoMatchesFound;
    private ListView objListView;
    private Button objButtonDismiss;
    private PopupWindow objPopupWindow;
    private int width;
    private int height;

    // POPUP ACTION CLICK LISTENER INTERFACE
    public static CustomListItemClickListener objCustomListItemClickListener;

    public ArrayList<CustomListModel> getArrCustomListModel() {
        return arrCustomListModel;
    }

    public void setArrCustomListModel(ArrayList<CustomListModel> arrCustomListModel) {
        this.arrCustomListModel = arrCustomListModel;
        this.arrDuplicate = arrCustomListModel;
    }

    public CustomListPopup(Activity context) {
        super(context);
        activity = context;
    }

    public CustomListPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomListPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void showPopupList() {
        try {
            this.removeAllViews();
            // TO SET THE HEIGHT WITH RESPECT TO DEVICE RESOLUTION.
            DisplayMetrics dm = this.getResources().getDisplayMetrics();
            int density = dm.densityDpi;


            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

            System.out.println("SCREEN DENSITY W * H = " + width + " * " + height);


            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_list_popup, null);

            objListView = (ListView) view.findViewById(R.id.listViewCustomPopup);
            objEditTextSearch = (EditText) view.findViewById(R.id.editTextSearch);
            objButtonDismiss = (Button) view.findViewById(R.id.buttonDismissListPopup);
            objTextViewNoMatchesFound = (TextView) view.findViewById(R.id.textViewNoMatchesFound);

            objButtonDismiss.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    objPopupWindow.dismiss();
                }
            });

            if (arrCustomListModel.size() > 0) {
                final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                final CustomListAdapter objCustomListAdapter = new CustomListAdapter();
                objListView.setAdapter(objCustomListAdapter);
                objListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        imm.hideSoftInputFromWindow(view.findViewById(R.id.textViewListItem).getWindowToken(), 0);
                        objCustomListItemClickListener.onCustomListItemClickEvent(arrCustomListModel.get(i).getId(), arrCustomListModel.get(i).getName());
                        objPopupWindow.dismiss();
                    }
                });

                objEditTextSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        if (objCustomListAdapter != null) {
                            if (s.toString().trim().length() > 0)
                                objCustomListAdapter.getFilter().filter(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                this.addView(view);

                objPopupWindow = new PopupWindow(this);
                objPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
                objPopupWindow.setHeight(LayoutParams.MATCH_PARENT);
                objPopupWindow.setFocusable(true);
                objPopupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
            } else {
                Toast.makeText(activity, "No data available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCustomListItemClickListener(CustomListPopup.CustomListItemClickListener listener) {
        objCustomListItemClickListener = listener;
    }

    public interface CustomListItemClickListener {
        public void onCustomListItemClickEvent(int id, String name);
    }

    private class CustomListAdapter extends BaseAdapter implements Filterable {

        private ItemFilter mFilter = new ItemFilter();


        @Override
        public int getCount() {
            return arrCustomListModel.size();
        }

        @Override
        public CustomListModel getItem(int i) {
            return arrCustomListModel.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        class ViewHolder {
            private TextView objTextViewName;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            ViewHolder objViewHolder = null;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_list_popup, viewGroup, false);
                objViewHolder = new ViewHolder();
                objViewHolder.objTextViewName = (TextView) view.findViewById(R.id.textViewListItem);
                view.setTag(objViewHolder);
            } else {
                objViewHolder = (ViewHolder) view.getTag();
            }
            CustomListModel objCustomListModel = arrCustomListModel.get(position);
            objViewHolder.objTextViewName.setText(objCustomListModel.getName());
            return view;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<CustomListModel> list = arrDuplicate;

                int count = list.size();
                final ArrayList<CustomListModel> nlist = new ArrayList<CustomListModel>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = "" + list.get(i).getName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        CustomListModel mCustomListModel = list.get(i);
                        nlist.add(mCustomListModel);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrCustomListModel = (ArrayList<CustomListModel>) results.values;
                notifyDataSetChanged();
                if (arrCustomListModel.size() == 0) {
                    objTextViewNoMatchesFound.setVisibility(TextView.VISIBLE);
                    objTextViewNoMatchesFound.setText("No results matching '" + objEditTextSearch.getText().toString() + "'");
                } else {
                    objTextViewNoMatchesFound.setVisibility(TextView.GONE);
                }
            }
        }
    }
}
