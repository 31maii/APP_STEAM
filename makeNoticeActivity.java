package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class makeNoticeActivity extends Activity { // 공지 만들기 페이지
    // 사용할 위젯 변수 선언
    EditText edtNoticeName, edtNoticeContent;
    RadioGroup rGroupNoticeType;
    RadioButton rBtnNormal, rBtnImportant;
    Button btnEnroll, btnCancel;
    String id;
    int GId;
    String noticeTitle, noticeContent, noticeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_notice);
        setTitle("공지사항 작성");
        // 위젯변수들 연결
        edtNoticeName = (EditText) findViewById(R.id.edtNoticeName);
        edtNoticeContent = (EditText) findViewById(R.id.edtNoticeContent);
        rGroupNoticeType = (RadioGroup) findViewById(R.id.rGroupNoticeType);
        rBtnNormal = (RadioButton) findViewById(R.id.rBtnNormal);
        rBtnImportant = (RadioButton) findViewById(R.id.rBtnImportant);
        btnEnroll = (Button) findViewById(R.id.btnEnroll);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        // 넘겨받은 데이터들 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        GId = inIntent.getIntExtra("GID", 0);
        // 등록버튼을 클릭했을 때
        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                noticeTitle = edtNoticeName.getText().toString();
                noticeContent = edtNoticeContent.getText().toString();

                if(noticeTitle.trim().equals("")){ // 공지 제목이 비어있다면 공지 저장 불가
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(makeNoticeActivity.this);
                    text.setText("공지 제목을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    if(noticeContent.trim().equals("")){ // 공지 내용이 비어있다면 공지 내용 저장 불가
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(makeNoticeActivity.this);
                        text.setText("공지 내용을 입력하세요");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }else{

                        if(rGroupNoticeType.getCheckedRadioButtonId()== R.id.rBtnImportant){
                            noticeType="중요"; // 체크된 공지 타입 라디오 버튼이 '중요'라면, 공지 타입 변수에 '중요' 저장
                        }
                        else{
                            noticeType="일반"; // 체크된 공지 타입 라디오 버튼이 '일반'이라면, 공지 타입 변수에 '일반' 저장
                        }

                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 현재 시간. 공지 등록 시간을 저장하기 위함

                        //db 내용 추가
                        MyDatabaseHelper myDB = new MyDatabaseHelper(makeNoticeActivity.this);
                        myDB.addNotice(GId, id, noticeTitle, noticeType, noticeContent, currentTime);// db에 공지 추가


                        // 끝내기
                        finish();

                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(makeNoticeActivity.this);
                        text.setText("공지가 등록되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();



                    }
                }



            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() { // 공지 등록 취소
            @Override
            public void onClick(View v) {
                finish();
            } // 공지 만들기 페이지 나가기
        });




    }
}
