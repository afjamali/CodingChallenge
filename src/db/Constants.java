package db;

final class Constants {
    protected static final String DBurl = "jdbc:mysql://localhost:3306/philosophy";
    protected static final String DBdriver = "com.mysql.jdbc.Driver";
    protected static final String DBuser = "test";
    protected static final String DBpassword = "test123";

    private Constants()	{
        throw new AssertionError();
    }
}
