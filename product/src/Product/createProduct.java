package Product;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("all")

public class createProduct {

    //PRODUCT CREATE HELP
    void help(){
        System.out.println("""

                create product using the following command

                product create [code,name,unit,type,price,stock] [code,name,unit,type,price,stock] ...

                	code - text, min - 2 - 6, mandatory
                	name - text, min 3 - 30 char, mandatory
                	unit - text, kg/l/piece/combo, mandatory
                	type - text, between enumerated values, mandatory
                	price - number, mandatory
                	stock - number, default 0
                """);
    }

    void inputProcessing(Statement statement, String productCmd){

        if(productCmd.split(" ")[2].equalsIgnoreCase("help")){
            help();
            return;
        }

        String[] products = productCmd.split("[\\[\\]]+");


        //Spliting products and properties
        for(int i=1; i<products.length; i+=2){

            String product = products[i];

            //product properties
            String[] properties = product.split(",");

            //Trim spaces && converting string into lowercase
            for(int j=0;j<properties.length;j++){
                properties[j] = properties[j].trim().toLowerCase();
            }

            //checking for stock
            boolean isStockGiven = false;
            if(properties.length == 7) isStockGiven = true;

            //properties validation
            if(productInputValidation(properties[0], properties[1])){
                //Insert product into database

                //product create [code,name,unit,type,price,date,stock] [code,name,unit,type,price,date,stock] ...

                if(isStockGiven) addProduct(statement, properties[0], properties[1], properties[2], properties[3], Double.parseDouble(properties[4]), properties[5], Double.parseDouble(properties[6]));
                else addProduct(statement, properties[0], properties[1], properties[2], properties[3], Double.parseDouble(properties[4]), properties[5], 0);

            }else{
                return;
            }
        }
        if(products.length == 1) System.out.println("\n Product added successfully !!");
        else System.out.println("\n Products added successfully !!");
    }

    boolean productInputValidation(String code, String name){
        if (code.length() < 2 || code.length() > 6) {
            System.out.println("Template mismatch !\nProduct code length is not matching !!");
            return false;
        }
        if (name.length() < 3 || name.length() > 30) {
            System.out.println("Template mismatch !\nProduct name length is not matching !!");
            return false;
        }
        return true;
    }

    void addProduct(Statement statement, String productCode, String productName, String unit, String type, double price, String expDate, double stock) {
        try {
            String sql = "INSERT INTO product (code, name, unit, type, price, expdate, stock) VALUES " +
                    "('" + productCode + "', '" + productName + "', '" + unit + "', '" + type + "', " + price + ", '" + expDate + "', "+ stock + ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {System.out.println(e.getMessage()); return;}
    }

}

































