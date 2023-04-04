package Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class listProduct {
    //PRODUCT LIST HELP
    void help(){
        System.out.println("""
                List product with the following options
                     product list - will list all the products default to maximum up-to 20 products
                     product list -p 10 - pageable list shows 10 products as default
                     product list -p 10 3 - pageable list shows 10 products in 3rd page, ie., product from 21 to 30
                     product list -s searchText - search the product with the given search text in all the searchable attributes
                     product list -s <attr>: searchText - search the product with the given search text in all the given attribute
                     product list -s <attr>: searchText -p 10 - pageable list shows 10 products in 6th page with the given search text in the given attribute
                     product list -s <attr>: searchText -p 10 6 - pageable list shows 10 products in 6th page with the given search text in the given attribute
                """);
    }
    void productCount(Statement statement){
        String query = "SELECT COUNT(*) FROM product AS count";
        try {
            ResultSet countResultSet = statement.executeQuery(query);
            countResultSet.next();
            System.out.println("Product count: "+countResultSet.getInt("count"));
        } catch (SQLException e) {System.out.println(e.getMessage());}
    }
    int largeProductId(Statement statement){
        try{
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM product");
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
        }catch(SQLException e){}
        return 0;
    }
    //Product existence checking by product id
    boolean checkProductExistence(Statement statement, int productId){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM product WHERE id = " + productId + ";");
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Product existence checking by product code
    boolean checkProductExistence(Statement statement, String productCode){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM product WHERE code = '" + productCode + "';");
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    void inputProcessing(Statement statement, String listCmd){
        int pageNumber = 0, pageSize = 0;
        String searchAttribute = null, searchValue = null;
        String[] listProperties = listCmd.split(" ");

        if (listProperties.length == 3 && listProperties[2].trim().equalsIgnoreCase("help")) {
            help();
            return;
        }

        if(listProperties.length == 2) pageSize = 20;
        else if (listProperties[2].trim().equalsIgnoreCase("-p")) {
            //product list -p 10 - pageable list shows 10 products as default
            if (listProperties.length == 4) {
                try {
                    pageSize = Integer.parseInt(listProperties[3].trim());
                } catch (NumberFormatException e) {
                    System.out.println(listCmd + ": command not found\nuse product list help");
                    return;
                }
            }
            //product list -p 10 3 - pageable list shows 10 products in 3rd page, i.e., product from 21 to 30
            else if(listProperties.length == 5) {
                try {
                    pageSize = Integer.parseInt(listProperties[3].trim());
                    pageNumber = Integer.parseInt(listProperties[4].trim());
                } catch (NumberFormatException e) {
                    System.out.println(listCmd + ": command not found\nuse product list help");
                    return;
                }
            }
            else{
                System.out.println(listCmd+": command not found\nuse product list help");
                return;
            }
        }

        else if (listProperties[2].equalsIgnoreCase("-s")){
            //product list -s searchText - search the product with the given search text in all the searchable attributes

            int pagingIndex = 0;
            //Setting pageSize and pageNumber
            if(listCmd.contains("-p")){
                pagingIndex = listCmd.indexOf("-p");
                String[] pageValues = listCmd.substring(pagingIndex + 2).trim().split(" ");
                if(pageValues.length >= 1){
                    try{
                        pageSize = Integer.parseInt(pageValues[0]);
                    } catch(NumberFormatException e){
                        System.out.println(listCmd + ": command not found\nuse product list help");
                        return;
                    }
                }
                if(pageValues.length == 2){
                    try{
                        pageNumber = Integer.parseInt(pageValues[1]);
                    } catch(NumberFormatException e){
                        System.out.println(listCmd + ": command not found\nuse product list help");
                        return;
                    }
                }
            }
            if(listCmd.contains(":")){
                searchAttribute = listCmd.substring(listCmd.indexOf("-s") + 2, listCmd.indexOf(":")).trim();
                if(pagingIndex!=0) searchValue = listCmd.substring(listCmd.indexOf(":")+1, listCmd.indexOf("-p")).trim();
                else searchValue = listCmd.substring(listCmd.indexOf(":")+1).trim();
            }else{
                if(pagingIndex!=0) searchValue = listCmd.substring(listCmd.indexOf("-s") + 2, listCmd.indexOf("-p"));
                else searchValue = listCmd.substring(listCmd.indexOf("-s") + 2).trim();
            }
        }
        if(searchAttribute != null && !searchAttribute.trim().equalsIgnoreCase("code") && !searchAttribute.trim().equalsIgnoreCase("name") && !searchAttribute.trim().equalsIgnoreCase("type") && !searchAttribute.trim().equalsIgnoreCase("unit")) System.out.println("Invalid search attribute");
        else productList(statement, pageSize, pageNumber, searchAttribute, searchValue);
    }

    void productList(Statement statement, int pageSize, int pageNumber, String searchAttribute, String searchValue){
        String Query = "SELECT * FROM PRODUCT";
        int offset, orderByFlag = 0;
        ResultSet resultSet;

        if (searchValue != null && !searchValue.isEmpty()){
            searchValue = searchValue.trim();
            if (searchAttribute == null) Query += " WHERE name LIKE '%" + searchValue + "%' OR code LIKE '%" + searchValue + "%' OR type LIKE '%" + searchValue + "%'";
            else if (!searchAttribute.isEmpty()) Query += " WHERE " + searchAttribute + " LIKE '%" + searchValue + "%'";

            Query += " ORDER BY id";
            orderByFlag = 1;

            try {
                resultSet = statement.executeQuery(Query);
                if (resultSetLength(resultSet) == 0) {
                    System.out.println("Given search text not found !!");
                    return;
                }
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (pageSize > 0){
            if(orderByFlag == 0) Query += " ORDER BY id";

            String queryWithoutPagination = Query;

            if (pageNumber == 0) {
                Query += " LIMIT " + pageSize;
            } else if (pageNumber > 0) {
                offset = (pageNumber - 1) * pageSize;
                Query += " LIMIT " + pageSize + " OFFSET " + offset;

                try{
                    resultSet = statement.executeQuery(Query);

                    int resultSetCount = resultSetLength(resultSet);
                    if(resultSetCount == 0){
                        int pageCount = 1;

                        resultSet = statement.executeQuery(queryWithoutPagination);
                        resultSetCount = resultSetLength(resultSet);

                        if(resultSetCount>pageSize){
                            if(resultSetCount%pageSize == 0) pageCount = resultSetCount/pageSize;
                            else pageCount = (resultSetCount/pageSize) + 1;
                        }
                        System.out.println("Requested page doesn't exist !\nExisting page count with given pagination: " + pageCount);
                        return;
                    }
                }catch(SQLException e){throw new RuntimeException(e);}
            }
        }

        System.out.printf("%-5s %-10s %-20s %-10s %-15s %10s %10s\n", "id", "code", "name", "unit", "type", "price", "stock");
        try{
            resultSet = statement.executeQuery(Query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String name = resultSet.getString("name");
                String unit = resultSet.getString("unit");
                String type = resultSet.getString("type");
                float price = resultSet.getFloat("price");
                float stock = resultSet.getFloat("stock");
                System.out.printf("%-5s %-10s %-20s %-10s %-15s %10s %10s\n", id, code, name, unit, type, price, stock);
            }
        }catch(SQLException e){throw new RuntimeException(e);}
    }


    int resultSetLength(ResultSet resultSet){
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
