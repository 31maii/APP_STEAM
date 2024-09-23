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

public class LoginActivity extends AppCompatActivity {
    // 필요한 위젯 변수 선언
    Button gotoSignUpButton, loginButton, gotoPasswordResetButton;
    EditText idEditText, pwEditText;
    // DB 이용을 위해 필요한 변수 선언
    MyDatabaseHelper helper;
    SQLiteDatabase database;
    String sql;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // xml의 회원가입 버튼 연결
        gotoSignUpButton = (Button) findViewById(R.id.gotoSignUpButton);
        // xml의 로그인 버튼 연결
        loginButton = (Button) findViewById(R.id.loginButton);
        // xml의 비밀번호 찾기 버튼 연결
        gotoPasswordResetButton = (Button) findViewById(R.id.gotoPasswordResetButton);
        // 아이디와 비밀번호 입력받을 EditText 연결
        idEditText = (EditText)findViewById(R.id.login_id_editText);
        pwEditText = (EditText)findViewById(R.id.login_pw_editText);
        // 사용하기 위해 만들어놓은 SQLiteOpenHelper 객체 생성
        helper = new MyDatabaseHelper(this);
        // DB에서 데이터를 읽기 위한 데이터베이스를 생성
        database = helper.getReadableDatabase();

        // 로그인 버튼을 눌렀을 때
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디, 비밀번호 각 EditText에 입력된 Text를 가져옴
                // 공백만 입력된 경우의 오류를 방지하기 위해서 .trim()으로 공백 제거
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();

                // 아이디 또는 비밀번호 둘 중 하나 이상 입력하지 않은 경우 (또는 공백을 입력한 경우)
                // 토스트메시지 출력
                if(id.length() == 0 || pw.length() == 0) {

                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(LoginActivity.this);
                    text.setText("아이디 또는 비밀번호를 입력하세요.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    return;
                }
                // 입력한 아이디가 존재하는지(회원인지) 검색하기 위한 sql문
                sql = "SELECT Name FROM CUSTOMER WHERE custID = '" + id + "'";
                cursor = database.rawQuery(sql, null);
                // Cursor의 개수가 1개가 아니면, 즉 가입된 아이디가 없으면
                if(cursor.getCount() != 1) {
                    // 토스트 메시지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(LoginActivity.this);
                    text.setText("존재하지 않는 아이디입니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    return;
                }
                // 입력한 비밀번호가 올바른 비밀번호인지 검색하여 확인하기 위한 sql문
                sql = "SELECT PASSWORD FROM CUSTOMER WHERE custID = '" + id + "'";
                cursor = database.rawQuery(sql, null);
                cursor.moveToNext();
                // 입력한 비밀번호와 데이터베이스에 저장되어있는 비밀번호와 일치하지 않으면
                if(!pw.equals(cursor.getString(0))) {
                    // 비밀번호가 틀렸음을 알려주는 토스트 메시지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(LoginActivity.this);
                    text.setText("비밀번호가 틀렸습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
                // 비밀번호가 올바른 경우
                else {
                    // 로그인이 성공했다는 토스트 메시지 출력 후
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(LoginActivity.this);
                    text.setText(id + "님 로그인에 성공했습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    // 메인(홈) 화면으로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // 현재 사용자의 아이디를 넘겨줌
                    // 앱을 사용하면서 현재 사용자와 관련된 데이터를 저장하거나 또는 불러오기 위해 아이디가 필요
                    intent.putExtra("ID1", id);
                    intent.putExtra("ID2", id);
                    // 로그인 후 메인 화면으로만 아이디를 넘겨주는 것이 아니라
                    // 앱이 작동하기 위해 거의 모든 액티비티에서 아이디가 필요하기 때문에
                    // 대부분의 Intent에 아이디를 덧붙여 넘겨줌

                    startActivity(intent);
                    finish();
                }
                cursor.close();
            }
        });
        // 회원가입 버튼을 클릭하면
        gotoSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        // 비밀번호 찾기 버튼을 클릭하면
        gotoPasswordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비밀번호 찾기 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), passwordActivity.class);
                startActivity(intent);
            }
        });
    }


}