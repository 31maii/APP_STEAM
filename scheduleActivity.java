package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class scheduleActivity extends Activity { // 일정 페이지

    TextView txtScheduleChange, tvWhatDate1;
    ImageButton btnMakeSchedule;
    LinearLayout layoutDateSchedule1;
    CalendarView sCalendarView1;

    int selectYear, selectMonth, selectDay;

    // Intent로 넘겨받는 것 (사용자 id, 그룹 id)
    String id;
    int gId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    TextView startTime;
    TextView finishTime;
    TextView SOwner;
    TextView SName;

    String type;

    // 순서대로 일정 아이디(sID), 그룹번호(gID), 그룹원 ID(custID), 일정 이름(sName), 일정 종류(sType), 일정 날짜(sDate), 시작 시간(scheduleStartTime), 종료 시간(scheduleEndTime)
    ArrayList<String> memberid, sname, stype, sdate, sStarttime, sEndtime;
    ArrayList<Integer> sid, gid;

    ArrayList<String> memberid2, sname2, stype2, sdate2, sStarttime2, sEndtime2;
    ArrayList<Integer> sid2, gid2;

    String memberId, sName, sType, sDate, sStartTime, sEndTime;
    int sId;

    String sOwner; // 일정 주인 표시를 위한

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);

        btnMakeSchedule = (ImageButton) findViewById(R.id.btnMakeSchedule);

        // 넘겨받은 데이터들 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);


        // 일정 추가 버튼. 일정 추가 화면으로 넘어감
        btnMakeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), plusScheduleActivity.class);
                intent.putExtra("ID" , id);
                intent.putExtra("GID", gId);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        layoutDateSchedule1 = (LinearLayout) findViewById(R.id.layoutDateSchedule1);
        sCalendarView1 = (CalendarView) findViewById(R.id.sCalendarView1);
        txtScheduleChange = (TextView) findViewById(R.id.txtScheduleChange);
        tvWhatDate1 = (TextView) findViewById(R.id.tvWhatDate1);

        // 현재 날짜
        selectYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        selectMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        selectDay = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        String strDate = selectYear+"."+selectMonth+"."+selectDay; // 현재 날짜를 년도, 월, 일을 다 합쳐 저장하는 문자열


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date currentDay = dateFormat.parse(strDate, new ParsePosition(0));
        Long currentLong = currentDay.getTime();

        sCalendarView1.setDate(currentLong); // 일정 페이지가 onStart 된다면, 캘린더 뷰에 표시된 날짜가 현재 날짜로 표시되도록 함.

        tvWhatDate1.setText(selectYear+"."+selectMonth+"."+selectDay+" 일정"); // 캘린더 뷰 아래에 표시된 레이아웃의 제목을 '(현재 날짜) 일정'으로 표시

        myHelper = new MyDatabaseHelper(this);

        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor=myHelper.ScheduleRead(gId);

        layoutDateSchedule1.removeAllViews(); // 레이아웃에 같은 정보가 쌓이는 것을 막아줌.


        // 일정 DB 테이블의 정보를 가져오기 위한 arraylist (일정 타입과 상관 없이 해당 그룹의 일정을 모두 저장)
        sid = new ArrayList<>(); // 일정 id
        gid = new ArrayList<>(); // 그룹 id
        memberid = new ArrayList<>(); // 멤버 id
        sname = new ArrayList<>(); // 멤버 이름
        stype = new ArrayList<>(); // 일정 타입
        sdate = new ArrayList<>(); // 일정 날짜
        sStarttime = new ArrayList<>(); // 일정 시작 시간
        sEndtime = new ArrayList<>(); // 일정 종료 시간


        // 그룹 일정만 저장됨
        sid2 = new ArrayList<>();
        gid2 = new ArrayList<>();
        memberid2 = new ArrayList<>();
        sname2 = new ArrayList<>();
        stype2 = new ArrayList<>();
        sdate2 = new ArrayList<>();
        sStarttime2 = new ArrayList<>();
        sEndtime2 = new ArrayList<>();

        if(cursor!=null){


            while(cursor.moveToNext()){ // db의 정보를 arraylist에 저장
                sid.add(cursor.getInt(0));
                gid.add(cursor.getInt(1));
                memberid.add(cursor.getString(2));
                sname.add(cursor.getString(3));
                stype.add(cursor.getString(4));
                sdate.add(cursor.getString(5));
                sStarttime.add(cursor.getString(6));
                sEndtime.add(cursor.getString(7));

                if(cursor.getString(4).equals("그룹")){ // 일정 종류가 그룹일 경우만 arraylist에 저장함
                    sid2.add(cursor.getInt(0));
                    gid2.add(cursor.getInt(1));
                    memberid2.add(cursor.getString(2));
                    sname2.add(cursor.getString(3));
                    stype2.add(cursor.getString(4));
                    sdate2.add(cursor.getString(5));
                    sStarttime2.add(cursor.getString(6));
                    sEndtime2.add(cursor.getString(7));
                }


            }

            for(int i=0;i<gid.size(); i++){ // 그룹의 모든 일정을 하나씩 변수에 저장

                sId = sid.get(i);
                gId = gid.get(i);
                memberId = memberid.get(i);
                sName = sname.get(i);
                sType = stype.get(i);
                sDate = sdate.get(i);
                sStartTime = sStarttime.get(i);
                sEndTime = sEndtime.get(i);


                // 캘린더 뷰에서 선택한 날짜에 대한 일정만 보여줌. 기본은 현재 날짜 스케쥴로..!
                if(sDate.trim().equals(selectYear+"."+selectMonth+"."+selectDay)){
                    addCard(sId, memberId, sName, sType, sDate, sStartTime, sEndTime); // 그룹 일정이 캘린더뷰 아래 layout에 표시됨
                }

            }


            try{
                // 업로드된 최신 그룹 일정의 날짜와 제목을 일정 페이지 상단에 표시
                txtScheduleChange.setText(" ["+sdate2.get(sid2.size()-1)+"]  "+sname2.get(sid2.size()-1));
            }catch(IndexOutOfBoundsException e){
               // 업로드된 최신 그룹 일정이 없을 경우 이벤트 없음. 그냥 비어있음.
            }

        }

        //캘린더뷰 클릭시 이벤트
        sCalendarView1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectYear = year; // 클릭한 날짜의 해당 년도를 selectYear에 저장
                selectMonth = month+1; // 클릭한 날짜의 해당 월을 selectMonth에 저장
                selectDay = dayOfMonth; // 클릭한 날짜의 해당 일을 selectDay에 저장

                tvWhatDate1.setText(selectYear+"."+selectMonth+"."+selectDay+" 일정"); // 캘린더뷰 밑의 레이아웃 제목을 클릭한 날짜로 바꿔준다

                layoutDateSchedule1.removeAllViews(); // 카드뷰가 중복으로 쌓이는 것을 막기 위한


                if(cursor!=null){


                    for(int i=0;i<gid.size(); i++){

                        sId = sid.get(i); // 일정 id
                        gId = gid.get(i); // 그룹 id
                        memberId = memberid.get(i); // 멤버 id
                        sName = sname.get(i); // 일정 이름
                        sType = stype.get(i); // 일정 종류(개인 |그룹)
                        sDate = sdate.get(i); // 일정 날짜
                        sStartTime = sStarttime.get(i); // 일정 시작 시간
                        sEndTime = sEndtime.get(i); // 일정 종료 시간


                        // 캘린더 뷰에서 선택한 날짜에 대한 일정만 보여줌. 기본은 현재 날짜 스케쥴로..!
                        if(sDate.trim().equals(selectYear+"."+selectMonth+"."+selectDay)){
                            addCard(sId, memberId, sName, sType, sDate, sStartTime, sEndTime);
                        }

                    }



                }else{
                    Toast.makeText(getApplicationContext(), "cursor가 비었음", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 일정을 동적 레이아웃으로 보여주기 위한
    private void addCard(int sId, String memberId, String sName, String sType, String sDate, String StartTime, String FinishTime) {

        myHelper = new MyDatabaseHelper(scheduleActivity.this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor2=myHelper.customerInfo(memberId);
        cursor2.moveToNext();

        final View view = getLayoutInflater().inflate(R.layout.card_schedule, null);
        startTime = view.findViewById(R.id.tvSStartTime1);
        finishTime = view.findViewById(R.id.tvSFinishTime1);
        SOwner = view.findViewById(R.id.tvSOwner1);
        SName = view.findViewById(R.id.tvSName1);

        Button btnModify = view.findViewById(R.id.btnSModify);
        Button btnDelete = view.findViewById(R.id.btnSDelete);

        // 수정, 삭제 버튼 안 보이게
        btnModify.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);


        // 팀장인지 팀원인지 확인을 위한
        Cursor gcursor = myHelper.customerGroups(id);
        gcursor.moveToNext();
        Cursor rcursor = myHelper.customerRoles(id);
        rcursor.moveToNext();


        // 일정 종류가 그룹인지 개인인지.
        if(sType.trim().equals("그룹")){
            sOwner = "그룹";

            // 팀장만 그룹 일정 수정, 삭제 가능. (일정 종류가 그룹일 때 수정 삭제 버튼은 팀장만 보이게)
            for(int i=0;i<4;i++){
                final int index;
                index = i; // index가 0일 때 현재 그룹 G1, 1일 때 현재 그룹 G2, 2일 때 현재 그룹 G3, 3일 때 현재 그룹 G4
                if(gId==gcursor.getInt(index)){
                    if(rcursor.getString(index).equals("팀장")){
                        btnModify.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

        }else{
            sOwner = cursor2.getString(2); // 일정 주인 이름 저장
            // 일정 주인만 수정, 삭제 가능하도록 (일정 종류가 개인일 때 수정 삭제 버튼은 일정 주인만 보이게)
            if(id.equals(memberId)){
                btnModify.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
        }

        // 카드 뷰에 일정 시작 시간, 종료 시간, 일정 주인, 일정 이름을 표시해준다.
        startTime.setText("[ " + StartTime);
        finishTime.setText(FinishTime + " ]");
        SOwner.setText(sOwner+":");
        SName.setText(sName);

        TextView txttttt = view.findViewById(R.id.txttttt); // 시간 표시에서 물결에 대한 아이디

        if(sOwner.equals("그룹")){ // 그룹 일정인 경우 카드뷰의 글자 색이 빨간색
            startTime.setTextColor(Color.rgb(220,0,0));
            txttttt.setTextColor(Color.rgb(220,0,0));
            finishTime.setTextColor(Color.rgb(220,0,0));
            SOwner.setTextColor(Color.rgb(220,0,0));
            SName.setTextColor(Color.rgb(220,0,0));
        }


        // 일정 수정 버튼을 누르면 수정 페이지가 나온다.
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), modifyScheduleActivity.class);
                intent.putExtra("sName",sName);
                intent.putExtra("sType", sType);
                intent.putExtra("sDate", sDate);
                intent.putExtra("StartTime", StartTime);
                intent.putExtra("FinishTime", FinishTime);
                intent.putExtra("ID", id);
                intent.putExtra("GID", gId);
                intent.putExtra("sId", sId);
                startActivity(intent);
            }
        });

        // 일정 삭제 버튼 클릭시
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(scheduleActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle("일정을 정말 삭제하시겠습니까?");

                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myHelper.deleteSchedule(gId, sId); // 일정 삭제

                        // 일정 페이지 재시작
                        Intent intent = new Intent(getApplicationContext(), scheduleActivity.class);
                        intent.putExtra("ID", id);
                        intent.putExtra("GID", gid);
                        startActivity(intent);
                        finish();

                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(scheduleActivity.this);
                        text.setText("일정이 삭제되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });

                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() { // 일정 삭제 취소
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(scheduleActivity.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });

        layoutDateSchedule1.addView(view); // 일정 layout에 추가





    }


}