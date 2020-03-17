package com.example.chitchat;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager myViewPager;


    private TabLayout mTabLayout;
    private TabAccessAdapter mTabAccessAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private String CurrentId;




    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        CurrentId=mAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();



        mtoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChitChat");


      myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
      mTabAccessAdapter=new TabAccessAdapter(getSupportFragmentManager());
      myViewPager.setAdapter(mTabAccessAdapter);

      mTabLayout=(TabLayout)findViewById(R.id.main_tabs);
      mTabLayout.setupWithViewPager(myViewPager);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.find_friend:
                 Intent friendIntent=new Intent(MainActivity.this,Find_friendsActivity.class);
                 startActivity(friendIntent);
                return true;
            case R.id.Logout:
                mAuth.signOut();
               Intent intent=new Intent(MainActivity.this,LoginActivity.class);
               startActivity(intent);
               return true;

             case R.id.create_Group:
                createNewGroup();
              return true;

             case R.id.settings:
                   Intent settingintent=new Intent(MainActivity.this,SettingsActivity.class);
                   startActivity(settingintent);
                  return true;

             default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void createNewGroup()
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        alertDialog.setTitle("Enter Group Name");
        alertDialog.setCancelable(true);
        final EditText input = new EditText(MainActivity.this);
        input.setHint("e.g. chatting Group");
        alertDialog.setView(input);



        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                final String groupName=input.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this,"Enter Group Name",Toast.LENGTH_SHORT).show();
                }
                else
                {

                  rootref.child("groups").addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              rootref.child("groups").child(groupName).child(CurrentId).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if(task.isSuccessful())
                                      {
                                          Toast.makeText(MainActivity.this,"User Added Successfully",Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              });
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });


                    rootref.child("users").child(CurrentId).child("groups").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                             rootref.child("users").child(CurrentId).child("groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()) {

                                       //Use bundle to pass data
//                                         data.putString("data", groupName);//put string, int, etc in bundle with a key value
//                                         GroupsFragment.setArguments(data);//Finally set argument bundle to fragment
//
//                                         mfragmentManager.beginTransaction().replace(R.id.fragmentContainer, GroupsFragment).commit();//now replace the argument fragment


                                         Toast.makeText(MainActivity.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                                     }
                                     else
                                         Toast.makeText(MainActivity.this, "Group is Not Created", Toast.LENGTH_SHORT).show();
                                 }
                             });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        alertDialog.show();


    }
}
