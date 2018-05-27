package ru.dude.simplepeg.entity;

public class CheckResult {

    private ResultType resultType;
    private String errorText;

    private CheckResult(){

    }

    public static CheckResult error(String errorText){
        CheckResult r = new CheckResult();
        r.resultType = ResultType.ERROR;
        r.errorText = errorText;
        return r;
    }

    public static CheckResult ok(){
        CheckResult r = new CheckResult();
        r.resultType = ResultType.OK;
        r.errorText = "";
        return r;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public String getErrorText() {
        return errorText;
    }

}
