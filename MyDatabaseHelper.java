package com.example.a3_termproject_steam;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// SLQite DB사용을 위해 SQLiteOpenHelper를 상속받는 MyDatabaseHelper class 생성
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="MyGroup7.db";
    private static final int DATABASE_VERSION=1;

    private static final String TABLE_NAME ="my_group2";
    private static final String GROUP_ID="gId";
    private static final String GROUP_NAME="gName";
    private static final String GROUP_TYPE="gType";
    private static final String GROUP_CONTENT="gContent";
    private static final String GROUP_CATEGORY="gCategory";
    private static final String GROUP_CAPACITY="gCapacity";
    private static final String GROUP_NUM="gNum";
    private static final String GROUP_CODE="gCode";
    private static final String TIME="time";

    // 사용자 테이블 생성을 위한 테이블 이름, 속성 이름 변수로 선언

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 그룹 테이블 생성
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GROUP_NAME + " TEXT NOT NULL, " + GROUP_CONTENT + " TEXT NOT NULL, " + GROUP_TYPE + " TEXT NOT NULL, " +
                GROUP_CATEGORY + " TEXT NOT NULL, " + GROUP_CAPACITY + " INTEGER NOT NULL, " + GROUP_NUM + " INTEGER NOT NULL, "
                + GROUP_CODE + " TEXT NOT NULL, " + TIME + " TEXT NOT NULL);";
        db.execSQL(query);
        // 사용자 테이블
        // 사용자에 대한 정보들 id, 비밀번호, 이름, 휴대폰번호, 이메일, 생년월일을 저장할 custID, PASSWORD, NAME, PHONE, EMAIL, BIRTH 속성
        // G1~G4 각 속성에 사용자가 참여되어있는 그룹의 아이디 값을 저장 (한 사용자는 최대 4개의 그룹에 참여 가능)
        // R1~R4 각 속성에는 현재 사용자가 각 그룹에서 어떤 역할을 맡고 있는지(팀장/팀원) 저장
        // 참여되어있는 그룹이 없는 경우는 그룹아이디에 -1, 역할은 x가 저장된다. (그룹을 탈퇴하거나 그룹이 삭제되면 다시 -1, x 값으로 UPDATE된다.)
        db.execSQL("CREATE TABLE IF NOT EXISTS CUSTOMER (custID TEXT PRIMARY KEY, PASSWORD TEXT NOT NULL, NAME TEXT NOT NULL, PHONE TEXT NOT NULL, " +
                "EMAIL TEXT NOT NULL, BIRTH TEXT NOT NULL, G1 INTEGER NOT NULL, R1 TEXT NOT NULL, G2 INTEGER NOT NULL, R2 TEXT NOT NULL, G3 INTEGER NOT NULL" +
                ", R3 TEXT NOT NULL, G4 INTEGER NOT NULL, R4 TEXT NOT NULL);");
        // 그룹 참여신청 내용을 저장할 테이블
        // 각 신청들을 구별할 PK, 어떤 사용자가 어떤 그룹에 신청했는지를 저장하기 위해 gId, custID 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS APPLY (aID INTEGER PRIMARY KEY AUTOINCREMENT, gId INTEGER NOT NULL, custID TEXT NOT NULL);");
        // 그룹 참여신청이 거절된 내역을 저장할 테이블
        // 각 신청 거절들을 구별할 PK, 어떤 그룹에서 어떤 사용자의 신청을 거절했는지 저장하기 위해 gId, custID 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS DENY (dID INTEGER PRIMARY KEY AUTOINCREMENT, gId INTEGER NOT NULL, custID TEXT NOT NULL);");
        // 그룹 참여신청이 수락된 내역을 저장할 테이블
        // 각 신청 수락들을 구별할 PK, 어떤 그룹에서 어떤 사용자의 신청을 수락했는지 저장하기 위해 gId, custID 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS CONSENT (cID INTEGER PRIMARY KEY AUTOINCREMENT, gId INTEGER NOT NULL, custID TEXT NOT NULL);");

        // 공지사항
        // 공지 Id, 그룹 번호, 멤버 id, 공지 제목, 공지 타입, 공지 내용, 공지 시간
        db.execSQL("CREATE TABLE IF NOT EXISTS NOTICE (nID INTEGER PRIMARY KEY AUTOINCREMENT, gId INTEGER NOT NULL, memberID TEXT NOT NULL, nTitle TEXT NOT NULL, "+
                "nType TEXT NOT NULL, nContent TEXT NOT NULL, nTime TEXT NOT NULL, FOREIGN KEY(gId) REFERENCES "+TABLE_NAME+"("+GROUP_ID+"), "+
                "FOREIGN KEY(memberID) REFERENCES CUSTOMER(custID));");

        // 출석 테이블
        // 순서대로 출석ID, 출석 코드, 그룹 번호, 그룹원ID, 날짜, 출석확인여부
        // 출석확인여부 -> boolean type -> integer (팀장이 팀원들의 출석을 확인 했는지 아닌지 체크)
        db.execSQL("CREATE TABLE IF NOT EXISTS ATTENDANCE (aID INTEGER PRIMARY KEY AUTOINCREMENT, aCODE INTEGER NOT NULL, gID INTEGER NOT NULL, custID TEXT NOT NULL, attendDate TEXT NOT NULL, attendCHECK INTEGER NOT NULL, " +
                "FOREIGN KEY(custID) REFERENCES CUSTOMER(custID), FOREIGN KEY(gID) REFERENCES "+TABLE_NAME+"("+GROUP_ID+"))");


        // 출석 여부 테이블(멤버별 출석, 지각, 결석을 기록)
        // 출석여부ID, 그룹번호, 그룹원ID, 출석ID(FK), 출석 여부
        db.execSQL("CREATE TABLE IF NOT EXISTS ATTENDANCESTATUS (asID INTEGER PRIMARY KEY AUTOINCREMENT, gID INTEGER NOT NULL, custID TEXT NOT NULL, aID INTEGER NOT NULL, aSTATUS TEXT NOT NULL,"+
                "FOREIGN KEY(gID) REFERENCES " + TABLE_NAME+"("+GROUP_ID+"), FOREIGN KEY(custID) REFERENCES CUSTOMER(custID), FOREIGN KEY(aID) REFERENCES ATTENDANCE(aID))");

        // SCHEDULE 테이블
        // 순서대로 일정 아이디(sID), 그룹번호(gID), 그룹원 ID(custID), 일정 이름(sName), 일정 종류(sType), 일정 날짜(sDate), 시작 시간(scheduleStartTime), 종료 시간(scheduleEndTime)
        db.execSQL("CREATE TABLE IF NOT EXISTS SCHEDULE (sID INTEGER PRIMARY KEY AUTOINCREMENT, gID INTEGER NOT NULL, custID TEXT NOT NULL, sName TEXT NOT NULL, sType TEXT NOT NULL, sDate TEXT NOT NULL, " +
                "scheduleStartTime TEXT NOT NULL, scheduleEndTime TEXT NOT NULL, FOREIGN KEY(custID) REFERENCES CUSTOMER(custID), FOREIGN KEY(gID) REFERENCES "+TABLE_NAME+"("+GROUP_ID+"))");

        // TASK 테이블
        // 순서대로 TaskID(PK), 그룹별 Task를 구별하기 위한 그룹 아이디(gID)속성, 사용자 id, Task의 제목과 내용, 시작일, 마감일을 저장할 tTitle, tContent, sDate, eDate 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS TASK (tID INTEGER PRIMARY KEY AUTOINCREMENT, gID INTEGER NOT NULL, custID TEXT NOT NULL, tTitle TEXT NOT NULL, tContent TEXT NOT NULL, sDate TEXT NOT NULL, eDate TEXT NOT NULL);");

        // TODO 테이블
        // 각 ToDo를 구별해줄 PK, ToDo를 Task별로 구별하기 위한 속성인 tID(Task의 id), ToDo의 이름, 담당장, 완료여부(1=완료/0=미완료)를 저장할 tdName, tdPerson, ISDONE, gID(그룹별 구분 위한 속성) 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS TODO (tdID INTEGER PRIMARY KEY AUTOINCREMENT, tID INTEGER NOT NULL,  tdName TEXT NOT NULL, tdPerson TEXT NOT NULL, ISDONE INTEGER NOT NULL, gID INTEGER NOT NULL);");

        // COMMENT 테이블
        // 각 Comment를 구별해줄 PK, Comment를 Task별로 구별하기 위한 속성 tID(Task의 id), 작성자(cWriter), 코멘트 내용(cmt), gID(그룹별 구분 위한 속성) 속성으로 구성
        db.execSQL("CREATE TABLE IF NOT EXISTS COMMENT (cmtID INTEGER NOT NULL PRIMARY KEY, tID INTEGER NOT NULL, custID TEXT NOT NULL, cmt TEXT NOT NULL, gID INTEGER NOT NULL);");

        // 문서 테이블
        // 첨부파일도 일단은 text로 해놓음..!
        db.execSQL("CREATE TABLE IF NOT EXISTS DOCUMENT (tID INTEGER NOT NULL, dID INTEGER PRIMARY KEY AUTOINCREMENT, dName TEXT NOT NULL, dWriter TEXT NOT NULL, " +
                "dContent TEXT NOT NULL, File TEXT NOT NULL, Source TEXT NOT NULL, uploadDate TEXT NOT NULL," +
                "FOREIGN KEY(tID) REFERENCES TASK(tID))");

        // 메모 테이블
        // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
        db.execSQL("CREATE TABLE IF NOT EXISTS MEMO (memoID INTEGER PRIMARY KEY AUTOINCREMENT, custID TEXT NOT NULL, memoTITLE TEXT NOT NULL, memoCONTENT TEXT NOT NULL, memoDATE TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS CUSTOMER");
        db.execSQL("DROP TABLE IF EXISTS NOTICE"); //공지사항
        db.execSQL("DROP TABLE IF EXISTS PARTICIPATE"); // 참여
        db.execSQL("DROP TABLE IF EXISTS ATTENDANCNE"); // 출석
        db.execSQL("DROP TABLE IF EXISTS ATTENDANCESTATUS"); // 출석 여부
        db.execSQL("DROP TABLE IF EXISTS SCHEDULE"); // Schedule
        db.execSQL("DROP TABLE IF EXISTS TASK"); // Task
        db.execSQL("DROP TABLE IF EXISTS TODO"); // Todo
        db.execSQL("DROP TABLE IF EXISTS DOCUMENT"); // Document
        db.execSQL("DROP TABLE IF EXISTS COMMENT"); // 문서
        db.execSQL("DROP TABLE IF EXISTS MEMO"); // 메모
    }

    // DB에 추가하는 INSERT문 사용 시 AUTO INCREMENT인 속성은 null값을 입력

    // 그룹 추가 메소드
    public void addGroup(String gName, String gContent, String gType, String gCategory, int gCapacity, int gNum, String gCode, String time){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("INSERT INTO "+ TABLE_NAME + " VALUES ("+null+",'"+gName+"', '"+gContent+"', '"+gType+"', '"+gCategory+"',"+gCapacity+","+gNum+", '"+gCode+"', '"+time+"');");
        db.close();

    }
    // 사용자 추가 메소드
    public void addCustomer(String cID, String pw, String name, String phone, String email, String birth, int g1, String r1, int g2, String r2, int g3, String r3, int g4, String r4){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO CUSTOMER VALUES ('"+cID+"','"+pw+"', '"+name+"','"+phone+"', '"+email+"', '"+birth+"', '"+g1+"', '"+r1+"', '"+g2+"', '"+r2+"', '"+g3+"', '"+r3+"', '"+g4+"', '"+r4+"');");
        db.close();
    }
    // 참여신청 내용을 추가하는 메소드
    public void addApply(int gid, String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO APPLY VALUES ("+null+", '"+gid+"', '"+cid+"');");
    }
    // 참여신청 거절 내역을 추가하는 메소드
    public void addDeny(int gid, String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO DENY VALUES ("+null+", '"+gid+"', '"+cid+"');");
    }
    // 참여신청 수락 내역을 추가하는 메소드
    public void addConsent(int gid, String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO CONSENT VALUES ("+null+", '"+gid+"', '"+cid+"');");
    }

    //공지사항 INSERT
    public void addNotice(Integer gId, String memberID, String nTitle, String nType, String nContent, String nTime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO NOTICE VALUES ("+null+","+gId+", '"+memberID+"', '"+nTitle+"', '"+nType+"', '"+nContent+"', '"+nTime+"')"); // Todo: gId를 가지고 있는 테이블에서는 autoincrement인데 참조되면 여전히 autoincrement로 되어 insert문에 null을 넣어도 되는걸까?
        db.close();
    }

    // 출석 등록(추가)
    // 순서대로 출석ID, 출석 코드, 그룹 번호, 그룹원ID, 날짜, 출석확인여부
    public void addAttendance(int aCODE, int gID, String custID, String attendDate, int attendCHECK){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO ATTENDANCE VALUES ("+null+", " +aCODE+", " +gID+", '"+custID+"', '"+attendDate+"', "+attendCHECK+");");
        db.close();
    }

    // 출석 여부 INSERT
    // 출석여부ID, 그룹번호, 그룹원ID, 출석ID, 출석 여부
    public void addAttendaceStatus(int gID, String custID, int aID, String aSTATUS){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO ATTENDANCESTATUS VALUES (" +null+", " +gID+", '"+custID+"', "+aID+", '"+aSTATUS+"');");
        db.close();
    }

    // Schedule 등록(추가)
    public void addSchedule(int gID, String custID, String sName, String sType, String sDate, String scheduleStartTime, String scheduleEndTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO SCHEDULE VALUES ("+null+", '"+gID+"', '"+custID+"', '"+sName+"', '"+sType+"', '"+sDate+"', '"+scheduleStartTime+"', '"+scheduleEndTime+"');");
        db.close();
    }

    // Task 등록(추가)
    public void addTask(int gID, String custID, String tTitle, String tContent, String sdate, String edate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO TASK VALUES ("+null+", '"+gID+"', '"+custID+"', '"+tTitle+"', '"+tContent+"', '"+sdate+"', '"+edate+"');");
        db.close();
    }
    // TODO 등록(추가)
    public void addTodo(int tID, String tdName, String tdPerson, int isDone, int gid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO TODO VALUES ("+null+", '"+tID+"', '"+tdName+"', '"+tdPerson+"', '"+isDone+"', '"+gid+"');");
        db.close();
    }

    // Document 등록(추가)
    public void addDocument(int tID, int dID, String dName, String dWriter, String dContent, String File, String Source, String uploadDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO TODO VALUES ('"+tID+"', '"+dID+"', '"+dName+"', '"+dWriter+"', '"+dContent+"', '"+File+"', '"+Source+"', '"+uploadDate+"');");
        db.close();
    }

    // Comment 등록(추가)
    public void addComment(int tID, String cid, String cmt, int gid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO COMMENT VALUES ("+null+", '"+tID+"', '"+cid+"', '"+cmt+"', '"+gid+"');");
        db.close();
    }

    // 메모 추가
    // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
    public void addMemo(String custID, String memoTitle, String memoContent, String memoDate){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO MEMO VALUES ("+null+", '"+custID+"', '"+memoTitle+"', '"+memoContent+"', '"+memoDate+"');");
        db.close();
    }

    // 사용자가 그룹에 참여하였을때 사용자 테이블 속성의 그룹아이디(G1~G4)와 역할(R1~R4) 속성을 변경해주는 메소드들
    // 참여되어있지 않은(그룹아이디 = -1, 역할 = x 인) 속성을 찾아서 현재 그룹의 아이디와 역할로 UPDATE해줌으로써 사용자가 참여된 그룹을 추가할 수 있다.
    public void updateCustomer0(String cID, int g1, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G1='"+g1+"', R1='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void updateCustomer1(String cID, int g1, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G2='"+g1+"', R2='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void updateCustomer2(String cID, int g1, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G3='"+g1+"', R3='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void updateCustomer3(String cID, int g1, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G4='"+g1+"', R4='"+r+"' WHERE custID = '"+cID+"'");
    }

    // 공지 수정
    public void updateNotice(int gId, int nID, String nTitle, String nType, String nContent){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE NOTICE SET nTitle='"+nTitle+"', nType='"+nType+"', nContent='"+nContent+"' WHERE gId = '"+gId+"' and nID='"+nID+"'");
    }

    // Schedule 수정
    //일정 이름(sName), 일정 종류(sType), 일정 날짜(sDate), 시작 시간(scheduleStartTime), 종료 시간(scheduleEndTime)
    public void updateSchedule(int gId, int sID, String sName, String sType, String sDate, String sStartTime, String sEndTime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SCHEDULE SET sName='"+sName+"', sType='"+sType+"', sDate='"+sDate+"', scheduleStartTime='"+sStartTime+"', scheduleEndTime='"+sEndTime+"' WHERE gID = '"+gId+"' and sID='"+sID+"'");
    }

    // 출석 수정
    public void updateAttendance(int gID, int aID, int attendCHECK){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ATTENDANCE SET attendCHECK='"+attendCHECK+"' WHERE gID='"+gID+"' and aID='"+aID+"'");
    }

    // 출석 여부 수정. (출석/지각/결석)
    public void updateAttendanceStatus(int gID, int aID, String custID, String aSTATUS){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE ATTENDANCESTATUS SET aSTATUS='"+aSTATUS+"' WHERE gID='"+gID+"' and aID='"+aID+"' and custID='"+custID+"'");
    }

    // 메모 수정
    // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
    public void updateMemo(int memoID, String custID, String memoTITLE, String memoCONTENT){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE MEMO SET memoTITLE='"+memoTITLE+"', memoCONTENT='"+memoCONTENT+"' WHERE memoID='"+memoID+"' and custID='"+custID+"'");
    }

    // 그룹이 삭제되었을 때 또는 사용자가 그룹에서 탈퇴했을 때 해당 사용자의 그룹정보와 역할을 초기값으로 다시 변경시켜주는 메소드
    // 파라미터의 g에 -1, r에 'x'를 입력하여 사용자가 참여되어있던 그룹을 없애준다.
    public void deleteCustomerGroup0(String cID, int g, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G1='"+g+"', R1='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void deleteCustomerGroup1(String cID, int g, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G2='"+g+"', R2='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void deleteCustomerGroup2(String cID, int g, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G3='"+g+"', R3='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void deleteCustomerGroup3(String cID, int g, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET G4='"+g+"', R4='"+r+"' WHERE custID = '"+cID+"'");
    }
    // 그룹 삭제
    // 입력 받은 gid값과 그룹 테이블의 gId값이 일치하는 그룹을 삭제
    public void deleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM my_group2 WHERE gId = '"+gid+"'");
    }
    // 참여신청을 삭제
    // 그룹(gid)에 사용자(cid)가 참여신청한 내역을 삭제
    public void deleteApply(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM APPLY WHERE gId = '"+gid+"' AND custID ='"+cid+"'");
    }
    // 해당 그룹에 대한 모든 참여신청을 삭제
    // 그룹원 정원을 초과한 경우 그룹에 대한 모든 참여신청을 지워버리기 위한 메소드
    public void deleteGroupApply(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM APPLY WHERE gId = '"+gid+"'");
    }
    // 참여신청 수락된 내역을 삭제
    // 그룹(gid)에서 사용자(cid)의 참여신청을 수락한 내역을 삭제
    public void deleteConsent(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CONSENT WHERE gId = '"+gid+"' AND custID = '"+cid+"'");
    }
    // 참여신청이 거절된 내역을 삭제
    // 그룹(gid)에서 사용자(cid)의 참여신청을 거절한 내역을 삭제
    public void deleteDeny(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DENY WHERE gId = '"+gid+"' AND custID = '"+cid+"'");
    }

    // 메모 삭제
    // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
    public void deleteMemo(int memoID, String custID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM MEMO WHERE memoID = '"+memoID+"' and custID = '"+custID+"'");
    }

    // 해당 그룹의 현재 인원수를 얻기 위한 메소드
    public Cursor getGNum(int gid){
        String query = "SELECT gNum FROM my_group2 WHERE gId = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 해당 그룹의 현재 인원수를 변경시켜주는 메소드 (팀원이 추가되었을 때 또는 감소하였을 때 사용)
    public void modifyGNum(int gid, int gnum){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE my_group2 SET gNum='"+gnum+"' WHERE gId = '"+gid+"'");
    }

    // 공지 삭제
    public void deleteNotice(int gId, int nid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM NOTICE WHERE gId = '"+gId+"' and nID = '"+nid+"'");
    }

    // Schedule 삭제
    public void deleteSchedule(int gId, int sId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SCHEDULE WHERE gID = '"+gId+"' and sID = '"+sId+"'");
    }

    // currentTime시간에 생성된 그룹의 그룹 id값을 얻기 위한 메소드
    // 그룹을 생성한 경우 : 그룹을 생성한 사용자의 그룹 속성, 역할을 현재 생성된 그룹의 id와 역할(팀장)로 UPDATE해줘야함
    // 근데 그룹 아이디 속성이 AUTO INCREMENT라서 현재 생성한 그룹의 id가 무엇인지 알 수 없음
    // 이 그룹 id를 알아내기 위해 그룹이 생성된 시간을 초단위로 저장하여 해당 시간에 생성된 그룹의 id를 가져오는 것
    public Cursor currentGID(String currentTime){
        String query = "SELECT " + GROUP_ID + " FROM " + TABLE_NAME + " WHERE " + TIME + " = '"+currentTime+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // 그룹에 대한 모든 정보를 가져오는 메소드
    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }
    // 사용자가 참여되어있는 그룹정보(G1~G4, 그룹 id)를 가져오는 메소드
    public Cursor customerGroups(String custid){
        String query = "SELECT G1, G2, G3, G4 FROM CUSTOMER WHERE custID = '" + custid + "'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 모든 사용자의 사용자 id와 사용자가 참여한 그룹들(G1~G4)의 id들을 가져오는 메소드
    public Cursor AllCustomerGroups(){
        String query = "SELECT custID, G1, G2, G3, G4, NAME FROM CUSTOMER";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 어떤 그룹에 대해 참여신청한 사용자들이 누구인지 사용자 id들을 가져오는 메소드
    public Cursor readAllApplies(int gid){
        String query = "SELECT custID FROM APPLY WHERE gId ='"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 사용자가 참여되어있는 그룹에서의 역할(R1~R4)들을 가져오는 메소드
    public Cursor customerRoles(String custid){
        String query = "SELECT R1, R2, R3, R4 FROM CUSTOMER WHERE custID = '" + custid + "'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 그룹의 타입(팀 프로젝트/스터디)이 무엇인지 가져오는 메소드
    public Cursor grouptypes(int gid){
        String query = "SELECT "+GROUP_TYPE+" FROM "+TABLE_NAME+" WHERE " + GROUP_ID +
                " = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 그룹의 이름을 가져오는 메소드
    public Cursor groupnames(int gid){
        String query = "SELECT "+GROUP_NAME+" FROM "+TABLE_NAME+" WHERE " + GROUP_ID +
                " = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 그룹의 현재 인원수를 가져오는 메소드
    public Cursor groupnum(int gid){
        String query = "SELECT "+GROUP_NUM+" FROM "+TABLE_NAME+" WHERE " + GROUP_ID +
                " = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 그룹의 제한인원을 가져오는 메소드
    public Cursor groupcap(int gid){
        String query = "SELECT "+GROUP_CAPACITY+" FROM "+TABLE_NAME+" WHERE " + GROUP_ID +
                " = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    // 사용자의 이름을 가져오는 메소드
    public Cursor customerNames(String cid){
        String query = "SELECT NAME FROM CUSTOMER WHERE custID = '"+cid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 어떤 사용자에 대해 어떤 그룹들이 참여신청을 수락했는지 그룹의 id를 가져오는 메소드
    public Cursor getConsent(String id){
        String query = "SELECT gId FROM CONSENT WHERE custID = '"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 어떤 사용자에 대해 어떤 그룹들이 참여신청을 거절했는지 그룹의 id를 가져오는 메소드
    public Cursor getDeny(String id){
        String query = "SELECT gId FROM DENY WHERE custID = '"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 사용자의 모든 정보를 가져오는 메소드
    public Cursor customerInfo(String custid){
        String query = "SELECT custID, PASSWORD, NAME, PHONE, EMAIL, BIRTH FROM CUSTOMER WHERE custID = '"+custid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    //공지사항
    public Cursor noticeRead(int gId){
        String query = "SELECT * FROM NOTICE WHERE gId= '"+gId+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }
    //중요 공지사항
    public Cursor InoticeRead(int gId){
        String query = "SELECT * FROM NOTICE WHERE gId='"+gId+"' and nType = '중요'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // 입력받은 gid에 해당하는 그룹의 태스크 정보들을 가져오는 메소드
    public Cursor getTasks(int gid){
        String query = "SELECT tID, tTitle, tContent, sDate, eDate FROM TASK WHERE gID = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 입력받은 tid에 해당하는 Task의 ToDo 정보들을 가져오는 메소드
    public Cursor getToDo(int tid){
        String query = "SELECT tdID, tdName, tdPerson, ISDONE FROM TODO WHERE tID = '"+tid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // ToDo가 완료되었는지(1) 미완료인지(0) UPDATE해주는 메소드
    public void updateTodoDone(int tdid, int isdone){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE TODO SET ISDONE='"+isdone+"' WHERE tdID = '"+tdid+"'");
    }
    // ToDo를 삭제하는 메소드
    public void deleteTodo(int tdid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TODO WHERE tdID = '"+tdid+"'");
    }
    // 입력받은 tid에 해당하는 Task의 Comment정보들을 가져오는 메소드
    public Cursor getComment(int tid){
        String query = "SELECT cmtID, custID, cmt FROM COMMENT WHERE tID = '"+tid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // Comment를 삭제하는 메소드
    public void deleteComment(int cmtid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COMMENT WHERE cmtID = '"+cmtid+"'");
    }
    // 그룹의 카테고리를 가져오는 메소드
    public Cursor getCategory(int gid){
        String query = "SELECT gCategory FROM my_group2 WHERE gId = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // Task의 이름과 마감기한을 가져오는 메소드
    public Cursor getTaskDeadLine(int gid){
        String query = "SELECT tTitle, eDate FROM TASK WHERE gId = '"+gid+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    // Schedule
    public Cursor ScheduleRead(int gId){
        String query = "SELECT * FROM SCHEDULE WHERE gID='"+gId+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // 해당 그룹 번호에 참가중인 고객 정보 가져오기
    public Cursor getGroupParticipant(int gId){
        String query = "SELECT * FROM CUSTOMER WHERE G1='"+gId+"' or G2='"+gId+"' or G3='"+gId+"' or G4='"+gId+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // Attendance
    public Cursor AttendanceRead(int gId){
        String query = "SELECT * FROM ATTENDANCE WHERE gID='"+gId+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // AttendanceStatus
    // 해당 그룹의 출석ID에 대해 해당 멤버 출석 여부를 보여주는
    public Cursor AttendanceStatusRead(int gID, int aID, String custID){
        String query = "SELECT aSTATUS FROM ATTENDANCESTATUS WHERE gID='"+gID+"' and aID='"+aID+"' and custID='"+custID+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // AttendanceStatusRate
    // 멤버의 출석률을 알기 위해
    public Cursor AttendanceStatusRate(int gID, String custID){
        String query = "SELECT * FROM ATTENDANCESTATUS WHERE gID='"+gID+"' and custID='"+custID+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // Attendance2
    // 그룹 아이디와 출석 아이디가 맞을 경우 데이터 불러오기
    public Cursor AttendanceRead2(int gId, int aID){
        String query = "SELECT * FROM ATTENDANCE WHERE gID='"+gId+"' and aID='"+aID+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // 메모 불러오기
    // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
    public Cursor MemoRead(String custID){
        String query = "SELECT * FROM MEMO WHERE custID='"+custID+"'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor=null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }

    // 회원탈퇴할 때 사용되는 메소드 (사용자 정보 삭제)
    public void dropoutCustomer(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CUSTOMER WHERE custID = '"+cid+"'");
    }
    // 사용자의 그룹에서의 역할을 변경해주는 메소드
    public void modifyCustomerRole1(String cID, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET R1='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void modifyCustomerRole2(String cID, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET R2='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void modifyCustomerRole3(String cID, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET R3='"+r+"' WHERE custID = '"+cID+"'");
    }
    public void modifyCustomerRole4(String cID, String r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET R4='"+r+"' WHERE custID = '"+cID+"'");
    }
    // 회원 탈퇴하는 경우 해당 사용자가 활동한 내용들
    // 참여신청, 수락, 거절, 공지, 출석, Task, ToDo, Comment 등의 데이터를 각각 삭제하는 메소드
    public void deleteApplyForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM APPLY WHERE custID ='"+cid+"'");
    }
    public void deleteDenyForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DENY WHERE custID ='"+cid+"'");
    }
    public void deleteConsentForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CONSENT WHERE custID ='"+cid+"'");
    }
    public void deleteNoticeForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM NOTICE WHERE memberID ='"+cid+"'");
    }
    public void deleteScheduleForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SCHEDULE WHERE custID ='"+cid+"'");
    }
    public void deleteAttendanceStatusForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ATTENDANCESTATUS WHERE custID ='"+cid+"'");
    }
    public void deleteAttendanceForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ATTENDANCE WHERE custID ='"+cid+"'");
    }
    public void deleteGroupTask(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TASK WHERE gID ='"+gid+"'");
    }
    public void deleteCommentForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COMMENT WHERE custID ='"+cid+"'");
    }
    // 회원 탈퇴시 해당 사용자의 메모장 데이터를 모두 삭제하는 메소드
    public void deleteMemoForDropOut(String cid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM MEMO WHERE custID ='"+cid+"'");
    }
    // 회원정보를 수정하는 메소드
    public void modifyCustomer(String cID, String pw, String name, String phone, String email, String birth){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CUSTOMER SET PASSWORD='"+pw+"', NAME='"+name+"', PHONE='"+phone+"', EMAIL='"+email+"', BIRTH='"+birth+"' WHERE custID = '"+cID+"'");
    }
    // 회원 정보를 가져오는 메소드
    public Cursor getGroupInfo(int gid){
        String query = "SELECT gName, gType, gNum, gCapacity, gCode FROM my_group2 WHERE gID = '"+gid+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 입력받은 파라미터 값에 해당하는 비밀번호를 가져오는 메소드
    public Cursor getPW(String cid, String phone, String birth){
        String query = "SELECT PASSWORD FROM CUSTOMER WHERE custID='"+cid+"' and PHONE='"+phone+"' and BIRTH='"+birth+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
    // 회원 탈퇴시 사용자가 탈퇴하는 그룹 내에서 한 활동(공지, 출석, 일정 등)들을 각각 삭제하는 메소드
    public void deleteApplyForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM APPLY WHERE custID ='"+cid+"' and gID ='"+gid+"'");
    }
    public void deleteDenyForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DENY WHERE custID ='"+cid+"' and gId ='"+gid+"'");
    }
    public void deleteConsentForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CONSENT WHERE custID ='"+cid+"' and gId ='"+gid+"'");
    }
    public void deleteNoticeForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM NOTICE WHERE memberID ='"+cid+"' and gId = '"+gid+"'");
    }
    public void deleteScheduleForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SCHEDULE WHERE custID ='"+cid+"' and gId ='"+gid+"'");
    }
    public void deleteCommentForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COMMENT WHERE custID ='"+cid+"' and gID ='"+gid+"'");
    }
    public void deleteAttendanceStatusForLeave(String cid, int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ATTENDANCESTATUS WHERE custID ='"+cid+"' and gID ='"+gid+"'");
    }
    // 팀장이 그룹을 삭제하는 경우 삭제되는 그룹 내에서 남아있는 활동 내용들
    // 공지, 출석, 일정, 업무 등의 데이터들을 각각 삭제하는 메소드들
    public void deleteApplyForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM APPLY WHERE custID = gID ='"+gid+"'");
    }
    public void deleteDenyForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM DENY WHERE gId ='"+gid+"'");
    }
    public void deleteConsentForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CONSENT WHERE gId ='"+gid+"'");
    }
    public void deleteNoticeForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM NOTICE WHERE gId = '"+gid+"'");
    }
    public void deleteScheduleForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SCHEDULE WHERE gId ='"+gid+"'");
    }
    public void deleteCommentForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COMMENT WHERE gID ='"+gid+"'");
    }
    public void deleteAttendanceStatusForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ATTENDANCESTATUS WHERE gID ='"+gid+"'");
    }
    public void deleteAttendanceForDeleteGroup(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ATTENDANCE WHERE gID ='"+gid+"'");
    }
    public void deleteGroupToDo(int gid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TODO WHERE gID ='"+gid+"'");
    }




}
