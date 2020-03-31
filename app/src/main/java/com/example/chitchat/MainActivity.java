package com.example.chitchat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import static com.example.chitchat.SettingsActivity.PICK_IMAGE;


public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager myViewPager;


    private TabLayout mTabLayout;
    private TabAccessAdapter mTabAccessAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private String CurrentId;
    private StorageReference GroupProfileImagesRef;
    private  String CurrentState;

    public static final int PICK_IMAGE = 1;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        CurrentId=mAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();

        GroupProfileImagesRef= FirebaseStorage.getInstance().getReference().child("GroupProfileImages");

        mtoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChitChat");


      myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
      mTabAccessAdapter=new TabAccessAdapter(getSupportFragmentManager());
      myViewPager.setAdapter(mTabAccessAdapter);

      mTabLayout=(TabLayout)findViewById(R.id.main_tabs);
      mTabLayout.setupWithViewPager(myViewPager);
      CurrentState="offline";
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
            case R.id.find_group:
                Intent groupIntent=new Intent(MainActivity.this,Find_GroupsActivity.class);
                startActivity(groupIntent);
                return true;

            case R.id.Logout:
                mAuth.signOut();
               Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               finish();
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
        Intent intent=new Intent(MainActivity.this,GroupProfile.class);
        intent.putExtra("Group_Name","No_Grp_name");
        startActivity(intent);
    }



    private void updateUserStatus(String state)
    {


        String SaveCurTime,SaveCurDate;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        SaveCurDate= currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        SaveCurTime= currentTime.format(calendar.getTime());

        HashMap<String,Object> onlineState=new HashMap<>();
        onlineState.put("time",SaveCurTime);
        onlineState.put("date",SaveCurDate);
        onlineState.put("state",state);


        rootref.child("users").child(CurrentId).child("userStatus").updateChildren(onlineState);

    }

//protected void onPause()
//{
//    super.onPause();
//    if(CurrentState.equals("online"))
//       updateUserStatus("offline");
//    CurrentState="onffine";
//
//}
//protected void onResume()
//{
//    super.onResume();
//    if(CurrentState.equals("offline"))
//    updateUserStatus("online");
//    CurrentState="online";
//}





}
