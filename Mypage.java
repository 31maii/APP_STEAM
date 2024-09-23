package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.VectorEnabledTintResources;

public class Mypage extends Activity {
    // 사용할 위젯변수들 선언
    ImageButton homeBar,joinBar,memoBar,mypageBar;
    TextView txtCustomerName;
    Button btnModifyMyInfo, btnLogOut, btnDropOut;
    View dialogView;
    // DB이용을 위해 필요한 변수들 선언
    String id;
    MyDatabaseHelper myDB;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        // 각 위젯변수들 연결
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        btnModifyMyInfo = (Button) findViewById(R.id.btnModifyMyInfo);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnDropOut = (Button) findViewById(R.id.btnDropOut);
        // 넘겨받은 데이터들 불러와서 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID1");
        // 생성한 DB Helper 객체 선언
        myDB = new MyDatabaseHelper(this);

        // 사용자 이름 불러와서 setText로 지정
        Cursor namecursor = myDB.customerNames(id);
        namecursor.moveToNext();
        name = namecursor.getString(0);
        txtCustomerName.setText(name+"님");

        // 회원정보 수정 버튼을 클릭했을 때
        btnModifyMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 만들어놓은 대화상자를 띄우고
                dialogView = (View) View.inflate(Mypage.this, R.layout.modify_my_info, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(Mypage.this, R.style.AlertDialogTheme);
                dlg.setView(dialogView);
                // 대화상자의 위젯들을 연결
                EditText edtModifyID = dialogView.findViewById(R.id.edtModifyID);
                EditText edtModifyPW1 = dialogView.findViewById(R.id.edtModifyPW1);
                EditText edtModifyPW2 = dialogView.findViewById(R.id.edtModifyPW2);
                EditText edtModifyName = dialogView.findViewById(R.id.edtModifyName);
                EditText edtModifyPhone = dialogView.findViewById(R.id.edtModifyPhone);
                EditText edtModifyEmail = dialogView.findViewById(R.id.edtModifyEmail);
                EditText edtModifyBirth = dialogView.findViewById(R.id.edtModifyBirth);

                // 현재 회원의 정보들을 검색해서 가져옴
                Cursor icursor = myDB.customerInfo(id);
                icursor.moveToNext();
                // EditText에 현재 회원의 정보를 초기 값으로 보여줌
                String curid = icursor.getString(0);
                String curpw = icursor.getString(1);
                String curname = icursor.getString(2);
                String curphone = icursor.getString(3);
                String curemail = icursor.getString(4);
                String curbirth = icursor.getString(5);
                edtModifyID.setText(curid);
                edtModifyPW1.setText(curpw);
                edtModifyPW2.setText(curpw);
                edtModifyName.setText(curname);
                edtModifyPhone.setText(curphone);
                edtModifyEmail.setText(curemail);
                edtModifyBirth.setText(curbirth);
                // 아이디는 변경이 불가능 하도록
                edtModifyID.setEnabled(false);
                // 수정버튼을 클릭하면
                dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 회원가입시와 동일하게 회원 정보들을 가져온다.
                        String id = edtModifyID.getText().toString().trim();
                        String pw = edtModifyPW1.getText().toString().trim();
                        String pw_check = edtModifyPW2.getText().toString().trim();
                        String name = edtModifyName.getText().toString().trim();
                        String phone = edtModifyPhone.getText().toString().trim();
                        String email = edtModifyEmail.getText().toString().trim();
                        String birth = edtModifyBirth.getText().toString().trim();
                        // 아래의 코드들은
                        // 정보를 올바르게 입력하지 않은 경우들에 대해 토스트 메세지를 출력하는 코드
                        if(id.length()==0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("아이디를 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (pw.length()==0){
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("비밀번호를 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (pw_check.length()==0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("비밀번호를 재입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (name.length()==0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("이름을 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (phone.length()==0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("연락처를 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (email.length() == 0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("이메일을 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (birth.length() == 0) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("생년월일을 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        } else if (!pw_check.equals(pw)) {
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("비밀번호가 일치하지 않습니다.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            return;
                        }

                        // 만들어놓은 UPDATE문을 사용하는 함수를 이용하여 수정
                        myDB.modifyCustomer(id, pw, name, phone, email, birth);
                        // 회원정보가 수정되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Mypage.this);
                        text.setText("회원정보가 수정되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                        Intent intent = new Intent(getApplicationContext(), Mypage.class);
                        intent.putExtra("ID1", id);
                        intent.putExtra("ID2", id);
                        startActivity(intent);
                        finish();
                    }
                });
                // 취소 버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지를 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Mypage.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // 로그아웃 버튼을 클릭하면
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 페이지로 이동하면서 기존에 사용한 모든 액티비티들을 삭제
                Intent intent = new Intent(Mypage.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                startActivity(intent);
            }
        });
        // 회원 탈퇴 버튼을 클릭했을때 작동하는 코드
        btnDropOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 대화상자를 만들어 다시 한번 더 확인
                AlertDialog.Builder dlg = new AlertDialog.Builder(Mypage.this, R.style.AlertDialogTheme);
                dlg.setTitle("정말로 탈퇴하시겠습니까?");
                // 탈퇴버튼을 누르면
                dlg.setPositiveButton("탈퇴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 사용자들의 그룹id 정보들을 가져옴
                        Cursor ccursor = myDB.customerGroups(id);
                        ccursor.moveToNext();
                        // 사용자들의 그룹에 대한 역할 정보들을 가져옴
                        Cursor rcursor = myDB.customerRoles(id);
                        rcursor.moveToNext();
                        // 아래에서 사용자가 팀장인 그룹이 있는 경우와 없는 경우를 나누는 변수로 사용
                        int v=0;
                        // 탈퇴하려는 사용자가 참여되어있는 그룹 중 팀장인 역할을 하는 그룹이 있는지 검사
                        for(int j=0; j<4; j++){
                            final int index;
                            index = j;
                            String customerRole = rcursor.getString(index);
                            int customerGroupId = ccursor.getInt(index);
                            Cursor gnumcursor = myDB.getGNum(customerGroupId);
                            gnumcursor.moveToNext();
                            try{
                                // 그룹의 현재 인원
                                int gnum = gnumcursor.getInt(0);
                                // 역할이 팀장인 경우 중에서도
                                if(customerRole.equals("팀장")){
                                    // 그룹의 인원이 한명이라면(= 자기 자신뿐이라면)
                                    if(gnum==1){
                                        // 그냥 그룹을 삭제하면 되기 때문에 v를 변경하지않음
                                    }
                                    // 그룹의 인원이 2명 이상이라면 (자기자신 외에 팀원이 있다면)
                                    else{
                                        // v=1로 변경
                                        v=1;
                                    }
                                }
                            }
                            catch(CursorIndexOutOfBoundsException e){
                            }
                        }
                        // 검사 후
                        // 사용자가 참여한 모든 그룹에서 역할이 팀원이거나
                        // 또는 팀장인 그룹이 있지만 그룹 인원이 한명인 경우
                        if(v==0){
                            // 다시 사용자들의 그룹에 대한 정보를 가져옴
                            Cursor c1cursor = myDB.customerGroups(id);
                            c1cursor.moveToNext();
                            // 사용자들의 그룹에 대한 역할 정보들을 가져옴
                            Cursor r1cursor = myDB.customerRoles(id);
                            r1cursor.moveToNext();
                            // 사용자가 참여되어있는 그룹들의 아이디와 역할 데이터들을 가져와서
                            // 하나의 그룹씩 조건에 맞게 처리
                            for(int k=0; k<4; k++){
                                final int index1;
                                index1 = k;
                                // 역할 및 그룹아이디 가져옴
                                String customerRole1 = r1cursor.getString(index1);
                                int customerGroupId1 = c1cursor.getInt(index1);
                                // 그룹의 현재인원(1명인 경우를 처리하기 위해 필요)
                                Cursor gnumcursor1 = myDB.getGNum(customerGroupId1);
                                gnumcursor1.moveToNext();
                                try{
                                    // 그룹의 현재인원
                                    int gnum1 = gnumcursor1.getInt(0);
                                    // 역할이 팀장인 경우 중에서도
                                    if(customerRole1.equals("팀장")){
                                        // 그룹의 인원이 한명이라면(= 자기 자신뿐이라면) 그룹을 그냥 삭제하면 됨
                                        if(gnum1==1){
                                            // 그룹과 그룹의 Task, ToDo를 같이 삭제
                                            // 나머지 Notice, Attendance, Comment 등의 정보들은 아래에서 사용자 아이디속성을 이용하여 삭제
                                            myDB.deleteGroup(customerGroupId1);
                                            myDB.deleteGroupTask(customerGroupId1);
                                            myDB.deleteGroupToDo(customerGroupId1);
                                        }
                                        // 위에서 검사를 한번 했기 때문에
                                        // 팀장인데 그룹원이 2명 이상인 경우는 없음
                                        else{
                                            // 없음
                                        }
                                    }
                                    // 역할이 팀원인 경우는 그룹을 삭제하면 안되고
                                    else{
                                        // 그룹의 인원을 1씩 감소시킴
                                        myDB.modifyGNum(customerGroupId1,gnum1-1);
                                    }
                                }
                                catch (CursorIndexOutOfBoundsException e){
                                }
                            }
                            // 이제는 사용자와 관련된 Notice, Schedule ,Task ToDo, Comment 등을 모두 삭제
                            myDB.deleteApplyForDropOut(id);
                            myDB.deleteConsentForDropOut(id);
                            myDB.deleteDenyForDropOut(id);
                            myDB.deleteNoticeForDropOut(id);
                            myDB.deleteAttendanceStatusForDropOut(id);
                            myDB.deleteAttendanceForDropOut(id);
                            myDB.deleteCommentForDropOut(id);
                            myDB.deleteScheduleForDropOut(id);
                            myDB.deleteMemoForDropOut(id);

                            // 사용자를 DB에서 삭제 후 탈퇴가 완료되었다는 토스트 메세지 출력
                            myDB.dropoutCustomer(id);
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("탈퇴되었습니다.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            // 로그인 화면으로 이동
                            Intent intent = new Intent(Mypage.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                            startActivity(intent);

                        }
                        // 탈퇴하려는 사용자가 팀장인 그룹이 있고 해당 그룹의 인원이 2명 이상이라면
                        else{
                            // 역할 변경 후 탈퇴를 다시 시도하도록 토스트 메시지 출력
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Mypage.this);
                            text.setText("팀장 역할 변경 후 탈퇴해주세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }
                    }
                });
                // 취소 버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Mypage.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // 하단바 버튼들 연결
        homeBar = (ImageButton) findViewById(R.id.homeBar_mypage);
        joinBar = (ImageButton) findViewById(R.id.joinBar_mypage);
        memoBar = (ImageButton) findViewById(R.id.memoBar_mypage);
        mypageBar = (ImageButton) findViewById(R.id.mypageBar_mypage);
        // 하단바 버튼에 리스너 적용
        Bottombar(homeBar, joinBar, memoBar,mypageBar);

    }
    // 하단바 버튼에 리스너 적용시키는 메소드
    public void Bottombar(ImageButton btnHome, ImageButton btnJoin, ImageButton btnMemo, ImageButton btnMypage){
        Intent data =getIntent();
        String id = data.getStringExtra("ID2");
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Join.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });

        btnMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Memo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });

        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 스택에 쌓인 액티비티 모두 종료
                intent.putExtra("ID1", id);
                intent.putExtra("ID2", id);
                startActivity(intent);
            }
        });
    }

    private long backpressedTime = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            LayoutInflater inflater = getLayoutInflater();

            View layout = inflater.inflate(R.layout.toastcustom,
                    (ViewGroup) findViewById(R.id.toastLayout));

            TextView text = layout.findViewById(R.id.toastText);

            Toast toast = new Toast(Mypage.this);
            text.setText("\'뒤로가기\'를 한 번 더 누르면 앱이 종료됩니다.");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }

    }
}
