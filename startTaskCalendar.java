package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class startTaskCalendar extends Activity {
    // 사용할 위젯변수, 날짜 저장할 변수 선언
    DatePicker dp;
    Button btnEnrollDate;
    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    String date;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        // 위젯 연결
        btnEnrollDate = (Button) findViewById(R.id.btnEnrollDate);
        dp = (DatePicker) findViewById(R.id.dp);
        // 데이트피커의 시작 날짜를 현재 날짜로 지정
        dp.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int y, int m, int d) {
                year = y;
                month = m+1;
                day = d;
            }
        });
        // 등록버튼을 클릭하면 작동하는 코드
        btnEnrollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 날짜를 문자열로 만들어서 넘겨주고 이전 페이지로 다시 이동
                int num=1;
                date = Integer.toString(year)+"."+Integer.toString(month)+"."+Integer.toString(day);

                Intent outIntent = new Intent(getApplicationContext(), plusScheduleActivity.class);
                outIntent.putExtra("SDate", date);
                outIntent.putExtra("Num", num);
                setResult(RESULT_OK,outIntent);
                finish();
            }
        });

    }
}
