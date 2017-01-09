package com.shoutin.main.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shoutin.R;
import com.shoutin.main.Model.SearchContinent;
import com.shoutin.main.Model.SearchModel;

import java.util.ArrayList;

/**
 * Created by jupitor on 26/10/16.
 */

public class SearchExpandableAdapter extends BaseExpandableListAdapter {


    private Context context;
    private Activity objActivity;
    private ArrayList<SearchContinent> continentList = new ArrayList<SearchContinent>();
    private ArrayList<SearchContinent> originalList = new ArrayList<SearchContinent>();

    public SearchExpandableAdapter(ArrayList<SearchContinent> continentList, Activity objActivity, Context context) {
        this.continentList = continentList;
        this.originalList.addAll(continentList);
        this.objActivity = objActivity;
        this.context = context;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<SearchModel> searchList = continentList.get(groupPosition).getArrSearchModelData();
        return searchList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    protected class ViewHolder {
        TextView childname;

        public ViewHolder(View view) {
            childname = (TextView) view.findViewById(R.id.txt_search_child_title);
        }
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        final SearchModel objSearchModel = (SearchModel) getChild(groupPosition, childPosition);

        ViewHolder objViewHolder = null;

        if (view == null) {

            System.out.println("IN NULL");

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.search_child_layout, parent, false);
            view.setTag(new ViewHolder(view));
        }
        // IF ROW IS NOT NULL THEN GET THE ROW USING getTag() method.

        initializeView(groupPosition, childPosition, objSearchModel, (ViewHolder) view.getTag());
        return view;
    }

    private void initializeView(int groupPosition, int childPosition, SearchModel objSearchModel, ViewHolder holder) {
        holder.childname.setText(objSearchModel.getTitle());
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SearchModel> searchList = continentList.get(groupPosition).getArrSearchModelData();
        return searchList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return continentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return continentList.size();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        SearchContinent continent = (SearchContinent) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.search_header_layout, null);
        }
        TextView heading = (TextView) view.findViewById(R.id.txt_search_header_title);
        heading.setText(continent.getHeader().trim());
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
