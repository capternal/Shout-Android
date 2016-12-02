package com.shout.shoutin.login.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shout.shoutin.R;
import com.shout.shoutin.Utils.CallWebService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.KeyboardUtils;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.login.InviteFriendsActivity;
import com.shout.shoutin.login.ProfileScreenActivity;
import com.shout.shoutin.login.model.ContactModel;
import com.shout.shoutin.main.Model.Continent;
import com.shout.shoutin.others.BitmapBorderTransformation;
import com.shout.shoutin.others.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by CapternalSystems on 5/11/2016.
 */
public class ContactExpandableListAdapter extends BaseExpandableListAdapter implements KeyboardUtils.SoftKeyboardToggleListener, CallWebService.WebserviceResponse {

    private Context context;
    private Activity objActivity;
    private ArrayList<Continent> continentList = new ArrayList<Continent>();
    private ArrayList<Continent> originalList = new ArrayList<Continent>();
    private ArrayList<CompoundButton> arrSelectedContactPosition = new ArrayList<CompoundButton>();
    private InputMethodManager inputMethodManager;
    private DatabaseHelper objDatabaseHelper;
    private int rowId = 0;

    public ContactExpandableListAdapter(Context context, Activity objActivity, ArrayList<Continent> continentList) {
        this.context = context;
        this.objActivity = objActivity;
        this.continentList.addAll(continentList);
        this.originalList.addAll(continentList);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        objDatabaseHelper = new DatabaseHelper(objActivity);
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
        ArrayList<ContactModel> countryList = continentList.get(groupPosition).getArrContactList();
        return countryList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    protected class ViewHolder {

        TextView name;
        TextView number;
        CheckBox objCheckBox;
        TextView objTextViewInvite;
        ImageView imageViewPhoneConnect;
        ImageView imageViewFacebookConnect;
        ImageView imageViewProfile;
        ImageView imageViewBlueLove;
        ImageView imageViewRefresh;
        Button btnAcceptFriendRequest;
        LinearLayout linearLayoutSocialConnect;
        ImageView imgFriendEmptyImage;
        RelativeLayout objRelativeNormalView;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.txt_contact_person_name);
            number = (TextView) view.findViewById(R.id.txt_contact_person_number);
            objCheckBox = (CheckBox) view.findViewById(R.id.chk_tick);
            objTextViewInvite = (TextView) view.findViewById(R.id.txt_invite);

            // Linear Layout Social
            linearLayoutSocialConnect = (LinearLayout) view.findViewById(R.id.linearLayoutSocialConnect);

            imageViewPhoneConnect = (ImageView) view.findViewById(R.id.imageViewPhoneConnect);
            imageViewFacebookConnect = (ImageView) view.findViewById(R.id.imageViewFacebookConnect);
            imageViewProfile = (ImageView) view.findViewById(R.id.imageViewProfile);
            imageViewBlueLove = (ImageView) view.findViewById(R.id.imageViewBlueLove);
            imageViewRefresh = (ImageView) view.findViewById(R.id.btn_refresh_friend_social_type);
            imgFriendEmptyImage = (ImageView) view.findViewById(R.id.contact_dummy_image);

            btnAcceptFriendRequest = (Button) view.findViewById(R.id.btn_accept_friend_request);

            objRelativeNormalView = (RelativeLayout) view.findViewById(R.id.relative_normal_contact);

        }
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        final ContactModel objContactModel = (ContactModel) getChild(groupPosition, childPosition);

        ViewHolder objViewHolder = null;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.contact_listview_cell, parent, false);
            try {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardUtils.hideKeyboard(v, inputMethodManager);
                    }
                });
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            view.startAnimation(animation);

            view.setTag(new ViewHolder(view));
        }
        // IF ROW IS NOT NULL THEN GET THE ROW USING getTag() method.
        initializeView(groupPosition, childPosition, objContactModel, (ViewHolder) view.getTag());
        return view;
    }

    private void initializeView(final int groupPosition, int childPosition, final ContactModel objContactModel, ViewHolder objViewHolder) {

        try {

            objViewHolder.objCheckBox.setChecked(objContactModel.getCheckBokChecked());

            System.out.println("SEARCH CONTACT : GROUP POSITION : " + objContactModel.getGroupPosition());

            objViewHolder.number.setVisibility(TextView.VISIBLE);

            objViewHolder.objTextViewInvite.setTag(childPosition);

            if (objContactModel.getGroupPosition() == 0) {
                /*
                * If Contact Name is "image" then consider it as there is no friend available on shoutin.
                * And in such case we are getting one image url from server in contact number parameter to display over there.
                * */
                if (objContactModel.getContactName().equals("image")) {
                    objViewHolder.imgFriendEmptyImage.setVisibility(ImageView.VISIBLE);
                    objViewHolder.objRelativeNormalView.setVisibility(RelativeLayout.GONE);
                    if (Constants.internetCheck()) {
                        Picasso.with(objActivity).load(objContactModel.getContactNumber()).transform(new BitmapBorderTransformation(0, 15, objActivity.getResources().getColor(R.color.white_text_color))).into(objViewHolder.imgFriendEmptyImage);
                    }
                } else {
                    objViewHolder.imgFriendEmptyImage.setVisibility(ImageView.GONE);
                    objViewHolder.objRelativeNormalView.setVisibility(RelativeLayout.VISIBLE);
                    objViewHolder.linearLayoutSocialConnect.setVisibility(LinearLayout.VISIBLE);

                    objViewHolder.objTextViewInvite.setVisibility(TextView.GONE);
                    objViewHolder.objCheckBox.setVisibility(CheckBox.GONE);
                    objViewHolder.imageViewProfile.setVisibility(ImageView.VISIBLE);
                    objViewHolder.imageViewBlueLove.setVisibility(ImageView.GONE);
                    Picasso.with(context).load(R.drawable.shout_placeholder).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                    objViewHolder.imageViewProfile.setPadding(Constants.DEFAULT_CIRCLE_PADDING,
                            Constants.DEFAULT_CIRCLE_PADDING,
                            Constants.DEFAULT_CIRCLE_PADDING,
                            Constants.DEFAULT_CIRCLE_PADDING);

                    if (!"".equals(objContactModel.getProfileImage())) {
                        Picasso.with(context).load(objContactModel.getProfileImage()).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                    } else {
                        Picasso.with(context).load(R.drawable.shout_placeholder).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                    }

                    if (!objContactModel.getButtonType().equals("N")) {
                        // BUTTON TYPE IS NOT "N" MEANS : Its' may be of type "A" OR "R"
                        objViewHolder.linearLayoutSocialConnect.setVisibility(LinearLayout.VISIBLE);
                        if (objContactModel.getButtonType().equals("A")) {
                            // BUTTON IS ACCEPT TYPE
                            objViewHolder.imageViewRefresh.setVisibility(ImageView.GONE);
                            objViewHolder.imageViewPhoneConnect.setVisibility(ImageView.GONE);
                            objViewHolder.imageViewFacebookConnect.setVisibility(ImageView.GONE);
                            objViewHolder.btnAcceptFriendRequest.setVisibility(Button.VISIBLE);
                            objViewHolder.number.setVisibility(TextView.GONE);
                            objViewHolder.btnAcceptFriendRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //prasanna
                                    Utils.d("ADAPTER", "USER ID : " + objContactModel.getId());
                                    Utils.d("ADAPTER", "TABLE ID : " + objContactModel.getTableId());
                                    rowId = objContactModel.getTableId();
                                    if (ConnectivityBroadcastReceiver.isConnected()) {
                                        callAcceptFriendAPI(objContactModel.getId());
                                    } else {
                                        Constants.showInternetToast(objActivity);
                                    }
                                }
                            });
                        } else {
                            objViewHolder.imageViewRefresh.setVisibility(ImageView.VISIBLE);
                            objViewHolder.imageViewPhoneConnect.setVisibility(ImageView.VISIBLE);
                            objViewHolder.imageViewFacebookConnect.setVisibility(ImageView.VISIBLE);
                            objViewHolder.btnAcceptFriendRequest.setVisibility(Button.GONE);

                            if (objContactModel.getIsFacebookFriend().equals("1")) {
                                objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_grey);
                                objViewHolder.imageViewFacebookConnect.setBackgroundResource(R.drawable.facebook_f);
                            } else {
                                objViewHolder.imageViewFacebookConnect.setBackgroundResource(R.drawable.facebook_f_default);
                            }
                            if (objContactModel.getIsPhoneFriend().equals("1")) {
                                objViewHolder.imageViewPhoneConnect.setVisibility(ImageView.VISIBLE);
                                objViewHolder.imageViewPhoneConnect.setBackgroundResource(R.drawable.call_g);
                                objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_grey);
                                objViewHolder.number.setVisibility(TextView.VISIBLE);
                            } else {
                                objViewHolder.number.setVisibility(TextView.GONE);
                                objViewHolder.imageViewPhoneConnect.setBackgroundResource(R.drawable.call_g_default);
                            }
                            if (objContactModel.getIsFacebookFriend().equals("0") && objContactModel.getIsPhoneFriend().equals("0")) {
                                objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_red);
                            }
                        }
                        if (objContactModel.getIsPhoneFriend().equals("0"))
                            objViewHolder.number.setVisibility(TextView.GONE);
                    } else {
                        // HERE ACCEPT BUTTON WILL NOT BE VISIBLE
                        //It means that facebook, phone and refresh button should be there.
                        objViewHolder.linearLayoutSocialConnect.setVisibility(LinearLayout.VISIBLE);
                        objViewHolder.imageViewRefresh.setVisibility(ImageView.VISIBLE);
                        objViewHolder.imageViewPhoneConnect.setVisibility(ImageView.VISIBLE);
                        objViewHolder.imageViewFacebookConnect.setVisibility(ImageView.VISIBLE);
                        objViewHolder.btnAcceptFriendRequest.setVisibility(Button.GONE);

                        if (objContactModel.getIsFacebookFriend().equals("1")) {
                            objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_grey);
                            objViewHolder.imageViewFacebookConnect.setBackgroundResource(R.drawable.facebook_f);
                        } else {
                            objViewHolder.imageViewFacebookConnect.setBackgroundResource(R.drawable.facebook_f_default);
                        }

                        if (objContactModel.getIsPhoneFriend().equals("1")) {
                            objViewHolder.imageViewPhoneConnect.setVisibility(ImageView.VISIBLE);
                            objViewHolder.imageViewPhoneConnect.setBackgroundResource(R.drawable.call_g);
                            objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_grey);
                            objViewHolder.number.setVisibility(TextView.VISIBLE);
                        } else {
                            objViewHolder.number.setVisibility(TextView.GONE);
                            objViewHolder.imageViewPhoneConnect.setBackgroundResource(R.drawable.call_g_default);
                        }
                        // facebookFriend and phoneFriend are zero then make refresh button turn red.
                        if (objContactModel.getIsFacebookFriend().equals("0") && objContactModel.getIsPhoneFriend().equals("0")) {
                            objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_red);
                        } else {
                            // facebookFriend and phoneFriend are 1, then make refresh button turn grey.
                            objViewHolder.imageViewRefresh.setBackgroundResource(R.drawable.refresh_grey);
                        }
                    }
                }
            } else {

                objViewHolder.imgFriendEmptyImage.setVisibility(ImageView.GONE);
                objViewHolder.objRelativeNormalView.setVisibility(RelativeLayout.VISIBLE);
                // disable the social connect layout null and display the profile default screen.

                // HERE WE MADE THE PROFILE IMAGE VIEW VISIBLE
                objViewHolder.linearLayoutSocialConnect.setVisibility(LinearLayout.GONE);
                objViewHolder.imageViewProfile.setVisibility(ImageView.VISIBLE);
                objViewHolder.imageViewBlueLove.setVisibility(ImageView.GONE);
                Picasso.with(context).load(R.drawable.shout_app_logo).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                objViewHolder.imageViewProfile.setPadding(Constants.DEFAULT_CIRCLE_PADDING,
                        Constants.DEFAULT_CIRCLE_PADDING,
                        Constants.DEFAULT_CIRCLE_PADDING,
                        Constants.DEFAULT_CIRCLE_PADDING);

                if (!"".equals(objContactModel.getProfileImage()) && objContactModel.getProfileImage() != null) {
                    Picasso.with(context).load(Uri.parse(objContactModel.getProfileImage())).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                } else {
                    Picasso.with(context).load(R.drawable.shout_app_logo).transform(new CircleTransform()).into(objViewHolder.imageViewProfile);
                }

                objViewHolder.objCheckBox.setVisibility(CheckBox.VISIBLE);
                objViewHolder.objTextViewInvite.setVisibility(TextView.VISIBLE);
                objViewHolder.objCheckBox.setButtonDrawable(R.drawable.contact_list_blue_checkbok);

            }

            objViewHolder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupPosition == 0) {
                        SharedPreferences objProfileSharedPreferences = objActivity.getSharedPreferences(Constants.PROFILE_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor objProfileEditor = objProfileSharedPreferences.edit();
                        System.out.println("CONTACT PERSON USER ID : " + objContactModel.getId());
                        objProfileEditor.putString(Constants.PROFILE_SCREEN_USER_ID, objContactModel.getId());
                        objProfileEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, Constants.INVITE_FRINEDS_SCREEN);
                        objProfileEditor.commit();
                        Intent objIntent = new Intent(objActivity, ProfileScreenActivity.class);
                        objActivity.startActivity(objIntent);
                        objActivity.overridePendingTransition(0, 0);
                    }
                }
            });

            objViewHolder.objCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox objCheck = (CheckBox) v;
                    ContactModel objNewContactModel = (ContactModel) objCheck.getTag();
                    objNewContactModel.setCheckBokChecked(objCheck.isChecked());
                    notifyDataSetChanged();
                    ((InviteFriendsActivity) objActivity).updateBottomButtonText();
                }
            });

            objViewHolder.name.setText(objContactModel.getContactName().trim());
            objViewHolder.number.setText(objContactModel.getContactNumber().trim());
            objViewHolder.objCheckBox.setTag(childPosition);
            objViewHolder.objCheckBox.setTag(objContactModel);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAcceptFriendAPI(String toId) {
        //prasad
        try {
            SharedPreferences objProfileSharedPreferences = objActivity.getSharedPreferences(Constants.PROFILE_PREFERENCES, Context.MODE_PRIVATE);
            JSONObject objJsonObject = new JSONObject();
            objJsonObject.put("from_id", objProfileSharedPreferences.getString(Constants.USER_ID, ""));
            objJsonObject.put("to_id", toId);
            new CallWebService(Constants.FRIEND_ACCEPT_API, objJsonObject, objActivity, this, true).execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ContactModel> countryList = continentList.get(groupPosition).getArrContactList();
        return countryList.size();
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
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        Continent continent = (Continent) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.contact_listview_header, null);
            TextView heading = (TextView) view.findViewById(R.id.expandable_listview_contactlist_header);
            heading.setText(continent.getHeader().trim());

        }
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


    public void filterData(String query) {
        query = query.toLowerCase();
        continentList.clear();
        if (query.length() == 0) {
            System.out.println("QUERY EMPTY : " + originalList.get(0).getArrContactList());
            continentList.addAll(originalList);

        } else {
            for (Continent continent : originalList) {
                ArrayList<ContactModel> countryList = continent.getArrContactList();
                ArrayList<ContactModel> list1 = new ArrayList<ContactModel>();
                for (ContactModel objContactModel : countryList) {
                    System.out.println("QUERY FILTER");
                    if (objContactModel.getContactNumber().toLowerCase().contains(query) || objContactModel.getContactName().toLowerCase().contains(query)) {
                        list1.add(objContactModel);
                    }
                }
                if (list1.size() > 0) {
                    System.out.println("FILTER IN IF QUERY STRING : " + query);
                    Continent nContinent = new Continent(continent.getHeader(), list1);
                    continentList.add(nContinent);
                } else {
                    System.out.println("FILTER MODEL ARRAY : " + countryList);
                    for (ContactModel objContactModel : countryList) {
                        System.out.println("FILTER IN ELSE QUERY STRING : " + query);
                        list1.clear();
                        if (objContactModel.getContactNumber().toLowerCase().contains(query) || objContactModel.getContactName().toLowerCase().contains(query)) {
                            list1.add(objContactModel);
                        }
                    }
                    Continent nContinent = new Continent(continent.getHeader(), list1);
                    continentList.add(nContinent);
                }
            }
        }
        Log.v("MyListAdapter", String.valueOf(continentList.size()));
        notifyDataSetChanged();
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {

    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (Constants.FRIEND_ACCEPT_API.equals(strUrl)) {
            try {

                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    if (rowId == 0) {
                        Utils.d("ADAPTER", "BAD ROW ID");
                    } else {
                        ContactModel objContactModel = objDatabaseHelper.getFriendModel(rowId);
                        objContactModel.setButtonType("N");
                        boolean result = objDatabaseHelper.updateFriend(objContactModel);
                        if (result) {
                            Utils.d("ADAPTER", "FRIEND UPDATED.");
                        } else {
                            Utils.d("ADAPTER", "FRIEND NOT UPDATED.");
                        }
                        ((InviteFriendsActivity) objActivity).setLocalData();
                    }
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
