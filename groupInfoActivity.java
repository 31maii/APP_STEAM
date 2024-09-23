package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class groupInfoActivity extends Activity {
    // 사용할 위젯변수, DB 변수 선언
    LinearLayout layoutMember, layoutChoiceLeader;
    TextView tvGroupName, tvGroupType, tvNum, tvLock;
    ImageView iv;
    MyDatabaseHelper myDB;
    Button btnChangeLeader;
    View dlgView;
    int gid;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_team_main_page);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // DB객체 생성 및 위젯들 연결
        myDB = new MyDatabaseHelper(this);
        layoutMember = (LinearLayout) findViewById(R.id.layoutMember);
        layoutMember.removeAllViews();
        tvGroupName = (TextView) findViewById(R.id.tvGName);
        tvGroupType = (TextView) findViewById(R.id.tvGType);
        tvNum = (TextView) findViewById(R.id.tvGNum);
        tvLock = (TextView) findViewById(R.id.tvLock);
        iv = (ImageView) findViewById(R.id.img2);
        // 넘겨받은 데이터들 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID",0);
        String gname = data.getStringExtra("GNAME");
        String gtype = data.getStringExtra("GTYPE");
        int gnum = data.getIntExtra("GNUM",0);
        int gcap = data.getIntExtra("GCAPACITY",0);
        String gcode = data.getStringExtra("GCODE");
        // 넘겨받은 데이터들을 이용하여 페이지 위젯들에 정보들을 적용하는 과정
        if(gcode.equals("no")){
            iv.setImageResource(R.drawable.unlock);
            tvLock.setText("공개");
        }
        else{
            iv.setImageResource(R.drawable.lock);
            tvLock.setText("비공개");
        }
        tvGroupName.setText(gname);
        tvGroupType.setText(gtype);
        tvNum.setText(Integer.toString(gnum)+"/"+Integer.toString(gcap));
        // 현재 그룹에 참여되어있는 사용자들을 불러온다.
        Cursor cursor = myDB.getGroupParticipant(gid);
        if(cursor != null){
            // 그룹 멤버들을 한명씩 카드뷰로 만들어서 추가하는 코드로
            // 역할이 팀장인 경우는 우측에 따로 표시되도록
            while(cursor.moveToNext()){
                String cid = cursor.getString(0);
                String name = cursor.getString(2);
                String role="";
                for(int j=6;j<13;j=j+2){
                    if(cursor.getInt(j)==gid){
                        role=cursor.getString(j+1);
                    }
                }
                // 만들어놓은 함수 이용
                addCard(cid, name, role);
            }
        }
        // 팀장 변경 버튼은 역할이 팀장인 사용자만 이용할 수 있도록 visible 속성 변경
        btnChangeLeader = (Button) findViewById(R.id.btnChangeLeader);
        btnChangeLeader.setVisibility(View.GONE);
        String role = data.getStringExtra("ROLE");
        if(role.equals("팀장")){
            btnChangeLeader.setVisibility(View.VISIBLE);
        }
        // 팀장 변경 버튼을 클릭하면
        btnChangeLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 만들어놓은 대화상자에 현재 그룹의 멤버들을 한 명씩 카드뷰로 만들어서 추가하여 보여준다.
                dlgView = (View) View.inflate(groupInfoActivity.this, R.layout.choice_leader, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(groupInfoActivity.this, R.style.AlertDialogTheme);
                dlg.setView(dlgView);
                layoutChoiceLeader = dlgView.findViewById(R.id.layoutChoiceLeader);
                Cursor cursor1 = myDB.getGroupParticipant(gid);
                if(cursor1 != null){
                    while(cursor1.moveToNext()){
                        String cid = cursor1.getString(0);
                        String name = cursor1.getString(2);
                        String role="";
                        for(int j=6;j<13;j=j+2){
                            if(cursor1.getInt(j)==gid){
                                role=cursor1.getString(j+1);
                            }
                        }
                        // 카드뷰 추가 함수를 이용
                        addCard1(cid, name, role);
                    }
                }
                // 취소 버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(groupInfoActivity.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
    }
    // 카드뷰 추가 함수는 이전에 작성한 코드 내용과 동일하다.
    private void addCard(String cid, String cname, String role) {
        final View view = getLayoutInflater().inflate(R.layout.card_member, null);
        TextView name = view.findViewById(R.id.txtMember);
        String txt = "   "+cname;
        if(role.equals("팀장")){
            txt=txt+" (팀장)";
        }

        name.setText(txt);
        layoutMember.addView(view);
    }
    // 카드뷰 추가 함수는 이전에 작성한 코드 내용과 동일하다.
    // 이 메소드에는 팀장 역할을 변경할 수 있도록 추가적인 클릭 이벤트 리스너가 적용되어있다.
    private void addCard1(String cid, String cname, String role) {
        final View view = getLayoutInflater().inflate(R.layout.card_member1, null);
        TextView tv = view.findViewById(R.id.txtMember);
        String txt = "   "+cname;
        if(role.equals("팀장")){
            txt=txt+" (팀장)";
        }

        tv.setText(txt);
        // 팀장역할을 넘겨줄 사용자를 클릭하면
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 대화상자를 보여주며 팀장 역할을 변경할 것인지 다시 한 번 확인 후
                AlertDialog.Builder dlg1 = new AlertDialog.Builder(groupInfoActivity.this, R.style.AlertDialogTheme);
                dlg1.setTitle("정말 팀장을 "+cname+"으로 변경하시겠습니까?");
                // 변경 버튼을 클릭하면
                dlg1.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 팀장만 팀장 변경 버튼이 보이도록 했으니 현재 사용자의 ID인 id가
                        // 그룹 팀장의 id임
                        Cursor gcursor1 = myDB.customerGroups(id);
                        gcursor1.moveToNext();
                        // 팀장의 그룹속성을 하나씩 확인해보면서
                        // 현재 그룹에 대한 역할을 팀장에서 팀원으로 변경
                        for(int j=0;j<4;j++){
                            if(gcursor1.getInt(j)==gid){
                                // 현재 그룹 팀장의 역할을 팀원으로 변경
                                if(j==0){
                                    myDB.modifyCustomerRole1(id, "팀원");
                                    break;
                                }
                                else if(j==1){
                                    myDB.modifyCustomerRole2(id, "팀원");
                                    break;
                                }
                                else if(j==2){
                                    myDB.modifyCustomerRole3(id, "팀원");
                                    break;
                                }
                                else if(j==3){
                                    myDB.modifyCustomerRole4(id, "팀원");
                                    break;
                                }
                            }
                        }
                        // 역할을 팀장으로 변경하기로 고른 팀원의 역할을 팀장으로 변경
                        Cursor gcursor2 = myDB.customerGroups(cid);
                        gcursor2.moveToNext();
                        for(int j=0;j<4;j++){
                            if(gcursor2.getInt(j)==gid){
                                // 역할을 팀장으로 변경해주기로 한 사용자의 그룹속성을 하나씩 확인해보면서
                                // 현재 그룹에 대한 역할을 팀원에서 팀장으로 변경
                                if(j==0){
                                    myDB.modifyCustomerRole1(cid, "팀장");
                                    break;
                                }
                                else if(j==1){
                                    myDB.modifyCustomerRole2(cid, "팀장");
                                    break;
                                }
                                else if(j==2){
                                    myDB.modifyCustomerRole3(cid, "팀장");
                                    break;
                                }
                                else if(j==3){
                                    myDB.modifyCustomerRole4(cid, "팀장");
                                    break;
                                }
                            }
                        }
                        // 사용자의 속성 변경 후 메인 홈 화면으로 이동
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("ID1", id);
                        intent.putExtra("ID2", id);
                        startActivity(intent);
                    }
                });
                // 취소 버튼을 클릭하면
                dlg1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(groupInfoActivity.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg1.show();
            }
        });
        // 최종적으로 완성된 카드뷰를 레이아웃에 추가
        layoutChoiceLeader.addView(view);
    }
}
