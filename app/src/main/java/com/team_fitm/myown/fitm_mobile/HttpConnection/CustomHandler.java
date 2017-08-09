package com.team_fitm.myown.fitm_mobile.HttpConnection;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Myown on 2017-08-09.
 */

public class CustomHandler extends Handler {

    private String msg;

    public CustomHandler(){
        this.msg = null;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        this.msg = (String)msg.obj;
    }

    public String getMsg(){
        return this.msg;
    }
}
