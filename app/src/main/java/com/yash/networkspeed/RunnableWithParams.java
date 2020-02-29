package com.yash.networkspeed;

public abstract class RunnableWithParams implements Runnable {
    protected String str;
    protected int i;
    protected long l;
    protected double d;
    protected float f;
    protected boolean b;

    RunnableWithParams(String str){
        this.str=str;
    }
    RunnableWithParams(int i){
       this.i=i;
    }
    RunnableWithParams(long l){
        this.l=l;
    }
    RunnableWithParams(double d){
        this.d=d;
    }
    RunnableWithParams(float f){
       this.f=f;
    }
    RunnableWithParams(boolean b){
        this.b=b;
    }
}
