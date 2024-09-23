package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class workActivity extends Activity {
    // 필요한 위젯변수 및 DB사용을 위한 변수 선언
    LinearLayout layoutTask;
    ImageButton btnMakeTask;
    int gid;
    String id, role;
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_main);
        // 사용할 버튼 연결
        btnMakeTask = (ImageButton) findViewById(R.id.btnMakeTask);
        // Task만들기 버튼을 클릭하면
        btnMakeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Task생성 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), makeTaskActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("GID",gid);
                startActivityForResult(intent,0);
            }
        });
        // Task생성 버튼은 팀장에게만 보이도록 지정하기 위해 일단 visible 속성을 gone으로 지정
        btnMakeTask.setVisibility(View.GONE);
        Intent data = getIntent();
        // 역할 확인 후 팀장인 경우 다시 visible속성을 VISIBLE로 지정
        role = data.getStringExtra("ROLE");
        if(role.equals("팀장")){
            btnMakeTask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Task들을 동적으로 레이아웃에 표시하기 위한 코드
        layoutTask = (LinearLayout) findViewById(R.id.layoutTask);
        // 시작 전 모든 뷰를 지운 후 (중복을 막기 위해)
        layoutTask.removeAllViews();
        // 넘겨받은 값들을 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID",0);
        role = data.getStringExtra("ROLE");
        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);
        // DB에 저장된 TASK들 중 현재 그룹아이디 속성을 가지는 Task들만을 가져옴
        Cursor taskCursors = myDB.getTasks(gid);
        // 가져온 모든 Task들의 정보들을 하나씩 가져와서
        while(taskCursors.moveToNext()){
            int taskId = taskCursors.getInt(0);
            String taskTitle = taskCursors.getString(1);
            String taskContent = taskCursors.getString(2);
            String sdate = taskCursors.getString(3);
            String edate = taskCursors.getString(4);
            // 만들어놓은 함수로 카드뷰로 만들어서 하나씩 추가
            addCardTask(taskId, taskTitle, taskContent, sdate, edate);
        }
    }
    // Task 카드뷰를 만들어서 레이아웃에 추가하는 함수로 Task와 관련된 데이터들을 파라미터로 입력받음
    private void addCardTask(int tid, String ttitle, String tcontent, String sDate, String eDate) {
        // 미리 만들어놓은 카드뷰를 가져와서
        final View view = getLayoutInflater().inflate(R.layout.card_task, null);
        // 카드뷰의 위젯 연결
        TextView name = view.findViewById(R.id.txtTaskName);
        // 입력받은 정보로 카드뷰의 위젯 변경
        name.setText(ttitle);
        // 카드뷰 위젯에 이벤트 리스너 적용
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Task를 클릭하면 해당 Task 상세 페이지로 이동하도록
                Intent intent = new Intent(getApplicationContext(), taskActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("ROLE",role);
                intent.putExtra("GID1",gid);
                intent.putExtra("GID2",gid);
                intent.putExtra("TID", tid);
                intent.putExtra("TTITLE", ttitle);
                intent.putExtra("TCONTENT", tcontent);
                intent.putExtra("SDATE", sDate);
                intent.putExtra("EDATE", eDate);
                startActivity(intent);
            }
        });
        // 다 만들어진 카드뷰를 레이아웃에 추가
        layoutTask.addView(view);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            LayoutInflater inflater = getLayoutInflater();

            View layout = inflater.inflate(R.layout.toastcustom,
                    (ViewGroup) findViewById(R.id.toastLayout));

            TextView text = layout.findViewById(R.id.toastText);

            Toast toast = new Toast(workActivity.this);
            text.setText("Task가 생성되었습니다.");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }


}
