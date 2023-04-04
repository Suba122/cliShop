import java.sql.*;
import java.util.Scanner;
import Product.*;

@SuppressWarnings("all")

public class testing1 {
    static Scanner sc = new Scanner(System.in);

    static String url = "jdbc:postgresql://localhost:5432/clishop";
    static String username = "postgres";
    static String password = "suba2002";
    static Statement statement;

    public static void main(String[] args) {

        //Initialing database
        database();

        while(true){

            System.out.print("> ");
            String inputCommand = sc.nextLine();

            if(inputCommand.equalsIgnoreCase("exit")) return;

            //Accepting secondLine (values)
            String[] listPurposeChecking = inputCommand.split(" ");
            if(inputCommand.split(" ").length == 2 && !listPurposeChecking[1].trim().equalsIgnoreCase("list") && !listPurposeChecking[1].trim().equalsIgnoreCase("count")){
                String values = sc.nextLine();
                if(values.length()==0){System.out.println("No values entered!"); continue;}
                inputCommand = inputCommand +" "+ values;
            }

            if(inputCommand.startsWith("product")){
                new productMain().functionIdentification(statement, inputCommand);
            }
        }
    }

    public static void database(){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
