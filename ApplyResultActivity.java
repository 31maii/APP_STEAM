package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ApplyResultActivity extends Activity {
    // 사용할 위젯, DB 변수 선언
    LinearLayout layoutConsent, layoutDeny;
    String id;
    MyDatabaseHelper myDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_result);
        // 사용할 위젯 연결
        layoutConsent = (LinearLayout) findViewById(R.id.layoutConsent);
        layoutDeny = (LinearLayout) findViewById(R.id.layoutDeny);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 넘겨받은 데이터들을 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID1");
        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);

        // 현재 사용자가 참여신청한 내역에 대한 결과(수락/거절)들을 모두 불러옴
        Cursor ccursor = myDB.getConsent(id);
        Cursor dcursor = myDB.getDeny(id);

        if(ccursor != null){
            // 수락 내역을 하나씩 카드뷰로 만들어서 보여주는 과정으로
            // 카드뷰를 추가하는 과정들은 앞서 설명한 코드와 동일하다
            while(ccursor.moveToNext()){
                int gid = ccursor.getInt(0);
                Cursor cursor = myDB.groupnames(gid);
                cursor.moveToNext();
                String txt = cursor.getString(0)+" 그룹에서 참여 신청을 수락하였습니다.";

                addCardConsent(txt, gid);
            }
        }
        if(dcursor != null){
            // 거절 내역을 하나씩 카드뷰로 만들어서 보여주는 과정으로
            // 카드뷰를 추가하는 과정들은 앞서 설명한 코드와 동일하다.
            while(dcursor.moveToNext()){
                int gid = dcursor.getInt(0);
                Cursor cursor = myDB.groupnames(gid);
                cursor.moveToNext();
                String txt = cursor.getString(0)+" 그룹에서 참여 신청을 거절하였습니다.";

                addCardDeny(txt, gid);
            }
        }
    }
    // 수락 내용을 카드뷰로 만들어서 하나씩 레이아웃에 추가하는 메소드로
    // 카드뷰를 추가하는 내용은 앞서 설명한 내용들과 동일하다.
    private void addCardConsent(String txt, int gid) {

        final View view = getLayoutInflater().inflate(R.layout.card_apply_result, null);
        TextView name = view.findViewById(R.id.txtApplyResultName);
        Button btn = view.findViewById(R.id.btnDeleteApplyResult);

        name.setText(txt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyResultActivity.class);
                intent.putExtra("ID1",id);
                myDB.deleteConsent(id, gid);
                finish();
                startActivity(intent);
            }
        });
        layoutConsent.addView(view);

    }
    // 수락 내용을 카드뷰로 만들어서 하나씩 레이아웃에 추가하는 메소드로
    // 카드뷰를 추가하는 내용은 앞서 설명한 내용들과 동일하다.
    private void addCardDeny(String txt, int gid) {

        final View view = getLayoutInflater().inflate(R.layout.card_apply_result, null);
        TextView name = view.findViewById(R.id.txtApplyResultName);
        Button btn = view.findViewById(R.id.btnDeleteApplyResult);

        name.setText(txt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplyResultActivity.class);
                intent.putExtra("ID1",id);
                myDB.deleteDeny(id, gid);
                finish();
                startActivity(intent);
            }
        });
        layoutDeny.addView(view);
    }
}
