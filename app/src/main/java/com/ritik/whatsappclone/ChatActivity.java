package com.ritik.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView chatListView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> arrayList;
    private String receivedUsername;
    private EditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent receivedIntentObject = getIntent();
        receivedUsername = receivedIntentObject.getStringExtra("username");

        setTitle(receivedUsername);

        chatListView = findViewById(R.id.chatListView);
        edtMessage = findViewById(R.id.edtMessage);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(ChatActivity.this, android.R.layout.simple_list_item_1, arrayList);
        chatListView.setAdapter(arrayAdapter);

        findViewById(R.id.btnSend).setOnClickListener(ChatActivity.this);

        try {
            ParseQuery<ParseObject> firstParseQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondParseQuery = ParseQuery.getQuery("Chat");

            firstParseQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstParseQuery.whereEqualTo("waReceiver", receivedUsername);
            secondParseQuery.whereEqualTo("waReceiver", ParseUser.getCurrentUser().getUsername());
            secondParseQuery.whereEqualTo("waSender", receivedUsername);

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstParseQuery);
            allQueries.add(secondParseQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(objects.size() > 0 && e == null) {
                        for(ParseObject chatObject : objects) {
                            String chatMessage = chatObject.get("waSender") + " : " + chatObject.get("message").toString();
                            arrayList.add(chatMessage);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rootLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        ParseObject chatMessage = new ParseObject("Chat");
        chatMessage.put("waSender", ParseUser.getCurrentUser().getUsername());
        chatMessage.put("waReceiver", receivedUsername);
        chatMessage.put("message", edtMessage.getText().toString());

        chatMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    String newMessage = ParseUser.getCurrentUser().getUsername() + " : " + edtMessage.getText().toString();
                    arrayList.add(newMessage);
                    arrayAdapter.notifyDataSetChanged();
                    edtMessage.setText("");
                }
            }
        });
    }
}
