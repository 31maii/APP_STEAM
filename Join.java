package com.example.a3_termproject_steam;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class Join extends Activity {
    // 필요한 위젯변수 선언
    ImageButton homeBar,joinBar,memoBar,mypageBar, imgBtnSearch1;
    Button btnMakeGroup;
    EditText edtSearch1;
    TextView tvToeic, tvCertificate, tvContest, tvSchoolTest, tvTeamplay, tvEtc;
    LinearLayout searchGroupLayout1;

    String searchWord; // 검색어 저장 변수

    String people; // 현재 인원/제한인원

    String lockOrUnlock; //공개, 비공개 여부 저장

    // DB 사용을 위한 변수선언
    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;
    ArrayList<String> gname, gcontent, gtype, gcategory, gcode;
    ArrayList<Integer> gid, gcapacity, gnum;
    String id;
    String gName, gContent, gType, gCategory, gCode;
    int groupid, gNum, gCapacity, currentgroupid, k;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        // 키보드 사용으로 키보드가 올라올 때, 레이아웃의 크기가 자동으로 설정되는 것을 막음. 위젯이 키보드 위로 같이 올라오지 않음.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // 데이터 불러오기
        Intent data = getIntent();
        id = data.getStringExtra("ID1");

        // 하단 버튼
        homeBar = (ImageButton) findViewById(R.id.homeBar_join);
        joinBar = (ImageButton) findViewById(R.id.joinBar_join);
        memoBar = (ImageButton) findViewById(R.id.memoBar_join);
        mypageBar = (ImageButton) findViewById(R.id.mypageBar_join);

        // 하단 버튼 함수
        Bottombar(homeBar, joinBar, memoBar,mypageBar);

        // 그룹 만들기 버튼
        btnMakeGroup = (Button) findViewById(R.id.btnMakeGroup);

        // 검색창 에디트 텍스트 & 검색 버튼
        edtSearch1 = (EditText) findViewById(R.id.edtSearch1);
        imgBtnSearch1 = (ImageButton)findViewById(R.id.imgBtnSearch1);

        // 카테고리별 search를 위한.
        tvToeic = (TextView) findViewById(R.id.tvToeic);
        tvCertificate = (TextView) findViewById(R.id.tvCertificate);
        tvContest = (TextView) findViewById(R.id.tvContest);
        tvSchoolTest = (TextView) findViewById(R.id.tvSchoolTest);
        tvTeamplay = (TextView) findViewById(R.id.tvTeamplay);
        tvEtc = (TextView) findViewById(R.id.tvEtc);

        // 그룹 보여주는 레이아웃
        searchGroupLayout1 = (LinearLayout) findViewById(R.id.searchGroupLayout1);

        // 카테고리 연결. 텍스트뷰 클릭시 해당 카테고리인 그룹의 레이아웃을 보이게 설정.
        tvToeic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '토익'인 그룹들을 보여줌
                search("토익");
            }
        });
        tvCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '자격증'인 그룹들을 보여줌
                search("자격증");

            }
        });
        tvContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '공모전'인 그룹들을 보여줌
                search("공모전");
            }
        });
        tvSchoolTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '학교 시험 대비'인 그룹들을 보여줌
                search("학교 시험 대비");
            }
        });
        tvTeamplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '팀플'인 그룹들을 보여줌
                search("팀플");
            }
        });
        tvEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래에 만들어놓은 함수를 이용하여 카테고리가 '기타'인 그룹들을 보여줌
                search("기타");
            }
        });

        // 검색 버튼
        imgBtnSearch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용을 가져온다.
                searchWord = edtSearch1.getText().toString();// 검색어 저장 변수에 검색어 내용 저장
                // 검색어를 입력하지 않은 경우
                if (searchWord.isEmpty()) {
                    // 토스트 메세지를 띄우기
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(Join.this);
                    text.setText("정보를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                }
                // 검색어를 입력한 경우
                else{
                    // 만들어놓은 함수를 이용하여 그룹이름에 검색어가 포함된 그룹들을 표시해준다.
                    searchBygGName(searchWord);

                    // 검색하였다는 토스트 메세지 출력
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(Join.this);
                    text.setText(searchWord + "을(를) 검색하였습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }

            }
        });

        // 검색창 액션 리스너. 휴대폰 키보드 enter 작동시 이벤트
        edtSearch1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                // 텍스트 내용을 가져온다.
                searchWord = textView.getText().toString(); // 검색어 저장 변수에 검색어 내용 저장

                // 텍스트 내용이 비어있다면...
                if (searchWord.isEmpty()) {

                    // 토스트 메세지를 띄우고,
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(Join.this);
                    text.setText("정보를 입력하세요");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    return true;
                }

                // 텍스트 내용이 비어있지않다면
                switch (i) {

                    // Search 버튼일경우. (키보드의 search 버튼)
                    case EditorInfo.IME_ACTION_SEARCH:
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Join.this);
                        text.setText(searchWord + "을(를) 검색하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                        // 만들어놓은 함수를 이용하여 그룹이름에 검색어가 포함된 그룹들을 표시해준다.
                        searchBygGName(searchWord);
                        break;

                    // 키보드 Enter 버튼일경우
                    default:

                        LayoutInflater inflater1 = getLayoutInflater();
                        View layout1 = inflater1.inflate(R.layout.toastcustom, (ViewGroup) findViewById(R.id.toastLayout));
                        TextView text1 = layout1.findViewById(R.id.toastText);
                        Toast toast1 = new Toast(Join.this);
                        text1.setText(searchWord + "을(를) 검색하였습니다.");
                        toast1.setDuration(Toast.LENGTH_SHORT);
                        toast1.setView(layout1);
                        toast1.show();

                        // 만들어놓은 함수를 이용하여 그룹이름에 검색어가 포함된 그룹들을 표시해준다.
                        searchBygGName(searchWord);
                        return true; // false로 바꾸면 enter 키패드 클릭시 다음줄로 넘어가버림
                }


                return true;

            }

        });
        // 그룹 생성 버튼을 클릭하면
        btnMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹 생성 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), MakeGroup.class);
                intent.putExtra("ID1",id);
                intent.putExtra("ID2",id);
                startActivity(intent);
            }
        });

        // 현재 사용자가 4개의 그룹(최대 4개까지만 참여 가능)에 참여되어 있으면
        // 그룹을 생성할 수 없도록 그룹 생성 버튼이 보이지 않도록 변경하는 코드
        myHelper = new MyDatabaseHelper(this);
        Cursor gcursor = myHelper.customerGroups(id);
        gcursor.moveToNext();
        k=0;
        for(int i=0;i<4;i++){
            if(gcursor.getInt(i)!=-1){
                k++;
            }
        }
        if(k==4){
            btnMakeGroup.setVisibility(View.GONE);
        }
    }
    // 검색어로 검색하는 함수
    private void searchBygGName(String searchWord) {
        // 그룹에 대한 모든 데이터 검색해옴
        Cursor cursor=myHelper.readAllData();
        // 일단 그룹을 표시할 레이아웃을 모두 지움
        searchGroupLayout1.removeAllViews();
        // 그룹의 각 정보들을 저장할 ArrayList선언
        gid = new ArrayList<>();
        gname = new ArrayList<>();
        gcontent = new ArrayList<>();
        gtype = new ArrayList<>();
        gcategory = new ArrayList<>();
        gcode = new ArrayList<>();
        gcapacity = new ArrayList<>();
        gnum = new ArrayList<>();

        if(cursor!=null){
            // 커서의 모든 데이터들을 ArrayList에 추가
            while(cursor.moveToNext()){
                gid.add(cursor.getInt(0));
                gname.add(cursor.getString(1));
                gcontent.add(cursor.getString(2));
                gtype.add(cursor.getString(3));
                gcategory.add(cursor.getString(4));
                gcapacity.add(cursor.getInt(5));
                gnum.add(cursor.getInt(6));
                gcode.add(cursor.getString(7));
            }

            // 각 그룹들에 대한 정보들을 변수에 하나씩 저장하여 카드뷰를 만들어서 추가하는 과정
            for(int i=0;i<gid.size(); i++){

                gName = gname.get(i);
                gType = gtype.get(i);
                gContent = gcontent.get(i);
                gCategory = gcategory.get(i);
                gCapacity = gcapacity.get(i);
                gNum = gnum.get(i);
                gCode = gcode.get(i);
                currentgroupid = gid.get(i);
                // 그룹코드가 없으면 공개
                if(gCode.equals("no")){
                    lockOrUnlock="공개";
                }
                // 그룹코드가 있으면 공개
                else{
                    lockOrUnlock="비공개";
                }
                // 검색어가 포함된 그룹들만 보여주기 위한 변수조작
                // 초기값 0으로 설정
                int n=0;
                // 검색어가 포함되어있으면 k=1로 변경
                if(gName.contains(searchWord)){
                    n=1;
                }

                // 현재 인원수가 제한인원과 같은 그룹들은 참여할 수 없으니 보이지 않도록 하기위해
                if(gNum==gCapacity){
                }
                // 이 경우의 그룹들만 addCard를 이용하여 layout에 추가
                else{
                    // k값이 1인 경우만 카드뷰를 추가
                    if(n==1){
                        addCard(gName, gType, gCategory, gCapacity, gNum, lockOrUnlock, gContent, gCode, currentgroupid);
                    }
                }
            }

        }

    }
    // 카테고리를 이용하여 검색하는 함수
    private void search(String category) {
        // 그룹 정보들을 가져오는 내용들은 전부 위의 검색어 검색 함수와 동일함
        Cursor cursor=myHelper.readAllData();

        searchGroupLayout1.removeAllViews();

        gid = new ArrayList<>();
        gname = new ArrayList<>();
        gcontent = new ArrayList<>();
        gtype = new ArrayList<>();
        gcategory = new ArrayList<>();
        gcode = new ArrayList<>();
        gcapacity = new ArrayList<>();
        gnum = new ArrayList<>();

        if(cursor!=null){

            while(cursor.moveToNext()){
                gid.add(cursor.getInt(0));
                gname.add(cursor.getString(1));
                gcontent.add(cursor.getString(2));
                gtype.add(cursor.getString(3));
                gcategory.add(cursor.getString(4));
                gcapacity.add(cursor.getInt(5));
                gnum.add(cursor.getInt(6));
                gcode.add(cursor.getString(7));

                //Toast.makeText(getApplicationContext(), "데이터가 들어오긴 한듯..?", Toast.LENGTH_SHORT).show();
            }

            for(int i=0;i<gid.size(); i++){

                gName = gname.get(i);
                gType = gtype.get(i);
                gContent = gcontent.get(i);
                gCategory = gcategory.get(i);
                gCapacity = gcapacity.get(i);
                gNum = gnum.get(i);
                gCode = gcode.get(i);
                currentgroupid = gid.get(i);

                // 카테고리 검색 함수에서는
                // 카테고리가 포함되었는지 확인하기 위한 과정이 필요

                // 여러개의 카테고리를 StringTokenizer로 각각으로 나누어 조작
                int k=0;
                StringTokenizer st = new StringTokenizer(gCategory, " # ");
                // 여러 카테고리들 중 동일한 카테고리가 있으면 k값 1로 변경
                while(st.hasMoreTokens()){
                    if(category.contains(st.nextToken())){
                        k=1;
                        break;
                    }
                }

                if(gCode.equals("no")){
                    lockOrUnlock="공개";
                }else{
                    lockOrUnlock="비공개";
                }
                // 현재 인원수가 제한인원과 같은 그룹들은 참여할 수 없으니 보이지 않도록 하기위해
                if(gNum==gCapacity){
                }
                else{
                    // 검색한 카테고리를 포함하는 그룹들만을 보여줌
                    if(k==1){
                        addCard(gName, gType, gCategory, gCapacity, gNum, lockOrUnlock, gContent, gCode, currentgroupid);
                    }
                }
            }

        }
        // 검색했다는 토스트 메세지 출력
        String txt = category + "을 검색하셨습니다.";
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.toastcustom,
                (ViewGroup) findViewById(R.id.toastLayout));

        TextView text = layout.findViewById(R.id.toastText);

        Toast toast = new Toast(Join.this);
        text.setText(txt);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    // 하단바 함수
    public void Bottombar(ImageButton btnHome, ImageButton btnJoin, ImageButton btnMemo, ImageButton btnMypage){
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

    @Override
    protected void onStart() {
        super.onStart();
        // 액티비티가 시작될 때마다 작동하는 코드의 내용으로
        // 위에서 검색 함수가 그룹들을 보여주는 방식과 동일하다
        // 위의 검색 함수에서는 검색어, 카테고리에 포함되는 그룹들만을 보여주었다면
        // 이 부분의 코드에서는 모든 그룹들을 보여준다.
        searchGroupLayout1 = (LinearLayout) findViewById(R.id.searchGroupLayout1);

        myHelper = new MyDatabaseHelper(this);

        sqlDB=myHelper.getReadableDatabase();
        Cursor cursor=myHelper.readAllData();

        searchGroupLayout1.removeAllViews();

        Intent data = getIntent();
        id = data.getStringExtra("ID2");

        gid = new ArrayList<>();
        gname = new ArrayList<>();
        gcontent = new ArrayList<>();
        gtype = new ArrayList<>();
        gcategory = new ArrayList<>();
        gcode = new ArrayList<>();
        gcapacity = new ArrayList<>();
        gnum = new ArrayList<>();

        if(cursor!=null){

            while(cursor.moveToNext()){
                gid.add(cursor.getInt(0));
                gname.add(cursor.getString(1));
                gcontent.add(cursor.getString(2));
                gtype.add(cursor.getString(3));
                gcategory.add(cursor.getString(4));
                gcapacity.add(cursor.getInt(5));
                gnum.add(cursor.getInt(6));
                gcode.add(cursor.getString(7));
            }

            for(int i=0;i<gid.size(); i++){
                groupid = gid.get(i);
                gName = gname.get(i);
                gType = gtype.get(i);
                gContent = gcontent.get(i);
                gCategory = gcategory.get(i);
                gCapacity = gcapacity.get(i);
                gNum = gnum.get(i);
                gCode = gcode.get(i);
                currentgroupid = gid.get(i);


                if(gCode.equals("no")){
                    lockOrUnlock="공개";
                }else{
                    lockOrUnlock="비공개";
                }

                Cursor ccursor = myHelper.customerGroups(id);
                ccursor.moveToNext();
                int mygroup = 0;
                for(int k=0;k<4;k++){
                    final int index;
                    index = k;
                    if(groupid == ccursor.getInt(index)){
                        mygroup=1;
                    }
                }


                // 현재 인원수가 제한인원과 같은 그룹들은 참여할 수 없으니 보이지 않도록 하기위해
                if(gNum==gCapacity){

                }
                // 이 경우의 그룹들만 addCard를 이용하여 layout에 추가
                else{
                    addCard(gName, gType, gCategory, gCapacity, gNum, lockOrUnlock, gContent, gCode, currentgroupid);
                }
            }

        }else{
            Toast.makeText(this, "cursor가 비었음", Toast.LENGTH_SHORT).show();
        }

    }
    // 레이아웃에 카드뷰를 추가하는 함수
    // 파라미터로 그룹에 대한 정보들을 입력받는다.
    private void addCard(String GroupName, String type, String category, int gCapacity, int gNum, String lockOrUnlock, String gContent, String gCode, int currentgroupid) {
        // 미리 만들어놓은 카드뷰 xml파일을 가져와서 연결한다.
        final View view = getLayoutInflater().inflate(R.layout.card1, null);
        TextView name = view.findViewById(R.id.name);
        TextView study = view.findViewById(R.id.study);
        TextView toeic = view.findViewById(R.id.toeic);
        TextView person = view.findViewById(R.id.person);
        ImageView lock = view.findViewById(R.id.lock);

        ImageButton btn = view.findViewById(R.id.btn);

        // 입력받은 파라미터들을 이용하여 카드뷰 내의 위젯들 내용들을 변경한다.
        people = Integer.toString(gNum) + "/" + Integer.toString(gCapacity);
        name.setText(GroupName);
        study.setText("# " + type);
        person.setText(people);
        toeic.setText(category);

        if(lockOrUnlock.equals("공개")){
            lock.setImageResource(R.drawable.unlock);
        }else if(lockOrUnlock.equals("비공개")){
            lock.setImageResource(R.drawable.lock);
        }
        // 버튼을 클릭했을 때 그룹정보를 보여주는 페이지로 이동하도록
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹정보를 보여주는 페이지에서 필요한 그룹정보 데이터들도 같이 넘겨줌
                Intent intent = new Intent(getApplicationContext(), GroupInformation.class);
                intent.putExtra("GroupName", GroupName);
                intent.putExtra("type", type);
                intent.putExtra("category", category);
                intent.putExtra("people", people);
                intent.putExtra("lockOrUnlock", lockOrUnlock);
                intent.putExtra("gContent", gContent);
                intent.putExtra("gCode", gCode);
                intent.putExtra("GID", currentgroupid);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });
        // 카드뷰 세팅을 완료훈 후에는 원하는 레이아웃에 추가한다.
        searchGroupLayout1.addView(view);

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

            Toast toast = new Toast(Join.this);
            text.setText("\'뒤로가기\'를 한 번 더 누르시면 앱이 종료됩니다.");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }

    }


}
