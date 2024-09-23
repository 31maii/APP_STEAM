package com.example.a3_termproject_steam;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class passwordActivity extends Activity {
    // 필요한 위젯 변수들 선언
    EditText edtID, edtPhone, edtBirth;
    TextView txtPW;
    Button btnFindPW;
    // DB 이용을 위한 변수 선언
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        // 선언된 모든 위젯들을 id값을 이용하여 연결
        edtID = (EditText) findViewById(R.id.edtID);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtBirth = (EditText) findViewById(R.id.edtBirth);
        txtPW = (TextView) findViewById(R.id.txtPW);
        btnFindPW = (Button) findViewById(R.id.btnFindPW);
        // 만들어놓은 DB 객체 생성
        myDB = new MyDatabaseHelper(this);
        // 비밀번호찾기 버튼을 클릭하였을 때 작동하는 코드
        btnFindPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText로 입력받은 정보들을 String으로 변환 후 공백을 제거하여 변수로 저장
                String id = edtID.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String birth = edtBirth.getText().toString().trim();
                // 입력 받은 정보가 하나라도 없는 경우
                if(id.length() == 0 || phone.length() == 0 || birth.length() == 0){
                    // 정보를 모두 입력하라고 토스트 메세지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(passwordActivity.this);
                    text.setText("정보를 모두 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                }
                // 정보가 제대로 입력된 경우
                else{
                    Cursor cursor = myDB.getPW(id, phone, birth);
                    // 아이디와 전화번호, 생년월일이 일치하는 경우 비밀번호 표시
                    if(cursor.getCount() != 0){
                        cursor.moveToNext();
                        String pw = cursor.getString(0);
                        txtPW.setText("비밀번호는 " + pw + "입니다.");
                    }
                    // 일치하는 정보가 없는 경우 (잘못된 정보가 입력된 경우)
                    else{
                        // 일치하는 정보가 없다고 알려주는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(passwordActivity.this);
                        text.setText("일치하는 정보가 없습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                }
            }
        });
    }
}
