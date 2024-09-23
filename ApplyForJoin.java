package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ApplyForJoin extends Activity {
    // 사용할 위젯변수들, DB관련 변수들 선언
    EditText edtGroupCodeKey1;
    Button btnApplyComplete1, btnApplyCancel1;
    TextView tvGroupCodeKey1;
    TextView tvApplyGroupName;
    MyDatabaseHelper myDB;
    String id;
    int gid;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_for_join);
        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);
        // 사용할 위젯 변수들 연결
        tvGroupCodeKey1 = (TextView) findViewById(R.id.tvGroupCodeKey1);
        edtGroupCodeKey1 = (EditText) findViewById(R.id.edtGroupCodeKey1);

        btnApplyComplete1 = (Button) findViewById(R.id.btnApplyComplete1);
        btnApplyCancel1= (Button) findViewById(R.id.btnApplyCancel1);

        tvApplyGroupName = (TextView) findViewById(R.id.tvApplyGroupName);

        // 넘겨받은 값들을 변수에 지정
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gid = inIntent.getIntExtra("GID",0);
        // 넘겨받은 그룹 이름을 텍스트뷰에 지정
        tvApplyGroupName.setText(inIntent.getStringExtra("GroupName"));
        // 현재 그룹의 그룹코드가 없으면
        if(inIntent.getStringExtra("gCode").equals("no")){
            // 그룹코드 입력 에디트 텍스트가 사용 불가능 하도록 지정 및 색상 변경
            edtGroupCodeKey1.setEnabled(false);
            edtGroupCodeKey1.setBackgroundColor(Color.LTGRAY);
        }
        // 그룹코드를 입력해야하는 경우
        else{
            // 그롭코드를 입력할 에디트 텍스트가 사용 가능하도록 변경
            edtGroupCodeKey1.setEnabled(true);
        }
        // 참여완료 버튼을 클릭하면
        btnApplyComplete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 공개 그룹인 경우 (그룹코드가 없는 경우)
                if(inIntent.getStringExtra("gCode").equals("no")){
                    // DB에 참여신청 데이터를 저장 후 참여신청 완료 페이지로 이동
                    myDB.addApply(gid,id);
                    Intent intent = new Intent(getApplicationContext(), ApplyForJoinCompleted.class);
                    intent.putExtra("ID",id);
                    startActivity(intent);
                    finish();
                }
                // 비공개 그룹인 경우 (그룹코드를 입력해야하는 경우)
               else{
                   // 그룹코드가 일치하는 경우
                    if(edtGroupCodeKey1.getText().toString().trim().equals(inIntent.getStringExtra("gCode"))){
                        // DB에 참여신청 데이터 저장 후 참여신청 완료 페이지로 이동
                        myDB.addApply(gid,id);
                        Intent intent = new Intent(getApplicationContext(), ApplyForJoinCompleted.class);
                        intent.putExtra("ID",id);
                        startActivity(intent);
                        finish();
                    }
                    // 그룹코드 일치하지 않는 경우
                    else{
                        // 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(ApplyForJoin.this);
                        text.setText("그룹 코드가 일치하지 않습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                }

            }
        });
        // 신청 취소 버튼을 클릭하면
        btnApplyCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), Join.class);
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
                finish();
            }
        });
    }
}
