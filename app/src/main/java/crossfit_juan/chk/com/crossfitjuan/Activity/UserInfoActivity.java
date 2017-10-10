package crossfit_juan.chk.com.crossfitjuan.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import crossfit_juan.chk.com.crossfitjuan.Common.Constants;
import crossfit_juan.chk.com.crossfitjuan.Common.User;
import crossfit_juan.chk.com.crossfitjuan.DataModel.UserInfoData;
import crossfit_juan.chk.com.crossfitjuan.HttpConnection.CustomThread.ReqHTTPJSONThread;
import crossfit_juan.chk.com.crossfitjuan.R;
import crossfit_juan.chk.com.crossfitjuan.tool.AWSService;
import crossfit_juan.chk.com.crossfitjuan.tool.ChatDBHelper;
import crossfit_juan.chk.com.crossfitjuan.tool.CircleImageView;
import crossfit_juan.chk.com.crossfitjuan.tool.UserInfoItemViewAdapter;

import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.DB_FILE_NAME;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.OAUTH_CLIENT_ID;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.OAUTH_CLIENT_NAME;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.OAUTH_CLIENT_SECRET;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.PICK_FROM_ALBUM_ACTION;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.PROFILE_PATH;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.REQUEST_PERMISSION_ACCESS_STORAGE;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.USER_INFO_INDEX_LOGOUT;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.USER_INFO_INDEX_REGISTER_PERIOD;
import static crossfit_juan.chk.com.crossfitjuan.Common.Constants.USER_INFO_INDEX_SIGNOUT;

public class UserInfoActivity extends AppCompatActivity {

    private OAuthLogin mOAuthLoginModule;
    private final String TAG = "DEBUG";
    Uri ImageCaptureUrl = null;
    Bitmap bm, resized;

    @BindView(R.id.user_info_profile_image)
    CircleImageView userInfoProfileImage;
    @BindView(R.id.user_info_photoAlbum)
    CircleImageView userInfoPhotoAlbum;
    @BindView(R.id.user_info_name)
    TextView userInfoName;
    @BindView(R.id.user_info_list)
    ListView userInfoList;

    UserInfoItemViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        userInfoName.setText(User.getInstance().getData().getUser_name());
        initAdapter();
        setListViewClickListener();
        Initalize_OAuthLogin();

    }

    public void initAdapter(){
        adapter=new UserInfoItemViewAdapter(this);
        adapter.addItem(new UserInfoData("이메일",User.getInstance().getData().getUser_email(),"",false));
        adapter.addItem(new UserInfoData("휴대폰 번호",User.getInstance().getData().getUser_phone_number(),"",false));
        int user_certfication=User.getInstance().getData().getCertification();
        String user_rank="JUAN Crossfit ";
        if(user_certfication==0){
            user_rank+="손님";
        }
        else if(user_certfication==1){
            user_rank+="준회원";
        }
        else if(user_certfication==2){
            user_rank+="정회원";
        }
        adapter.addItem(new UserInfoData("회원등급",user_rank,"",false));
        String locker_num="";
        int lock_num=User.getInstance().getData().getUser_locker_num();
        if(lock_num!=-1){
            locker_num=String.valueOf(lock_num);
            locker_num+=" ("+User.getInstance().getData().getUser_start_date()+"~"+User.getInstance().getData().getUser_start_date()+")";
        }
        adapter.addItem(new UserInfoData("락커 번호",locker_num,"락커를 등록해 주세요",false));
        String period="";
        if(User.getInstance().getData().getUser_start_date()!=null && !User.getInstance().getData().getUser_start_date().equals("")){
            period=User.getInstance().getData().getUser_start_date()+"~"+User.getInstance().getData().getUser_finish_date();
        }
        adapter.addItem(new UserInfoData("등록기간",period,"등록되지 않은 사용자 입니다",true));
        adapter.addItem(new UserInfoData("로그아웃","","로그아웃 시 회원정보는 삭제되지 않습니다",false));
        adapter.addItem(new UserInfoData("회원탈퇴","","회원 정보와 함께 기존의 대화내용이 모두 지워집니다",false));
        userInfoList.setAdapter(adapter);
    }

    public void setListViewClickListener(){
        userInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int idx, long l) {
                switch (idx){
                    case USER_INFO_INDEX_REGISTER_PERIOD:
                        Toast.makeText(getApplicationContext(),"휴회버튼 구현 예정",Toast.LENGTH_SHORT).show();
                        break;
                    case USER_INFO_INDEX_LOGOUT:
                        mOAuthLoginModule.logout(getApplicationContext());
                        LogoutUserCheckDialog();
                        break;
                    case USER_INFO_INDEX_SIGNOUT:
                        DeleteUserCheckDialog();
                        break;
                }
            }
        });
    }


    public void TakeImagetoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM_ACTION);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_ALBUM_ACTION:
                ImageCaptureUrl = data.getData();
                String realpath = getImagePath(ImageCaptureUrl);
                AWSService.getInstance().upload(new File(realpath));
                break;
        }
    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    void SetProfileImage() {
        Thread ImageSetThread = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                try {
                    URL url = new URL(PROFILE_PATH + User.getInstance().getData().getUser_email() + ".png");
                    Log.d("DEBUGYU", url.toString());
                    InputStream is = url.openStream();
                    bm = BitmapFactory.decodeStream(is);
                    resized = Bitmap.createScaledBitmap(bm, 128, 128, true);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            userInfoProfileImage.setImageBitmap(resized);
                        }
                    });
                    //    tProfileImg.setImageBitmap(resized); //비트맵 객체로 보여주기
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        ImageSetThread.start();
    }

    void LogoutUserCheckDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("로그아웃 시 회원 정보는 지워지지 않습니다. 로그아웃 하시겠습니까?").setCancelable(
                false).setPositiveButton("아니요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button

                        dialog.cancel();

                    }
                }).setNegativeButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        logout();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("로그아웃 요청");
        // Icon for AlertDialog
        alert.show();
    }
    void logout(){
        mOAuthLoginModule.logoutAndDeleteToken(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    void DeleteUserCheckDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("회원 탈퇴할 경우 회원 정보와 함께 기존의 대화내용이 모두 지워집니다. 그래도 회원탈퇴 하시겠습니까?").setCancelable(
                false).setPositiveButton("아니요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button

                        dialog.cancel();

                    }
                }).setNegativeButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        DeleteUser();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("회원 탈퇴 요청");
        // Icon for AlertDialog
        alert.show();
    }

    void DeleteUser() {
        JSONObject send_data = new JSONObject();
        try {
            send_data.put("id_email", User.getInstance().getData().getUser_email());
        } catch (JSONException jsonex) {
            jsonex.printStackTrace();
        }
        ReqHTTPJSONThread thread = new ReqHTTPJSONThread(Constants.REQ_DELETE_USER, send_data);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException interex) {
            interex.printStackTrace();
        }
        String result = thread.handler.getMsg();
        JSONObject result_data = null;
        int result_code = 0;
        try {
            result_data = new JSONObject(result);
            result_code = result_data.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result_code == 9999) {
            Toast.makeText(getApplicationContext(), "아이디가 삭제되었습니다", Toast.LENGTH_LONG).show();
            ChatDBHelper dbHelper = new ChatDBHelper(getApplicationContext(), DB_FILE_NAME, null, 1);
            dbHelper.DeleteDataBase();
            logout();

        } else if (result_code == 8888) {
            Toast.makeText(getApplicationContext(), "잘못된 요청입니다", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_STORAGE:
                int permission_num = 0;
                Log.d("DEUBGYU", String.valueOf(permissions.length));
                for (int i = 0; i < permissions.length; i++) {
                    int grantResult = grantResults[i];
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permission_num++;
                    }
                }

                if (permission_num == 2) {
                    TakeImagetoAlbum();
                } else {
                    Toast.makeText(getApplicationContext(), "권한을 허용해야 프로필 이미지 변경 기능을 사용할 수 있습니다", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SetProfileImage();
    }


    @OnClick({R.id.user_info_photoAlbum})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_info_photoAlbum:
                int permission_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permission_ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_ACCESS_STORAGE);
                } else {
                    TakeImagetoAlbum();
                }
                break;
        }
    }
    void Initalize_OAuthLogin(){
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                UserInfoActivity.this
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
    }
}
