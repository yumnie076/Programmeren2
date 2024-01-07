package main.java;

public class databaseConnect {
    static String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup;encrypt=true;trustServerCertificate=true;";
    static String gebruikersnaam = "salim";
    static String wachtwoord = "salim";

    public static String getUrl() {
        return url;
    }

    public static String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public static String GetPass() {
        return wachtwoord;
    }

}
