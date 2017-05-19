package com.example.theopsyphertxt.tibbit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheoPsyphertxt on 23/03/2017.
 */
public class InboxFragment extends ListFragment{

    protected List<ParseObject> mObjects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query  = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    //We found messages/objects;
                    mObjects = objects;

                    String [] usernames = new String [mObjects.size ()];
                    int i = 0;
                    for (ParseObject object:mObjects) {
                        usernames[i] = object.getString (ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    /* ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                            getListView ().getContext (), android.R.layout.simple_list_item_1, usernames); */
                    //below calls image adapter class or 'container'
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(
                                getListView().getContext(), mObjects);
                        setListAdapter(adapter);
                    }
                    else {
                        //refill the adapter!
                        ((MessageAdapter)getListView().getAdapter()).refill(mObjects);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject object = mObjects.get(position);
        String objectType = object.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = object.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());
        if(objectType.equals(ParseConstants.TYPE_IMAGE)) {
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else {
            //view video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video");
            startActivity(intent);
        }
        List<String> ids = object.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if (ids.size() == 1) {
            //last recipient - delete the whole thing
            object.deleteInBackground();
        }
        else {
            //remove the recipient
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            object.removeAll(ParseConstants.KEY_RECIPIENT_IDS,idsToRemove);
            object.saveInBackground();
        }
    }

}
