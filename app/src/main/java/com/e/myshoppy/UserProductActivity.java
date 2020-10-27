package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class UserProductActivity extends AppCompatActivity {

    DatabaseReference ref;
    EditText searchBar;
    ListView listView;
    ArrayList<ShoppingItem> list;
    ProgressBar progressBar;
    TextView empty;
    ProductListAdapter adapter;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product);

        type = getIntent().getStringExtra("category");
        searchBar = findViewById(R.id.searchBar);
        listView = findViewById(R.id.listview);
        list = new ArrayList<>();
        progressBar = findViewById(R.id.progress);
        empty = findViewById(R.id.empty_view);
        listView.setEmptyView(empty);

        ref = FirebaseDatabase.getInstance().getReference().child("shopkeepers/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list = getItems(snapshot);
                adapter = new ProductListAdapter(getApplicationContext(),list,"customer");
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                listView.setTextFilterEnabled(true);
                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        /*
                        int textlength = charSequence.length();
                        ArrayList<ShoppingItem> tempShoppingItems = new ArrayList<>();
                        for(ShoppingItem x: list){
                            if (textlength <= x.getTitle().length()) {
                                if (x.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    tempShoppingItems.add(x);
                                }
                            }
                        }
                        listView.setAdapter(new ProductListAdapter(getApplicationContext(), tempShoppingItems));

                         */
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(UserProductActivity.this,IndividualProduct.class);
                intent.putExtra("item",list.get(i));
                startActivity(intent);

            }
        });

    }

    public ArrayList<ShoppingItem> getItems(DataSnapshot snapshot){

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();

        for(DataSnapshot snapshot1: snapshot.getChildren()){

            for (DataSnapshot snapshot2: snapshot1.child("products").getChildren()){

                if(snapshot2.getKey().equals(type)){

                    for(DataSnapshot snap: snapshot2.getChildren()){

                        int quantity = 0;
                        String itemPrice = snap.child("price").getValue().toString();

                        quantity = Integer.valueOf(snap.child("quantity").getValue().toString());
                        items.add(new ShoppingItem(
                                snap.child("productID").getValue().toString(),
                                snap.child("title").getValue().toString(),
                                snap.child("type").getValue().toString(),
                                snap.child("description").getValue().toString(),
                                "Rs. "+itemPrice,
                                quantity,
                                snapshot1.getKey()
                        ));

                    }

                }

            }

        }
        return items;
    }
}