package com.weezlabs.imagegallery.job;

import android.util.Log;

import com.google.gson.JsonObject;
import com.path.android.jobqueue.Params;
import com.weezlabs.imagegallery.R;
import com.weezlabs.imagegallery.model.flickr.User;
import com.weezlabs.imagegallery.service.flickr.FlickrService;
import com.weezlabs.imagegallery.storage.FlickrStorage;
import com.weezlabs.imagegallery.tool.Events;
import com.weezlabs.imagegallery.tool.Events.UserLogonEvent;

import de.greenrobot.event.EventBus;


public class LoginFlickrUserJob extends BaseFlickrJob {

    public static final String LOG_TAG = "LOGIN";
    public static final String GROUP_ID = "login-user";

    public LoginFlickrUserJob(FlickrStorage flickrStorage, FlickrService flickrService) {
        super(new Params(Priority.HIGH).requireNetwork().groupBy(GROUP_ID), flickrStorage, flickrService);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        JsonObject userJson = getFlickrService().loginUser()
                .getAsJsonObject(getString(R.string.json_key_user));
        String userId = userJson.get(getString(R.string.json_key_id)).getAsString();
        String username = userJson.getAsJsonObject(getString(R.string.json_key_username))
                .get(getString(R.string.json_key_content)).getAsString();

        JsonObject personJson = getFlickrService().getUserInfo(userId)
                .getAsJsonObject(getString(R.string.json_key_person));
        User user = parseUserInfo(userId, username, personJson);
        getFlickrStorage().saveUser(user);

        Log.i(LOG_TAG, user.toString());
        EventBus.getDefault().postSticky(new UserLogonEvent());
    }

    private User parseUserInfo(String userId, String username, JsonObject personJson) {
        String nsid = personJson.get(getString(R.string.json_key_nsid)).getAsString();
        String realName = personJson.getAsJsonObject(getString(R.string.json_key_real_name))
                .get(getString(R.string.json_key_content)).getAsString();
        int iconServer = personJson.get(getString(R.string.json_key_icon_server)).getAsInt();
        int iconFarm = personJson.get(getString(R.string.json_key_icon_farm)).getAsInt();
        return new User(userId, username, realName, nsid, iconFarm, iconServer);
    }

    @Override
    protected void onCancel() {

    }

}
