package com.example.a3_termproject_steam;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    // DB이용을 위해 필요한 변수 선언
    int version =1;
    MyDatabaseHelper helper;
    SQLiteDatabase database;
    String sql;
    Cursor cursor;

    // 필요한 위젯 변수들 선언
    EditText id_EditText, pw_EditText, pw_check_EditText, name_EditText,  phone_EditText, email_EditText, birth_EditText;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // ??
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // 필요한 모든 위젯들을 id값을 이용하여 연결
        id_EditText = (EditText) findViewById(R.id.signup_id_editText);
        pw_EditText = (EditText) findViewById(R.id.signup_pw_editText);
        pw_check_EditText = (EditText) findViewById(R.id.signup_pw_check_editText);
        name_EditText = (EditText) findViewById(R.id.signup_name_editText);
        phone_EditText = (EditText) findViewById(R.id.signup_phone_editText);
        email_EditText = (EditText) findViewById(R.id.signup_email_editText);
        birth_EditText = (EditText) findViewById(R.id.signup_birth_editText);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        // DB Helper 객체와 읽기 전용 database 생성
        helper = new MyDatabaseHelper(this);
        database = helper.getReadableDatabase();

        // 회원가입 버튼을 눌렀을 때 작동하는 코드
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 입력된 모든 Text를 가져와서 String 형으로 변경 후 공백까지 제거
                String id = id_EditText.getText().toString().trim();
                String pw = pw_EditText.getText().toString().trim();
                String pw_check = pw_check_EditText.getText().toString().trim();
                String name = name_EditText.getText().toString().trim();
                String phone = phone_EditText.getText().toString().trim();
                String email = email_EditText.getText().toString().trim();
                String birth = birth_EditText.getText().toString().trim();
                // 누락된 정보가 없는지 확인하는 과정
                // 누락된 정보가 있는 경우 토스트 메세지 출력
                if(!(id.length()>=6 && id.length()<=10)) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("아이디를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (!(pw.length()>=6 && pw.length()<=10)){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("비밀번호를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (!(pw_check.length()>=6 && pw_check.length()<=10)) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("비밀번호를 재입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (name.length()==0) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("이름을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (phone.length()==0) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("연락처를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (email.length() == 0) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("이메일을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                } else if (birth.length() == 0) {
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("생년월일를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                    // 비밀번호와 비밀번호 확인 입력값이 일치하는지 검사하여
                } else if (!pw_check.equals(pw)) {
                    // 일치하지 않는 경우 토스트 메세지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("비밀번호가 일치하지 않습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return;
                }

                // 입력한 아이디를 사용중인 다른 사용자가 있는지 검색하여 확인
                sql = "SELECT NAME FROM CUSTOMER WHERE custID = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                // 다른 사용자가 아이디를 사용중인 경우
                if(cursor.getCount() != 0) {
                    // 토스트 메세지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("이미 존재하는 아이디입니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    // 사용 가능한 아이디인 경우
                } else {
                    // 미리 만들어놓은 사용자 추가 함수를 이용하여 입력받은 값들의 정보를 가지는 사용자를 추가 (회원가입을 완료)
                    helper.addCustomer(id, pw, name, phone, email, birth, -1, "x", -1, "x", -1, "x", -1, "x");
                    // DB에 정보를 추가한 후 회원가입이 완료되었다는 토스트메세지 출력 후 다시 로그인 화면으로 이동
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(SignUpActivity.this);
                    text.setText("회원가입을 완료했습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}