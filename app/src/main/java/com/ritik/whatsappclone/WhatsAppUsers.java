package com.ritik.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> arrayList;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_users);

        setTitle(ParseUser.getCurrentUser().getUsername().toUpperCase());

        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(WhatsAppUsers.this, android.R.layout.simple_list_item_1, arrayList);

        listView.setOnItemClickListener(WhatsAppUsers.this);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseUser user : objects) {
                            arrayList.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username", arrayList);
                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0 && e == null) {
                                for (ParseUser user : objects) {
                                    arrayList.add(user.getUsername());
                                }
                                arrayAdapter.notifyDataSetChanged();
                                if(swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                            else {
                                if(swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Intent intent = new Intent(WhatsAppUsers.this, LoginActivity.class);
                        startActivity(intent);
                        FancyToast.makeText(WhatsAppUsers.this, "Logged Out", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        finish();
                    }
                    else {
                        FancyToast.makeText(WhatsAppUsers.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(WhatsAppUsers.this, ChatActivity.class);
        intent.putExtra("username", arrayList.get(position));
        startActivity(intent);
    }
}
