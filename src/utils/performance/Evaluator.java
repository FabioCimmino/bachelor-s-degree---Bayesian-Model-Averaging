package utils.performance;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Evaluator {



    //	public static void main(String args[]){
    //		int counterNEG[] = {995,0,325};
    //		int counterPOS[] = {323,0,600};
    //		evaluatePerformance(counterNEG, counterPOS,null);
    //	}

    /*
     * Valuta 	precision, recall per le classi neg e pos
     * 			e accuratezza globale
     * counterNeg = risultati sul dataset negativo
     * counterPos = risultati sul dataset positivo
     * Elementi in counter*:
     * 		# frasi neg,
     * 		# frasi neu,
     * 		# frasi pos
     * */
    public static String evaluatePerformance(int counterNeg[], int counterPos[],
            performance per, String path) throws IOException{

        int N = counterNeg[0]+counterNeg[1]+counterNeg[2];		//totale negative
        int P = counterPos[0]+counterPos[1]+counterPos[2];		//totale positive

        int TP = counterPos[2];
        int TN = counterNeg[0];
        int FN = counterPos[0];
        int FP = counterNeg[2];

        double precision_neg, recall_neg, f1_neg, accuracy;
        double precision_pos, recall_pos, f1_pos;



        String performance = "REAL CASES:\t\tNEG("+N+")\tPOS("+P+")\n"
                + "PREDICTED|\tNEG:\t"+ TN + "\t\t"+ FN+"" +
                "\n\t|\tPOS:\t"+ FP +"\t\t"+ TP+"\n";

        //NEGATIVE
        precision_neg = (double)TN/(TN+FN);
        if(Double.isNaN(precision_neg))  precision_neg=0;

        recall_neg = (double)TN/N;
        if(Double.isNaN(recall_neg))  recall_neg=0;

        f1_neg = (2*precision_neg*recall_neg)/(precision_neg+recall_neg);
        if(Double.isNaN(f1_neg))  f1_neg=0;

        performance += ("NEGATIVE" +
                "\n\tPrecision:\t"+ precision_neg+
                "\n\tRecall:\t"+ recall_neg+
                "\n\tF1:\t"+ f1_neg);

        per.globalPerformance(precision_neg, recall_neg, f1_neg, 0);

        //POSITIVE
        precision_pos = (double)TP/(TP+FP);
        if(Double.isNaN(precision_pos))  precision_pos=0;

        recall_pos = (double)TP/P;
        if(Double.isNaN(recall_pos))  recall_pos=0;

        f1_pos = (2*precision_pos*recall_pos)/(precision_pos+recall_pos);
        if(Double.isNaN(f1_pos))  f1_pos=0;


        performance += ("\nPOSITIVE" +
                "\n\tPrecision:\t"+ precision_pos+
                "\n\tRecall:\t"+ recall_pos+
                "\n\tF1:\t"+ f1_pos);

        per.globalPerformance(precision_pos, recall_pos, f1_pos, 1);

        //GLOBAL
        accuracy = (double)(TP+TN)/(P+N);
        if(Double.isNaN(accuracy))  accuracy=0;

        performance += ("\nGLOBAL" +
                "\n\tAccuracy:\t"+ accuracy +"\n\n");

      //  per.globalAccuracy(accuracy);

//        System.out.println(performance);


        if(path != null){
            BufferedWriter BW2 = new BufferedWriter(new FileWriter(path, false));
            BW2.write(Double.toString(precision_neg));
            BW2.newLine();
            BW2.write(Double.toString(recall_neg));
            BW2.newLine();
            BW2.write(Double.toString(f1_neg));
            BW2.newLine();
            BW2.write(Double.toString(precision_pos));
            BW2.newLine();
            BW2.write(Double.toString(recall_pos));
            BW2.newLine();
            BW2.write(Double.toString(f1_pos));
            BW2.newLine();
            BW2.write(Double.toString(accuracy));

            BW2.flush();
            BW2.close();
        }


        return performance;

    }


     public static void evaluatePerformance(int matrixPol[][], int classes,
            performance per, String path, double epsilon) throws IOException{
				
    	 int [] total_neg_pos= new int [classes];
    	 
    	 
    	 double [] prec= new double [classes];
    	 double [] recall= new double [classes];
    	 double [] f_measure= new double [classes];
    	 double  accuracy= 0.0;
    	 double cont_tot=0;
    	 
    	 // PRECISION
    	 double [] fp=new double [classes];
    	 for (int i=0; i<classes; i++) {
    		 for (int j=0; j<classes; j++) {
    			 cont_tot+=matrixPol[i][j];
    			 if(i==j)
    				 continue;
    			 else
    				 fp[j]+=matrixPol[i][j];
    			 
    			 
    			 
    		 }
    	 }
    	 
    	 
    	 
    	 for(int i=0; i<classes; i++) {
    		 prec[i]= matrixPol[i][i]/(matrixPol[i][i]+fp[i]);
    		 if(Double.isNaN(prec[i]))  prec[i]=0;
    		 if(prec[i] == 0.0) prec[i] = epsilon;
    	 }
    	 
    	 //RECALL
    	 
    	 double [] fn=new double [classes];
    	 for (int i=0; i<classes; i++) {
    		 for (int j=0; j<classes; j++) {
    			 if(i==j)
    				 continue;
    			 else
    				 fn[i]+=matrixPol[i][j];
    			 
    		 }
    	 }
    	 
    	 for(int i=0; i<classes; i++) {
    		 recall[i]= matrixPol[i][i]/(matrixPol[i][i]+fn[i]);
    		 if(Double.isNaN(recall[i]))  recall[i]=0;
    		 if(recall[i] == 0.0) recall[i] = epsilon;
    	 }
    	 
    	 // F-MEASURE
    	 
    	 for(int i=0; i<classes; i++) {
    		 f_measure[i]= (2*prec[i]*recall[i])/(prec[i]+recall[i]);
    		 if(Double.isNaN(f_measure[i]))  f_measure[i]=0;
    	 }
    	 
    	 // ACCURACY
    	 
    	 double sum_tp=0;
    	 
    	 for(int i=0; i<classes; i++)
    		 sum_tp+=matrixPol[i][i];
    	 
    	 accuracy=sum_tp/cont_tot;
    	 if(Double.isNaN(accuracy))  accuracy=0;
    	 
    	 
    	 for(int i=0; i<classes; i++)
    		 per.globalPerformance(prec[i], recall[i], f_measure[i], i);
    	 
    	 
    	 per.globalAccuracy(accuracy);
    	 
    	 
         if(path != null){
             BufferedWriter BW2 = new BufferedWriter(new FileWriter(path, false));
             
             for(int i=0; i<classes; i++)
            	 BW2.write(Double.toString(prec[i])+ " ");
             BW2.newLine();
             
             for(int i=0; i<classes; i++)
            	 BW2.write(Double.toString(recall[i])+ " ");
             BW2.newLine();
             
             for(int i=0; i<classes; i++)
            	 BW2.write(Double.toString(f_measure[i])+ " ");
             BW2.newLine();
             
             
             BW2.write(Double.toString(accuracy)+ " ");
             
             BW2.flush();
             BW2.close();
         }

        
    }


      public static String evaluatePerformance_PNF(int counterNeg[],
              int counterPos[], int counterNeu[],
            performance per, String path, double epsilon) throws IOException{

        int N = counterNeg[0]+counterNeg[1]+counterNeg[2];		//totale negative
        int P = counterPos[0]+counterPos[1]+counterPos[2];		//totale positive
         int F = counterNeu[0]+counterNeu[1]+counterNeu[2];		//totale neutral

        int TP = counterPos[0];
        int TN = counterNeg[1];
        int TF = counterNeu[2];
        int FN = counterPos[1] + counterNeu[1];
        int FP = counterNeg[0] + counterNeu[0];
          int FF = counterNeg[2] + counterPos[2];

        double precision_neg, recall_neg, f1_neg, accuracy;
        double precision_pos, recall_pos, f1_pos;
 double precision_neu, recall_neu, f1_neu;


               String performance = "REAL CASES:\t\tPOS("+P+")\tNEG("+N+")\tNEU("+N+")\n"
                + "PREDICTED|\tPOS:\t"+ TP + "\t\t"+ counterNeg[0]+ "\t\t"+ counterNeu[0]+"" +
                "\n\t |\tNEG:\t"+ counterPos[1] +"\t\t"+ TN+ "\t\t"+ counterNeu[1] +"" +
                "\n\t |\tNEU:\t"+  counterPos[2]  +"\t\t"+ counterNeg[2] +"\t\t"+ TF+"\n";



        //POSITIVE
        precision_pos = (double)TP/(TP+FP);
        if(Double.isNaN(precision_pos))  precision_pos=0;

        recall_pos = (double)TP/P;
        if(Double.isNaN(recall_pos))  recall_pos=0;

        if(precision_pos == 0.0)
            precision_pos = epsilon;
          if(recall_pos == 0.0)
            recall_pos = epsilon;

        f1_pos = (2*precision_pos*recall_pos)/(precision_pos+recall_pos);
        if(Double.isNaN(f1_pos))  f1_pos=0;




        performance += ("\nPOSITIVE" +
                "\n\tPrecision:\t"+ precision_pos+
                "\n\tRecall:\t"+ recall_pos+
                "\n\tF1:\t"+ f1_pos);

        per.globalPerformance(precision_pos, recall_pos, f1_pos, 0);

          //NEGATIVE
        precision_neg = (double)TN/(TN+FN);
        if(Double.isNaN(precision_neg))  precision_neg=0;

        recall_neg = (double)TN/N;
        if(Double.isNaN(recall_neg))  recall_neg=0;

            if(precision_neg == 0.0)
            precision_neg = epsilon;
          if(recall_neg == 0.0)
            recall_neg = epsilon;

        f1_neg = (2*precision_neg*recall_neg)/(precision_neg+recall_neg);
        if(Double.isNaN(f1_neg))  f1_neg=0;

        performance += ("NEGATIVE" +
                "\n\tPrecision:\t"+ precision_neg+
                "\n\tRecall:\t"+ recall_neg+
                "\n\tF1:\t"+ f1_neg);

        per.globalPerformance(precision_neg, recall_neg, f1_neg, 1);

         //NEUTRAL
        precision_neu = (double)TF/(TF+FF);
        if(Double.isNaN(precision_neu))  precision_neu=0;

        recall_neu = (double)TF/F;
        if(Double.isNaN(recall_neu))  recall_neu=0;

            if(precision_neu == 0.0)
            precision_neu = epsilon;
          if(recall_neu == 0.0)
            recall_neu = epsilon;

        f1_neu = (2*precision_neu*recall_neu)/(precision_neu+recall_neu);
        if(Double.isNaN(f1_neu))  f1_neu=0;

        performance += ("NEUTRAL" +
                "\n\tPrecision:\t"+ precision_neu+
                "\n\tRecall:\t"+ recall_neu+
                "\n\tF1:\t"+ f1_neu);

        per.globalPerformance(precision_neu, recall_neu, f1_neu, 2);

        //GLOBAL
        accuracy = (double)(TP+TN+TF)/(P+N+F);
        if(Double.isNaN(accuracy))  accuracy=0;

        performance += ("\nGLOBAL" +
                "\n\tAccuracy:\t"+ accuracy +"\n\n");

        // per.globalAccuracy(accuracy);

        System.out.println(performance);


        if(path != null){
            BufferedWriter BW2 = new BufferedWriter(new FileWriter(path, false));
               BW2.write(Double.toString(precision_pos));
            BW2.newLine();
            BW2.write(Double.toString(recall_pos));
            BW2.newLine();
            BW2.write(Double.toString(f1_pos));
            BW2.newLine();
            BW2.write(Double.toString(precision_neg));
            BW2.newLine();
            BW2.write(Double.toString(recall_neg));
            BW2.newLine();
            BW2.write(Double.toString(f1_neg));
            BW2.newLine();
             BW2.write(Double.toString(precision_neu));
            BW2.newLine();
            BW2.write(Double.toString(recall_neu));
            BW2.newLine();
            BW2.write(Double.toString(f1_neu));
            BW2.newLine();
            BW2.write(Double.toString(accuracy));

            BW2.flush();
            BW2.close();
        }


//        return performance;

        return Double.toString(precision_pos)+"\n"+Double.toString(recall_pos)+"\n"+
                Double.toString(f1_pos)+"\n"+ Double.toString(precision_neg) +"\n"+Double.toString(recall_neg)+"\n"+
                Double.toString(f1_neg)+"\n"+ Double.toString(precision_neu) +"\n"+Double.toString(recall_neu)+"\n"+
                Double.toString(f1_neu)+"\n"+Double.toString(accuracy);
    }



    //    public static double[] evaluatePerformanceDouble(int counterNeg[], int counterPos[]){
    //        int N = counterNeg[0]+counterNeg[1]+counterNeg[2];		//totale negative
    //        int P = counterPos[0]+counterPos[1]+counterPos[2];		//totale positive
    //        int TP = counterPos[2];
    //        int TN = counterNeg[0];
    //        int FP = counterNeg[2];
    //        int FN = counterPos[0];
    //
    //        double precision_neg, recall_neg, f1_neg, accuracy;
    //        double precision_pos, recall_pos, f1_pos;
    //
    //        String performance = "REAL CASES:\t\tNEG("+N+")\tPOS("+P+")\n"
    //                + "PREDICTED|\tNEG:\t"+ TN + "\t\t"+ FN+"" +
    //                "\n\t|\tPOS:\t"+ FP +"\t\t"+ TP+"\n\n";
    //
    //
    //
    //
    //
    //
    //        //NEGATIVE
    //        precision_neg = (double)TN/(TN+FN);
    //             if(Double.isNaN(precision_neg))  precision_neg=0;
    //
    //        recall_neg = (double)TN/N;
    //             if(Double.isNaN(recall_neg))  recall_neg=0;
    //
    //        f1_neg = (2*precision_neg*recall_neg)/(precision_neg+recall_neg);
    //                 if(Double.isNaN(f1_neg))  f1_neg=0;
    //
    //        performance += ("NEGATIVE" +
    //                "\n\tPrecision:\t"+ precision_neg+
    //                "\n\tRecall:\t"+ recall_neg+
    //                "\n\tF1:\t"+ f1_neg);
    //
    ////        per.globalPerformance(precision_neg, recall_neg, f1_neg, "neg");
    //
    //        //POSITIVE
    //        precision_pos = (double)TP/(TP+FP);
    //         if(Double.isNaN(precision_pos))  precision_pos=0;
    //
    //        recall_pos = (double)TP/P;
    //                 if(Double.isNaN(recall_pos))  recall_pos=0;
    //
    //        f1_pos = (2*precision_pos*recall_pos)/(precision_pos+recall_pos);
    //                      if(Double.isNaN(f1_pos))  f1_pos=0;
    //
    //        performance += ("\nPOSITIVE" +
    //                "\n\tPrecision:\t"+ precision_pos+
    //                "\n\tRecall:\t"+ recall_pos+
    //                "\n\tF1:\t"+ f1_pos);
    //
    ////        per.globalPerformance(precision_pos, recall_pos, f1_pos, "pos");
    //
    //        //GLOBAL
    //        accuracy = (double)(TP+TN)/(P+N);
    //                    if(Double.isNaN(accuracy))  accuracy=0;
    //
    //        performance += ("\nGLOBAL" +
    //                "\n\tAccuracy:\t"+ accuracy +"\n\n");
    //
    ////        per.globalAccuracy(accuracy);
    //
    //        System.out.println(performance);
    //
    //
    //        double aux[] = {precision_neg,recall_neg,f1_neg,precision_pos,recall_pos,f1_pos,accuracy};
    //        return aux;
    //
    //    }

    public static String evaluatePerformanceNEU(int counterNeg[], int counterNeu[], int counterPos[], performance per){
        int NEG = counterNeg[0]+counterNeg[1]+counterNeg[2];		//totale negative
        int NEU = counterNeu[0]+counterNeu[1]+counterNeu[2];		//totale neutre
        int POS = counterPos[0]+counterPos[1]+counterPos[2];		//totale positive


        double precision, recall, f1, accuracy;
        String performance = "REAL CASES:\t\tNEG("+NEG+")\tNEU("+NEU+")\tPOS("+POS+")\n"
                + "PREDICTED|\tNEG:\t"+ counterNeg[0] + "\t\t"+ counterNeu[0]+"\t\t"+ counterPos[0]+"" +
                "\n\t|\tNEU:\t"+ counterNeg[1] +"\t\t"+ counterNeu[1]+"\t\t"+ counterPos[1]+"" +
                "\n\t|\tPOS:\t"+ counterNeg[2] +"\t\t"+ counterNeu[2]+"\t\t"+ counterPos[2]+"\n\n";

        //NEGATIVE
        precision = (double)counterNeg[0]/(counterNeg[0]+counterNeu[0]+counterPos[0]);
        if(Double.isNaN(precision))  precision=0;

        recall = (double)counterNeg[0]/NEG;
        if(Double.isNaN(recall))  recall=0;

        f1 = (2*precision*recall)/(precision+recall);
        if(Double.isNaN(f1))  f1=0;

        performance += ("NEGATIVE" +
                "\n\tPrecision:\t"+ precision+
                "\n\tRecall:\t"+ recall+
                "\n\tF1:\t"+ f1);


        per.globalPerformance(precision, recall, f1, 0);

        //NEUTRALI
        precision = (double)counterNeu[1]/(counterNeg[1]+counterNeu[1]+counterPos[1]);
        if(Double.isNaN(precision))  precision=0;

        recall = (double)counterNeu[1]/NEU;
        if(Double.isNaN(recall))  recall=0;

        f1 = (2*precision*recall)/(precision+recall);
        if(Double.isNaN(f1))  f1=0;

        performance += ("\nNEUTRAL" +
                "\n\tPrecision:\t"+ precision+
                "\n\tRecall:\t"+ recall+
                "\n\tF1:\t"+ f1);



        per.globalPerformance(precision, recall, f1, 1);

        //POSITIVE
        precision = (double)counterPos[2]/(counterPos[2]+counterNeg[2]+counterNeu[2]);
        if(Double.isNaN(precision))  precision=0;

        recall = (double)counterPos[2]/POS;
        if(Double.isNaN(recall))  recall=0;

        f1 = (2*precision*recall)/(precision+recall);
        if(Double.isNaN(f1))  f1=0;

        performance += ("\nPOSITIVE" +
                "\n\tPrecision:\t"+ precision+
                "\n\tRecall:\t"+ recall+
                "\n\tF1:\t"+ f1);

        per.globalPerformance(precision, recall, f1, 2);

        //GLOBAL
        accuracy = (double)(counterNeg[0]+counterNeu[1]+counterPos[2])/(NEG+NEU+POS);
        if(Double.isNaN(accuracy))  accuracy=0;

        performance += ("\nGLOBAL" +
                "\n\tAccuracy:\t"+ accuracy +"\n\n");



      //  per.globalAccuracy(accuracy);

        System.out.println(performance);

        return performance;

    }

    /*
     * result[0] = # token negativi
     * result[1] = # token neutri
     * result[2] = # token positivi
     * */
    public static int thresholdNEU(int result[], double[] alpha){

        //        if(result[2] <= alpha * result[0])
        //            return 0;		//negativo
        //        else
        //            return 2;		//positivo



        if(alpha[2]*result[2] <= alpha[0]*result[0]){
            if(alpha[1]*result[1] <= alpha[0]*result[0])
                return 0;
            else
                return 1;

        }else{
            if(alpha[1]*result[1] <= alpha[2]*result[2])
                return 2;
            else
                return 1;
        }


    }

    public static int threshold(int result[], double[] alpha){

        if(result[2] <= alpha[0] * result[0])
            return 0;		//negativo
        else
            return 2;		//positivo




    }


}
