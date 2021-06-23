package com.why.powerlistener.task;

import android.content.Context;
import android.os.Handler;

public abstract class Task implements Runnable {

    protected final Handler handler;

    protected final Context context;

    public Task(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void run() {
        this.execute( this.handler, this.context);
    }

    protected abstract void execute(Handler handler, Context context);
}
