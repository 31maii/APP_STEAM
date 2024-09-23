package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ApplyListActivity extends Activity {
    // 사용할 위젯, DB 변수 선언
    String id;
    int gid, gnum, gcap;
    LinearLayout layoutApplyList;
    MyDatabaseHelper myDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 신청내역들을 표시해줄 레이아웃 연결
        layoutApplyList = (LinearLayout) findViewById(R.id.layoutApplyList);
        // 넘겨받은 데이터 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID",0);
        // 레이아웃에 있는 모든 내용을 지움
        layoutApplyList.removeAllViews();
        // DB 객체 생성
        myDB = new MyDatabaseHelper(this);

        // 그룹의 현재인원과 제한인원 값을 가져온다.
        // 그룹의 현재인원이 제한 인원과 같아진 경우 더 이상 참여신청을 받으면 안되기 때문에
        // 이 경우를 처리해주는 내용이 아래의 코드이다.
        Cursor gnumcursor = myDB.getGNum(gid);
        Cursor gcapcursor = myDB.groupcap(gid);

        gnumcursor.moveToNext();
        gcapcursor.moveToNext();

        try{
            gnum = gnumcursor.getInt(0);
            gcap = gcapcursor.getInt(0);
        }
        catch(CursorIndexOutOfBoundsException e){

        }

        // 제한 인원 이상으로 그룹 팀원이 추가되는 것을 막기 위해
        // '인원<=제한인원'일 경우 해당 그룹에 대한 모든 신청을 삭제
        if(gnum>=gcap){
            myDB.deleteGroupApply(gid);
            finish();
        }
        // 현재 그룹에 대한 모든 참여신청 데이터들을 불러온다
        Cursor cursor = myDB.readAllApplies(gid);

        if(cursor != null){
            // 참여신청 데이터들을 하나씩 가져와서
            while(cursor.moveToNext()){
                // 데이터들을 이용하여 카드뷰를 만들어서 레이아웃에 추가하는 코드
                String cid = cursor.getString(0);

                Cursor ncursor = myDB.customerNames(cid);
                ncursor.moveToNext();
                String name = ncursor.getString(0);
                String text = name + "님이 그룹 참여를 신청하였습니다.";
                // 만들어놓은 카드뷰 추가 함수 이용
                addCard(cid, text);
            }
        }
    }
    // 참여신청 내역들을 카드뷰로 하나씩 만들어서 레이아웃에 추가하는 함수
    private void addCard(String cid, String text) {
        // 만들어놓은 카드뷰를 불러와서
        final View view = getLayoutInflater().inflate(R.layout.card_apply, null);
        // 카드뷰의 위젯들 연결
        TextView txtCustomerName = view.findViewById(R.id.txtApplyName);
        Button btnConsent = view.findViewById(R.id.btnConsent);
        Button btnDeny = view.findViewById(R.id.btnDeny);

        // 넘겨받은 내용을 텍스트뷰에 적용하고
        txtCustomerName.setText(text);
        // 수락버튼을 클릭하면
        btnConsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 신청한 사용자의 그룹속성, 역할속성을 변경하기 위한 코드 내용으로
                // 신청한 사용자 속성 G1~G4 중에서 -1인 그룹속성을 찾아서
                // 현재 그룹의 아이디 값으로 변경해주고, 역할 역시 팀원으로 변경해주는 코드이다.
                // 추가적으로 참여 신청 데이터를 삭제
                // 수락한 경우는 수락 데이터를 추가하고, 거절한 경우는 거절 데이터를 추가한다.
                Cursor ccursor = myDB.customerGroups(cid);
                ccursor.moveToNext();
                Cursor numcursor = myDB.getGNum(gid);
                numcursor.moveToNext();

                for(int i=0;i<4;i++){
                    final int index;
                    index = i;
                    if(ccursor.getInt(index)==-1){
                        if(index == 0){
                            myDB.updateCustomer0(cid, gid, "팀원");
                            myDB.modifyGNum(gid, gnum+1);
                            myDB.deleteApply(cid, gid);
                            myDB.addConsent(gid, cid);
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(ApplyListActivity.this);
                            text.setText("수락하였습니다");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            break;
                        }
                        else if(index == 1){
                            myDB.updateCustomer1(cid, gid, "팀원");
                            myDB.modifyGNum(gid, gnum+1);
                            myDB.deleteApply(cid, gid);
                            myDB.addConsent(gid, cid);
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(ApplyListActivity.this);
                            text.setText("수락하였습니다");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            break;
                        }
                        else if(index == 2){
                            myDB.updateCustomer2(cid, gid, "팀원");
                            myDB.modifyGNum(gid, gnum+1);
                            myDB.deleteApply(cid, gid);
                            myDB.addConsent(gid, cid);
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(ApplyListActivity.this);
                            text.setText("수락하였습니다");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            break;
                        }
                        else if(index == 3){
                            myDB.updateCustomer3(cid, gid, "팀원");
                            myDB.modifyGNum(gid, gnum+1);
                            myDB.deleteApply(cid, gid);
                            myDB.addConsent(gid, cid);
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(ApplyListActivity.this);
                            text.setText("수락하였습니다");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            break;
                        }
                    }
                }
                // 신청한 사용자의 그룹, 역할 속성 변경 처리를 완료한 후 신청 리스트 페이지로 다시 이동
                Intent intent = new Intent(getApplicationContext(), ApplyListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // 거절 버튼을 클릭하면
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에서 참여신청 데이터를 삭제하고, 거절 데이터를 추가
                myDB.deleteApply(cid, gid);
                myDB.addDeny(gid, cid);
                // 참여 신청리스트로 다시 이동하고 토스트 메세지 출력
                Intent intent = new Intent(getApplicationContext(), ApplyListActivity.class);
                finish();
                startActivity(intent);
                LayoutInflater inflater = getLayoutInflater();

                View layout = inflater.inflate(R.layout.toastcustom,
                        (ViewGroup) findViewById(R.id.toastLayout));

                TextView text = layout.findViewById(R.id.toastText);

                Toast toast = new Toast(ApplyListActivity.this);
                text.setText("거절되었습니다");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        });
        // 최종적으로 완성된 카드뷰를 레이아웃에 추가
        layoutApplyList.addView(view);

    }
}
