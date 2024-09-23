package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InoticeActivity extends Activity { // 중요 공지 페이지

    LinearLayout layoutImportantNotice;

    ArrayList<Integer> nid, gid;
    ArrayList<String> memberid, ntitle, ntype, ncontent, ntime;
    String memberID, nTitle, nType, nContent, nTime, nWriter;
    int nId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    TextView nTitle1, nWriter1, nType1, nTime1;
    ImageButton btnNReadMore1;

    int layoutI=1;

    String id;
    int GId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_important);
        setTitle("중요 공지 내용");

        layoutImportantNotice = (LinearLayout) findViewById(R.id.layoutImportantNotice);

        // 넘겨받은 데이터들 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        GId = inIntent.getIntExtra("GID", 0);


    }

    @Override
    protected void onStart() {
        super.onStart();



        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor=myHelper.InoticeRead(GId); // 그룹의 중요 공지를 읽어옴

        layoutImportantNotice = (LinearLayout) findViewById(R.id.layoutImportantNotice);
        layoutImportantNotice.removeAllViews(); // 같은 정보가 쌓이지 않도록 함.


        // 공지 db 정보를 저장하기 위한 arraylist
        nid = new ArrayList<>();
        gid = new ArrayList<>();
        memberid = new ArrayList<>();
        ntitle = new ArrayList<>();
        ntype = new ArrayList<>();
        ncontent = new ArrayList<>();
        ntime = new ArrayList<>();

        if(cursor!=null){

            while(cursor.moveToNext()){ // 공지 db 정보를 arraylist에 저장
                nid.add(cursor.getInt(0));
                gid.add(cursor.getInt(1));
                memberid.add(cursor.getString(2));
                ntitle.add(cursor.getString(3));
                ntype.add(cursor.getString(4));
                ncontent.add(cursor.getString(5));
                ntime.add(cursor.getString(6));
            }

            for(int i=gid.size()-1 ;i>=0; i--){ // 각 공지의 내용 하나씩 변수에 저장
                nId = nid.get(i);
                memberID = memberid.get(i);
                nTitle = ntitle.get(i);
                nType = ntype.get(i);
                nContent = ncontent.get(i);
                nTime = ntime.get(i);

                addCard(nId, memberID, nTitle, nType, nContent, nTime); // 레이아웃에 각각의 공지를 추가해줌


            }



        }else{
            Toast.makeText(this, "cursor가 비었음", Toast.LENGTH_SHORT).show();
        }

    }


    // 중요 공지를 동적으로 layout에 추가하기 위한 함수
    private void addCard(int nId, String writer, String nTitle, String type, String nContent, String nTime) {

        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor2=myHelper.customerInfo(writer);
        cursor2.moveToNext();

        final View view = getLayoutInflater().inflate(R.layout.card_notice, null);
        nTitle1 = view.findViewById(R.id.nTitle1);
        nWriter1 = view.findViewById(R.id.nWriter1);
        nType1 = view.findViewById(R.id.nType1);
        nTime1 = view.findViewById(R.id.nTime1);

        btnNReadMore1 = view.findViewById(R.id.btnNReadMore1);

        nWriter = cursor2.getString(2); // 공지 작성자 이름 가져옴
        nTitle1.setText(nTitle); // 공지 제목 업로드
        nWriter1.setText(nWriter); // 공지 작성자 이름 업로드
        nType1.setText(type); // 공지 타입 업로드
        nTime1.setText(nTime); // 공지 시간 업로드

        if(type.trim().equals("중요")){
            nTitle1.setTextColor(Color.RED);
        }else{
            nTitle1.setTextColor(Color.BLACK);
        }

        // '>' 버튼 클릭시 공지 내용 보는 페이지로 넘어감
        btnNReadMore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), noticeContentActivity.class);
                intent.putExtra("writer", nWriter);
                intent.putExtra("nTitle", nTitle);
                intent.putExtra("type", type);
                intent.putExtra("nContent", nContent);
                intent.putExtra("nTime", nTime);
                intent.putExtra("nId", nId);
                intent.putExtra("GID", gid);
                intent.putExtra("ID", id);
                intent.putExtra("layoutI", layoutI);
                startActivity(intent);
            }
        });



        layoutImportantNotice.addView(view); // layout에 공지 추가해줌





    }



}
