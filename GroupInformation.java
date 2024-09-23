package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupInformation extends Activity {
    // 필요한 위젯, DB관련 변수들 선언
    Button btnApply1;
    TextView tvGroupName1, tvGroupSorting1, tvParticipaitonNum1, tvLockUnlock1, tvGroupCategory1, tvGroupIntroduce1;
    ImageView imgGroupSorting1,imgLockUnlock1;
    String id;
    int gid;
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_information);
        // 사용할 위젯들을 연결
        btnApply1 = (Button) findViewById(R.id.btnApply1);

        tvGroupName1 = (TextView) findViewById(R.id.tvGroupName1);
        tvGroupSorting1 = (TextView) findViewById(R.id.tvGroupSorting1);
        tvParticipaitonNum1 = (TextView) findViewById(R.id.tvParticipaitonNum1);
        tvLockUnlock1 = (TextView) findViewById(R.id.tvLockUnlock1);
        tvGroupCategory1 = (TextView) findViewById(R.id.tvGroupCategory1);
        tvGroupIntroduce1 = (TextView) findViewById(R.id.tvGroupIntroduce1);

        imgGroupSorting1 = (ImageView) findViewById(R.id.imgGroupSorting1);
        imgLockUnlock1 = (ImageView) findViewById(R.id.imgLockUnlock1);
        // 넘겨받은 값들을 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gid = inIntent.getIntExtra("GID",0);
        // 넘겨받은 이름을 텍스트에 적용
        tvGroupName1.setText(inIntent.getStringExtra("GroupName"));

        // 그룹 구분이 스터디인지 팀플인지에 따라 GroupSorting 텍스트뷰(tvGroupSorting1)와 이미지뷰(imgGroupSorting1) 바꿔야 함.
        tvGroupSorting1.setText(inIntent.getStringExtra("type"));
        if(tvGroupSorting1.getText().toString().equals("스터디")){
            imgGroupSorting1.setImageResource(R.drawable.edit); //--> 스터디
        }else if(tvGroupSorting1.getText().toString().equals("팀 프로젝트")){
            imgGroupSorting1.setImageResource(R.drawable.laptop); //--> 팀플
        }

        // 제한 인원과 현재 참여 인원에 따라 tvParticipationNum1 텍스트 뷰 바꿔야함
        tvParticipaitonNum1.setText(inIntent.getStringExtra("people"));

        // 그룹 공개 여부에 따라 텍스트뷰(tvLockUnlock1), 이미지뷰(imgLockUnlock1) 바꿔야 함
        tvLockUnlock1.setText(inIntent.getStringExtra("lockOrUnlock"));
        if(tvLockUnlock1.getText().toString().equals("공개")){
            imgLockUnlock1.setImageResource(R.drawable.unlock); //--> 공개시
        }else if(tvLockUnlock1.getText().toString().equals("비공개")){
            imgLockUnlock1.setImageResource(R.drawable.lock); //--> 비공개시
        }

        // 해당 그룹의 카테고리 표시해주기 (tvGroupCategory1)
        tvGroupCategory1.setText(inIntent.getStringExtra("category"));

        // 해당 그룹의 그룹 소개 표시해주기 (tvGroupIntroduce1)
        tvGroupIntroduce1.setText(inIntent.getStringExtra("gContent"));

        // 참여신청 버튼을 클릭하면
        btnApply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 참여신청 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ApplyForJoin.class);
                intent.putExtra("GroupName", inIntent.getStringExtra("GroupName"));
                intent.putExtra("gCode", inIntent.getStringExtra("gCode"));
                intent.putExtra("GID",gid);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });
        // DB사용을 위한 객체 생성
        myDB = new MyDatabaseHelper(this);
        // DB에서 현재 사용자가 참여되어있는 그룹정보를 가져옴(G1~G4를 가져옴)
        Cursor cursor = myDB.customerGroups(id);
        cursor.moveToNext();
        // G1~G4 중에서 현재 그룹과 일치하는 그룹이 있는지 확인 -> 자신이 현재 참여중인 그룹에는 참여신청을 할 수 없도록 만들기 위한 목적
        // 자신이 현재 그룹에 참여중이지 않으면 정수형 변수 mygroup의 값은 반복문을 지난 후에도 그대로 0일 것이고
        // 참여중인 그룹이 있다면 1로 변경되었을 것이다.
        // 추가적으로 현재 사용자가 4개의 그룹에 모두 참여되어있어서 더이상 그룹에 참여할 수 없는 경우도
        // 그룹에 참여신청을 할 수 없도록 해줘야한다.
        // 이것을 확인하기 위한 변수로 gnum을 초기값 0으로 설정한다.
        // 만약 반복문을 돌고나온 후에 gnum값이 4인 경우는 현재 사용자가 4개의 그룹에 참여되어있기 때문에 더이상 그룹에 참여할 수 없는 경우이다.
        int mygroup = 0;
        int gnum=0;
        for(int k=0;k<4;k++){
            final int index;
            index = k;
            int g1234 = cursor.getInt(index);
            // 현재 그룹에 사용자가 이미 참여되어있으면
            if(gid == g1234){
                // 변수 값을 1로 변경
                mygroup=1;
            }
            // 현재 사용자가 참여되어있는 그룹이 있는 경우
            if(g1234 != -1){
                // 1씩 증가
                gnum++;
            }
        }
        // 만약 현재 사용자가 이미 참여중인 그룹이거나 또는 이미 4개의 그룹에 참여중인 경우
        if(mygroup==1 || gnum==4){
            // 참여신청 버튼이 보이지 않도록
            btnApply1.setVisibility(View.GONE);
        }
    }
}
