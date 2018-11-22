package com.treefinance.saas.grapserver.common.exception;

/**
 * @author haojiahong on 2017/8/31.
 */
public class AppIdUncheckException extends RuntimeException {

    private static final long serialVersionUID = 1685701930804628971L;

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    private int mark;

    public AppIdUncheckException(String message) {
        super(message);
    }

    public AppIdUncheckException(String message, int mark) {
        super(message);
        this.mark = mark;
    }

}
