package Product;

import java.sql.Statement;
public class productMain {
    public void functionIdentification(Statement statement, String productCmdInput){

        if(productCmdInput.split(" ")[1].trim().equalsIgnoreCase("create"))
            new createProduct().inputProcessing(statement, productCmdInput);
        else if(productCmdInput.split(" ")[1].trim().equalsIgnoreCase("delete"))
            new deleteProduct().inputProcessing(statement, productCmdInput);
        else if(productCmdInput.split(" ")[1].trim().equalsIgnoreCase("list"))
            new listProduct().inputProcessing(statement, productCmdInput);
        else if(productCmdInput.split(" ")[1].trim().equalsIgnoreCase("edit"))
            new editProduct().inputProcessing(statement, productCmdInput);
        else if(productCmdInput.split(" ")[1].trim().equalsIgnoreCase("count"))
            new listProduct().productCount(statement);
    }
}
