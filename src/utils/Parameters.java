/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package utils;


import java.util.ArrayList;

/**
 *
 * @author federico.pozzi
 */
public class Parameters {



      public static String host="149.132.178.210";
    public static String dbName="sarcasm";

    public static String mysql_user="macca";
    public static String mysql_pw="macca";


//    public static String table="sarcasm";
// public static String table="sarcasm_copy";
// public static String table="sarcasm_8000";

     public static String table="sarcasm_4000_1";
// public static String table="sarcasm_4000_2";

//      public static String table="polarity_train_2000_1";
// public static String table="polarity_train_2000_2";

    public static String[] mCategories = { "Pos", "Neg" };


    public static String rootFolder= "C:/Users/federico.pozzi/Desktop/";

    // List Example implement with ArrayList
    public static ArrayList<String> list = new ArrayList<String>() {
        {
            add("TAGSMILE");
            add("TAGNEU");
            add("TAGSAD");
            add("TAGMENTIONEDUSER");
            add("TAGLAUGHT");
            add("NEGEMOTION");
            add("POSEMOTION");
        }
    };

}
