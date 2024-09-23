package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class modifyScheduleActivity extends Activity { // 일정 수정 페이지

    String ScheduleName,ScheduleType; // 스케쥴 명, 스케쥴 종류

    EditText edtScheduleName2;
    TextView tvScheduleDate2, tvStartTime2, tvEndTime2;
    ImageButton btnChoiceScheduleDate2;
    ImageButton btnStartTime2;
    Button btnEndTime2;
    Button btnModifySchedule1;
    Button btnCancelModifySchedule1;
    RadioGroup RScheduleType2;
    RadioButton rIndividualSchedule2, rGroupSchedule2;


    Calendar cal = Calendar.getInstance();
    int mYear = cal.get(Calendar.YEAR);
    int mMonth = cal.get(Calendar.MONTH);
    int mDay = cal.get(Calendar.DAY_OF_MONTH);
    int mHour = cal.get(Calendar.HOUR_OF_DAY);
    int mMinute = cal.get(Calendar.MINUTE);

    String date, startTime, endTime;

    String id, sName, sType, sDate, StartTime, FinishTime;
    int gId, sId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    ArrayList<String> memberid, sname, stype, sdate, sStarttime, sEndtime;
    ArrayList<Integer> sid, gid;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_schedule);

        edtScheduleName2 = (EditText) findViewById(R.id.edtScheduleName2);

        tvScheduleDate2 = (TextView) findViewById(R.id.tvScheduleDate2);
        tvStartTime2 = (TextView) findViewById(R.id.tvStartTime2);
        tvEndTime2 = (TextView) findViewById(R.id.tvEndTime2);


        btnChoiceScheduleDate2 = (ImageButton) findViewById(R.id.btnChoiceScheduleDate2);
        btnStartTime2 = (ImageButton) findViewById(R.id.btnStartTime2);
        btnEndTime2 = (Button) findViewById(R.id.btnEndTime2);
        btnModifySchedule1 = (Button) findViewById(R.id.btnModifySchedule1);
        btnCancelModifySchedule1 = (Button) findViewById(R.id.btnCancelModifySchedule1);

        RScheduleType2 = (RadioGroup) findViewById(R.id.RScheduleType2);
        rIndividualSchedule2 = (RadioButton) findViewById(R.id.rIndividualSchedule2);
        rGroupSchedule2 = (RadioButton) findViewById(R.id.rGroupSchedule2);


        // 전 화면(일정 페이지)에서 정보 가져오기
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);
        sId = inIntent.getIntExtra("sId",0);
        sName = inIntent.getStringExtra("sName");
        sType = inIntent.getStringExtra("sType");
        sDate = inIntent.getStringExtra("sDate");
        StartTime = inIntent.getStringExtra("StartTime");
        FinishTime = inIntent.getStringExtra("FinishTime");

        // 전에 설정했던 일정 내용을 작성란에 표시해줌.
        edtScheduleName2.setText(sName);
        tvScheduleDate2.setText(sDate);
        tvStartTime2.setText(StartTime);
        tvEndTime2.setText(FinishTime);

        // 그룹 일정이었다면, 이를 수정할 수 있는 사람은 팀장 뿐이므로 일정 타입을 '그룹'도 선택할 수 있게 해준다
        // 그룹 일정이었다면 라디오 버튼이 '그룹'으로 체크되어 있도록 한다.
        if(sType.trim().equals("그룹")){
            rGroupSchedule2.setChecked(true);
            rGroupSchedule2.setEnabled(true);
        }else{
            rIndividualSchedule2.setChecked(true); // 그룹일정이 아니므로 개인 일정으로 라디오 버튼 체크
            rGroupSchedule2.setEnabled(false);
        }




        myHelper = new MyDatabaseHelper(this);

        // 팀장인지 팀원인지 확인을 위한
        Cursor gcursor = myHelper.customerGroups(id);
        gcursor.moveToNext();
        Cursor rcursor = myHelper.customerRoles(id);
        rcursor.moveToNext();

        // 팀장만 그룹 일정 작성 가능.
        for(int i=0;i<4;i++){
            final int index;
            index = i; // index가 0일 때 현재 그룹 G1, 1일 때 현재 그룹 G2, 2일 때 현재 그룹 G3, 3일 때 현재 그룹 G4
            if(gId==gcursor.getInt(index)){
                if(rcursor.getString(index).equals("팀장")){
                    rGroupSchedule2.setEnabled(true);  // 팀장일 경우에만 그룹 스케쥴 라디오 버튼 작동할 수 있음
                    break;
                }
            }
        }


        // 일정 날짜 설정 dialog 함수
        DatePickerDialog datePickerDialog = new DatePickerDialog(modifyScheduleActivity.this, R.style.CustomDatePicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = Integer.toString(year)+"."+Integer.toString(month+1)+"."+Integer.toString(dayOfMonth);
                tvScheduleDate2.setText(date); // 선택한 날짜로 일정 날짜에 표시된다.
            }
        }, mYear, mMonth, mDay);

        // 일정 시작 시간 설정 dialog 함수
        TimePickerDialog timePickerDialog = new TimePickerDialog(modifyScheduleActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                tvStartTime2.setText(startTime); // 설정한 시간으로 일정 시작 시간에 표시된다.
            }
        },mHour, mMinute, false ); // 타임피커에서 오전, 오후를 나눠 볼 수 있음.

        // 일정 종료 시간 설정 dialog 함수
        TimePickerDialog timePickerDialog2 = new TimePickerDialog(modifyScheduleActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                tvEndTime2.setText(endTime); // 설정한 시간으로 일정 종료 시간에 표시된다.
            }
        },mHour, mMinute, false );


        // 날짜 선택
        btnChoiceScheduleDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 날짜 선택 dialog 보여주기
                datePickerDialog.show();
            }
        });



        // 일정 시작 시간 선택
        btnStartTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            } // timepicker dialog 보여주기
        });

        //일정 종료 시간 선택
        btnEndTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            } // timepicker dialog 보여주기
        });

        // 일정 수정 취소
        btnCancelModifySchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // 수정 화면 종료
        });

        // 일정 수정 버튼
        btnModifySchedule1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 항목 미입력시 toast 메시지
                if(edtScheduleName2.getText().toString().trim().equals("") || tvScheduleDate2.getText().toString().trim().equals("")
                        || tvStartTime2.getText().toString().trim().equals("") || tvEndTime2.getText().toString().trim().equals("")){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(modifyScheduleActivity.this);
                    text.setText("모든 항목을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                } else{
                    // 선택한 시작 시간, 종료 시간, 날짜를 변수에 저장
                    startTime = tvStartTime2.getText().toString().trim();
                    endTime = tvEndTime2.getText().toString().trim();
                    date = tvScheduleDate2.getText().toString().trim();

                    String start[] = startTime.split(":"); // ':'로 문자열을 분리하여 선택한 StartTime의 시와 분을 나눠 배열에 저장
                    String end[] = endTime.split(":"); // ':'로 문자열을 분리하여 선택한 EndTime의 시와 분을 나눠 배열에 저장

                    int Start[]=new int[2]; // 선택한 StartTime의 시와 분을 저장한 배열 내용을 int 형으로 바꾸기 위해
                    int End[]=new int[2]; // 선택한 EndTime의 시와 분을 저장한 배열 내용을 int 형으로 바꾸기 위해

                    // 시, 분으로 int 형으로 바꿔줌
                    for(int j =0; j<2; j++){
                        Start[j] = Integer.parseInt(start[j]);
                        End[j] = Integer.parseInt(end[j]);
                    }

                    // 선택한 시작 시간이 종료 시간보다 빠르게 설정되었거나, 같게 설정되었을 때 Toast 메시지.
                    if(Start[0]>End[0] || (Start[0]==End[0] && Start[1]>End[1]) || (Start[0]==End[0] && Start[1]==End[1])){
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(modifyScheduleActivity.this);
                        text.setText("일정 시작 및 종료 시간이 잘못 설정되었습니다. 다시 설정해주세요.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    } else{
                        // 일정이 기존 일정과 겹치는 경우 알려주기 위함
                        // 설정한 일정이 이미 있는 일정과 겹치는지 확인하기 위해 db 사용
                        sqlDB=myHelper.getReadableDatabase();
                        Cursor cursor=myHelper.ScheduleRead(gId);

                        // 일정 DB 테이블의 정보를 저장하기 위한 arraylist
                        sid = new ArrayList<>();
                        gid = new ArrayList<>();
                        memberid = new ArrayList<>();
                        sname = new ArrayList<>();
                        stype = new ArrayList<>();
                        sdate = new ArrayList<>();
                        sStarttime = new ArrayList<>();
                        sEndtime = new ArrayList<>();

                        String sOwner=""; // 일정 주인
                        String sTitle=""; // 일정 제목
                        int plusType = 0; // 일정 저장 타입 (0: 겹치는 일정 없음. 바로 저장, 1: 시작 시간이 이미 있는 일정과 겹침. 저장 선택, 2: 종료 시간이 이미 있는 일정과 겹침. 저장 선택)

                        if(cursor!=null){
                            while(cursor.moveToNext()){ // db 일정 정보를 arraylist에 저장
                                sid.add(cursor.getInt(0));
                                gid.add(cursor.getInt(1));
                                memberid.add(cursor.getString(2));
                                sname.add(cursor.getString(3));
                                stype.add(cursor.getString(4));
                                sdate.add(cursor.getString(5));
                                sStarttime.add(cursor.getString(6));
                                sEndtime.add(cursor.getString(7));
                            }

                            for(int i=0;i<gid.size(); i++){

                                Cursor cursor2=myHelper.customerInfo(memberid.get(i)); // 멤버 이름을 불러오기 위함
                                cursor2.moveToNext();

                                // 일정 주인 저장
                                if(stype.get(i).trim().equals("그룹")){ // 그룹 일정인 경우
                                    sOwner = "그룹"; // 일정 주인은 그룹

                                }else{ // 개인 일정인 경우
                                    sOwner = cursor2.getString(2); // 일정 주인 이름 저장
                                }

                                sTitle = sname.get(i); // 일정 제목


                                // 겹치는 일정이 있는지 확인

                                // sId가 지금 수정하고자 하던 공지의 id라면 일정이 겹치는지 확인할 필요 없음.
                                if(sId == sid.get(i)){
                                    continue; // 다음 코드를 진행하지 않고 그 다음 공지에 대해 반복문 진행됨.
                                }


                                if(date.equals(sdate.get(i))){ // 이미 있는 일정과 같은 날짜인 경우
                                    String dbstart[] = sStarttime.get(i).split(":"); // 시작 시간을 시와 분으로 나눠 배열에 저장
                                    String dbend[] = sEndtime.get(i).split(":"); // 종료 시간을 시와 분으로 나눠 배열에 저장


                                    int dbStart[]=new int[2];
                                    int dbEnd[]=new int[2];

                                    for(int j =0; j<2; j++){ // 앞서 배열에 저장된 시간은 String 형이므로 int형으로 바꾸어 int 배열에 다시 저장
                                        dbStart[j] = Integer.parseInt(dbstart[j]);
                                        dbEnd[j] = Integer.parseInt(dbend[j]);
                                    }

                                    // 설정한 일정 시간이 이미 있는 일정과 같다면 plusType을 바꿔줌.
                                    if(Start[0]>dbStart[0]&& Start[0]<dbEnd[0] || Start[0]==dbStart[0]&& Start[1]>=dbStart[1] || Start[0]==dbEnd[0]&& Start[1]<=dbEnd[1]){
                                        plusType = 1; // 시작 시간이 이미 있는 일정 시간과 같은 경우
                                        break;
                                    } else if(End[0]>dbStart[0]&& End[0]<dbEnd[0] || End[0]==dbStart[0]&& End[1]>=dbStart[1] || End[0]==dbEnd[0]&& End[1]<=dbEnd[1]){
                                        plusType = 2; // 종료 시간이 이미 있는 일정 시간과 같은 경우
                                        break;

                                    } else{ // 일정이 겹치지 않는다면
                                        break;
                                    }

                                }
                            }

                        }

                        switch(plusType){
                            case 0:
                                modifyScheduleComplete(date, startTime, endTime, gId); // 일정 수정 함수 호출
                                break; // 일정 수정 페이지 종료

                            case 1: // 설정한 시작시간이 이미 있는 일정과 겹치는 경우
                                AlertDialog.Builder dlg = new AlertDialog.Builder(modifyScheduleActivity.this, R.style.AlertDialogTheme);
                                dlg.setTitle("주의");
                                dlg.setIcon(R.drawable.warning);
                                dlg.setMessage("시작 시간이 '" + sOwner + ": " + sTitle + "' 일정과 겹칩니다. 일정 등록을 계속 진행하시겠습니까?");
                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        modifyScheduleComplete(date, startTime, endTime, gId); // 일정 수정 함수 호출
                                        finish(); // 일정 수정 페이지 종료
                                    }
                                });
                                dlg.setNegativeButton("취소", null);
                                dlg.show();
                                break;

                            case 2:// 설정한 종료시간이 이미 있는 일정과 겹치는 경우
                                AlertDialog.Builder dlg2 = new AlertDialog.Builder(modifyScheduleActivity.this, R.style.AlertDialogTheme);
                                dlg2.setTitle("주의");
                                dlg2.setIcon(R.drawable.warning);
                                dlg2.setMessage("종료 시간이 '" + sOwner + ": " + sTitle + "' 일정과 겹칩니다. 일정 등록을 계속 진행하시겠습니까?");
                                dlg2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        modifyScheduleComplete(date, startTime, endTime, gId); // 일정 수정 함수 호출
                                        finish(); // 일정 수정 페이지 종료
                                    }
                                });
                                dlg2.setNegativeButton("취소", null);
                                dlg2.show();
                                break;
                        }
                    }

                }







            }
        });



    }

    // 일정 수정 함수
    public void modifyScheduleComplete(String date, String startTime, String endTime, int gId){
        rIndividualSchedule2 = (RadioButton) findViewById(R.id.rIndividualSchedule2);
        rGroupSchedule2 = (RadioButton) findViewById(R.id.rGroupSchedule2);
        edtScheduleName2 = (EditText) findViewById(R.id.edtScheduleName2);
        myHelper = new MyDatabaseHelper(getApplicationContext());

        ScheduleName = edtScheduleName2.getText().toString(); // 작성된 일정 이름을 변수에 저장

        if(rIndividualSchedule2.isChecked()){
            ScheduleType = rIndividualSchedule2.getText().toString().trim(); // 일정 타입이 개인 일정이라면 일정 타입 변수에 '개인' 저장
        } else if(rGroupSchedule2.isChecked()){
            ScheduleType = rGroupSchedule2.getText().toString().trim(); // 일정 타입이 그룹 일정이라면 일정 타입 변수에 '그룹' 저장
        }


        myHelper.updateSchedule(gId, sId, ScheduleName, ScheduleType, date, startTime, endTime); // 일정 수정 (db 내용 수정됨)


        finish(); // 일정 수정 페이지 종료

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toastcustom,
                (ViewGroup) findViewById(R.id.toastLayout));

        TextView text = layout.findViewById(R.id.toastText);

        Toast toast = new Toast(modifyScheduleActivity.this);
        text.setText("일정이 수정되었습니다.");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
