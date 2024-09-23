package com.example.a3_termproject_steam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class taskActivity extends Activity {
    // 필요한 위젯변수 선언
    TextView txtTaskTitle2, txtTaskContent, txtTaskExpirationDate;
    Button btnMakeComment;
    ImageButton btnMakeToDo;
    LinearLayout layout_ToDo, layout_Comment;
    EditText edtComment;
    String id, role, title, content, sdate, edate;

    int gid, tid;
    // 넘겨받은 값들을 가져오는 Intent 선언
    Intent data;
    View dialogView;
    // 사용할 DB Helper 선언
    MyDatabaseHelper myDB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_task);
        // 넘겨받은 값들 변수에 저장
        Intent data = getIntent();
        gid = data.getIntExtra("GID1",0);
        // 사용할 위젯들 연결
        btnMakeToDo = (ImageButton) findViewById(R.id.btnMakeToDo);
        btnMakeComment = (Button) findViewById(R.id.btnMakeComment);
        edtComment = (EditText) findViewById(R.id.edtComment);
        // ToDo만들기 버튼을 클릭하면
        btnMakeToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 만들어놓은 대화상자를 불러와서 보여준다
                dialogView = (View) View.inflate(taskActivity.this, R.layout.make_todo, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(taskActivity.this, R.style.AlertDialogTheme);
                dlg.setView(dialogView);
                // 등록 버튼을 클릭하면
                dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 입력한 정보들의 내용을 가지는 ToDo리스트 하나 생성
                        EditText edtTodoName = dialogView.findViewById(R.id.edtTodoName);
                        EditText edtTodoInCharge = dialogView.findViewById(R.id.edtTodoInCharge);
                        // 입력한 값들 가져옴
                        String todoname = edtTodoName.getText().toString();
                        String todoperson = edtTodoInCharge.getText().toString();
                        // 입력받은 값이 없는 경우 처리 코드
                        if(todoname.trim().equals("") || todoperson.trim().equals("")){
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(taskActivity.this);
                            text.setText("ToDo 제목과 담당자를 입력하세요");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }
                        // 올바른 입력인 경우
                        else{
                            // DB에 ToDo 데이터를 저장 후
                            myDB.addTodo(tid, todoname, todoperson, 0, gid);
                            // 등록이 완료되었다고 토스트 메세지 출력 후
                            LayoutInflater inflater = getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toastcustom,
                                    (ViewGroup) findViewById(R.id.toastLayout));

                            TextView text = layout.findViewById(R.id.toastText);

                            Toast toast = new Toast(taskActivity.this);
                            text.setText("ToDo가 등록되었습니다.");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            // 현재 액티비티를 재시작
                            Intent intent = new Intent(getApplicationContext(), taskActivity.class);
                            intent.putExtra("ID",id);
                            intent.putExtra("ROLE",role);
                            intent.putExtra("GID",gid);
                            intent.putExtra("TID", tid);
                            intent.putExtra("TTITLE", title);
                            intent.putExtra("TCONTENT", content);
                            intent.putExtra("SDATE", sdate);
                            intent.putExtra("EDATE", edate);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                // 취소버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(taskActivity.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // Comment만들기 버튼을 클릭하면 작동하는 코드
        btnMakeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력받은 코멘트를 가져와서
                String cmt = edtComment.getText().toString();
                // 입력받은 값이 있는지 확인
                if(cmt.trim().equals("")){
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(taskActivity.this);
                    text.setText("Comment를 입력해주세요.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
                // 입력받은 값이 있으면
                else{
                    // DB에 저장 후
                    myDB.addComment(tid, id, cmt, gid);
                    // 등록이 완료되었다고 토스트 메세지 출력 후
                    LayoutInflater inflater = getLayoutInflater();

                    View layout = inflater.inflate(R.layout.toastcustom,
                            (ViewGroup) findViewById(R.id.toastLayout));

                    TextView text = layout.findViewById(R.id.toastText);

                    Toast toast = new Toast(taskActivity.this);
                    text.setText("Comment가 등록되었습니다.");
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    // 현재 액티비티 재시작
                    Intent intent = new Intent(getApplicationContext(), taskActivity.class);
                    intent.putExtra("ID",id);
                    intent.putExtra("ROLE",role);
                    intent.putExtra("GID",gid);
                    intent.putExtra("TID", tid);
                    intent.putExtra("TTITLE", title);
                    intent.putExtra("TCONTENT", content);
                    intent.putExtra("SDATE", sdate);
                    intent.putExtra("EDATE", edate);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Task의 이름과 내용, 마감기한을 지정할 위젯변수들 연결
        txtTaskTitle2 = (TextView) findViewById(R.id.txtTaskTitle2);
        txtTaskContent = (TextView) findViewById(R.id.txtTaskContent);
        txtTaskExpirationDate = (TextView) findViewById(R.id.txtTaskExpirationDate);
        // ToDo와 Comment를 보여줄 레이아웃 연결
        layout_ToDo = (LinearLayout) findViewById(R.id.layout_ToDo);
        layout_Comment = (LinearLayout) findViewById(R.id.layout_Comment);
        // 가장 먼저 레이아웃에 있는 모든 내용 삭제
        layout_ToDo.removeAllViews();
        layout_Comment.removeAllViews();
        // DB 객체 선언
        myDB = new MyDatabaseHelper(this);
        // 넘겨받은 값들 변수에 저장
        data = getIntent();
        id = data.getStringExtra("ID");
        gid = data.getIntExtra("GID2",0);
        role = data.getStringExtra("ROLE");
        tid = data.getIntExtra("TID", 0);
        title = data.getStringExtra("TTITLE");
        content = data.getStringExtra("TCONTENT");
        sdate = data.getStringExtra("SDATE");
        edate = data.getStringExtra("EDATE");
        // 역할이 팀장인 경우만 ToDo만들기 버튼이 보이도록
        if(role.equals("팀장")){
            btnMakeToDo.setVisibility(View.VISIBLE);
        }
        // 현재 사용자의 이름
        Cursor namecursor = myDB.customerNames(id);
        namecursor.moveToNext();
        String cname = namecursor.getString(0);
        // Task이름, 내용, 기간 지정
        txtTaskTitle2.setText(title);
        txtTaskContent.setText(content);
        txtTaskExpirationDate.setText(sdate+" ~ "+edate);
        // 해당 Task의 ToDo리스트 데이터를 불러옴
        Cursor todocursor = myDB.getToDo(tid);
        while(todocursor.moveToNext()){
            // 각 ToDo들의 데이터들을 이용하여 카드뷰를 만들어서 하나씩 레이아웃에 추가하는 과정
            int tdid = todocursor.getInt(0);
            String tdname = todocursor.getString(1);
            String tdperson = todocursor.getString(2);
            int tdisdone = todocursor.getInt(3);
            // 만들어놓은 카드뷰 추가 함수 이용
            addCardToDo(tdid, tdname, tdperson, tdisdone);
        }
        // 코멘트도 ToDo와 동일하게 DB에서 정보들을 불러온 뒤
        Cursor commentcursor = myDB.getComment(tid);
        while(commentcursor.moveToNext()){
            // 각 Comment들에 대해 카드뷰를 만들어 추가
            int cmtid = commentcursor.getInt(0);
            String custid = commentcursor.getString(1);
            String cmt = commentcursor.getString(2);

            Cursor custnamecursor = myDB.customerNames(custid);
            custnamecursor.moveToNext();

            String cmtwriter = custnamecursor.getString(0);
            // 코멘트 카드뷰를 추가하는 함수
            addCardComment(cmtid, cmtwriter, cmt, cname);
        }
    }
    // ToDo카드뷰를 만들어 레이아웃에 추가하는 함수, 파라미터로 ToDo와 관련된 데이터들을 입력받음
    private void addCardToDo(int tdid, String tdname, String tdperson, int tdisdone) {
        // 만들어놓은 카드뷰를 불러옴
        final View view = getLayoutInflater().inflate(R.layout.card_todo, null);
        // 카드뷰의 위젯들을 연결
        TextView name = view.findViewById(R.id.txtTodoNameCard);
        TextView person = view.findViewById(R.id.txtTodoPerson);
        CheckBox chck = view.findViewById(R.id.chckTodo);
        ImageButton btn = view.findViewById(R.id.btnDeleteTodo);
        // 삭제 버튼은 팀장만 이용가능하도록 visible 속성 조작
        btn.setVisibility(View.GONE);
        if(role.equals("팀장")){
            btn.setVisibility(View.VISIBLE);
            chck.setEnabled(true);
        }
        // ToDo리스트의 내용들 지정
        name.setText(tdname);
        person.setText(tdperson);
        // 해당 ToDo를 완료했는지 여부를 체크박스로 표시하는 과정
        if(tdisdone==1){
            chck.setChecked(true);
            // 취소선 표시
            name.setPaintFlags(chck.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            name.setTextColor(Color.rgb(170,170,170));
        }
        else{
            chck.setChecked(false);
            // 취소선 해제
            name.setPaintFlags(chck.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            name.setTextColor(Color.BLACK);
        }
        // 체크박스 표시가 변경되면
        chck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 체크되면 표시를 체크표시로 변경하고 DB의 ISDONE 속성 값을 1로 변경
                if(chck.isChecked()==true){
                    chck.setChecked(true);
                    // 취소선 표시
                    name.setPaintFlags(chck.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    name.setTextColor(Color.rgb(170,170,170));
                    myDB.updateTodoDone(tdid, 1);
                }
                // 체크가 해제되면 체크표시 해제 후 DB의 ISDONE 속성 값을 0으로 변경
                else{
                    chck.setChecked(false);
                    // 취소선 해제
                    name.setPaintFlags(chck.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    name.setTextColor(Color.BLACK);
                    myDB.updateTodoDone(tdid, 0);
                }
            }
        });
        // 삭제버튼을 클릭하면
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 정말로 삭제할 것인지 대화상자를 이용하여 다시 한번 확인 후
                AlertDialog.Builder dlg = new AlertDialog.Builder(taskActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle("정말로 삭제하시겠습니까?");
                // 삭제버튼을 클릭하면
                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // DB에서 삭제 후 페이지 재시작
                        myDB.deleteTodo(tdid);
                        Intent intent = new Intent(getApplicationContext(), taskActivity.class);
                        intent.putExtra("ID",id);
                        intent.putExtra("ROLE",role);
                        intent.putExtra("GID",gid);
                        intent.putExtra("TID", tid);
                        intent.putExtra("TTITLE", title);
                        intent.putExtra("TCONTENT", content);
                        intent.putExtra("SDATE", sdate);
                        intent.putExtra("EDATE", edate);
                        startActivity(intent);
                        finish();
                    }
                });
                // 취소버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(taskActivity.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // 최종적으로 완성된 ToDo를 레이아웃에 추가
        layout_ToDo.addView(view);
    }
    // Comment 카드뷰를 추가하는 함수, 파라미터로 Comment에 대한 정보들을 입력받음
    private void addCardComment(int cmtid, String cmtwriter, String cmt, String cname) {
        // 만들어놓은 카드뷰를 불러온다.
        final View view = getLayoutInflater().inflate(R.layout.card_comment, null);
        // 카드뷰의 위젯들 연결
        TextView name = view.findViewById(R.id.txtCommentCard);
        Button btn = view.findViewById(R.id.btnDeleteComment);
        TextView wirter = view.findViewById(R.id.txtCommentWriter2);
        // 삭제 버튼은 본인만 사용할 수 있도록 하기 위하여 일단은 visible속성을 gone으로 준다.
        btn.setVisibility(View.GONE);
        // 현재 사용자 이름과 작성자의 이름이 일치하면 삭제버튼을 이용할 수 있도록 다시 visible 속성 변경
        if(cname.equals(cmtwriter)){
            btn.setVisibility(View.VISIBLE);
        }
        // 코멘트 내용을 텍스트 뷰에 적용한다.
        wirter.setText(cmtwriter+": ");
        name.setText(cmt);
        // 삭제 버튼을 클릭하면
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 정말로 삭제할 것인지 다시 한번 더 확인 후
                AlertDialog.Builder dlg = new AlertDialog.Builder(taskActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle("정말로 삭제하시겠습니까?");
                // 삭제버튼을 클릭하면
                dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // DB에서 삭제 후
                        myDB.deleteComment(cmtid);
                        // 현재 액티비티 재시작
                        Intent intent = new Intent(getApplicationContext(), taskActivity.class);
                        intent.putExtra("ID",id);
                        intent.putExtra("ROLE",role);
                        intent.putExtra("GID",gid);
                        intent.putExtra("TID", tid);
                        intent.putExtra("TTITLE", title);
                        intent.putExtra("TCONTENT", content);
                        intent.putExtra("SDATE", sdate);
                        intent.putExtra("EDATE", edate);
                        startActivity(intent);
                        finish();
                    }
                });
                // 취소 버튼을 클릭하면
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 취소되었다는 토스트 메세지 출력
                        LayoutInflater inflater = getLayoutInflater();

                        View layout = inflater.inflate(R.layout.toastcustom,
                                (ViewGroup) findViewById(R.id.toastLayout));

                        TextView text = layout.findViewById(R.id.toastText);

                        Toast toast = new Toast(taskActivity.this);
                        text.setText("취소되었습니다.");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                });
                dlg.show();
            }
        });
        // 최종적으로 완성된 카드뷰를 레이아웃에 추가
        layout_Comment.addView(view);


    }
}
