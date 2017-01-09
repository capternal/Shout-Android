package com.shoutin.main.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoutin.R;
import com.shoutin.main.Model.SearchTagModel;
import com.shoutin.main.ShoutDefaultActivity;
import com.shoutin.main.ShoutSearchActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchTagLayoutAdapter extends BaseAdapter {

    private List<SearchTagModel> objects = new ArrayList<SearchTagModel>();

    private Context context;
    private Activity objActivity;
    private LayoutInflater layoutInflater;

    public SearchTagLayoutAdapter(Context context, Activity objActivity, ArrayList<SearchTagModel> objects) {
        this.context = context;
        this.objActivity = objActivity;
        this.objects = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public SearchTagModel getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.search_tag_layout, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((SearchTagModel) getItem(position), (ViewHolder) convertView.getTag(), position);
        return convertView;
    }

    private void initializeViews(final SearchTagModel object, ViewHolder holder, final int intPosition) {
        //TODO implement
        holder.txtTagWord.setText(object.getSearchedTagWord());

        holder.txtTagWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent objIntent = new Intent(objActivity, ShoutSearchActivity.class);
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                objActivity.startActivity(objIntent);
                objActivity.finish();
                objActivity.overridePendingTransition(0, 0);
            }
        });

        holder.imageviewRemoveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("REMOVE TAG NAME : " + objects.get(intPosition).getSearchedTagWord());
                objects.remove(intPosition);
                ((ShoutDefaultActivity) objActivity).updateSearchUI();
            }
        });
    }

    protected class ViewHolder {
        private TextView txtTagWord;
        private ImageView imageviewRemoveTag;

        public ViewHolder(View view) {
            txtTagWord = (TextView) view.findViewById(R.id.txt_tag_word);
            imageviewRemoveTag = (ImageView) view.findViewById(R.id.imageview_remove_tag);
        }
    }
}
