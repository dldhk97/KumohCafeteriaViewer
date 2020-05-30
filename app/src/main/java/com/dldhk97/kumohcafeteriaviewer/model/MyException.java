package com.dldhk97.kumohcafeteriaviewer.model;

import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;

public class MyException extends Exception{
    private ExceptionType type;

    public MyException(ExceptionType type, String msg){
        super(msg);
        this.type = type;
    }

    public MyException(ExceptionType type, Exception e){
        super(e);
        this.type = type;
    }


    @Override
    public String getMessage() {
        return "[" + type.toString() + "]\n" + super.getMessage();
    }
}