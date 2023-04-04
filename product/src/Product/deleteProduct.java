package Product;

import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("all")

public class deleteProduct {

    //PRODUCT DELETE HELP
    void help(){
        System.out.println("""

                >> delete product using the following command
                                          	
                product delete productId1, productId2, ...
                product delete -c productCode1, productCode2, ...
                """);
    }

    void deleteProductByCode(Statement statement, String productCode){
        try {statement.executeUpdate("DELETE FROM PRODUCT WHERE code = '"+productCode+"';");} catch (SQLException e) {System.out.println(e.getMessage()); return;}
    }
    void deleteProductById(Statement statement, int productId){
        try {statement.executeUpdate("DELETE FROM PRODUCT WHERE id = "+productId+";");} catch (SQLException e) {System.out.println(e.getMessage()); return;}
    }

    void inputProcessing(Statement statement, String productCmd){
        String[] products = productCmd.trim().split("\\s+");
        String list = "";

        if(products[2].trim().equalsIgnoreCase("-c")){
            list += "code ";
            if(products.length == 4) list += products[3].replaceAll(",", " ");
            else{
                String[] productId = products[3].split(",");
                for(String id : productId){
                    list += id.trim() + " ";
                }
                for(int i =4; i<products.length;i++){
                    String[] furtherIds = products[i].split(",");
                    for(String id : furtherIds) list += id.trim() + " ";
                }
            }
        }else{
            list += "id ";
            if(products.length == 3) list += products[2].replaceAll(",", " ");
            else{
                String[] productId = products[2].split(",");
                for(String id : productId){
                    list += id.trim() + " ";
                }
                for(int i =3; i<products.length;i++){
                    String[] furtherIds = products[i].split(",");
                    for(String id : furtherIds) list += id.trim() + " ";
                }
            }
        }

        String[] productlist = list.split(" ");
        if(productlist[0].equalsIgnoreCase("code")){
            for(int i=1; i<productlist.length; i++){
                if(new listProduct().checkProductExistence(statement, productlist[i].trim())){
                    deleteProductByCode(statement, productlist[i].trim());
                    System.out.println(productlist[i]+": product deleted !!");
                }else{
                    System.out.println(productlist[i]+": product not found !!");
                }
            }
        }else{
            for(int i=1; i<productlist.length; i++){
                int productId = Integer.parseInt(productlist[i].trim());
                if(new listProduct().checkProductExistence(statement, productId)){
                    deleteProductById(statement, productId);
                    System.out.println(productlist[i]+": product deleted !!");
                }else{
                    System.out.println(productlist[i]+": product not found !!");
                }
            }
        }
    }
}
