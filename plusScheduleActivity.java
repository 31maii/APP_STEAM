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
import android.util.TypedValue;
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

public class plusScheduleActivity extends Activity { // 일정 추가 페이지
    String ScheduleName,ScheduleType; // 스케쥴 명, 스케쥴 종류

    EditText edtScheduleName1;
    TextView tvScheduleDate1, tvStartTime1, tvEndTime1;
    ImageButton btnChoiceScheduleDate1;
    Button btnStartTime1;
    ImageButton btnEndTime1;
    Button btnEnrollSchedule1;
    Button btnCancelEnrollSchedule1;
    RadioGroup RScheduleType1;
    RadioButton rIndividualSchedule, rGroupSchedule;


    Calendar cal = Calendar.getInstance();
    int mYear = cal.get(Calendar.YEAR);
    int mMonth = cal.get(Calendar.MONTH);
    int mDay = cal.get(Calendar.DAY_OF_MONTH);
    int mHour = cal.get(Calendar.HOUR_OF_DAY);
    int mMinute = cal.get(Calendar.MINUTE);


    String date, startTime, endTime;

    String id;
    int gId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    ArrayList<String> memberid, sname, stype, sdate, sStarttime, sEndtime;
    ArrayList<Integer> sid, gid;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plus_schedule);

        edtScheduleName1 = (EditText) findViewById(R.id.edtScheduleName1);

        tvScheduleDate1 = (TextView) findViewById(R.id.tvScheduleDate1);
        tvStartTime1 = (TextView) findViewById(R.id.tvStartTime1);
        tvEndTime1 = (TextView) findViewById(R.id.tvEndTime1);


        btnChoiceScheduleDate1 = (ImageButton) findViewById(R.id.btnChoiceScheduleDate1);
        btnStartTime1 = (Button) findViewById(R.id.btnStartTime1);
        btnEndTime1 = (ImageButton) findViewById(R.id.btnEndTime1);
        btnEnrollSchedule1 = (Button) findViewById(R.id.btnEnrollSchedule1);
        btnCancelEnrollSchedule1 = (Button) findViewById(R.id.btnCancelEnrollSchedule1);

        RScheduleType1 = (RadioGroup) findViewById(R.id.RScheduleType1);
        rIndividualSchedule = (RadioButton) findViewById(R.id.rIndividualSchedule);
        rGroupSchedule = (RadioButton) findViewById(R.id.rGroupSchedule);



        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);

        rGroupSchedule.setEnabled(false);

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
                    rGroupSchedule.setEnabled(true);  // 팀장일 경우에만 그룹 스케쥴 라디오 버튼 작동할 수 있음
                    break;
                }
            }
        }


        // 날짜 선택 다이얼로그
        DatePickerDialog datePickerDialog = new DatePickerDialog(plusScheduleActivity.this, R.style.CustomDatePicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = Integer.toString(year)+"."+Integer.toString(month+1)+"."+Integer.toString(dayOfMonth);
                tvScheduleDate1.setText(date); // 날짜를 선택하면 일정 추가 페이지의 일정 날짜에 선택한 날짜가 표시된다.
            }
        }, mYear, mMonth, mDay);

        // 시작 시간 선택을 위한 다이얼로그
        TimePickerDialog timePickerDialog = new TimePickerDialog(plusScheduleActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                tvStartTime1.setText(startTime); // 시간을 선택하면 일정 추가 페이지의 일정 시작 시간에 선택한 시간이 표시된다.
            }
        },mHour, mMinute, false ); // dialog에 표시되는 타임피커는 오전/오후 표시가 되는 타임피커

        // 종료 시간 선택을 위한 다이얼로그
        TimePickerDialog timePickerDialog2 = new TimePickerDialog(plusScheduleActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTime = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                tvEndTime1.setText(endTime); // 시간을 선택하면 일정 추가 페이지의 일정 종료 시간에 선택한 시간이 표시된다.
            }
        },mHour, mMinute, false );


        // 날짜 선택
        btnChoiceScheduleDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 날짜 선택 dialog
                datePickerDialog.show();
            }
        });



        // 일정 시작 시간 선택
        btnStartTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        //일정 종료 시간 선택
        btnEndTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            }
        });

        // 일정 등록 취소
        btnCancelEnrollSchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 일정 등록 버튼
        btnEnrollSchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 항목 미입력시 toast 메시지
                if(edtScheduleName1.getText().toString().trim().equals("") || tvScheduleDate1.getText().toString().trim().equals("")
                        || tvStartTime1.getText().toString().trim().equals("") || tvEndTime1.getText().toString().trim().equals("")){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(plusScheduleActivity.this);
                    text.setText("모든 항목을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else{

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

                        Toast toast = new Toast(plusScheduleActivity.this);
                        text.setText("일정 시작 시간 및 종료 시간이 잘못 설정되었습니다. 다시 설정해주세요.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    } else{
                        // 선택한 날짜와 시간이 겹치는 일정이 있는지 확인해주기 위한 것.
                        // 설정한 일정이 이미 있는 일정과 겹치는지 확인하기 위해 db 사용
                        sqlDB=myHelper.getReadableDatabase();
                        Cursor cursor=myHelper.ScheduleRead(gId);

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


                            while(cursor.moveToNext()){
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

                                Cursor cursor2=myHelper.customerInfo(memberid.get(i));
                                cursor2.moveToNext();


                                // 일정 주인 저장
                                if(stype.get(i).trim().equals("그룹")){
                                    sOwner = "그룹";

                                }else{
                                    sOwner = cursor2.getString(2); // 일정 주인 이름 저장
                                }

                                sTitle = sname.get(i); // 일정 제목


                                if(date.equals(sdate.get(i))){ // 이미 있는 일정과 같은 날짜인 경우
                                    String dbstart[] = sStarttime.get(i).split(":"); // 일정 시작 시간을 분리하여 배열에 저장
                                    String dbend[] = sEndtime.get(i).split(":"); // 일정 종료 시간을 분리하여 배열에 저장


                                    int dbStart[]=new int[2]; // 분리한 시간들을 정수형으로 저장하기 위한 배열
                                    int dbEnd[]=new int[2];

                                    for(int j =0; j<2; j++){ // String 형으로 저장한 시간들을 정수형으로 저장해줌
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

                        }else{
                            Toast.makeText(getApplicationContext(), "cursor가 비었음", Toast.LENGTH_SHORT).show();
                        }

                        switch(plusType){
                            case 0: // 겹치는 일정이 없는 경우 바로 일정 저장
                                plusScheduleComplete(id, date, startTime, endTime, gId);
                                break;

                            case 1: // 설정한 시작시간이 이미 있는 일정과 겹치는 경우 경고 dialog (어떤 사람의 어떤 일정과 겹치는 지 표시함)
                                AlertDialog.Builder dlg = new AlertDialog.Builder(plusScheduleActivity.this, R.style.AlertDialogTheme);
                                dlg.setTitle("주의");
                                dlg.setIcon(R.drawable.warning);
                                dlg.setMessage("시작 시간이 '" + sOwner + ": " + sTitle + "' 일정과 겹칩니다. 일정 등록을 계속 진행하시겠습니까?");
                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() { // 확인 버튼을 누르면 일정 등록 완료
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        plusScheduleComplete(id, date, startTime, endTime, gId);
                                        finish(); // 일정 추가 페이지 종료
                                    }
                                });
                                dlg.setNegativeButton("취소", null); // 취소 버튼 누르면 일정 등록 취소
                                dlg.show();
                                break;

                            case 2:// 설정한 종료시간이 이미 있는 일정과 겹치는 경우
                                AlertDialog.Builder dlg2 = new AlertDialog.Builder(plusScheduleActivity.this, R.style.AlertDialogTheme);
                                dlg2.setTitle("주의");
                                dlg2.setIcon(R.drawable.warning);
                                dlg2.setMessage("종료 시간이 '" + sOwner + ": " + sTitle + "' 일정과 겹칩니다. 일정 등록을 계속 진행하시겠습니까?");
                                dlg2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        plusScheduleComplete(id, date, startTime, endTime, gId); // 일정 추가 함수 호출
                                        finish(); // 일정 추가 페이지 종료
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

    // 일정 추가 함수
    public void plusScheduleComplete(String id, String date, String startTime, String endTime, int gId){
        rIndividualSchedule = (RadioButton) findViewById(R.id.rIndividualSchedule);
        rGroupSchedule = (RadioButton) findViewById(R.id.rGroupSchedule);
        edtScheduleName1 = (EditText) findViewById(R.id.edtScheduleName1);
        myHelper = new MyDatabaseHelper(getApplicationContext());

        ScheduleName = edtScheduleName1.getText().toString();

        if(rIndividualSchedule.isChecked()){ // 일정 타입에 대한 라디오 버튼이 '개인'에 체크되어 있다면
            ScheduleType = rIndividualSchedule.getText().toString().trim(); // 일정 타입을 '개인'으로 저장
        } else if(rGroupSchedule.isChecked()){ //일정 타입의 라디오 버튼이 '그룹'에 체크되어 있다면
            ScheduleType = rGroupSchedule.getText().toString().trim(); // 일정 타입을 '그룹'으로 저장
        }
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toastcustom,
                (ViewGroup) findViewById(R.id.toastLayout));


        myHelper.addSchedule(gId, id, ScheduleName, ScheduleType, date, startTime, endTime); // 일정 추가

        finish(); // 일정 추가 페이지 종료

        LayoutInflater inflater1 = getLayoutInflater();

        View layout1 = inflater1.inflate(R.layout.toastcustom,
                (ViewGroup) findViewById(R.id.toastLayout));

        TextView text1 = layout.findViewById(R.id.toastText);

        Toast toast1 = new Toast(plusScheduleActivity.this);
        text1.setText("일정이 등록되었습니다.");
        toast1.setDuration(Toast.LENGTH_SHORT);
        toast1.setView(layout);
        toast1.show();

    }
}

