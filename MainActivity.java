package com.example.a3_termproject_steam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 필요한 위젯 변수들 선언
    ImageButton homeBar,joinBar,memoBar,mypageBar, btnApplyResult;
    LinearLayout layoutStudy, layoutTeamProject;

    // DB 이용을 위해 필요한 변수들 선언
    MyDatabaseHelper helper;
    SQLiteDatabase database;
    ArrayList<Integer> gIdList;
    ArrayList<Integer> studyList;
    ArrayList<Integer> teamprojectList;
    // 로그인하면서 넘겨받은 id 값을 저장하는 변수
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Intent로 넘겨받은 값들을 받아옴
        Intent data = getIntent();
        // 로그인하면서 넘겨받은 아이디 값을 변수 id에 저장
        id = data.getStringExtra("ID1");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        homeBar = (ImageButton) findViewById(R.id.homeBar);
        joinBar = (ImageButton) findViewById(R.id.joinBar);
        memoBar = (ImageButton) findViewById(R.id.memoBar);
        mypageBar = (ImageButton) findViewById(R.id.mypageBar);

        Bottombar(homeBar, joinBar, memoBar,mypageBar);

        // 그룹에 참여신청한 결과를 보여주는 페이지로 이동하는 버튼
        btnApplyResult = (ImageButton) findViewById(R.id.btnApplyResult);
        // 버튼을 클릭하면
        btnApplyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹에 참여신청한 결과(수락/거절)를 보여주는 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ApplyResultActivity.class);
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });

    }
    // 하단바의 버튼 4개
    public void Bottombar(ImageButton btnHome, ImageButton btnJoin, ImageButton btnMemo, ImageButton btnMypage){
        // 홈버튼 클릭시 홈화면으로 이동
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });
        // Join버튼 클릭시 Join페이지로 이동
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Join.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });
        // Memo버튼을 클릭시 Chat페이지로 이동
        btnMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Memo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });
        // 마이페이지 버튼을 클릭시 마이페이지로 이동
        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);

                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // 넘겨받은 ID를 변수에 저장하여 사용
        Intent data = getIntent();
        id = data.getStringExtra("ID2");

        // 자신이 참여중인 스터디 리스트를 표시해줄 레이아웃
        layoutStudy = (LinearLayout) findViewById(R.id.layoutStudy);
        layoutTeamProject = (LinearLayout) findViewById(R.id.layoutTeamProject);
        // 자신이 참여중인 팀플 리스트를 표시해줄 레이아웃

        // 리스트가 중복되는 것을 막기 위해
        // 새로 액티비티가 시작할 때마다 처음에 모든 리스트를 지워줌 (이후에 다시 최신화된 리스트를 불러옴)
        layoutStudy.removeAllViews();
        layoutTeamProject.removeAllViews();
        // DB 사용을 위한 객체 선언, 읽기전용 DB 생성
        helper = new MyDatabaseHelper(this);
        database = helper.getReadableDatabase();
        // 미리 만들어둔 메소드를 이용하여 현재 사용자가 참여중인 그룹 정보(G1, G2, G3, G4)를 가져온다.
        Cursor cursor = helper.customerGroups(id);
        cursor.moveToNext();
        // 자신이 참여중인 그룹의 정보들을 저장할 리스트
        gIdList = new ArrayList<>();

        // 자신이 참여중인 그룹 G1, G2, G3, G4를 각각 하나씩 확인해보면서
        for(int i=0;i<4;i++){
            final int index;
            index = i;
            int gid = cursor.getInt(index);
            // 참여되어있는 그룹이 있는 경우 만들어놓은 gIdList에 추가
            if(gid!=-1) {
                gIdList.add(gid);
            }
        }
        // 사용자가 참여되어있는 그룹이 있으면
        if(gIdList.size()!=0){
            // gIdList에 들어있는 그룹들이 각각 어떤 종류인지(스터디인지 팀플인지) 구분
            for(int j=0;j<gIdList.size();j++){
                int gid = gIdList.get(j);
                // 미리 만들어놓은 그룹의 타입을 가져오는 메소드 이용
                Cursor typecursor = helper.grouptypes(gid);
                typecursor.moveToNext();
                // 스터디이면
                if(typecursor.getString(0).equals("스터디")){
                    // 해당 그룹의 이름을 가져옴 (미리 만들어놓은 메소드 이용)
                    Cursor scursor = helper.groupnames(gid);
                    scursor.moveToNext();
                    String gname = scursor.getString(0);
                    // 그룹 아이디와 그룹 이름을 넘겨주고 해당 그룹의 카드 뷰를 스터디 레이아웃에 추가하는 메소드 실행
                    addCardStudy(gid, gname);
                }
                // 팀플이면
                else{
                    // 해당 그룹의 이름을 가져옴 (미리 만들어놓은 메소드 이용)
                    Cursor tcursor = helper.groupnames(gid);
                    tcursor.moveToNext();
                    String gname = tcursor.getString(0);
                    // 그룹 아이디와 그룹 이름을 넘겨주고 해당 그룹의 카드 뷰를 팀플 레이아웃에 추가하는 메소드 실행
                    addCardTeam(gid, gname);
                }

            }
        }
    }
    // 스터디 레이아웃에 스터디그룹 카드뷰를 추가하는 메소드
    private void addCardStudy(int GroupId, String GroupName) {
        // 미리 만들어놓은 layout파일(card_study)를 getLayoutInflater()를 이용하여 inflate해준다.
        final View view = getLayoutInflater().inflate(R.layout.card_study, null);
        // card_study에 있는 TextView와 Button을 연결
        TextView name = view.findViewById(R.id.txtsName);
        ImageButton btn = view.findViewById(R.id.btnEnterStudy);
        // TextView에 그룹 이름을 적용해줌
        name.setText(GroupName);
        // 버튼을 클릭했을 때 실행되는 코드
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TeamMainPage로 이동
                Intent intent = new Intent(getApplicationContext(), TeamMainPage.class);
                // 이동 시 TemaMainPage에서 필요한 정보들(사용자 아이디, 그룹 아이디, 그룹 이름)을 넘겨줌
                intent.putExtra("ID",id);
                intent.putExtra("GID", GroupId);
                intent.putExtra("GNAME", GroupName);
                startActivity(intent);
            }
        });
        // 스터디 레이아웃에 카드뷰를 추가
        layoutStudy.addView(view);

    }
    // 팀플 레이아웃에 팀플그룹 카드뷰를 추가하는 메소드
    private void addCardTeam(int GroupId, String GroupName) {
        // 미리 만들어놓은 layout파일(card_team)를 getLayoutInflater()를 이용하여 inflate해준다.
        final View view = getLayoutInflater().inflate(R.layout.card_team, null);
        // card_team에 있는 TextView와 Button을 연결
        TextView name = view.findViewById(R.id.txttName);
        ImageButton btn = view.findViewById(R.id.btnEnterTeam);
        // TextView에 그룹 이름을 적용해줌
        name.setText(GroupName);
        // 버튼을 클릭했을 때 실행되는 코드
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TeamMainPage로 이동
                Intent intent = new Intent(getApplicationContext(), TeamMainPage.class);
                // 이동 시 TemaMainPage에서 필요한 정보들(사용자 아이디, 그룹 아이디, 그룹 이름)을 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", GroupId);
                intent.putExtra("GNAME", GroupName);
                startActivity(intent);
            }
        });
        // 팀플 레이아웃에 카드뷰를 추가
        layoutTeamProject.addView(view);

    }

    private long backpressedTime = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();

            LayoutInflater inflater = getLayoutInflater();

            View layout = inflater.inflate(R.layout.toastcustom,
                    (ViewGroup) findViewById(R.id.toastLayout));

            TextView text = layout.findViewById(R.id.toastText);

            Toast toast = new Toast(MainActivity.this);
            text.setText("\'뒤로가기\'를 한 번 더 누르시면 앱이 종료됩니다.");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }

    }
}