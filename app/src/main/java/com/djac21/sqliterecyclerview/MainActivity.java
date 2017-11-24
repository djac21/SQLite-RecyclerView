package com.djac21.sqliterecyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickListener, RecyclerViewAdapter.LongClickListener {

    public static List<DataModel> data = new ArrayList<>();
    RecyclerViewAdapter adapter;
    DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAdapter = new DatabaseAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.vpnRecyclerView);
        adapter = new RecyclerViewAdapter(this, getData());
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        landingScreen();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addData();
            }
        });
    }

    public List<DataModel> getData() {
        databaseAdapter.getAllData(data);
        return data;
    }

    public void landingScreen() {
        if (data.isEmpty()) {
            findViewById(R.id.vpnRecyclerView).setVisibility(View.GONE);
            findViewById(R.id.landingScreen).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.vpnRecyclerView).setVisibility(View.VISIBLE);
            findViewById(R.id.landingScreen).setVisibility(View.GONE);
        }
    }

    @Override
    public void itemClicked(View view, int position) {
        String item = data.get(position).getText();
        Toast.makeText(this, "Data: " + item , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemLongClicked(View view, final int position) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Server?")
                .setMessage("Are you sure you like to delete the following server?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, data.size());

                        Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        landingScreen();
                    }
                })
                .setNegativeButton("Cancel", null);
        deleteDialog.create().show();
    }

    public void addData() {
        final LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleEditText = new EditText(MainActivity.this);
        titleEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        titleEditText.setHint("Title");

        final EditText dataEditText = new EditText(MainActivity.this);
        dataEditText.setHint("Data");

        linearLayout.addView(titleEditText);
        linearLayout.addView(dataEditText);
        linearLayout.setPadding(50, 50, 50, 50);

        AlertDialog.Builder addNewData = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add New Data")
                .setView(linearLayout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addTitle = titleEditText.getText().toString();
                        String addText = dataEditText.getText().toString();

                        if (addTitle.isEmpty() || addText.isEmpty())
                            Toast.makeText(MainActivity.this, "Please be sure all fields are filled", Toast.LENGTH_SHORT).show();
                        else {
                            DataModel items = new DataModel();
                            items.setTitle(addTitle);
                            items.setText(addText);
                            data.add(items);
                            adapter.notifyDataSetChanged();

                            long id = databaseAdapter.insertData(addTitle, addText);
                            if (id < 0)
                                Toast.makeText(MainActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, "Successful Added", Toast.LENGTH_SHORT).show();

                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);

                            landingScreen();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        addNewData.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.data)
            addData();

        return super.onOptionsItemSelected(item);
    }
}