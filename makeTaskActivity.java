package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class makeTaskActivity extends Activity {
    // 사용할 위젯 변수, DB 변수 선언
    EditText edtTaskName, edtTaskContent;
    TextView txtTaskStartDate, txtTaskEndDate;
    ImageButton btnChoiceTaskStartDate;
    ImageButton btnChoiceTaskEndDate;
    Button btnEnrollTask;
    String name, content, id, customerName, sdate, edate;
    int gid;
    MyDatabaseHelper myDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_task);
        // 넘겨받은 데이터들 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID",0);
        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);
        // 위젯 변수들 연결
        edtTaskName = (EditText) findViewById(R.id.edtTaskName);
        edtTaskContent = (EditText) findViewById(R.id.edtTaskContent);
        txtTaskStartDate = (TextView) findViewById(R.id.txtTaskStartDate);
        txtTaskEndDate = (TextView) findViewById(R.id.txtTaskEndDate);
        btnChoiceTaskStartDate = (ImageButton) findViewById(R.id.btnChoiceTaskStartDate);
        btnChoiceTaskEndDate = (ImageButton) findViewById(R.id.btnChoiceTaskEndDate);
        // 시작일 선택 버튼을 클릭하면
        btnChoiceTaskStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 날짜선택 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), startTaskCalendar.class);
                // 결과를 넘겨받는 startAcitivityForResult 함수를 사용
                startActivityForResult(intent, 0);
            }
        });
        // 마감일 선택 버튼을 클릭하면
        btnChoiceTaskEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 날짜선택 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), endTaskCalendar.class);
                // 결과를 넘겨받는 startAcitivityForResult 함수를 사용
                startActivityForResult(intent, 0);
            }
        });
        // Task 등록 버튼
        btnEnrollTask = (Button) findViewById(R.id.btnEnrollTask);
        // 등록 버튼 클릭 하면
        btnEnrollTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // name을 이름으로, content를 내용으로 하는 Task 생성
                name = edtTaskName.getText().toString();
                content = edtTaskContent.getText().toString();
                // 입력값이 빈칸인지 확인하는 코드
                if(name.trim().equals("")){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(makeTaskActivity.this);
                    text.setText("이름을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    if(content.trim().equals("")){
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(makeTaskActivity.this);
                        text.setText("내용을 입력하세요");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }else{
                        Intent outIntent = new Intent(getApplicationContext(), workActivity.class);
                        // Task 테이블에 정보 저장
                        myDB.addTask(gid, id, name, content, sdate, edate);
                        setResult(RESULT_OK, outIntent);
                        finish();
                    }
                }
            }
        });
    }
    // 넘겨받은 결과값을 가져와서 처리하는 코드
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            // 넘겨받은 변수 i가 어떤 값인지에 따라 시작일, 마감일을 구분하여 저장 후 텍스트뷰에 날짜도 적용하도록
            int i = data.getIntExtra("Num",0);
            if(i==1){
                sdate = data.getStringExtra("SDate");
                txtTaskStartDate.setText(sdate);
            }
            else if(i==2){
                edate = data.getStringExtra("EDate");
                txtTaskEndDate.setText(edate);
            }

        }
    }
}
