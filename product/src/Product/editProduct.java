package Product;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")

public class editProduct {

    //PRODUCT EDIT HELP
    void help(){
        System.out.println("""
                Syntax :
                product edit [id:5, name:milk], [id:4, name:cheese], ...
                
                >> Edit product using following template. Copy the product data from the list, edit the attribute values.\s
                >> id: <id - 6>, name: <name-edited>, unitCode: <unitCode>,  type: <type>, price: <price>
                                
                >> You can also restrict the product data by editable attributes. Id attribute is mandatory for all the edit operation.
                >> id: <id - 6>, name: <name-edited>, unitCode: <unitCode-edited>
                                
                >> You can not give empty or null values to the mandatory attributes.
                >> id: <id - 6>, name: , unitCode: null
                
                 	id	 - number, mandatory
                	name - text, min 3 - 30 char, mandatory
                	unitCode - text, kg/l/piece/combo, mandatory
                	type - text, between enumerated values, mandatory\s
                	costPrice - numeric, mandatory
        """);
    }

    void inputProcessing(Statement statement, String editCmd){
        //Help
        if(editCmd.split(" ")[2].equalsIgnoreCase("help")){help();return;}

        //Pattern checking
        String editPropertyPattern = "\\[\\s*id\\s*:\\s*\\d+\\s*,\\s*\\w+\\s*:\\s*[\\w\\s]+\\s*(,\\s*\\w+\\s*:\\s*[\\w\\s]+\\s*)*\\]";
        String inputPattern = "product\\s+edit\\s+(" + editPropertyPattern + "(\\s*,\\s*" + editPropertyPattern + ")*)";
        if (!Pattern.matches(inputPattern, editCmd)) {System.out.println("Invalid command!\nUse > product edit help"); return;}

        //Parameters needed from inputProcessing
        int id;
        String code, name, unit, type, expdate;
        double price, stock;

        //Splitting by [] to separate each product
        String[] properties = editCmd.split("[\\[\\]]+");

        //processing attributes and values inside the []
        for(int i=1; i<properties.length; i+=2){

            //Getting id
            Pattern idPattern = Pattern.compile("\\bid\\s*:\\s*(\\d+)\\b"); Matcher idMatcher = idPattern.matcher(properties[i]);
            if (!idMatcher.find()) {System.out.println("Invalid command! Product Id is missing");return;}
            id = Integer.parseInt(idMatcher.group(1));

            //Id validation
            if(!new listProduct().checkProductExistence(statement, id)){System.out.println("Product doesn't exist"); return;}

            //Splitting for single property
            String[] property = properties[i].split(",");

            for(String attribute_value:property){
                String Query = "UPDATE product SET ";
                String attribute = attribute_value.split(":")[0].trim();
                String value = attribute_value.split(":")[1].trim();

                if(attribute.equals("id")) continue;
                else if(attribute.equals("code")) Query += "code='"+value+"'";
                else if(attribute.equals("name")) Query += "name='"+value+"'";
                else if(attribute.equals("unit")) Query += "unit='"+value+"'";
                else if(attribute.equals("type")) Query += "type='"+value+"'";
                else if(attribute.equals("expdate")) Query += "expdate='"+value+"'";
                else {
                    try {
                        if (attribute.equals("price")) Query += "price="+Double.parseDouble(value);
                        else if (attribute.equals("stock")) Query += "stock="+Double.parseDouble(value);
                        else {System.out.println(attribute + ": Invalid 'attribute' to edit");break;}
                    }catch(NumberFormatException e) {
                        System.out.println("Invalid command! Wrong 'data type' found\nUse > product edit help");
                    }
                }
                Query+= "WHERE id="+id;
                try{statement.executeUpdate(Query);} catch(SQLException e){System.out.println(e.getMessage()); return;}
            }
        }
        if(properties.length<=1) System.out.println("Product detail updated successfully !!");
        else System.out.println("Products detail updated successfully !!");
    }
}
