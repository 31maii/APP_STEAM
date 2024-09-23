package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AttendanceMain extends Activity {
    TextView tvMyName, tvAttendanceRate1, tvLateRate1, tvAbsenceRate1, tvMessage1, tvAttendDate1, tvMyAttendance1, tvACode1;
    Button btnAManagement, btnMakeCode;
    LinearLayout layoutAttend1,layout_Acode;
    EditText edtACode;

    // card_attendance_status
    TextView memberName1, tvMemberAttendRate1, tvMemberLateRate1, tvMemberAbsenceRate1;


    String id;
    int gId;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;


    String memberType = "팀원"; // 팀장인지 팀원인지
    int btnCodeType = 1; // 출석코드 버튼 타입, 팀원일 경우 0, 팀장일 경우 1

    String stringCode; // 출석 코드 String 형으로 저장할 변수
    int intCode; // 출석 코드 int 형으로 저장할 변수



   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_main);

        tvMyName = (TextView) findViewById(R.id.tvMyName);

       // 넘겨받은 데이터 변수에 저장
        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        gId = inIntent.getIntExtra("GID", 0);


        // 사용자 이름 가져오기
        myHelper = new MyDatabaseHelper(this);
        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor2=myHelper.customerInfo(id);
        cursor2.moveToNext();
        tvMyName.setText(cursor2.getString(2)); // 나의 출석 현황의 이름 들어가는 곳에 사용자 이름 업로드








    }

    @Override
    protected void onStart() {
        super.onStart();

        // 나의 출석 현황
        tvAttendanceRate1 = (TextView) findViewById(R.id.tvAttendanceRate1);
        tvLateRate1 = (TextView) findViewById(R.id.tvLateRate1);
        tvAbsenceRate1 = (TextView) findViewById(R.id.tvAbsenceRate1);
        tvMessage1 = (TextView) findViewById(R.id.tvMessage1);

        // 팀 출석 현황
        btnAManagement = (Button) findViewById(R.id.btnAManagement);
        layoutAttend1 = (LinearLayout) findViewById(R.id.layoutAttend1);

        // 기록된 마지막 출석 코드
        tvAttendDate1 = (TextView) findViewById(R.id.tvAttendDate1); // 마지막으로 출석 코드 입력한 날짜 표시
        tvMyAttendance1 = (TextView) findViewById(R.id.tvMyAttendance1); // 나의 출석 현황
        tvACode1 = (TextView) findViewById(R.id.tvACode1); // 마지막으로 입력했던 출석코드
        layout_Acode = (LinearLayout) findViewById(R.id.layout_Acode); // 팀장만 출석 코드 볼 수 있게 하기 위해

        // 출석 코드 입력
        edtACode = (EditText) findViewById(R.id.edtACode);
        btnMakeCode = (Button) findViewById(R.id.btnMakeCode);

        myHelper = new MyDatabaseHelper(this);
        sqlDB=myHelper.getReadableDatabase();


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
                    memberType = "팀장";
                    break;
                }
            }
        }


        // 출석 코드 버튼이 팀장일 경우에는 "생성"이라고 표시되고, 팀원이면 "등록"이라 표시됨
        if(memberType.trim().equals("팀장")){
            // 팀장일 경우
            btnMakeCode.setText("생성");
            btnCodeType = 1; // 출석 코드 생성 버튼(팀장)
            layout_Acode.setVisibility(View.VISIBLE); // 생성했던 출석 코드 보이게

            // 출석 관리를 할 수 있을 경우만 출석 관리 버튼 표시
            Cursor cursor2 = myHelper.AttendanceRead(gId);

            ArrayList <Integer> acheck = new ArrayList<>(); // 출석 확인 여부를 저장하는 ArrayList

            int aCheck=1; // 출석 확인여부. 1: 확인, 0: 미확인. 출석코드 생성될 때 확인 여부는 0의 기본 값을 가짐

            if(cursor2!=null){
                while (cursor2.moveToNext()){
                    acheck.add(cursor2.getInt(5)); // 출석 확인 여부 가져오기
                }


                try{
                    aCheck = acheck.get(acheck.size()-1); // 출석 마지막 행의 출석 확인 여부

                }catch (ArrayIndexOutOfBoundsException e){
                    // 아직 출석 코드를 등록한 적이 없을 경우
                }

                if(aCheck==1){ // 출석 확인이 완료되었다면 관리 버튼 안 보이게
                    btnAManagement.setVisibility(View.INVISIBLE); // 출석 관리 버튼 안보이게
                }else{ // 출석 확인이 아직 완료되지 않았다면 관리 버튼 보이게
                    btnAManagement.setVisibility(View.VISIBLE); // 출석 관리 버튼 보이게
                }
            }



        }else{ // 팀원일 경우
            btnMakeCode.setText("등록");
            btnCodeType = 0; // 출석 코드 등록 버튼(팀원)
            btnAManagement.setVisibility(View.INVISIBLE); // 출석 관리 버튼 안 보임
            layout_Acode.setVisibility(View.GONE); // 출석 코드 안 보이게
        }

        // 출석 코드 입력창에는 6자리 입력 가능. 6자리가 아니라면 출석코드 버튼을 이용할 수 없음
        edtACode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==KeyEvent.ACTION_UP){

                    if(edtACode.getText().toString().trim().length()!=6){ // 출석 코드 입력이 6자리가 아니라면 출석 코드 버튼 이용 불가. 버튼 색 회색.
                        btnMakeCode.setEnabled(false);
                        ViewCompat.setBackgroundTintList(btnMakeCode, ColorStateList.valueOf(Color.parseColor("#666666")));
                    }else{ // 출석 코드 입력이 6자리가 아니라면 출석 코드 버튼 이용 가능. 버튼 색 원래 색으로.
                        btnMakeCode.setEnabled(true);
                        ViewCompat.setBackgroundTintList(btnMakeCode, ColorStateList.valueOf(Color.parseColor("#24019D")));
                    }
                }

                return false;
            }
        });






        // 출석 관리 열기 버튼
        btnAManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AttendanceMain.this, AttendanceManagement.class);
                intent.putExtra("ID", id);
                intent.putExtra("GID", gId);
                startActivity(intent);

            }
        });

        // 출석 코드 버튼 눌렀을 때, 팀장일 경우 출석 db 생성, 팀원일 경우 출석 여부 db "출석"으로 업데이트
        btnMakeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stringCode = edtACode.getText().toString(); // 입력한 코드를 변수에 저장

                if(stringCode.trim().equals("")){ // 코드 미입력시 toast 메시지. 코드 생성 불가능
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(AttendanceMain.this);
                    text.setText("그룹코드를 입력하세요.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{

                    if(btnCodeType == 1){ // 팀장일 때
                        intCode = Integer.parseInt(stringCode); // 입력한 코드를 int형으로 바꿔 저장.

                        String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm").format(new Date()); // 현재 시간

                        myHelper.addAttendance(intCode, gId, id, currentTime, 0);  // attendCHECK: 팀장이 팀원의 출석을 모두 확인했는지 저장하는 변수. 0: 확인 안 함(기본값), 1: 확인함

                        Cursor cursor = myHelper.getGroupParticipant(gId);
                        Cursor cursor2 = myHelper.AttendanceRead(gId);

                        // 현재 그룹에 참여중인 멤버의 아이디와 이름을 저장할 ArrayList
                        ArrayList <String> participantID = new ArrayList<>();
                        ArrayList <String> participantName = new ArrayList<>();

                        // 출석ID를 저장할 ArrayList
                        ArrayList <Integer> aid = new ArrayList<>();


                        if(cursor2!=null){
                            while (cursor2.moveToNext()){
                                aid.add(cursor2.getInt(0));
                            }

                            // 출석 페이지를 열었을 때 최신의 출석 코드를 보여주기 위해
                            int aId = aid.get(aid.size()-1); // 현재 등록하는 출석의 출석ID 저장 (최신 출석 코드)

                            // 출석 여부 db 테이블에 모든 팀원에 대한 출석 여부 저장
                            if(cursor!=null){
                                while(cursor.moveToNext()){
                                    participantID.add(cursor.getString(0));
                                    participantName.add(cursor.getString(2));
                                }

                                for(int i=0; i<participantID.size(); i++){
                                    String pID = participantID.get(i);

                                    myHelper.addAttendaceStatus(gId, pID, aId,"결석"); // 출석 여부 db 테이블에 그룹의 모든 멤버들의 출석 여부를 저장함. 이때 기본 값은 '결석'
                                }
                            }

                        }

                        tvAttendDate1.setText(currentTime); // 최신 출석 코드 등록 시간 업로드
                        tvACode1.setText(Integer.toString(intCode)); // 입력한 출석 코드를 코드 생성 바로 위에 있는 '출석 코드:' 에 업로드 해줌. 생성한 코드를 볼 수 있음.



                    }else{ // 팀원일 때
                        intCode = Integer.parseInt(stringCode); // 입력 받은 코드를 int 형으로 저장

                        Cursor cursor2 = myHelper.AttendanceRead(gId);

                        // 출석ID, 출석확인여부, 출석 코드를 저장할 ArrayList
                        ArrayList <Integer> aid = new ArrayList<>();
                        ArrayList <Integer> acheck = new ArrayList<>();
                        ArrayList <Integer> code = new ArrayList<>();

                        if(cursor2!=null){
                            while (cursor2.moveToNext()){
                                aid.add(cursor2.getInt(0));
                                acheck.add(cursor2.getInt(5));
                                code.add(cursor2.getInt(1));
                            }

                            int aId = aid.get(aid.size()-1); // 출석 마지막 행의 출석ID
                            int aCheck = acheck.get(aid.size()-1); // 출석 마지막 행의 출석 확인 여부
                            int Code = code.get(aid.size()-1); // 출석 마지막 행의 출석 코드

                            try{
                                if(aCheck==0){ // 아직 팀장이 출석 확인을 완료하지 않았을 경우

                                    // 팀원이 입력한 출석 코드가 팀장이 생성한 출석코드와 일치할 때, 출석 여부 수정
                                    if(intCode == Code){
                                        myHelper.updateAttendanceStatus(gId, aId, id, "출석"); // 출석 여부 수정

                                        LayoutInflater inflater = getLayoutInflater();

                                        View layout = inflater.inflate(R.layout.toastcustom,
                                                (ViewGroup) findViewById(R.id.toastLayout));

                                        TextView text = layout.findViewById(R.id.toastText);

                                        Toast toast = new Toast(AttendanceMain.this);
                                        text.setText("출석 완료");
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();

                                        tvMyAttendance1.setText("출석"); // 나의 출석 현황 수정
                                    }else{ // 출석 코드 불일치 시
                                        LayoutInflater inflater = getLayoutInflater();

                                        View layout = inflater.inflate(R.layout.toastcustom,
                                                (ViewGroup) findViewById(R.id.toastLayout));

                                        TextView text = layout.findViewById(R.id.toastText);

                                        Toast toast = new Toast(AttendanceMain.this);
                                        text.setText("출석코드가 일치하지 않습니다.");
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();
                                    }

                                }else{ // 팀장이 출석 확인을 완료했을 경우 -> 출석 여부 수정 불가
                                    LayoutInflater inflater = getLayoutInflater();

                                    View layout = inflater.inflate(R.layout.toastcustom,
                                            (ViewGroup) findViewById(R.id.toastLayout));

                                    TextView text = layout.findViewById(R.id.toastText);

                                    Toast toast = new Toast(AttendanceMain.this);
                                    text.setText("지금은 출석 여부를 수정할 수 없습니다.");
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }catch(NullPointerException e){
                                // 아직 출석 코드를 생성한 적이 없는 경우
                            }

                        }



                    }

                }

                // 새로고침 기능. 출석 코드 입력 후 버튼 클릭하면 화면이 새로 고침됨.
                Intent intent = new Intent(AttendanceMain.this, AttendanceMain.class);
                intent.putExtra("ID", id);
                intent.putExtra("GID", gId);
                startActivity(intent);
                finish();

            }
        });


        // 자신의 출석률 업데이트하기

        int myAttendNum=0; // 자신의 출석수
        int myLateNum=0; // 자신의 지각수
        int myAbsenceNum=0; //자신의 결석수

        float myAttendRate; // 자신의 출석률
        float myLateRate; // 자신의 지각률
        float myAbsenceRate; // 자신의 결석률

        String MyAttendRate, MyLateRate, MyAbsenceRate;

        ArrayList <String> attendList = new ArrayList<>();
        String attendStatus;

        Cursor cursorMyRate = myHelper.AttendanceStatusRate(gId, id); // 열번호 4: 출석 여부
        Cursor cursorAttend = myHelper.AttendanceRead(gId); // 최신 출석 날짜, 최신 출석 코드


        ArrayList <String> attendDate = new ArrayList<>();
        ArrayList <Integer> attendCode = new ArrayList<>();


        // 가장 최근 출석 생성 날짜와 출석 코드 표시
        if(cursorAttend!=null){
            while(cursorAttend.moveToNext()){
                attendDate.add(cursorAttend.getString(4));
                attendCode.add(cursorAttend.getInt(1));
            }

            try{
                tvAttendDate1.setText(attendDate.get(attendDate.size()-1)); // 가장 최근 출석 생성날짜 표시
                tvACode1.setText(Integer.toString(attendCode.get(attendCode.size()-1))); // 가장 최근의 출석 코드 표시
            }catch(ArrayIndexOutOfBoundsException e){ // 생성된 출석 코드가 아얘 없을 경우
                tvAttendDate1.setText("");
                tvACode1.setText("");
            }


        }

        int totalNum=0; // 총 출석 체크 수 (사람마다 그룹 참가 타임이 다를 수 있으므로 총 횟수는 그사람이 이 그룹에 참가했을 때부터로 설정 해야함)

        // gid에 맞는 멤버 불러오기 custid 저장할 곳
        Cursor cursorMember = myHelper.getGroupParticipant(gId);
        ArrayList <String> memberId = new ArrayList<>();
        String MemberId;

        // aID 저장할 곳
        Cursor cursorMatchAId;
        ArrayList <Integer> attendID = new ArrayList<>();
        int AttendID;

        // 해당 aID 수를 구하기 위한
        Cursor cursorAIdNum;
        ArrayList <Integer> aIdCount = new ArrayList<>();

        // AttendStatus에서 gid, custid가 맞는 aid 가져오기 열넘버3
        cursorMatchAId = myHelper.AttendanceStatusRate(gId, id);
        if(cursorMatchAId!=null){
            while(cursorMatchAId.moveToNext()){
                attendID.add(cursorMatchAId.getInt(3));
            }

            for(int j=0; j<attendID.size(); j++){
                AttendID = attendID.get(j);

                // 가져온 aid를 AttendanceRead2에 넣어서 aid 개수 가져오기. 열 넘버0 그 갯수가 totalNum
                cursorAIdNum = myHelper.AttendanceRead2(gId, AttendID);

                if(cursorAIdNum!=null){
                    while(cursorAIdNum.moveToNext()){
                        aIdCount.add(cursorAIdNum.getInt(0));
                    }

                    totalNum = aIdCount.size(); // 출석 체크 총 횟수 (사람마다 그룹 참가 타임이 다를 수 있으므로 총 횟수는 그사람이 이 그룹에 참가했을 때부터로 설정함)
                }

            }
        }




        if(cursorMyRate!=null){
            while(cursorMyRate.moveToNext()){
                attendList.add(cursorMyRate.getString(4)); // 출석 여부 가져오기
            }

            try{
                tvMyAttendance1.setText(attendList.get(attendList.size()-1)); // 가장 최근의 출석 상황
            }catch(ArrayIndexOutOfBoundsException e){
                tvMyAttendance1.setText("");
            }



            for(int i=0; i<attendList.size(); i++){ // 출석, 지각, 결석 횟수
                attendStatus = attendList.get(i);

                if(attendStatus.trim().equals("출석")){
                    myAttendNum+=1;
                }else if(attendStatus.trim().equals("지각")){
                    myLateNum+=1;
                }else if(attendStatus.trim().equals("결석")){
                    myAbsenceNum+=1;
                }

            }



            if(totalNum!=0){
                myAttendRate = (float)myAttendNum/totalNum*100; // 출석률 계산 (출석 수 / 전체 출석체크 수)*100
                myLateRate = (float)myLateNum/totalNum*100; // 지각률 계산 (지각 수 / 전체 출석체크 수)*100
                myAbsenceRate = (float)myAbsenceNum/totalNum*100; // 결석률 계산 (결석 수 / 전체 출석체크 수)*100


                if(myAttendRate>=90.0){ // 출석률 90% 이상
                    tvMessage1.setText(" 팀 활동 적극적 참여 중! 고마운 사람:D");
                }else if(myAttendRate>=70.0){ // 출석률 70% 이상
                    tvMessage1.setText(" 좋아요! 좀 더 적극적인 참여 기대할게요;)");
                }else if(myAttendRate>=50.0){ // 출석률 50% 이상
                    tvMessage1.setText(" 적극적 참여 부탁합니다!");
                }else{ // 출석률 50% 미만
                    tvMessage1.setText(" 참여가 부진해요:(");
                }

                MyAttendRate = String.format("%.1f", myAttendRate); // 소수점 첫째자리까지 저장.
                MyLateRate = String.format("%.1f", myLateRate);
                MyAbsenceRate = String.format("%.1f", myAbsenceRate);

                tvAttendanceRate1.setText(MyAttendRate+" %"); // 자신의 출석률 업로드
                tvLateRate1.setText(MyLateRate+" %");  // 자신의 지각률 업로드
                tvAbsenceRate1.setText(MyAbsenceRate+" %"); // 자신의 결석률 업로드
            }
        }

        // 그룹 멤버 출석 현황 보이는
        layoutAttend1.removeAllViews(); // 같은 정보가 또 쌓이는 것을 막아줌.
        replayCard(gId); // 팀 출석 현황에서 전체 팀원들의 출석률을 보기 위해 동적으로 카드뷰를 추가해준다.




    }


    // 팀원 전체의 출석률 현황을 동적 레이아웃으로 추가해줌
    private void addCardMemberAttend(String Name, String AttendRate, String LateRate, String AbsenceRate){
        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();

        final View view = getLayoutInflater().inflate(R.layout.card_attendance_status, null);
        memberName1 = view.findViewById(R.id.memberName1);
        tvMemberAttendRate1 = view.findViewById(R.id.tvMemberAttendRate1);
        tvMemberLateRate1 = view.findViewById(R.id.tvMemberLateRate1);
        tvMemberAbsenceRate1 = view.findViewById(R.id.tvMemberAbsenceRate1);

        memberName1.setText(Name);
        tvMemberAttendRate1.setText(AttendRate + " %"); // 출석 비율 업로드
        tvMemberLateRate1.setText(LateRate + " %"); // 지각 비율 업로드
        tvMemberAbsenceRate1.setText(AbsenceRate + " %"); // 결석 비율 업로드



        layoutAttend1.addView(view); // 레이아웃에 카드뷰 추가
    }

    // 팀원 출석 현황에 대한 layout 추가 함수를 불러오기 위해 만든 함수
    public void replayCard(int gId){

        Cursor cursor = myHelper.getGroupParticipant(gId);


        // 현재 그룹에 참여중인 멤버의 아이디와 이름을 저장할 ArrayList
        ArrayList <String> participantID = new ArrayList<>();
        ArrayList <String> participantName = new ArrayList<>();


        if(cursor!=null){
            while(cursor.moveToNext()){
                participantID.add(cursor.getString(0)); // 현재 그룹에 참여중인 팀원의 id
                participantName.add(cursor.getString(2)); // 현재 그룹에 참여중인 팀원의 이름
            }

            for(int i=0; i<participantID.size(); i++){
                String pID = participantID.get(i);
                String pName = participantName.get(i);

                int attendNum=0; // 출석수
                int lateNum=0; // 지각수
                int absenceNum=0; //결석수

                float attendRate; // 출석률
                float lateRate; // 지각률
                float absenceRate; // 결석률

                String AttendRate, LateRate, AbsenceRate;

                myHelper = new MyDatabaseHelper(this);
                sqlDB=myHelper.getReadableDatabase();

                int totalNum=0; // 총 출석 체크 수 (사람마다 그룹 참가 타임이 다를 수 있으므로 총 횟수는 그사람이 이 그룹에 참가했을 때부터로 설정 해야함)

                // aID 저장할 곳
                Cursor cursorMatchAId;
                ArrayList <Integer> attendID = new ArrayList<>();
                int AttendID;

                // 해당 aID 수를 구하기 위한
                Cursor cursorAIdNum;
                ArrayList <Integer> aIdCount = new ArrayList<>();

                // AttendStatus에서 gid, custid가 맞는 aid 가져오기 열넘버3
                cursorMatchAId = myHelper.AttendanceStatusRate(gId, pID);
                if(cursorMatchAId!=null){
                    while(cursorMatchAId.moveToNext()){
                        attendID.add(cursorMatchAId.getInt(3));
                    }

                    for(int k=0; k<attendID.size(); k++){
                        AttendID = attendID.get(k);

                        // 가져온 aid를 AttendanceRead2에 넣어서 aid 개수 가져오기. 열 넘버0 그 갯수가 totalNum
                        cursorAIdNum = myHelper.AttendanceRead2(gId, AttendID);

                        if(cursorAIdNum!=null){
                            while(cursorAIdNum.moveToNext()){
                                aIdCount.add(cursorAIdNum.getInt(0));
                            }

                            totalNum = aIdCount.size(); // 총 출석 체크 수 (사람마다 그룹 참가 타임이 다를 수 있으므로 총 횟수는 그사람이 이 그룹에 참가했을 때부터로 설정함)

                        }

                    }
                }



                ArrayList <String> attendListM = new ArrayList<>();
                String attendStatus;

                Cursor cursorRate = myHelper.AttendanceStatusRate(gId, pID);
                if(cursorRate!=null){
                    while(cursorRate.moveToNext()){
                        attendListM.add(cursorRate.getString(4)); // 출석 여부 가져오기
                    }


                    for(int j=0; j<attendListM.size(); j++){
                        attendStatus = attendListM.get(j); // 출석, 지각, 결석 횟수

                        if(attendStatus.trim().equals("출석")){
                            attendNum+=1;
                        }else if(attendStatus.trim().equals("지각")){
                            lateNum+=1;
                        }else if(attendStatus.trim().equals("결석")){
                            absenceNum+=1;
                        }
                    }

                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    // 팀원을 출석, 지각, 결석 수를 알려줌
                    Toast toast = new Toast(AttendanceMain.this);
                    text.setText(pName+" 출석:"+attendNum+" 지각:"+lateNum+" 결석:"+absenceNum);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();



                    if(totalNum!=0){
                        attendRate = (float)attendNum/totalNum*100; // (출석 수 / 출석체크 수) *100
                        lateRate = (float)lateNum/totalNum*100; // (지각 수 / 출석체크 수) *100
                        absenceRate = (float)absenceNum/totalNum*100; // (결석 수 / 출석체크 수) *100

                        AttendRate = String.format("%.1f", attendRate); // 출석률
                        LateRate = String.format("%.1f", lateRate); // 지각률
                        AbsenceRate = String.format("%.1f", absenceRate); // 결석률

                        addCardMemberAttend(pName, AttendRate, LateRate, AbsenceRate); // 출석률 카드 추가
                    }

                }





            }
        }




    }


}
