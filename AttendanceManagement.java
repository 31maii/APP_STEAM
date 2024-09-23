package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AttendanceManagement extends Activity {

    CheckBox chkAttend;
    LinearLayout layout_attendCheck;
    Button btnAttendOK;
    TextView tvAttendDate2;

    // card_attendance_check
    TextView tvMemberName;
    RadioGroup rgAttend;
    RadioButton rAttend, rLate, rAbsence;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    String id;
    int gId;

    // 출석ID를 저장할 ArrayList
    ArrayList<Integer> aid;
    int aId, aId2;

    // 출석 생성 날짜 저장할 ArrayList
    ArrayList<String> aDate;

    // 현재 그룹에 참여중인 멤버의 아이디와 이름을 저장할 ArrayList
    ArrayList <String> participantID, participantName;
    String pID, pName;

    // 출석 여부(출석, 지각, 결석)를 저장할 ArrayList
    ArrayList <String> asattend;
    String asAttend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_management);

        // 넘겨받은 데이터 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);

    }

    @Override
    protected void onStart() {
        super.onStart();

        myHelper = new MyDatabaseHelper(this);
        sqlDB=myHelper.getReadableDatabase();

        chkAttend = (CheckBox) findViewById(R.id.chkAttend);
        layout_attendCheck = (LinearLayout) findViewById(R.id.layout_attendCheck);
        btnAttendOK = (Button) findViewById(R.id.btnAttendOK);
        tvAttendDate2 = (TextView) findViewById(R.id.tvAttendDate2);

        Cursor cursor = myHelper.getGroupParticipant(gId); // 현재 그룹원들의 아이디와 이름을 불러오기 위해
        Cursor cursor2 = myHelper.AttendanceRead(gId); // 출석ID를 불러오기 위해
        Cursor cursor3; // 출석 코드 생성 당 해당 멤버의 출석 여부 저장

        // 출석ID를 저장할 ArrayList
        aid = new ArrayList<>();

        // 출석 생성 날짜를 저장할 ArrayList
        aDate = new ArrayList<>();

        layout_attendCheck.removeAllViews();

        if(cursor2!=null){
            while (cursor2.moveToNext()){
                aid.add(cursor2.getInt(0));
                aDate.add(cursor2.getString(4));
            }

            aId = aid.get(aid.size()-1); // 최신 출석ID
            tvAttendDate2.setText("출석 날짜 |   "+aDate.get(aDate.size()-1)); // 최신 출석 생성 날짜 가져오기

            // 현재 그룹에 참여중인 멤버의 아이디와 이름을 저장할 ArrayList
            participantID = new ArrayList<>();
            participantName = new ArrayList<>();


            if(cursor!=null){
                while(cursor.moveToNext()){
                    participantID.add(cursor.getString(0)); // 팀원들의 아이디
                    participantName.add(cursor.getString(2)); // 팀원들 이름
                }

                for(int i=0; i<participantID.size(); i++){
                    pID = participantID.get(i);
                    pName = participantName.get(i);

                    cursor3 = myHelper.AttendanceStatusRead(gId, aId, pID); // 해당 멤버에 대한 최신 출석 여부 저장

                    // 출석 여부(출석, 지각, 결석)를 저장할 ArrayList
                    asattend = new ArrayList<>();

                    if(cursor3!=null){
                        while(cursor3.moveToNext()){
                            asattend.add(cursor3.getString(0));
                        }
                        for(int j=0; j<asattend.size(); j++){
                            asAttend=asattend.get(j);

                            if(pID.trim().equals(id)){
                                // 팀장일 경우 출석 체크 카드 표시 안 함.
                            }else{// 팀원의 출석 체크 카드만 표시.
                                addCard(gId, aId, pID, asAttend, pName);
                            }

                        }
                    }


                }
            }

        }

        btnAttendOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chkAttend.isChecked()){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(AttendanceManagement.this, R.style.AlertDialogTheme);
                    dlg.setTitle("주의");
                    dlg.setMessage("팀원의 출석을 모두 확인하셨습니까?\n확인 버튼을 누르면 더이상 출석을 수정할 수 없습니다.");
                    dlg.setIcon(R.drawable.warning);
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Cursor cursor0 = myHelper.AttendanceRead(gId);

                            // 출석ID를 저장할 ArrayList
                            aid = new ArrayList<>();


                            if(cursor0!=null){
                                while (cursor0.moveToNext()){
                                    aid.add(cursor0.getInt(0));
                                }

                                aId2 = aid.get(aid.size()-1); // 출석 마지막 행의 출석ID

                                myHelper.updateAttendanceStatus(gId, aId2, id, "출석"); // 팀장을 출석처리함.
                                myHelper.updateAttendance(gId, aId2, 1); // 팀원 출석 확인 여부 수정 -> 출석관리버튼 안 보임. (해당 출석 더이상 수정 불가)
                                finish(); // 출석 관리 페이지 종료
                            }

                        }
                    });
                    dlg.setNegativeButton("취소", null);
                    dlg.show();
                }else{
                    finish();
                }
            }
        });

    }

    // 팀원들의 출석 여부 수정할 수 있는 카드뷰가 동적으로 layout에 추가되는 함수
    private void addCard(int gId, int aId, String ID, String attendStatus, String Name) {

        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();

        // dialog의 카드뷰에 대한 것들
        final View view = getLayoutInflater().inflate(R.layout.card_attendance_check, null);
        tvMemberName = view.findViewById(R.id.tvMemberName);
        rgAttend = view.findViewById(R.id.rgAttend);
        rAttend = view.findViewById(R.id.rAttend);
        rLate = view.findViewById(R.id.rLate);
        rAbsence = view.findViewById(R.id.rAbsence);

        tvMemberName.setText(Name);

        // 팀원의 출석 여부 db에 저장된 출석 상태에 따라 라디오 버튼 체크
        if(attendStatus.trim().equals("출석")){
            rAttend.setChecked(true);
        } else if(attendStatus.trim().equals("지각")){
            rLate.setChecked(true);
        } else if(attendStatus.trim().equals("결석")){
            rAbsence.setChecked(true);
        }

        // 라디오버튼 change시 출석 여부 수정
        rgAttend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId==R.id.rAttend){ // 출석 라디오 버튼 클릭시
                    myHelper.updateAttendanceStatus(gId, aId, ID, "출석"); // 팀원이 출석 처리됨. db 수정.

                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(AttendanceManagement.this);
                    text.setText(Name+"님이 출석처리 되었습니다");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                }else if(checkedId ==R.id.rLate){ // 지각 라디오 버튼 클릭시
                    myHelper.updateAttendanceStatus(gId, aId, ID, "지각"); // 팀원이 지각 처리됨. db 수정

                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(AttendanceManagement.this);
                    text.setText(Name+"님이 지각처리 되었습니다");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();


                }else if(checkedId==R.id.rAbsence){ // 결석 라디오 버튼 클릭시
                    myHelper.updateAttendanceStatus(gId, aId, ID, "결석"); // 팀원이 결석 처리됨. db 수정

                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(AttendanceManagement.this);
                    text.setText(Name+"님이 결석처리 되었습니다");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                }
            }
        });


        layout_attendCheck.addView(view); // 팀원의 출석 여부를 수정하는 카드를 layout에 추가


    }
}
