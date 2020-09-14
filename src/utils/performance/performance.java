package utils.performance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.write.WriteException;
import utils.Parameters;
import utils.Voting4Chart;



public class performance {

    double[] glob_precision,
    glob_recall,
    glob_f1;
    double glob_accuracy;
    int classes;

public performance(int classes){

this.classes=classes;

glob_precision=new double[classes];
glob_recall=new double[classes];
glob_f1=new double[classes];
glob_accuracy=0;
for(int i=0; i<classes; i++){
    glob_precision[i]=0;
    glob_recall[i]=0;
    glob_f1[i]=0;
    
}


}


public void globalPerformance(double precision,double recall,double f1,int clas_index){

//  int i=Arrays.asList(Parameters.mCategories).indexOf(label);


  glob_precision[clas_index]+=precision;
  glob_recall[clas_index]+=recall;
  glob_f1[clas_index]+=f1;

}

public void globalAccuracy(double acc){
    glob_accuracy+=acc;
}

public void printGlobPerformance(int num_folds, String file)
        throws IOException{

    BufferedWriter BW = new BufferedWriter(new FileWriter(file, false));

   // BW.write(getGlobPerformance(num_folds));

    BW.flush();
    BW.close();


}

public String getGlobPerformance(int num_folds,int classes){


    String perf="";
    for (int cat = 0; cat < classes; cat++) {
        perf += "\n" + Integer.toString(cat+1)+
                "\n\tPrecision:\t"+ glob_precision[cat]/num_folds+
                "\n\tRecall:\t"+ glob_recall[cat]/num_folds+
                "\n\tF1:\t"+ glob_f1[cat]/num_folds;
    }


    perf += "\nGLOBAL" +
            "\n\tAccuracy:\t"+ glob_accuracy/num_folds +"\n\n" +
            "----------------------------------------------------\n";

return perf;
}

public String RawGlobPerformance(int num_folds) throws IOException{

    String out = "";
    for (int i = 0; i < classes; i++) {
        out += (glob_precision[i]/num_folds)+"\n"+(glob_recall[i]/num_folds) +"\n"+(glob_f1[i]/num_folds) +"\n";
        

    }
    out+=glob_accuracy/num_folds;
    

 return out;

}

public void printGlobPerformance(int num_folds, String file, String model, Voting4Chart excel, int col, int row)
        throws IOException{
		/*
    excel.setRowOffset(row);
    try {
        excel.writeString(model, col);
    } catch (WriteException ex) {
        Logger.getLogger(performance.class.getName()).log(Level.SEVERE, null, ex);
    }

    excel.setRowOffset(++row);

    BufferedWriter BW = new BufferedWriter(new FileWriter(file, false));
    String perf="";
    for (int cat = 0; cat < Parameters.mCategories.length; cat++) {
        perf += "\n" + Parameters.mCategories[cat].toUpperCase()+
                "\n\tPrecision:\t"+ glob_precision[cat]/num_folds+
                "\n\tRecall:\t"+ glob_recall[cat]/num_folds+
                "\n\tF1:\t"+ glob_f1[cat]/num_folds;

        try {
            excel.setRowOffset(++row);
            excel.writeString(Parameters.mCategories[cat].toUpperCase(), col);
            excel.setRowOffset(++row);
            excel.writeNumber(glob_precision[cat]/num_folds,col);
            excel.setRowOffset(++row);
            excel.writeNumber(glob_recall[cat]/num_folds,col);
            excel.setRowOffset(++row);
            excel.writeNumber(glob_f1[cat]/num_folds,col);
        } catch (WriteException ex) {
            Logger.getLogger(performance.class.getName()).log(Level.SEVERE, null, ex);
        }

        //             excel.setRowOffset(row+4);
    }

    excel.setRowOffset(++row);

    try {
        excel.writeString("GLOBAL", col);
    } catch (WriteException ex) {
        Logger.getLogger(performance.class.getName()).log(Level.SEVERE, null, ex);
    }

    excel.setRowOffset(++row);

    try {
     //   excel.writeNumber(glob_accuracy/num_folds,col);
    } catch (WriteException ex) {
        Logger.getLogger(performance.class.getName()).log(Level.SEVERE, null, ex);
    }

 //   perf += "\nGLOBAL" +
   //         "\n\tAccuracy:\t"+ glob_accuracy/num_folds +"\n\n" +
     //       "----------------------------------------------------\n";
    BW.write(perf);

    BW.flush();
    BW.close();




    System.out.println(perf);
    
    */
}


public double getGlob_accuracy() {
    return glob_accuracy;
}



public void printPMeasures4Voting(int num_folds) throws IOException {

    BufferedWriter BW2 = new BufferedWriter(new FileWriter(Parameters.rootFolder+"pMeasures.txt", true));

    for (int cat = 0; cat < Parameters.mCategories.length; cat++) {


        BW2.append(Double.toString(glob_precision[cat]/num_folds)+"\n");
        BW2.append(Double.toString(glob_recall[cat]/num_folds)+"\n");
        BW2.append(Double.toString(glob_f1[cat]/num_folds)+"\n");


    }
  //  BW2.append(Double.toString(glob_accuracy/num_folds)+"\n\n");
    BW2.flush();
    BW2.close();

}







}
