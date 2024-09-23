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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Memo extends Activity {
    ImageButton btnMakeMemo;
    LinearLayout memoLayout;

    ImageButton homeBar,joinBar,memoBar,mypageBar;

    String id;

    View makeMemoDialog, modifyMemoDialog;

    EditText edtMemoTitle, edtMemoContent;

    String Title, Content;

    MyDatabaseHelper myHelper;
    SQLiteDatabase sqlDB;

    ArrayList <Integer> memoid;
    ArrayList <String> custid, memotitle, memocontent, memodate;

    int memoId;
    String custId, memoTitle, memoContent, memoDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);

        Intent data = getIntent();
        id = data.getStringExtra("ID1");

        // 하단 버튼
        homeBar = (ImageButton) findViewById(R.id.homeBar_memo);
        joinBar = (ImageButton) findViewById(R.id.joinBar_memo);
        memoBar = (ImageButton) findViewById(R.id.memoBar_memo);
        mypageBar = (ImageButton) findViewById(R.id.mypageBar_memo);

        // 하단 버튼 함수
        Bottombar(homeBar, joinBar, memoBar,mypageBar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        btnMakeMemo = (ImageButton) findViewById(R.id.btnMakeMemo);
        memoLayout = (LinearLayout) findViewById(R.id.memoLayout);

        myHelper = new MyDatabaseHelper(this);
        sqlDB=myHelper.getReadableDatabase();

        memoLayout.removeAllViews();

        btnMakeMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMemoDialog = (View) View.inflate(Memo.this, R.layout.memo_dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(Memo.this, R.style.AlertDialogTheme);
                dlg.setView(makeMemoDialog);

                edtMemoTitle = makeMemoDialog.findViewById(R.id.edtMemoTitle);
                edtMemoContent = makeMemoDialog.findViewById(R.id.edtMemoContent);

                dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 여기에 이벤트를 줄 경우 버튼 클릭 시 어떤 조건이든 dialog가 꺼짐
                    }
                });

                dlg.setNegativeButton("취소", null);

                AlertDialog dialog = dlg.create();
                dialog.show();


                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View. OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Title = edtMemoTitle.getText().toString(); // 작성한 제목 가져오기
                        Content = edtMemoContent.getText().toString(); // 작성한 내용 가져오기

                        if(Title.equals("")){
                            // 제목을 작성하지 않았을 경우, 제목 없음으로 자동 저장
                            Title="제목 없음";
                        }

                        if(Content.trim().equals("")){ // 내용이 비었으면 DB에 저장 되지 않으며, dialog가 종료되지 않는다.

                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Memo.this);
                            text.setText("내용을 입력하세요.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();


                        }else{
                            String currentTime = new SimpleDateFormat("yyyy.MM.dd  HH:mm").format(new Date());
                            // 메모 db 저장
                            myHelper.addMemo(id,Title, Content, currentTime);
                            // dialog 종료하면서 memo 페이지도 종료 후 다시 열기
                            Intent intent = new Intent(getApplicationContext(),Memo.class);
                            intent.putExtra("ID1", id);
                            startActivity(intent);
                            finish();

                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Memo.this);
                            text.setText("메모가 저장되었습니다.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();

                            dialog.dismiss(); // dialog 종료.

                        }
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    } // 취소 버튼 클릭 시, dialog 종료.
                });



            }
        });

        // 카드 추가

        // 메모 db 저장할 arraylist
        memoid = new ArrayList<>(); // 메모 아이디
        custid = new ArrayList<>(); // 고객 아이디
        memotitle = new ArrayList<>(); // 메모 제목
        memocontent = new ArrayList<>(); // 메모 내용
        memodate = new ArrayList<>(); //메모 작성 날짜

        Cursor cursorMemo = myHelper.MemoRead(id); // db 읽어오기 위한

        if(cursorMemo!=null){
            while(cursorMemo.moveToNext()){
                // 메모 아이디, 고객 아이디, 메모 제목, 메모 내용, 메모 날짜
                memoid.add(cursorMemo.getInt(0));
                custid.add(cursorMemo.getString(1));
                memotitle.add(cursorMemo.getString(2));
                memocontent.add(cursorMemo.getString(3));
                memodate.add(cursorMemo.getString(4));
            }

            for(int i=memoid.size()-1; i>=0; i--){ // 최신 메모가 위로 올라가게 함
                memoId = memoid.get(i);
                custId = custid.get(i);
                memoTitle = memotitle.get(i);
                memoContent = memocontent.get(i);
                memoDate = memodate.get(i);

                if(custId.trim().equals(id)){ // 자신의 메모만 보여줌
                    addCard(memoId, memoTitle, memoContent, memoDate); // 메모 layout에 추가하기
                }
            }
        }


    }

    private void addCard(int memoID, String Title, String Content, String Date){
        final View view = getLayoutInflater().inflate(R.layout.card_memo, null);
        TextView tvMemoTitle = view.findViewById(R.id.tvMemoTitle);
        TextView tvMemoContent = view.findViewById(R.id.tvMemoContent);
        TextView tvMemoDate = view.findViewById(R.id.tvMemoDate);
        Button btnMemoModify = view.findViewById(R.id.btnMemoModify);
        Button btnMemoDelete = view.findViewById(R.id.btnMemoDelete);

        tvMemoTitle.setText(Title);
        tvMemoContent.setText(Content);
        tvMemoDate.setText(Date);

        // 메모 수정
        btnMemoModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyMemoDialog = (View) View.inflate(Memo.this, R.layout.memo_dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(Memo.this, R.style.AlertDialogTheme);
                dlg.setTitle("Memo");
                dlg.setView(modifyMemoDialog);

                edtMemoTitle = modifyMemoDialog.findViewById(R.id.edtMemoTitle);
                edtMemoContent = modifyMemoDialog.findViewById(R.id.edtMemoContent);

                edtMemoTitle.setText(Title); // 원래 작성했던 제목을 넣어줌
                edtMemoContent.setText(Content); // 원래 작성했던 내용을 넣어줌

                dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dlg.setNegativeButton("취소", null);

                AlertDialog dialog = dlg.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View. OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String ModifyTitle = edtMemoTitle.getText().toString(); // 수정한 제목을 저장
                        String ModifyContent = edtMemoContent.getText().toString(); // 수정한 내용을 저장

                        if(ModifyTitle.equals("")){
                            ModifyTitle="제목 없음"; // 제목이 없을 경우 제목 없음으로 자동 저장
                        }

                        if(ModifyContent.trim().equals("")){ // 내용이 없으면 오류 메시지
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Memo.this);
                            text.setText("내용을 입력하세요.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }else{
                            // 메모 db 수정
                            myHelper.updateMemo(memoID, id, ModifyTitle, ModifyContent);
                            // dialog 종료시 메모 페이지를 다시 시작해주기
                            Intent intent = new Intent(getApplicationContext(),Memo.class);
                            intent.putExtra("ID1", id);
                            startActivity(intent);
                            finish();
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(Memo.this);
                            text.setText("메모가 수정되었습니다.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();

                            dialog.dismiss(); // dialog 종료.
                        }
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    } // dialog 종료.
                });



            }
        });

        // 메모 삭제 버튼
        btnMemoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: db 삭제
                AlertDialog.Builder dlg = new AlertDialog.Builder(Memo.this, R.style.AlertDialogTheme);
                dlg.setTitle("공지를 정말 삭제하시겠습니까?");

                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myHelper.deleteMemo(memoID, id); // 메모 삭제
                        // 메모 재시작
                        Intent intent = new Intent(getApplicationContext(),Memo.class);
                        intent.putExtra("ID1", id);
                        startActivity(intent);
                        finish();

                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Memo.this);
                        text.setText("메모가 삭제되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });

                // 삭제 취소
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(Memo.this);
                        text.setText("취소하였습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });

        memoLayout.addView(view);

    }

    // 하단바
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


    // 뒤로가기 두번 클릭시 앱 종료
    private long backpressedTime = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            LayoutInflater inflater = getLayoutInflater();

            View layout = inflater.inflate(R.layout.toastcustom,
                    (ViewGroup) findViewById(R.id.toastLayout));

            TextView text = layout.findViewById(R.id.toastText);

            Toast toast = new Toast(Memo.this);
            text.setText("\'뒤로가기\'를 한 번 더 누르시면 앱이 종료됩니다.");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }

    }
}
