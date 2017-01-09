package com.shoutin.main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoutin.R;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.main.Model.MessageBoardModel;
import com.shoutin.others.CircleTransform;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by CapternalSystems on 5/23/2016.
 */
public class MessageBoardListAdapter extends BaseAdapter {


    ArrayList<MessageBoardModel> arrMessageBoardModels = new ArrayList<MessageBoardModel>();
    Context objContext;

    public MessageBoardListAdapter(ArrayList<MessageBoardModel> arrMessageBoardModels, Context objContext) {
        this.arrMessageBoardModels = arrMessageBoardModels;
        this.objContext = objContext;
    }

    @Override
    public int getCount() {
        return arrMessageBoardModels.size();
    }

    @Override
    public Object getItem(int position) {
        return arrMessageBoardModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView objItemImage;
        TextView objTextViewItemTitle;
        TextView objTextViewGroupMembers;
        TextView objTextViewMessageCount;
        ImageView objIMageChatIcon;
        TextView objTextViewTimeStamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View objView = convertView;
        final ViewHolder objViewHolder;

        if (convertView == null) {
            LayoutInflater objLayoutInflater = (LayoutInflater) objContext.getSystemService(objContext.LAYOUT_INFLATER_SERVICE);
            objView = objLayoutInflater.inflate(R.layout.messageboard_list_layout, parent, false);

            objViewHolder = new ViewHolder();

            objViewHolder.objItemImage = (ImageView) objView.findViewById(R.id.message_board_item_image);
            objViewHolder.objTextViewItemTitle = (TextView) objView.findViewById(R.id.txt_message_board_title);
            objViewHolder.objTextViewGroupMembers = (TextView) objView.findViewById(R.id.txt_message_board_group_member);
            objViewHolder.objTextViewMessageCount = (TextView) objView.findViewById(R.id.txt_message_board_count);
            objViewHolder.objIMageChatIcon = (ImageView) objView.findViewById(R.id.image_message_chat);
            objViewHolder.objTextViewTimeStamp = (TextView) objView.findViewById(R.id.message_time_stamp);

            objView.setTag(objViewHolder);
        } else {
            objViewHolder = (ViewHolder) objView.getTag();
        }

        MessageBoardModel objMessageBoardModel = arrMessageBoardModels.get(position);

        if (objMessageBoardModel.getItemImage().equals("")) {
            Picasso.with(objContext).load(R.drawable.app_icon_circle).transform(new CircleTransform()).into(objViewHolder.objItemImage);
//            setCustomAnimation(objViewHolder.objItemImage);
        } else {
            if (ConnectivityBroadcastReceiver.isConnected()) {
                Picasso.with(objContext).load(objMessageBoardModel.getItemImage()).transform(new CircleTransform()).into(objViewHolder.objItemImage);
            } else {
                Picasso.with(objContext).load(objMessageBoardModel.getItemImage()).transform(new CircleTransform()).networkPolicy(NetworkPolicy.OFFLINE).into(objViewHolder.objItemImage);
            }
        }

        objViewHolder.objItemImage.setPadding(
                Constants.DEFAULT_MESSAGE_BOARD_PADDING,
                Constants.DEFAULT_MESSAGE_BOARD_PADDING,
                Constants.DEFAULT_MESSAGE_BOARD_PADDING,
                Constants.DEFAULT_MESSAGE_BOARD_PADDING);

        if (objMessageBoardModel.getMessageTitle().equals("")) {
            objViewHolder.objTextViewItemTitle.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
            objViewHolder.objTextViewItemTitle.setBackgroundResource(R.drawable.rounded_corner_shout_detail_light_pink);
            setCustomAnimation(objViewHolder.objTextViewItemTitle);
        } else {
            objViewHolder.objTextViewItemTitle.setText(objMessageBoardModel.getMessageTitle());
        }

        if (objMessageBoardModel.getMessageTitle().equals("")) {
            objViewHolder.objTextViewGroupMembers.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
            objViewHolder.objTextViewGroupMembers.setBackgroundResource(R.drawable.rounded_corner_shout_detail_light_grey);
            setCustomAnimation(objViewHolder.objTextViewGroupMembers);
        } else {
            objViewHolder.objTextViewGroupMembers.setText(objMessageBoardModel.getUsers());
        }

        if (objMessageBoardModel.getMessageCount().toString().length() > 0) {
            if (Integer.parseInt(objMessageBoardModel.getMessageCount().toString()) > 0) {
                objViewHolder.objIMageChatIcon.setBackgroundResource(R.drawable.chat_red);
                objViewHolder.objTextViewMessageCount.setText(objMessageBoardModel.getMessageCount());
                objViewHolder.objTextViewTimeStamp.setTextColor(objContext.getResources().getColor(R.color.red_background_color));
            }
        }

        objViewHolder.objTextViewTimeStamp.setText(objMessageBoardModel.getTime_stamp());

        return objView;
    }

    private void setCustomAnimation(View view) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }
}
