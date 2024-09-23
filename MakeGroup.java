package com.example.a3_termproject_steam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeGroup extends Activity {
    // 위젯 변수 선언
    EditText edtMakeGroup [] = new EditText[4];
    RadioGroup RGroupType1;
    RadioButton rStudy1, rTeamplay1;
    CheckBox chkCategory []= new CheckBox[6];
    Button btnMakeGroup2;
    String id;
    // 카테고리 체크박스 몇개 체크 되었는지 저장하는 변수 선언
    int checkCount;

    // 제한 인원 숫자형
    int numPeople;

    String type;  // 스터디 or 팀플
    int gnum; // 현재 참가 인원 명 수 --> Todo: DB 연결하여 현재 참가 인원수 저장해야함.
    String num; // 현재 참가 인원 명 수를 String 형으로 바꾸기
    String people; // '현재 참가 인원 수/모집 인원 수' 저장 변수

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_group);

        // 그룹명, 그룹 소개, 제한 인원, 그룹 코드 입력창
        Integer edtMakeGroupId[]={R.id.edtGroupName1, R.id.edtGroupIntroduce1, R.id.edtNumPeople1, R.id.edtGroupCode1};
        for(int i=0; i<edtMakeGroup.length;i++){
            edtMakeGroup[i] = (EditText) findViewById(edtMakeGroupId[i]);
        }


        // 그룹 구분 라디오 그룹, 라디오 버튼
        RGroupType1 = (RadioGroup) findViewById(R.id.RGroupType1);
        rStudy1 = (RadioButton) findViewById(R.id.rStudy1);
        rTeamplay1 = (RadioButton) findViewById(R.id.rTeamplay1);
        // 넘겨받은 데이터 변수에 저장
        Intent data = getIntent();
        id = data.getStringExtra("ID1");

        // 카테고리 체크박스
        Integer chkCategoryId[] = {R.id.chkToeic, R.id.chkCertificate, R.id.chkContest, R.id.chkSchoolTest, R.id.chkTeamplay, R.id.chkEtc};

        for(int i=0; i<chkCategory.length; i++){
            chkCategory[i] = (CheckBox) findViewById(chkCategoryId[i]);
        }


        // 그룹 만들기 버튼
        btnMakeGroup2= (Button) findViewById(R.id.btnMakeGroup2);

        //입력창의 값들을 String 형으로 바꿔 저장할 배열
        String edt[] = new String[4];


        // 그룹 만들기 버튼 클릭시 그룹 생성 완료 페이지로 연결
        btnMakeGroup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력창의 값들을 String 형으로 바꿈.
                // edt[0]:그룹 이름, edt[1]:그룹 소개, edt[2]:제한 인원, edt[3]:그룹 코드
                for(int i=0; i<edt.length; i++){
                    edt[i] = edtMakeGroup[i].getText().toString();
                }
                // 카테고리 체크 개수 확인
                checkCount=0;

                for(int i=0; i<chkCategory.length; i++){
                    if(chkCategory[i].isChecked()){
                        checkCount++;
                    }
                }

                // 그룹 이름 입력 유무 확인
                if(edt[0].trim().equals("")){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(MakeGroup.this);
                    text.setText("그룹명을 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    // 카테고리 체크 유무 확인
                    if(checkCount==0){
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(MakeGroup.this);
                        text.setText("카테고리를 하나 이상 선택하세요");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }else{
                        // 그룹 소개 입력 유무 확인
                        if(edt[1].trim().equals("")){
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(MakeGroup.this);
                            text.setText("그룹 소개글을 작성하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }else{
                            // 제한 인원 입력 유무 확인
                            if(edt[2].trim().equals("")){
                                LayoutInflater inflater = getLayoutInflater();

                                View layout = inflater.inflate(R.layout.toastcustom,
                                        (ViewGroup) findViewById(R.id.toastLayout));

                                TextView text = layout.findViewById(R.id.toastText);

                                Toast toast = new Toast(MakeGroup.this);
                                text.setText("제한 인원을 입력하세요");
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();
                            }else{
                                // 제한 인원 숫자만 입력 가능
                                try{
                                    // 제한 인원 int 형으로 바꿔 주기
                                    numPeople = Integer.parseInt(edt[2]);

                                    // 제한 인원 2~50 사이로 입력했는지 확인
                                    if(numPeople<2 || numPeople>50){
                                        LayoutInflater inflater = getLayoutInflater();

                                        View layout = inflater.inflate(R.layout.toastcustom,
                                                (ViewGroup) findViewById(R.id.toastLayout));

                                        TextView text = layout.findViewById(R.id.toastText);

                                        Toast toast = new Toast(MakeGroup.this);
                                        text.setText("제한 인원은 2~50명까지 등록 가능합니다.");
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();
                                    }else{
                                        // 그룹 생성 완료!

                                        if(rStudy1.isChecked()){
                                            type="스터디";
                                        }else if(rTeamplay1.isChecked()){
                                            type="팀 프로젝트";
                                        }
                                        gnum=1; // 그룹을 생성한 초기에는 자신밖에 팀원이 없기 때문에 인원수를 1로 지정
                                        num = Integer.toString(gnum);
                                        people = num + "/" + edt[2]; // '현재 인원수/ 제한인원'

                                        String check=""; // 카테고리 저장하기 위한 변수
                                        // 체크된 카테고리 내용들을 문자열로 만듬
                                        for(int i=0; i<chkCategory.length;i++){
                                            if(chkCategory[i].isChecked()){
                                                check=check + "# "+chkCategory[i].getText().toString();
                                            }
                                        }

                                        // DB에 저장할 그룹코드
                                        String gcode = edt[3];
                                        if(edt[3].trim().equals("")){
                                            // 그룹코드가 없는 경우는 no로 지정
                                            gcode="no";
                                        }
                                        // 현재 시간을 문자열로 저장
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                        // 데이터베이스 객체 생성
                                        MyDatabaseHelper myDB = new MyDatabaseHelper(MakeGroup.this);
                                        // DB에 데이터를 저장
                                        // 그룹을 추가할 때 그룹 아이디 속성이 AUTO INCREMENT로 자동 증가되기 때문에
                                        // 그룹을 생성한 사용자 속성에 어떤 그룹 아이디를 저장해야하는지 모름
                                        // 이 자동으로 저장된 그룹 아이디를 알아내기 위한 용도로 위에 선언한 현재시간(그룹 생성 시간)을 이용한다.
                                        myDB.addGroup(edt[0].trim(), edt[1], type, check, numPeople, gnum, gcode, currentTime);

                                        // DB에서 현재 사용자의 그룹속성들을 검사하여 -1인 속성을 찾아서 변경
                                        Cursor cursor = myDB.customerGroups(id);
                                        cursor.moveToNext();
                                        // 파라미터로 입력한 그룹 생성시간에 생성된 그룹의 그룹아이디 값을 가져오는 함수
                                        Cursor gcursor = myDB.currentGID(currentTime);
                                        gcursor.moveToNext();
                                        // 가져온 그룹 아이디를 생성한 사용자의 그룹속성에 적용해줌
                                        // 역할인 팀장으로 적용
                                        int gid = gcursor.getInt(0);

                                        for(int i=0;i<4;i++){
                                            final int index;
                                            index = i;
                                            if(cursor.getInt(index)==-1){
                                                if(index == 0){
                                                    myDB.updateCustomer0(id, gid, "팀장");
                                                    break;
                                                }
                                                else if(index == 1){
                                                    myDB.updateCustomer1(id, gid, "팀장");
                                                    break;
                                                }
                                                else if(index == 2){
                                                    myDB.updateCustomer2(id, gid, "팀장");
                                                    break;
                                                }
                                                else if(index == 3){
                                                    myDB.updateCustomer3(id, gid, "팀장");
                                                    break;
                                                }
                                            }
                                        }
                                        // 생성 완료 안내 창으로 연결
                                        Intent intent = new Intent(getApplicationContext(), GroupCreationCompleted.class);
                                        intent.putExtra("ID1", id);
                                        intent.putExtra("ID2", id);
                                        startActivity(intent);
                                        finish();
                                    }
                                }catch(IllegalArgumentException e){
                                    // 제한 인원을 숫자가 아닌 문자 입력시 토스트 메세지
                                    LayoutInflater inflater = getLayoutInflater();

                                    View layout = inflater.inflate(R.layout.toastcustom,
                                            (ViewGroup) findViewById(R.id.toastLayout));

                                    TextView text = layout.findViewById(R.id.toastText);

                                    Toast toast = new Toast(MakeGroup.this);
                                    text.setText("숫자만 입력 가능합니다.");
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                }
                            }
                        }
                    }
                }
            }
        });

    }
}
