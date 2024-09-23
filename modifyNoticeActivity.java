package com.example.a3_termproject_steam;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.Date;


public class modifyNoticeActivity extends Activity { // 공지 수정 페이지

    EditText edtNoticeName, edtNoticeContent;
    RadioGroup rGroupNoticeType;
    RadioButton rBtnNormal, rBtnImportant;
    Button btnModify, btnCancel;

    String id;
    int GId, nId;


    String noticeTitle, noticeContent, noticeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_notice);
        setTitle("공지사항 작성");
        edtNoticeName = (EditText) findViewById(R.id.edtModifyNoticeName);
        edtNoticeContent = (EditText) findViewById(R.id.edtModifyNoticeContent);
        rGroupNoticeType = (RadioGroup) findViewById(R.id.rGroupNoticeType2);
        rBtnNormal = (RadioButton) findViewById(R.id.rBtnNormal2);
        rBtnImportant = (RadioButton) findViewById(R.id.rBtnImportant2);
        btnModify = (Button) findViewById(R.id.btnNModify2);
        btnCancel = (Button) findViewById(R.id.btnNModifyCancel2);

        // 공지 내용 페이지에서 해당 공지에 대한 정보 가져옴
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        GId = inIntent.getIntExtra("GID", 0);
        nId = inIntent.getIntExtra("nId", 0);
        noticeTitle = inIntent.getStringExtra("nTitle");
        noticeType = inIntent.getStringExtra("nType");
        noticeContent = inIntent.getStringExtra("nContent");

        // 수정하고자 하는 공지의 기존 공지 제목과 내용이 작성란에 표시됨
        edtNoticeName.setText(noticeTitle);
        edtNoticeContent.setText(noticeContent);

        // 수정하고자 하는 공지의 기존 공지 타입에 따라 radio 체크
        if(noticeType.trim().equals("중요")){
            rBtnImportant.setChecked(true);
        }else{
            rBtnNormal.setChecked(true);
        }

        // 공지 수정 버튼
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeTitle = edtNoticeName.getText().toString(); // 작성한 공지 제목 변수에 저장
                noticeContent = edtNoticeContent.getText().toString(); // 작성한 공지 내용 변수에 저장

                if(noticeTitle.trim().equals("")){ // 공지 제목이 비어있다면 수정 불가능
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(modifyNoticeActivity.this);
                    text.setText("공지명을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    if(noticeContent.trim().equals("")){ // 공지 내용이 비어있다면 수정 불가능
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(modifyNoticeActivity.this);
                        text.setText("공지 내용을 입력하세요");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }else{ // 공지 제목과 내용이 작성되어 있다면

                        if(rBtnImportant.isChecked()){ // 공지 타입에 따라 공지 타입 변수에 저장
                            noticeType="중요";
                        }
                        else{
                            noticeType="일반";
                        }

                        //db 내용 추가
                        MyDatabaseHelper myDB = new MyDatabaseHelper(modifyNoticeActivity.this);
                        myDB.updateNotice(GId, nId, noticeTitle, noticeType, noticeContent); // 공지 db 수정

                        Intent outIntent = new Intent(getApplicationContext(), noticeContentActivity.class);
                        outIntent.putExtra("nTitle", noticeTitle);
                        outIntent.putExtra("nType", noticeType);
                        outIntent.putExtra("nContent", noticeContent);
                        setResult(RESULT_OK, outIntent); // 이전 화면에 수정된 내용을 돌려주기 위해


                        // 공지 수정 화면 종료
                        finish();

                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(modifyNoticeActivity.this);
                        text.setText("공지가 수정되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();




                    }
                }



            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() { // 수정 취소 버튼
            @Override
            public void onClick(View v) {
                finish();
            } // 공지 수정 화면 종료
        });




    }
}
