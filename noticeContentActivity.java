package com.example.a3_termproject_steam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

public class noticeContentActivity extends Activity { // 공지 내용 페이지

    TextView tvNTitle1, tvnWriter2, tvntype2, tvnDate2, tvnContent2;
    Button btnNModify1, btnNDelete1;

    MyDatabaseHelper myDB = new MyDatabaseHelper(noticeContentActivity.this);
    SQLiteDatabase sqlDB;

    String id, userName;
    int GId, nId;

    int layoutI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_content);
        setTitle("공지 내용");

        tvNTitle1 = (TextView) findViewById(R.id.tvNTitle1);
        tvnWriter2 = (TextView) findViewById(R.id.tvnWriter2);
        tvntype2 = (TextView) findViewById(R.id.tvntype2);
        tvnDate2 = (TextView) findViewById(R.id.tvnDate2);
        tvnContent2 = (TextView) findViewById(R.id.tvnContent2);

        btnNModify1 = (Button) findViewById(R.id.btnNModify1);
        btnNDelete1 = (Button) findViewById(R.id.btnNDelete1);

        // 넘겨받은 데이터들 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        GId = inIntent.getIntExtra("GID", 0);
        nId = inIntent.getIntExtra("nId", 0);
        layoutI = inIntent.getIntExtra("layoutI", 0); // 중요공지 페이지에서 본 것인지, 공지 메인 페이지에서 본 것인지 확인하기 위한 변수


        // 현재 사용자의 id를 통해 사용자 이름을 불러옴
        sqlDB=myDB.getReadableDatabase();
        Cursor cursor2=myDB.customerInfo(id);
        cursor2.moveToNext();
        userName= cursor2.getString(2);

        // 작성한 공지 정보를 해당 위치에 표시
        tvNTitle1.setText(inIntent.getStringExtra("nTitle"));
        tvnWriter2.setText(inIntent.getStringExtra("writer"));
        tvntype2.setText(inIntent.getStringExtra("type"));
        tvnDate2.setText(inIntent.getStringExtra("nTime"));
        tvnContent2.setText(inIntent.getStringExtra("nContent"));



        if(layoutI==1){ // 중요 공지에서 넘어온 것. 공지 수정/삭제가 불가능함. 수정/삭제 버튼 안 보이게.
            btnNModify1.setVisibility(View.INVISIBLE);
            btnNDelete1.setVisibility(View.INVISIBLE);
        }else{ // 공지 메인 페이지에서 넘어온 것. 공지 수정/삭제 가능함. 수정/삭제 버튼 보이게.
            btnNModify1.setVisibility(View.VISIBLE);
            btnNDelete1.setVisibility(View.VISIBLE);

            // 공지 수정/삭제는 공지 작성자만 가능함
            // 현재 사용자와 공지 작성자 이름이 같다면 수정/삭제 버튼 보이게, 아니면 안 보이게 함
            if(userName.trim().equals(tvnWriter2.getText().toString())){
                btnNModify1.setVisibility(View.VISIBLE);
                btnNDelete1.setVisibility(View.VISIBLE);
            }else{
                btnNModify1.setVisibility(View.INVISIBLE);
                btnNDelete1.setVisibility(View.INVISIBLE);
            }
        }



        // 공지 수정 버튼. 누르면 수정 페이지로 넘어감
        btnNModify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), modifyNoticeActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("GID", GId);
                intent.putExtra("nId", nId);
                intent.putExtra("nTitle", tvNTitle1.getText().toString());
                intent.putExtra("nType", tvntype2.getText().toString());
                intent.putExtra("nContent", tvnContent2.getText().toString());
                startActivityForResult(intent,0);
            }
        });

        // 공지 삭제 버튼
        btnNDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 공지 삭제 버튼 클릭시 경고창
                AlertDialog.Builder dlg = new AlertDialog.Builder(noticeContentActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle("공지를 정말 삭제하시겠습니까?");
                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() { // 삭제 클릭시 공지가 삭제됨
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myDB.deleteNotice(GId, nId); // 공지 삭제

                        finish();
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(noticeContentActivity.this);
                        text.setText("공지가 삭제되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });

                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() { // 공지 삭제 취소
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(noticeContentActivity.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){ // 공지 수정 페이지에서 수정 후 다시 이 페이로 넘어왔을 때 수정한 정보를 표시해 주기 위함.
            tvNTitle1.setText(data.getStringExtra("nTitle"));
            tvnContent2.setText(data.getStringExtra("nContent"));
            tvntype2.setText(data.getStringExtra("nType"));
        }
    }
}
