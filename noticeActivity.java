package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class noticeActivity extends Activity { // 공지 메인 페이지
    LinearLayout layoutNotice;
    ImageButton btnMakeNotice;

    ArrayList<Integer> nid, gid;
    ArrayList<String> memberid, ntitle, ntype, ncontent, ntime;
    String memberID, nTitle, nType, nContent, nTime, nWriter;
    int nId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    TextView nTitle1, nWriter1, nType1, nTime1;
    ImageButton btnNReadMore1;

    int layoutI=0;

    String id;
    int GId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_main);
        setTitle("공지사항");
        layoutNotice = (LinearLayout) findViewById(R.id.layoutNotice);
        btnMakeNotice = (ImageButton) findViewById(R.id.btnmakeNotice);

        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        GId = inIntent.getIntExtra("GID", 0);

        // 공지 만들기 버튼. 공지 작성하는 페이지로 넘어감.
        btnMakeNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), makeNoticeActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("GID", GId);
                startActivity(intent);

            }
        });







    }
    @Override
    protected void onStart() {
        super.onStart();



        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor=myHelper.noticeRead(GId);

        layoutNotice = (LinearLayout) findViewById(R.id.layoutNotice);
        layoutNotice.removeAllViews();


        // 공지 DB 테이블 내용을 저장하기 위한 arraylist
        nid = new ArrayList<>(); // 공지 id(공지 번호)
        gid = new ArrayList<>(); // 그룹 id
        memberid = new ArrayList<>(); // 멤버 id
        ntitle = new ArrayList<>(); // 공지 제목
        ntype = new ArrayList<>(); // 공지 타입 (일반|중요)
        ncontent = new ArrayList<>(); // 공지 내용
        ntime = new ArrayList<>(); // 공지 시간

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

            for(int i=gid.size()-1 ;i>=0; i--){ // 공지 정보를 하나씩 변수에 저장
                nId = nid.get(i); // 공지 id
                memberID = memberid.get(i); // 멤버 id
                nTitle = ntitle.get(i); //공지 제목
                nType = ntype.get(i); // 공지 타입
                nContent = ncontent.get(i); // 공지 내용
                nTime = ntime.get(i); // 공지 시간


                addCard(nId, memberID, nTitle, nType, nContent, nTime); // 공지를 layout에 추가


            }

        }

    }


    // 공지를 동적으로 추가하기 위한 함수
    private void addCard(int nId, String writer, String nTitle, String type, String nContent, String nTime) {

        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor2=myHelper.customerInfo(writer); // 사용자 이름을 가져오기 위함
        cursor2.moveToNext();

        final View view = getLayoutInflater().inflate(R.layout.card_notice, null);
        nTitle1 = view.findViewById(R.id.nTitle1);
        nWriter1 = view.findViewById(R.id.nWriter1);
        nType1 = view.findViewById(R.id.nType1);
        nTime1 = view.findViewById(R.id.nTime1);

        btnNReadMore1 = view.findViewById(R.id.btnNReadMore1);

        nWriter = cursor2.getString(2); // 공지 작성자 이름 변수에 저장
        nTitle1.setText(nTitle); // 공지 제목 업로드
        nWriter1.setText(nWriter); // 공지 작성자 이름 업로드
        nType1.setText(type); // 공지 타입 업로드
        nTime1.setText(nTime); // 공지 시간 업로드

        if(type.trim().equals("중요")){ // 중요공지일 경우 제목이 빨간색으로 표시됨
            nTitle1.setTextColor(Color.RED);
        }else{
            nTitle1.setTextColor(Color.BLACK);
        }

        // '>' 버튼 클릭시 공지 내용 보는 페이지로 이동
        btnNReadMore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 해당 공지의 정보를 넘겨줌
                Intent intent =  new Intent(getApplicationContext(), noticeContentActivity.class);
                intent.putExtra("writer", nWriter);
                intent.putExtra("nTitle", nTitle);
                intent.putExtra("type", type);
                intent.putExtra("nContent", nContent);
                intent.putExtra("nTime", nTime);
                intent.putExtra("nId", nId);
                intent.putExtra("ID", id);
                intent.putExtra("GID", GId);
                intent.putExtra("layoutI", layoutI);
                startActivity(intent);
            }
        });



        layoutNotice.addView(view); // 공지 layout에 추가





    }


}
