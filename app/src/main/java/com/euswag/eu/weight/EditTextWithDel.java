package com.euswag.eu.weight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.euswag.eu.R;

/**
 * Created by ASUS on 2017/3/14.
 */

public class EditTextWithDel extends EditText implements View.OnFocusChangeListener,TextWatcher{

    //private final static String TAG = "EditTextWithDel";
    private Drawable imgAble;

    private boolean hasFoucs;
    private boolean isShow = false;

    public EditTextWithDel(Context context) {
        this(context,null);
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.editTextStyle);
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        imgAble = getCompoundDrawables()[2];
        if(imgAble == null) {
            imgAble = getResources().getDrawable(R.drawable.ic_highlight_off_gray);
        }
        imgAble.setBounds(0, 0, imgAble.getIntrinsicWidth(), imgAble.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                //getTotalPaddingRight()图标左边缘至控件右边缘的距离
                //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
                //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    //设置清除图标的显示与隐藏
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? imgAble : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        hasFoucs = b;
        if (hasFoucs) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if(hasFoucs){
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
