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

public class endTaskCalendar extends Activity {
    // 사용할 위젯, 날짜를 저장할 변수들 선언
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
        // 사용할 위젯들 연결
        btnEnrollDate = (Button) findViewById(R.id.btnEnrollDate);
        dp = (DatePicker) findViewById(R.id.dp);
        // 현재시간으로 데이트피커 설정
        dp.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int y, int m, int d) {
                year = y;
                month = m+1;
                day = d;
            }
        });
        // 날짜 등록 버튼을 클릭하면
        btnEnrollDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 날짜를 문자열로 만들어서 넘겨주고 다시 이전 페이지로 이동
                int num=2;
                date = Integer.toString(year)+"."+Integer.toString(month)+"."+Integer.toString(day);
                Intent outIntent = new Intent(getApplicationContext(), plusScheduleActivity.class);
                outIntent.putExtra("EDate", date);
                outIntent.putExtra("Num",num);
                setResult(RESULT_OK,outIntent);
                finish();
            }
        });

    }
}
