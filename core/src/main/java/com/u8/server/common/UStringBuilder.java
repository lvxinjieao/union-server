package com.u8.server.common;

/**
 * 默认 StringBuilder的append方法， 如果参数为null，会把append一个字符串的null，这个在拼接字符串的时候，有些问题。有时候
 * 我们希望，在拼接的时候，如果参数是null，拼接一个空字符串，而不是一个字符串的"null"
 * Created by ant on 2017/2/25.
 */
public final class UStringBuilder {

    public static final String NULL_REPLACE_TYPE = "null";
    public static final String EMPTY_REPLACE_TYPE = "";

    private StringBuilder sb = new StringBuilder();

    private String nullReplaced;

    public UStringBuilder(){
        nullReplaced = EMPTY_REPLACE_TYPE;
    }

    public UStringBuilder append(CharSequence str){
        if(str == null){
            sb.append(nullReplaced);
            return this;
        }
        sb.append(str);
        return this;
    }

    public UStringBuilder append(int val){
        append(val+"");
        return this;
    }

    public UStringBuilder append(char val){
        append(val+"");
        return  this;
    }

    public UStringBuilder append(long val){
        append(val+"");
        return  this;
    }

    public UStringBuilder append(short val){
        append(val+"");
        return this;
    }

    public UStringBuilder append(double val){
        append(val+"");
        return this;
    }

    public UStringBuilder append(byte val){
        append(val+"");
        return this;
    }

    public UStringBuilder append(boolean val){
        if(val){
            append("true");
        }else{
            append("false");
        }
        return this;
    }

    public void setNullReplaceStr(String replaceStr){
        if(replaceStr == null){
            replaceStr = EMPTY_REPLACE_TYPE;
        }
        this.nullReplaced = replaceStr;
    }

    public String toString(){
        return sb.toString();
    }
}
