package com.graham.nofreeride.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by grahamherceg on 4/5/18.
 */

public class EditTextEnterEvent extends AppCompatEditText {

    private EditTextImeBackListener mOnImeBack;

    public EditTextEnterEvent(Context context) {
        super(context);
    }

    public EditTextEnterEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextEnterEvent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mOnImeBack != null)
                mOnImeBack.onImeBack(this, this.getText().toString());
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mOnImeBack = listener;
    }

}