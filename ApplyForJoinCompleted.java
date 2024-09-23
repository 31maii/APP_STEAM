package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ApplyForJoinCompleted extends Activity {
    // 사용할 위젯 변수 선언
    Button btnGoToHome2, btnMoreGroup1;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_for_join_completed);
        // 넘겨받은 데이터 변수에 저장
        Intent data = getIntent();
        String id = data.getStringExtra("ID");
        // 사용할 위젯변수 연결
        btnGoToHome2 = (Button) findViewById(R.id.btnGoToHome2);
        btnMoreGroup1 = (Button) findViewById(R.id.btnMoreGroup1);
        // 홈으로 버튼을 클릭하면
        btnGoToHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈으로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ID1",id);
                intent.putExtra("ID2",id);
                startActivity(intent);
                finish();
            }
        });
        // 그룹 더보기 버튼을 클릭하면
        btnMoreGroup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 참여 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), Join.class);
                intent.putExtra("ID1",id);
                intent.putExtra("ID2",id);
                startActivity(intent);
                finish();
            }
        });
    }
}
