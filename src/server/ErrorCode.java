package server;

public enum ErrorCode {
    INVALID_ACC ("Invalid account number"),
    INVALID_PASS ("Invalid password"),
    INVALID_NAME ("Invalid user name"),
    ERROR_DELETE_ACCOUNT ("Error while trying to delete the account"),
    INSUFFICIENT_BALANCE("User has insufficient balance"),
    INVALID_RECEIVER_ID("Invalid receiver account number"),
    ;
    private String msg;
    ErrorCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {return this.msg;}
}
