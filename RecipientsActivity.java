package com.example.theopsyphertxt.tibbit;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends ListActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendRelations;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected ParseObject message;

    //protected MenuItem mSendMenuItem;
    protected Button mSendButton;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        setupActionBar();
        mSendButton = (Button) findViewById(R.id.send_button);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject message = createMessage();
                if(message == null) {
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_file_selection)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                send(message);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelations = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendRelations.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(), android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void setupActionBar() {
        // getActionBar().setDisplayHomeAsUpEnabled(true);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipient, menu);
        return true;
    } */

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_send:
                ParseObject message = createMessage();
                //send(message);

                return true;
        }

        return super.onOptionsItemSelected(item);
    } */

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
             if(e == null) {
                 //succes
                 Toast.makeText(RecipientsActivity.this, R.string.message_sent, Toast.LENGTH_LONG);
             }
             else {
                 AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                 builder.setMessage(R.string.error_selecting_file)
                         .setTitle(R.string.error_title)
                         .setPositiveButton(android.R.string.ok, null);
                 AlertDialog dialog = builder.create();
                 dialog.show();
             }

            }
        });
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if(fileBytes == null) {
            return null;
        }
        else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    //used to get list of recipients
    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < getListView().getCount(); i++) {
            if (getListView().isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (l.getCheckedItemCount() > 0)
            mSendButton.setVisibility(View.VISIBLE);
        else
            mSendButton.setVisibility(View.INVISIBLE);
    }

}
