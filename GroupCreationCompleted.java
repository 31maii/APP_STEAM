package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GroupCreationCompleted extends Activity {
    // 사용할 위젯 변수 선언
    Button btnGoToHome;
    String id;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_creation_completed);
        // 버튼 연결
        btnGoToHome = (Button) findViewById(R.id.btnGoToHome);
        // 넘겨받은 데이터 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID1");
        // 홈으로 버튼 클릭하면
        btnGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 화면으로 이동하도록
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ID1",id);
                intent.putExtra("ID2",id);
                startActivity(intent);
            }
        });

    }
}
