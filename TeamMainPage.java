package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;


public class TeamMainPage extends Activity { // 팀 메인 페이지
    // 필요한 위젯변수들 선언
    Button btnNotice, btnAttendance, btnSchedule, btnWorks, btnGroupInfo, btnDeleteGroup, btnshowApplyList, btnLeaveGroup;
    TextView tvGroupName0, txtSchedule, txtDeadline;
    ImageButton home, replay;

    // DB 사용에 필요한 변수들 및 Intent로 넘겨준 값을 저장할 변수 선언
    MyDatabaseHelper myDB;
    int gid;
    String id, role;

    // 현재 날짜 (Schedule, Task들이 유효한지 확인하기 위해 필요)
    Calendar cal = Calendar.getInstance();
    int currentyear = cal.get(Calendar.YEAR);
    int currentmonth = cal.get(Calendar.MONTH)+1;
    int currentday = cal.get(Calendar.DAY_OF_MONTH);

    // 중요 공지
    ArrayList<String> memberid, ntitle, ntype, ncontent, ntime;
    ImageButton btnImpNotice;
    TextView tvRecentInotice;
    String nTitle, memberName, nWriter, nType, nContent, nTime;
    SQLiteDatabase sqlDB;

    // 다가오는 일정
    // 순서대로 일정 아이디(sID), 그룹번호(gID), 그룹원 ID(custID), 일정 이름(sName), 일정 종류(sType), 일정 날짜(sDate), 시작 시간(scheduleStartTime), 종료 시간(scheduleEndTime)
    ArrayList<String> smemberid, sname, stype, sdate, sStarttime, sEndtime;
    ArrayList<Integer> sid;
    String sMemberId, sName, sType, sDate, sStartTime, sEndTime;
    int sId;
    int mYear, mMonth, mDay, mHour, mMinute; // 현재 년도, 월, 일, 시, 분

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_main_page);

        // 선언한 위젯변수들을 id값을 이용하여 연결
        home = (ImageButton) findViewById(R.id.home);
        replay = (ImageButton) findViewById(R.id.replay);

        btnImpNotice = (ImageButton) findViewById(R.id.btnImpNotice);
        btnNotice = (Button) findViewById(R.id.btnNotice);
        btnAttendance = (Button) findViewById(R.id.btnAttendance);
        btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnWorks = (Button) findViewById(R.id.btnWorks);
        btnGroupInfo = (Button) findViewById(R.id.btnGroupInfo);
        btnshowApplyList = (Button) findViewById(R.id.btnshowApplyList);
        btnDeleteGroup = (Button) findViewById(R.id.btnDeleteGroup);
        btnLeaveGroup = (Button) findViewById(R.id.btnLeaveGroup);

        txtSchedule = (TextView) findViewById(R.id.txtSchedule);
        txtDeadline = (TextView) findViewById(R.id.txtDeadline);

        tvGroupName0 = (TextView) findViewById(R.id.tvGroupName0);

        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);
        // Intent로 넘겨준 정보들을 받아와서
        Intent data = getIntent();
        // 사용자 아이디, 그룹 아이디, 그룹 이름을 각각 변수에 저장
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID",0);
        String gname = data.getStringExtra("GNAME");

        // 그룹 이름을 표시하는 TextView에 넘겨받은 그룹 이름을 적용
        tvGroupName0.setText(gname);

        // 그룹 정보들을 가지는 Cursor 선언 (미리 만들어놓은 메소드 이용)
        // 그룹 정보 페이지에 넘겨주기 위한 목적으로 미리 받아 놓음
        Cursor infocursor = myDB.getGroupInfo(gid);
        infocursor.moveToNext();
        // 필요한 정보들(그룹 타입, 그룹 인원, 그룹 제한인원, 그룹 코드)을 변수에 저장 (gname도 필요한데 위에서 이미 받아놓음)
        String gtype = infocursor.getString(1);
        int gnum = infocursor.getInt(2);
        int gcapacity = infocursor.getInt(3);
        String gcode = infocursor.getString(4);
        // 홈 버튼 클릭시
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인화면으로 이동
                Intent intent = new Intent(TeamMainPage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                // 메인 화면에서 필요한 아이디를 같이 넘겨줌
                startActivity(intent);
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);

            }
        });
        // 새로고침 버튼을 클릭하면
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 페이지 재시작
                Intent intent = new Intent(getApplicationContext(), TeamMainPage.class);
                // 재시작하기위해 필요한 정보들도 Intent로 같이 넘겨줌
                intent.putExtra("ID",id);
                intent.putExtra("GID", gid);
                intent.putExtra("GNAME", gname);
                startActivity(intent);
                finish();
            }
        });
        // 만들어놓은 메소드를 이용하여 현재 사용자의 그룹정보들을 가져옴
        Cursor gcursor = myDB.customerGroups(id);
        gcursor.moveToNext();
        // 만들어놓은 메소드를 이용하여 현재 사용자의 그룹에서의 역할 정보들을 가져옴
        Cursor rcursor = myDB.customerRoles(id);
        rcursor.moveToNext();
        // 그룹탈퇴, 그룹삭제, 신청리스트보기 각 3개의 버튼은 그룹 내에서의 역할이
        // 팀장인지 팀원인지에 따라 이용가능 여부가 다르기 때문에
        // 일단 모든 버튼을 사용할 수 없게 visible 속성을 gone으로 적용
        btnLeaveGroup.setVisibility(View.GONE);
        btnDeleteGroup.setVisibility(View.GONE);
        btnshowApplyList.setVisibility(View.GONE);

        // 사용자의 역할을 초기값으로 팀원이라고 지정
        role = "팀원";
        // 현재 그룹에서 사용자의 역할이 무엇인지 확인하는 과정
        // 사용자의 G1~G4 중 현재 그룹의 아이디값과 같은 것을 찾고
        // 해당 그룹의 역할을 DB에서 가져와서 팀장인지 비교
        for(int i=0;i<4;i++){
            final int index;
            index = i;
            if(gid==gcursor.getInt(index)){
                // 팀장이면
                if(rcursor.getString(index).equals("팀장")){
                    // 그룹삭제, 신청리스트보기 버튼을 볼 수 있게 visible 속성을 변경해주고
                    btnDeleteGroup.setVisibility(View.VISIBLE);
                    btnshowApplyList.setVisibility(View.VISIBLE);
                    // role값을 팀장으로 변경해줌
                    role = "팀장";
                    // 반복문 탈출
                    break;
                }
            }
        }
        // 팀원이면
        if(role.equals("팀원")){
            // 그룹탈퇴버튼을 이용할 수 있도록 visible 속성을 변경해줌
            btnLeaveGroup.setVisibility(View.VISIBLE);
        }
        // 그룹탈퇴 버튼을 클릭했을때 작동하는 코드
        btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 대화상자를 하나 만든다
                AlertDialog.Builder dlg = new AlertDialog.Builder(TeamMainPage.this);
                // 그룹을 탈퇴할 것인지 확인하고
                dlg.setTitle("그룹을 탈퇴하시겠습니까?");
                // 탈퇴버튼을 클릭하면
                dlg.setPositiveButton("탈퇴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 아래에서 사용하는 DB에서 가져오는 메소드들은 MyDatabaseHelper class에 만들어놓은 것들
                        // 현재 사용자의 그룹정보들(G1~G4)를 가져오고
                        Cursor gcursor = myDB.customerGroups(id);
                        gcursor.moveToNext();
                        // 현재 사용자의 역할정보들(R1~R4)를 가져옴
                        Cursor gnumcursor = myDB.getGNum(gid);
                        // 현재 그룹의 인원을 가져옴
                        gnumcursor.moveToNext();
                        int gnum = gnumcursor.getInt(0);
                        // G1부터 G4까지 하나씩 확인해보면서
                        for(int j=0; j<4; j++){
                            final int index;
                            index=j;
                            // 현재 그룹 아이디와 G1~G4 중 어떤 값이 일치하면
                            if(gcursor.getInt(index)==gid){
                                // 여기서 index은 일치하는 값이 G1인지 G2인지 G3인지 G4인지 알기 위해서이다.
                                // G1 값이 일치하면
                                if(index==0){
                                    // 사용자의 G1값을 -1로, R1값을 x로 변경
                                    myDB.deleteCustomerGroup0(id, -1, "x");
                                    // 그룹 인원을 1감소시켜줌
                                    myDB.modifyGNum(gid, gnum-1);
                                    // 사용자의 그룹활동을 모두 삭제
                                    myDB.deleteApplyForLeave(id, gid);
                                    myDB.deleteConsentForLeave(id, gid);
                                    myDB.deleteDenyForLeave(id, gid);
                                    myDB.deleteNoticeForLeave(id, gid);
                                    myDB.deleteCommentForLeave(id, gid);
                                    myDB.deleteScheduleForLeave(id, gid);
                                    myDB.deleteAttendanceStatusForLeave(id, gid);
                                    // 반복문 탈출
                                    break;
                                }
                                // G2 값이 일치하면
                                else if(index == 1){
                                    // 사용자의 G2값을 -1로, R2값을 x로 변경
                                    myDB.deleteCustomerGroup1(id, -1, "x");
                                    // 그룹 인원을 1감소시켜줌
                                    myDB.modifyGNum(gid, gnum-1);
                                    // 사용자의 그룹활동을 모두 삭제
                                    myDB.deleteApplyForLeave(id, gid);
                                    myDB.deleteConsentForLeave(id, gid);
                                    myDB.deleteDenyForLeave(id, gid);
                                    myDB.deleteNoticeForLeave(id, gid);
                                    myDB.deleteCommentForLeave(id, gid);
                                    myDB.deleteScheduleForLeave(id, gid);
                                    myDB.deleteAttendanceStatusForLeave(id, gid);
                                    // 반복문 탈출
                                    break;
                                }
                                // G3 값이 일치하면
                                else if(index == 2){
                                    // 사용자의 G3값을 -1로, R3값을 x로 변경
                                    myDB.deleteCustomerGroup2(id, -1, "x");
                                    // 그룹 인원을 1감소시켜줌
                                    myDB.modifyGNum(gid, gnum-1);
                                    // 사용자의 그룹활동을 모두 삭제
                                    myDB.deleteApplyForLeave(id, gid);
                                    myDB.deleteConsentForLeave(id, gid);
                                    myDB.deleteDenyForLeave(id, gid);
                                    myDB.deleteNoticeForLeave(id, gid);
                                    myDB.deleteCommentForLeave(id, gid);
                                    myDB.deleteScheduleForLeave(id, gid);
                                    myDB.deleteAttendanceStatusForLeave(id, gid);
                                    // 반복문 탈출
                                    break;
                                }
                                // G4 값이 일치하면
                                else if(index == 3){
                                    // 사용자의 G4값을 -1로, R4값을 x로 변경
                                    myDB.deleteCustomerGroup3(id, -1, "x");
                                    // 그룹 인원을 1감소시켜줌
                                    myDB.modifyGNum(gid, gnum-1);
                                    // 사용자의 그룹활동을 모두 삭제
                                    myDB.deleteApplyForLeave(id, gid);
                                    myDB.deleteConsentForLeave(id, gid);
                                    myDB.deleteDenyForLeave(id, gid);
                                    myDB.deleteNoticeForLeave(id, gid);
                                    myDB.deleteCommentForLeave(id, gid);
                                    myDB.deleteScheduleForLeave(id, gid);
                                    myDB.deleteAttendanceStatusForLeave(id, gid);
                                    // 반복문 탈출
                                    break;
                                }
                            }
                        }
                        // 모든 탈퇴에 필요한 과정을 마친 후에는
                        // 메인화면으로 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 필요한 아이디값을 같이 넘겨줌
                        intent.putExtra("ID1", id);
                        intent.putExtra("ID2", id);
                        startActivity(intent);

                    }
                });
                // 취소버튼을 누르면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소하였다는 토스트 메세지를 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(TeamMainPage.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                // 대화상자를 보여줌
                dlg.show();

            }
        });

        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 대화상자를 생성
                AlertDialog.Builder dlg = new AlertDialog.Builder(TeamMainPage.this, R.style.AlertDialogTheme);
                // 그룹을 정말 삭제할 것인지 한번 더 확인 후
                dlg.setTitle("그룹을 정말 삭제하시겠습니까?");
                // 삭제버튼을 클릭했을 때 작동하는 코드
                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자들의 아이디, G1~G4값을 가져오는 메소드 (미리 만들어놓은 메소드)를 이용하여
                        // DB에서 해당 정보들을 가져옴
                        Cursor cursor = myDB.AllCustomerGroups();
                        // 모든 사용자들의 그룹 정보 관련 속성들을 검사하는 과정
                        while(cursor.moveToNext()){
                            // 아이디값을 일단 가져와서 변수에 저장
                            String cid = cursor.getString(0);
                            // 모든 한명 한명의 사용자의 그룹 정보 속성(G1~G4) 4개를 모두 검사
                            for(int i=1;i<5;i++){
                                final int index;
                                index=i;
                                // 만약 현재 삭제하는 그룹의 아이디와 일치하는 그룹정보가 있으면
                                if(cursor.getInt(index)==gid){
                                    if(index ==1){
                                        // 사용자의 그룹 정보 속성 중 현재 삭제하는 그룹의 값들을 -1과 x로 변경시켜줌
                                        // 각각 4개의 조건문이 사용된 것은 사용자의 그룹 관련 속성 몇번째 속성을 바꿔줄 것인지
                                        // G1~G4, R1~R4 중 어떤 것을 바꿔줄 것인지 다르기 때문
                                        myDB.deleteCustomerGroup0(cid, -1, "x");
                                        break;
                                    }
                                    else if(index==2){
                                        myDB.deleteCustomerGroup1(cid, -1, "x");
                                        break;
                                    }
                                    else if(index==3){
                                        myDB.deleteCustomerGroup2(cid, -1, "x");
                                        break;
                                    }
                                    else if(index==4){
                                        myDB.deleteCustomerGroup3(cid, -1, "x");
                                        break;
                                    }
                                }
                            }
                        }
                        // 그룹을 삭제하기 위해 그룹에 참여되어있는 사용자들의 속성들을 모두 변경한 후
                        // 그룹과 관련된 활동내용 공지, 출석내역, 일정 등등
                        myDB.deleteApplyForDeleteGroup(gid);
                        myDB.deleteConsentForDeleteGroup(gid);
                        myDB.deleteDenyForDeleteGroup(gid);
                        myDB.deleteNoticeForDeleteGroup(gid);
                        myDB.deleteAttendanceForDeleteGroup(gid);
                        myDB.deleteAttendanceStatusForDeleteGroup(gid);
                        myDB.deleteScheduleForDeleteGroup(gid);
                        myDB.deleteGroupTask(gid);
                        myDB.deleteCommentForDeleteGroup(gid);
                        myDB.deleteGroupToDo(gid);
                        // 그룹을 삭제
                        myDB.deleteGroup(gid);

                        // 그룹삭제까지 마친 후에는 메인 화면으로 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 메인화면에서 필요한 아이디값들을 같이 넘겨줌
                        intent.putExtra("ID1",id);
                        intent.putExtra("ID2",id);
                        startActivity(intent);
                        finish();
                        // 그룹이 삭제되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(TeamMainPage.this);
                        text.setText("그룹이 삭제되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }

                });
                // 취소 버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소하였다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(TeamMainPage.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // 그룹 참여신청리스트를 보여주는 버튼을 클릭하면
        btnshowApplyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹 참여신청리스트를 보여주는 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ApplyListActivity.class);
                // 이동하는 페이지에서 필요한 값들을 같이 넘겨줌
                intent.putExtra("GID", gid);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });
        // 그룹에 참여신청을 한 사람이 있으면 참여신청 버튼의 색상을 변경
        Cursor applycursor = myDB.readAllApplies(gid);
        if(applycursor.getCount()!=0){
            btnshowApplyList.setText("참여 신청 확인 요청");
            ViewCompat.setBackgroundTintList(btnshowApplyList, ColorStateList.valueOf(Color.parseColor("#4caf50")));
        }

        // 중요공지 버튼 클릭했을때 작동하는 코드
        btnImpNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중요공지 리스트를 보여주는 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), InoticeActivity.class);
                // 해당 페이지에서 필요한 정보들을 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", gid);
                startActivity(intent);
            }
        });

        // 공지사항 버튼을 클릭했을때 작동하는 코드
        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 공지사항 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), noticeActivity.class);
                // 공지사항 페이지에서 필요한 정보들을 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", gid);
                startActivity(intent);
            }
        });

        // 출석 버튼을 클릭했을때 작동하는 코드
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 출석 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), AttendanceMain.class);
                // 출석페이지에서 필요한 정보들을 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", gid);
                startActivity(intent);
            }
        });

        // Schedule버튼을 클릭했을때 작동하는 코드
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Schedule 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), scheduleActivity.class);
                // 해당 페이지에서 필요한 정보들 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", gid);
                startActivity(intent);

            }
        });
        // Work버튼을 클릭했을때 작동하는 코드
        btnWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Work페이지로 이동
                Intent intent = new Intent(getApplicationContext(), workActivity.class);
                // 해당 페이지에서 필요한 정보들을 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID",gid);
                intent.putExtra("ROLE", role);
                startActivity(intent);
            }
        });
        // 그룹정보 버튼을 클릭했을때 작동하는 코드
        btnGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 그룹정보 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), groupInfoActivity.class);
                // 그룹정보 페이지에서 필요한 정보들(위에서 미리 DB에서 가져온 값들)을 같이 넘겨줌
                intent.putExtra("ID", id);
                intent.putExtra("GID", gid);
                intent.putExtra("ROLE", role);
                intent.putExtra("GNAME", gname);
                intent.putExtra("GTYPE", gtype);
                intent.putExtra("GNUM", gnum);
                intent.putExtra("GCAPACITY", gcapacity);
                intent.putExtra("GCODE", gcode);
                startActivity(intent);
            }
        });
    }



    protected void onStart() {
        super.onStart();

        // 가장 최신의 중요 공지를 표시해주는 텍스트뷰
        tvRecentInotice = (TextView) findViewById(R.id.tvRecentInotice);
        // 가장 가까운 일정을 표시해주는 텍스트뷰
        txtDeadline = (TextView) findViewById(R.id.txtDeadline);

        // DB 사용을 위해 객체 생성
        myDB = new MyDatabaseHelper(this);
        // 읽기전용 database를 생성
        sqlDB=myDB.getReadableDatabase();
        // 중요공지 정보를 읽어오는 코드. 공지 타입이 '중요'일 경우에  정보를 불러옴
        Cursor cursor=myDB.InoticeRead(gid);

        memberid = new ArrayList<>(); // 멤버 아이디 저장 arraylist
        ntitle = new ArrayList<>(); // 공지 제목 저장  arraylist
        ntype = new ArrayList<>(); // 공지 타입
        ncontent = new ArrayList<>(); // 공지 내용
        ntime = new ArrayList<>(); // 공지 시간

        if(cursor!=null){ // cursor가 중요공지에 관한 것이기 때문에 중요 공지에 대한 정보만이 arraylist에 저장된다.
            while(cursor.moveToNext()){ // 공지사항 DB 테이블에서 값을 하나씩 가져와 해당 arraylist에 저장
                memberid.add(cursor.getString(2));
                ntitle.add(cursor.getString(3));
                ntype.add(cursor.getString(4));
                ncontent.add(cursor.getString(5));
                ntime.add(cursor.getString(6));
            }

            try{ // 최신 중요 공지를 불러오기 위해. arraylist의 가장 마지막 항을 불러옴으로써 최신의 정보를 가져온다.
                nTitle = ntitle.get(ntitle.size()-1);
                memberName = memberid.get(memberid.size()-1); // 멤버 아이디 저장. (이름을 불러오기 위해 사용됨)
                nType = ntype.get(ntype.size()-1);
                nContent = ncontent.get(ncontent.size()-1);
                nTime = ntime.get(ntime.size()-1);

                Cursor cursor2=myDB.customerInfo(memberName); // 멤버 id를 넣어주면 해당 사람의 정보를 가져옴
                cursor2.moveToNext();

                nWriter = cursor2.getString(2); // memberName 변수에 저장된 id를 가진 사람의 이름을 저장함

                tvRecentInotice.setText(nTitle); // 팀 메인 페이지 상단에 최신 중요 공지 제목이 표시된다.

            }catch(ArrayIndexOutOfBoundsException e){
                tvRecentInotice.setText(""); // 공지가 아직 생성되지 않았다면 상단 중요 공지는 비어있다.
            }

        }else{
            Toast.makeText(this, "cursor가 비었음", Toast.LENGTH_SHORT).show();
        }

        // 상단 중요 공지 제목을 클릭하면 중요공지만 몰아볼 수 있는 페이지로 들어간다.
        tvRecentInotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutI = 1; // 중요 공지 페이지에서 클릭한 것임을 알 수 있게 하기 위해. (중요 공지 페이지에서 noticeContentActivity으로 넘어갈 경우, 공지 수정/삭제 불가능)
                Intent intent = new Intent(getApplicationContext(), noticeContentActivity.class);
                intent.putExtra("writer", nWriter);
                intent.putExtra("nTitle", nTitle);
                intent.putExtra("type", nType);
                intent.putExtra("nContent", nContent);
                intent.putExtra("nTime", nTime);
                intent.putExtra("GID", gid);
                intent.putExtra("ID", id);
                intent.putExtra("layoutI", layoutI);
                startActivity(intent);
            }
        });



        // 현재 그룹의 Task이름과 마감일을 모두 불러옴 (미리 만들어놓은 메소드 이용)
        Cursor taskCursor = myDB.getTaskDeadLine(gid);
        // 유효한 Task의 마감일과 제목을 저장할 ArrayList들을 각각 생성
        ArrayList<String> validDates = new ArrayList<>();
        ArrayList<String> validTitles = new ArrayList<>();

        // Task들 중에서 마감 기한이 현재 날짜와 동일하거나, 현재 날짜 이후인
        // (지난 날짜들을 제외한) 유효한 Task의 이름과 마감일을 따로 저장
        if(taskCursor != null){
            while(taskCursor.moveToNext()){
                String title = taskCursor.getString(0);
                String date = taskCursor.getString(1);
                // 날짜를 '연.월.일'로 저장하였는데 이것들을 각각 연/월/일로 구분하기 위해 StringTokenizer이용
                StringTokenizer st = new StringTokenizer(date, ".");
                // 연, 월, 일 각각의 값들을 변수에 저장
                int year = Integer.parseInt(st.nextToken());
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());
                // 현재 년도보다 Task의 마감일 년도가 더 늦으면
                if(year>currentyear){
                    // 유효 마감일, 유효 Task 이름을 각각 ArrayList에 저장
                    validDates.add(date);
                    validTitles.add(title);
                }
                // 현재 년도와 Task의 마감 년도가 같고, 현재 월보다 마감날짜의 월이 더 늦으면
                if(year==currentyear && month > currentmonth){
                    // 유효 마감일, 유효 Task 이름을 각각 ArrayList에 저장
                    validDates.add(date);
                    validTitles.add(title);
                }
                // 현재 년도와 월이 Task 마감일의 년도와 월이 같고, 현재 일자보다 마감날짜의 일이 더 늦으면
                if(year == currentyear && month == currentmonth && day>=currentday){
                    // 유효 마감일, 유효 Task 이름을 각각 ArrayList에 저장
                    validDates.add(date);
                    validTitles.add(title);
                }
            }
        }

        // 유효한 Task들 중에서 마감일이 가장 최신인 날짜를 찾는 과정
        if(validDates.size()!=0){
            // 일단 가장 최신의 날짜를 validDate가 저장되어있는 ArrayList의 첫번째 값으로 지정
            String recentdate = validDates.get(0);

            // 유효한 날짜가 저장된 ArrayList size만큼반복 (모든 마감일을 비교하면서 가장 최신의 날짜를 찾는 과정)
            for(int i=1;i<validDates.size();i++){
                // 지금까지 가장 최신날짜를 연/월/일로 각각 구분하여 변수에 저장(StringTokenizer 이용)
                StringTokenizer rctSt = new StringTokenizer(recentdate, ".");
                int rctyear = Integer.parseInt(rctSt.nextToken());
                int rctmonth = Integer.parseInt(rctSt.nextToken());
                int rctday = Integer.parseInt(rctSt.nextToken());
                // ArrayList의 다음 값을 연/월/일로 각각 구분하여 변수에 저장(StringTokenizer 이용)
                String date = validDates.get(i);
                StringTokenizer st = new StringTokenizer(date, ".");
                int year = Integer.parseInt(st.nextToken());
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                // 지금까지 가장 최신의 날짜와 ArrayList에서 얻은 마감일을 비교하는 과정
                // 연도가 더 가까운 마감일을 최신 날짜로
                if(rctyear>year){
                    recentdate = date;
                }
                // 연도가 같다면 월 기준으로 더 가까운 마감일을 최신 날짜로
                else if(rctmonth>month){
                    recentdate = date;
                }
                // 연도와 월이 같다면 일 기준으로 더 가까운 마감일을 최신 날짜로
                else if(rctday>day){
                    recentdate = date;
                }
            }
            // 최종적으로 나온 recentdate 값이 가장 가까운 마감기한

            // 이제 recentdate 값과 마감일이 일치하는 Task들의 이름과 날짜를 표시해주면 된다.

            // 가장 가까운 마감일을 찾기는 했는데 해당 마감일에 2개 이상의 Task가 있을 수 있기에
            // 여러 Task가 있는 경우 추가하여 표시해주기 위해 일단 빈 문자열을 하나 생성
            String txt = "";

            // Task의 마감기한들을 모두 불러온다 (미리 만들어놓은 메소드 이용)
            Cursor tcursor = myDB.getTaskDeadLine(gid);
            // Cursor가 비어있지 않으면 (Task가 있으면)
            if(tcursor != null){
                while(tcursor.moveToNext()){
                    // Task 제목
                    String ttitle = tcursor.getString(0);
                    // Task 마감일
                    String tdate = tcursor.getString(1);
                    // 위에서 구한 가장 가까운 날짜와 Task의 마감기한이 같으면
                    if(recentdate.equals(tdate)){
                        // 빈 문자열이었던 txt에 해당 Task의 이름과 마감일을 더해준다.
                        txt = txt + ttitle + " : " + tdate + " / ";
                    }
                }
            }
            // 마감일이 같은 날짜들을 이어주는 과정에서 마지막에 슬래시 문자가 하나 남는데
            // 이것을 지워주기 위해 문자열을 StringBuffer로 변경해준뒤 index를 찾는다.
            int idx = txt.lastIndexOf("/");
            StringBuffer sb = new StringBuffer(txt);
            // 해당 index의 문자열을 삭제
            if(idx!=-1){
                sb.delete(idx,idx+1);
            }
            // StringBuffer를 다시 String으로 바꿔주고
            txt = sb.toString();
            // 최종적으로 만들어진 문자열을
            // 텍스트뷰에 setText로 적용
            txtDeadline.setText(txt);
        }



        // 다가오는 일정
        // 다가오는 일정의 종류가 '그룹'이거나 자기 자신의 일정이라면 표시

        txtSchedule = (TextView) findViewById(R.id.txtSchedule);

        Cursor cursorS=myDB.ScheduleRead(gid);

        // 일정 db table 저장할 arraylist
        sid = new ArrayList<>(); // 일정 id
        smemberid = new ArrayList<>(); // 멤버 id
        sname = new ArrayList<>(); // 멤버 이름
        stype = new ArrayList<>(); // 일정 타입(개인 | 그룹)
        sdate = new ArrayList<>(); // 일정 날짜
        sStarttime = new ArrayList<>(); // 일정 시작 시간
        sEndtime = new ArrayList<>(); // 일정 종료 시간

        if(cursorS!=null){

            while(cursorS.moveToNext()){

                // 일정의 종류가 '그룹'이거나 자기 자신의 일정이라면 ArrayList에 추가
                if(cursorS.getString(4).equals("그룹") || cursorS.getString(2).equals(id)){
                    sid.add(cursorS.getInt(0));
                    smemberid.add(cursorS.getString(2));
                    sname.add(cursorS.getString(3));
                    stype.add(cursorS.getString(4));
                    sdate.add(cursorS.getString(5));
                    sStarttime.add(cursorS.getString(6));
                    sEndtime.add(cursorS.getString(7));
                }
            }
            // 현재 날짜와 시간을 가져와서

            mYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())); // 현재 년도
            mMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())); // 현재 월
            mDay = Integer.parseInt(new SimpleDateFormat("dd").format(new Date())); // 현재 일
            mHour = Integer.parseInt(new SimpleDateFormat("hh").format(new Date())); // 현재 시
            mMinute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date())); // 현재 분

            // 일정 db에 저장되어 있던 일정 날짜와 시작/종료 시간을 각각 저장하기 위한 arryalist
            ArrayList<Integer> Year, Month, Day, Hour, Minute;
            Year = new ArrayList<>();
            Month = new ArrayList<>();
            Day = new ArrayList<>();
            Hour = new ArrayList<>();
            Minute = new ArrayList<>();
            ArrayList<String> Title, Owner;
            Title = new ArrayList<>();
            Owner = new ArrayList<>();

            // 다가올 일정에 대한 날짜와 시작 시간을 저장하기 위한 변수 (년, 월, 일, 시, 분)
            int soonYear, soonMonth, soonDay, soonHour, soonMinute;
            String soonDate, soonTime; // 다가올 날짜와 시간을 문자형으로 바꿔 저장
            String soonTitle, soonOwner; // 다가올 일정의 제목과 일정 주인 저장할 변수

            for(int i=0;i<sid.size(); i++){

                // 일정 DB를 불러와 arraylist에 저장된 정보를 하나씩 꺼내서 변수에 저장
                sId = sid.get(i);
                sMemberId = smemberid.get(i);
                sName = sname.get(i);
                sType = stype.get(i);
                sDate = sdate.get(i);
                sStartTime = sStarttime.get(i);
                sEndTime = sEndtime.get(i);

                // DB에 저장된 날짜는 '2022.12.10' 형식으로 저장되어 있으므로, 구분자 '.'을 이용해 날짜를 년도, 월, 일을 따로 따로 저장하다.
                String scheduledate[] = sDate.split("\\."); // scheduledate[0]: String형 년도,  scheduledate[1]: String형 월,  scheduledate[1]: String형 일
                // split가 특수문자 인식을 잘 못하는 경우가 있는데 그 때는 특수문자 앞에 "\\"를 붙이면 된다.
                int scheduleDate[] = new int[3]; // scheduledate[]는 날짜를 문자형으로 저장했으므로 정수형으로 바꿔준다.
                for(int j=0; j<scheduledate.length; j++){
                    scheduleDate[j] = Integer.parseInt(scheduledate[j]);
                }

                // DB에 저장된 시간은 '10:15' 형식으로 저장되어 있으므로, 구분자 ':'를 이용하여 시, 분을 따로 저장하다.
                String scheduletime[] = sStartTime.split(":"); // scheduletime[0]: String형 시, scheduletime[1]: String형 분
                int scheduleTime[] = new int[2]; // 문자로 저장된 시와 분을 정수형으로 바꿔 저장할 변수
                for(int j=0; j<scheduletime.length; j++){
                    scheduleTime[j] = Integer.parseInt(scheduletime[j]);
                }

                // 현재 날짜와 시간보다 이후의 일정의 날짜, 시간을 arraylist에 각각 저장한다.
                if(scheduleDate[0]>mYear || (scheduleDate[0]==mYear && scheduleDate[1] > mMonth) || (scheduleDate[0]==mYear && scheduleDate[1] == mMonth && scheduleDate[2] > mDay)
                        || (scheduleDate[0]==mYear && scheduleDate[1] == mMonth && scheduleDate[2] == mDay && scheduleTime[0]>mHour)
                        || (scheduleDate[0]==mYear && scheduleDate[1] == mMonth && scheduleDate[2] == mDay && scheduleTime[0]==mHour && scheduleTime[1]>=mMinute)){
                    Year.add(scheduleDate[0]);
                    Month.add(scheduleDate[1]);
                    Day.add(scheduleDate[2]);
                    Hour.add(scheduleTime[0]);
                    Minute.add(scheduleTime[1]);
                    Title.add(sName);
                    Owner.add(sType);
                }else{

                }
            }

            try{
                // 곡 다가올 날짜, 시간을 현재 날짜 이후의 일정으로 저장된 arraylist에서 첫번째 일정에 대한 날짜 시간으로 설정한다.
                soonYear = Year.get(0);
                soonMonth = Month.get(0);
                soonDay = Day.get(0);
                soonHour = Hour.get(0);
                soonMinute = Minute.get(0);
                soonTitle = Title.get(0);
                soonOwner = Owner.get(0);

                // arraylist 크기만큼 돌리기. 곧 다가올 일정 변수에 저장된 일정보다 빠른 일정이라면 곧 다가올 일정으로 저장
                for(int k=1; k<Year.size(); k++){
                    if(Year.get(k)>soonYear || (Year.get(k)==soonYear && Month.get(k)>soonMonth) || (Year.get(k)==soonYear && Month.get(k)==soonMonth && Day.get(k)>soonDay)
                            || (Year.get(k)==soonYear && Month.get(k)==soonMonth && Day.get(k)==soonDay && Hour.get(k)>soonHour)
                            || (Year.get(k)==soonYear && Month.get(k)==soonMonth && Day.get(k)==soonDay && Hour.get(k)==soonHour && Minute.get(k)>soonMinute)){

                    }else{ // 곧 다가올 일정으로 저장
                        soonYear = Year.get(k);
                        soonMonth = Month.get(k);
                        soonDay = Day.get(k);
                        soonHour = Hour.get(k);
                        soonMinute = Minute.get(k);
                        soonTitle = Title.get(k);
                        soonOwner = Owner.get(k);
                    }
                }

                soonDate = soonYear+"."+soonMonth+"."+soonDay; // 곧 다가올 일정의 날짜를 '2022.10.12' 형태로 저장
                soonTime = soonHour+":"+soonMinute; // 곧 다가올 일정의 시작 시간을 '10:12' 형태로 저장

                txtSchedule.setText("["+soonDate+"] ("+soonTime+") "+ soonTitle);
                if(soonOwner.equals("그룹")){ // 곧 다가올 일정 주인이 그룹이라면 상단에 표시될 다가올 일정을 빨간색 글씨로 표현한다.
                    txtSchedule.setTextColor(Color.rgb(220,0,0));
                }else{ // 개인 일정이라면 검정색 글씨
                    txtSchedule.setTextColor(Color.BLACK);
                }

            }catch(IndexOutOfBoundsException e){
                txtSchedule.setText(""); // 다가올 일정이 없다면 비어있음
            }

        }else{
            Toast.makeText(this, "cursor가 비었음", Toast.LENGTH_SHORT).show();
        }

    }

}
