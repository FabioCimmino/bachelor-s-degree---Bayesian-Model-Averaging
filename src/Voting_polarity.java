
import utils.performance.performance;
import utils.performance.Evaluator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import utils.Voting4Chart;


public class Voting_polarity{

	static String rootFolder;
	static double epsilon=0.00001;


	//f1 measure
	static int index_perf = 2;

	static  int numFolds=10;
	//    static String[] classifiers={"DT","SVM","MNB"};
	static String[] classifiers;
	static int n_classifier ;
	


	static String fileName = "rt-polarity";
	static String[] measures = {"P+","R+","F1+","P-","R-","F-","ACC"};
	static int classes;
	
	public static void setNumClasses (String path) throws FileNotFoundException, IOException{
		
		BufferedReader BR = new BufferedReader(new FileReader(path));
		BR.readLine(); //skip headline
		
		String aux= BR.readLine();
		String s[]=aux.split(",");
		 classes=s.length-4;
		BR.close();
	}
	
	
	
	public static int cercaClass (String stringaDaCercare, String [] arrayListe){
		int  index=-1;
		for (int i=0; i<arrayListe.length;i++)
			if (arrayListe[i].equals(stringaDaCercare))
					index=i;
		
		
		return index;
		
		}
	
	/**
	 * Compute the performance measures for a particular classifier for each fold.
	 * Generate the file classifier_labels-fold.csv.
	 * 
	 * @param path - the path for a particular classifier
	 */
	public static performance splitFoldsFromCSV(String path,String classifier) throws FileNotFoundException, IOException{

		//        System.out.println(path);
		String[] pathSplit = path.split("\\.");
		//        System.out.println(pathSplit[0]);
		String ext = pathSplit[pathSplit.length-1];

		//int[] contNeg=new int[3];
		//int[] contPos=new int[3];

		int [][] confMatrix = new int [classes][classes];
		performance per = new performance(classes);

		BufferedWriter BW;

		BufferedReader BR = new BufferedReader(new FileReader(path));
		BR.readLine(); //skip headline
		String aux;
		String accum="";
		int cont=0;
		int fold=0;
		while(  (aux=BR.readLine()) != null ){

			if(!aux.isEmpty()){

				cont++;
				accum += aux + "\n";


				String[] fields = aux.split(",");
				String inst = fields[0];
				String[] valuesCorrectPol = fields[1].split(":");
				String[] valuesClassifiedPol = fields[2].split(":");

				//Count TP,FP,TN,FN
					confMatrix[Integer.parseInt(valuesCorrectPol[0])-1][Integer.parseInt(valuesClassifiedPol[0])-1]++;
			
					
				BR.mark(30000);


				String[] fieldsNext = BR.readLine().split(",");


				if(fieldsNext[0].equals("1") ) { //start another fold
					BW = new BufferedWriter(new FileWriter(rootFolder+classifier+"-"+fold+"."+ext));
					BW.write(accum);
					BW.close();

					cont=0;
					accum = "";

					Evaluator.evaluatePerformance(confMatrix,classes,
							per, rootFolder+classifier+"_perf-"+fold+".txt", epsilon);



					fold++;
					 confMatrix = new int [classes][classes];
				}

				BR.reset(); //back to previous line
			}
		}


		//write last fold
		BW = new BufferedWriter(new FileWriter(rootFolder+classifier+"-"+fold+"."+ext));
		BW.write(accum);
		BW.close();

		Evaluator.evaluatePerformance(confMatrix,classes,
				per,  rootFolder+classifier+"_perf-"+fold+".txt", epsilon);

		BR.close();

		
		
		return per;
	}

	public static void main(String[] args) throws Exception{

		String dataset = "Person - stretch";
	//	rootFolder = dataset+"\\predictions\\";
	//	String dataset = args[0];
	//	rootFolder = args[1]+"\\";
		
		File dirRoot = new File(args[0]);
		  String[] childrenRoot = dirRoot.list();
		  
	for(int y=0; y<childrenRoot.length; y++) {	  
		
	rootFolder = args[1]+"\\"+childrenRoot[y]+"\\";	
	String rootCsv= args[0]+"\\"+childrenRoot[y];
	
	File theDir = new File(args[1]+"\\"+childrenRoot[y]);
	if (!theDir.exists()) {
	    System.out.println("creating directory: " + theDir.getName());
	    theDir.mkdir();
	    }
	
	
	File dir = new File(rootCsv);
	 String[] children = dir.list();
	 
	  classifiers = new String [children.length];
	  n_classifier=classifiers.length;
	  for(int x=0; x<children.length;x++) {
		  classifiers[x]=children[x].replaceAll(".csv", "");
		  System.out.println( classifiers[x]);
	  }
		  
	  
		/*File theDir = new File(rootFolder);
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = false;
		        theDir.mkdir();
		        result = true;*/

		
		Voting4Chart excel =
				new Voting4Chart(rootFolder + "VOTING-"+childrenRoot[y]+".xls");
		

		
		
		setNumClasses(rootCsv+"\\"+children[0]);

		
		performance [] perClassifiers = new performance [classifiers.length];
		
		
		for (int x=0; x<perClassifiers.length;x++)
			perClassifiers[x]= splitFoldsFromCSV(rootCsv+"\\"+children[x],classifiers[x]);
	
		
		//Write global performances
		
		BufferedWriter BW = new BufferedWriter(new FileWriter(rootFolder+"pMeasures.txt", false));
		
		for(int x=0; x<classifiers.length;x++) {
			BW.append(perClassifiers[x].RawGlobPerformance(numFolds));
			BW.newLine();
			BW.newLine();
		}
		
		BW.close();
		meanPerfFolds(n_classifier,numFolds); 
		
		int row=0;

		excel.setRowOffset(row);

	//	String res="VOTING FOR: "+dataset+"\n\n";
	//	System.out.println(res);
		
		double[][] pMeasures = extractPerformances(n_classifier);
		
		
		double  [] accuracy4max = new double [n_classifier];
		
		for(int i=0; i<pMeasures.length;i++)
			accuracy4max[i]=pMeasures[i][3*classes];
		
		
		
		Object[] maxAccuracy_ob = max(accuracy4max,classifiers);

		double maxAccuracy=(Double)maxAccuracy_ob[0];
		int index_cl=(Integer)maxAccuracy_ob[1];
		
		
		row+=4;

		double ret[];
	//	excel.writeString(dataset, 2);
		excel.setRowOffset(++row);
		
		for(int i=0; i<pMeasures.length;i++) {
		excel.writeNumber(pMeasures[i][3*classes],1);
		excel.writeString(classifiers[i],2);
		excel.setRowOffset(++row);
		}
		
	
		row += 4;
		excel.setRowOffset(row);
		
		excel.writeString("Democratic",2);
		excel.writeString("Bayesian",3);
		excel.writeString("Max",4);
		excel.writeString("Mean",5);
		excel.writeString("Product",6);

		row += 1;
		excel.setRowOffset(row);
		
		
		
		//generazione combinazioni
		List<String[]> lista=bool(n_classifier);
		
		String [] [] matrix = new String [lista.size()-1-n_classifier][n_classifier];
		
		
		int index_table=0;
		for(int i=0; i<lista.size();i++) {
			if(num_true(lista.get(i))>=2) {
			matrix[index_table]=lista.get(i);
		//	System.out.print(matrix[index_table][0]+ " " +matrix[index_table][1]+ " "+matrix[index_table][2]+ " "+matrix[index_table][3]+" "  +matrix[index_table][4]);
		//	System.out.println();
			index_table++;
			}
		}
		
		
		
		
	//	String [] [] matrix= new String [1][n_classifier];
	//	matrix[0]= contribution(rootCsv+"\\",children,accuracy4max);
		
		for (int i = 0; i < matrix.length; i++) {	
			
			for(int x=0 ; x<n_classifier;x++)
				if(matrix[i][x].equals("true")) 
					matrix[i][x]=classifiers[x]+", ";
				else
					matrix[i][x]="";
			
			
			String configuration="( ";
			
			for(int x=0;x<n_classifier;x++)
				configuration+=matrix[i][x];
			
			configuration+=")";
			
		//	System.out.println(configuration);
			
			//apply
			
			boolean [] conf = new boolean [n_classifier];
			
			for(int x=0; x<n_classifier;x++)
				conf[x]= !matrix[i][x].equals("");
			
			ret=apply(configuration, conf);
				
			excel.setRowOffset(row+i);
			excel.writeNumber(i+1,1);
			excel.setRowOffset(row+i);
			excel.writeNumber(ret[0],2);
			excel.setRowOffset(row+i);
			excel.writeNumber(ret[1],3);
			excel.setRowOffset(row+i);
			excel.writeNumber(ret[2],4);
			excel.setRowOffset(row+i);
			excel.writeNumber(ret[3],5);
			excel.setRowOffset(row+i);
			excel.writeNumber(ret[4],6);
			excel.setRowOffset(row+i);
			
			excel.writeString(replaceLast(configuration,",",""),7);
			
			/*
			System.out.print(Double.toString(ret[0])+"\t"+Double.toString(ret[1])+"\t"+
					configuration+"\t"+
					Double.toString(ret[0]-maxAccuracy)+"\t"+
					Double.toString(ret[1]-maxAccuracy)+ "\t"+
					Double.toString(ret[1]-ret[0]));
			System.out.println();
			
			*/
		}
		
		System.out.println("Max classifier: "+classifiers[index_cl]+" ("+String.format("%.4g", maxAccuracy)+")");
		
		
		excel.close();
		
	}
	
	}




	/**
	 * Extract the perfomances of a specific fold.
	 * They are extracted from the file pMeasures-fold.txt file.
	 * Each file contains the performances of the four classifiers.
	 * 
	 * @param num_existing_class
	 * @param fold
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static double[][] extractPerformances(int num_existing_class)
			throws FileNotFoundException, IOException{



		double pMeasures[][] = new double[n_classifier][3*classes+1];

		//Salvo in pMeasures le informazioni del file Voting/pMeasures.txt
		//pMeasures[i][*]: P+,R+,F1+,P-,R-,F1-,P=,R=,F1=,ACC
		


		BufferedReader BR;
	
		BR = new BufferedReader(new FileReader(rootFolder+"pMeasures.txt"));
		String aux;
		for(int x=0; x<n_classifier;x++) {
	//	 System.out.println("n_classifier"+x);
			for(int y=0; y<3*classes+1; y++) {
				aux= BR.readLine();
				pMeasures[x][y]=Double.parseDouble(aux);
			//	System.out.println(pMeasures[x][y]);
			}
			aux = BR.readLine();
		}

		
		BR.close();

		return pMeasures;
	}

	public static double[][][] extractPerformances_2(int num_existing_class, int fold)
			throws FileNotFoundException, IOException{
		BufferedReader BR;
		BR = new BufferedReader(new FileReader(rootFolder+"pMeasures-"+fold+".txt"));
		double pMeasures[][][] = new double[n_classifier][4][classes];
		
		for(int n_clas=0; n_clas<n_classifier; n_clas++) {
			for(int x=0; x<4;x++) {
				String [] aux= BR.readLine().split(" ");
				for (int i=0; i<classes; i++)
					pMeasures[n_clas][x][i]=Double.parseDouble(aux[i]);
			}
			BR.readLine();
		}
		
		BR.close();
		
		return pMeasures;
		
		
	}
	/**
	 * Read the performance measures of each fold of every classifier and put them in 
	 * pMeasures-fold.txt.
	 * Each file will contain the performance measures for a fold, for every classifiers.
	 * 
	 * @param num_existing_class
	 * @param numFolds
	 */
	public static void meanPerfFolds(int num_existing_class, int numFolds)
			throws FileNotFoundException, IOException{
		
		double [][][][] pMeasures= new double [n_classifier][numFolds][4][classes];
		
		for(int x=0; x<n_classifier;x++)
			pMeasures[x]=extractFoldsPerformances(rootFolder+classifiers[x]+"_perf.txt",numFolds);
		
		
			
		double temp[][][];
		BufferedWriter BW2;
		for (int fold_i = 0; fold_i < numFolds; fold_i++) {
			temp = new double[num_existing_class][4][classes];
			
			for (int fold_j = 0; fold_j < numFolds; fold_j++) {
					
			if (fold_i != fold_j) {
				
				for(int i=0; i<4;i++)
					for(int j=0; j<classes;j++) {
						for(int x=0;x<n_classifier;x++)
							temp[x][i][j]+=pMeasures[x][fold_j][i][j];
					}
				
			//	System.out.println(temp[0][0][1]);
			}
			
			}

			BW2 = new BufferedWriter(new FileWriter(rootFolder+"/pMeasures-"+fold_i+".txt", false));
			
			for(int clas=0; clas<temp.length; clas++) {
				for(int i=0;i< 4; i++) {
					for(int j=0; j<classes;j++) {
						BW2.append(Double.toString(temp[clas][i][j]/(numFolds-1))+ " ");
					}
					BW2.newLine();
				}
				BW2.newLine();
			}
			BW2.flush();
			BW2.close();
		}
		
		
	}


	/**
	 *  Read the performance measures of every fold of a specific classifier
	 *  and put them in an array.
	 *  
	 * @param path - the path of the specific classifier
	 * @param numFolds
	 * @return pMeasures - Array with the performance measures
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static double[][][] extractFoldsPerformances(String path, int numFolds) throws FileNotFoundException, IOException{

		// System.out.println(path);
		String[] pathSplit = path.split("\\.");
		String ext = pathSplit[pathSplit.length-1];

		double pMeasures[][][] = new double[numFolds][4][classes];

		BufferedReader BR;

		for (int fold = 0; fold < numFolds; fold++) {
		//	System.out.println("fold"+fold);
			BR = new BufferedReader(new FileReader(path.replace("perf", "perf-"+fold)));
			for(int mes=0;mes<3;mes++) {
			//	System.out.println("riga"+mes);
				String [] riga= BR.readLine().split(" ");
				for (int clas=0; clas<classes;clas++) {
					pMeasures[fold][mes][clas]=Double.parseDouble(riga[clas]);
				//	System.out.print(pMeasures[fold][mes][clas]+ " ");
				}
			//	System.out.println(" ");
			}
			String riga= BR.readLine();
			pMeasures[fold][3][0]=Double.parseDouble(riga);
		//	System.out.println(pMeasures[fold][3][0]);
			BR.close();
		}

		return pMeasures;
	}




	public static double[] apply(String configuration, boolean [] conf) throws Exception{



		performance per = new performance(classes);
		performance per2 = new performance(classes);
		performance per3 = new performance(classes);
		performance per4 = new performance(classes);
		performance per5 = new performance(classes);

		for(int fold=0; fold<numFolds; fold++){

			int matrixDem[][]= new int [classes][classes];
			int matrixBay[][]= new int [classes][classes];
			int matrixMax[][]= new int [classes][classes];
			int matrixMean[][]= new int [classes][classes];
			int matrixProduct[][]= new int [classes][classes];
			
			for(int clas=0; clas<classes; clas++) {
			
			//	int [][] counter_pol;
				
				int counterPos[][] = vote
						(fold, Integer.toString(clas+1), fileName, conf,configuration);
				
				matrixDem[clas]=counterPos[0];
				
				matrixBay[clas]=counterPos[1];
				
				matrixMax[clas]=counterPos[2];
				
				matrixMean[clas]=counterPos[3];
				
				matrixProduct[clas]=counterPos[4];
				
			}
			
		//	System.out.println("FOLD "+ fold + " - DEMOCRATIC");
			Evaluator.evaluatePerformance(matrixDem,classes,  per, null, epsilon);
			
		//	System.out.println("FOLD "+ fold + " - BAYESIAN");
			Evaluator.evaluatePerformance(matrixBay,classes,  per2, null, epsilon);
			
		//	System.out.println("FOLD "+ fold + " - MAX PROB");
			Evaluator.evaluatePerformance(matrixMax,classes,  per3, null, epsilon);
			
		//	System.out.println("FOLD "+ fold + " - MEAN PROB");
			Evaluator.evaluatePerformance(matrixMean,classes,per4, null, epsilon);
			
		//	System.out.println("FOLD "+ fold + " - PRODUCT PROB");
			Evaluator.evaluatePerformance(matrixProduct,classes, per5, null, epsilon);
			
		//	System.out.println("==================================");


		
				
		}
		
		BufferedWriter BW = new BufferedWriter(new FileWriter(rootFolder+"PERF_democratic.txt", true));
		BW.append("ENSEMBLE: "+configuration);
		BW.newLine();
		BW.append(per.getGlobPerformance(numFolds,classes));
		BW.newLine();
		BW.flush();
		BW.close();

		BW = new BufferedWriter(new FileWriter(rootFolder+"PERF_bayesian.txt", true));
		BW.append("ENSEMBLE: "+configuration);
		BW.newLine();
		BW.append(per2.getGlobPerformance(numFolds,classes));
		BW.newLine();
		BW.flush();
		BW.close();
		
		BW = new BufferedWriter(new FileWriter(rootFolder+"PERF_max_prob.txt", true));
		BW.append("ENSEMBLE: "+configuration);
		BW.newLine();
		BW.append(per3.getGlobPerformance(numFolds,classes));
		BW.newLine();
		BW.flush();
		BW.close();
		
		BW = new BufferedWriter(new FileWriter(rootFolder+"PERF_mean_prob.txt", true));
		BW.append("ENSEMBLE: "+configuration);
		BW.newLine();
		BW.append(per4.getGlobPerformance(numFolds,classes));
		BW.newLine();
		BW.flush();
		BW.close();
		
		BW = new BufferedWriter(new FileWriter(rootFolder+"PERF_product_prob.txt", true));
		BW.append("ENSEMBLE: "+configuration);
		BW.newLine();
		BW.append(per5.getGlobPerformance(numFolds,classes));
		BW.newLine();
		BW.flush();
		BW.close();
		
		double[] ret={per.getGlob_accuracy() / numFolds,
				per2.getGlob_accuracy() / numFolds,
				per3.getGlob_accuracy() / numFolds,
				per4.getGlob_accuracy()/ numFolds,
				per5.getGlob_accuracy()/ numFolds};

		return ret;
		

	}

	public static Object[] max(double[] t, String[] classifiers) {
		double maximum = t[0];   // start with the first value
		int cl_index=0;
		for (int i=1; i<t.length; i++) {
			if (t[i] > maximum) {
				maximum = t[i];   // new maximum
				cl_index=i;
			}
		}
		Object[] ob=new Object[2];
		ob[0]=maximum;
		ob[1]=cl_index;
		return ob;
	}

	
	
	/**
	 * 
	 * @param fold
	 * @param polarity - The ground truth label 
	 * @param fileName
	 * @param booleanDT - if DT is in the ensemble set
	 * @param booleanSVM - if SVM is in the ensemble set
	 * @param booleanMNB - if MNB is in the ensemble set
	 * @param booleanBN - if BN is in the ensemble set
	 * @return
	 * @throws Exception
	 */
	public static int [][] vote(int fold, String polarity, String fileName, boolean [] conf,String configuration)
					throws Exception{
		
		double[][][] pMeasures=extractPerformances_2(n_classifier, fold);

	//	System.out.println(configuration);
		int cont = 0,min=99999999;
		for(int x=0; x<conf.length;x++)
			if(conf[x]) {
				cont=num_corr_polarity(polarity, rootFolder+classifiers[x]+"-"+fold+".csv");
				//break;
				if(cont<min)
					min=cont;
				//System.out.println(cont);
				
			}
		
		cont=min;
		
		double tagsClassifiers[][][] = new double [n_classifier][cont][classes+1] ;
		
		for(int x=0; x<n_classifier; x++)
			tagsClassifiers[x]=getTagsFromCSV(polarity,rootFolder+classifiers[x]+"-"+fold+".csv");
		
		
		int democraticScore;
		int bayesianScore;
		int[] count_pol=new int [classes];	//# classificatori che assegnano etichetta POS e NEG a un caso
		double[] prob_pol=new double[classes];			//prob risultato NEG o POS


		int counterDemocratic[] = new int [classes];	//# frasi pos,# frasi neg,# frasi neu
		int counterBayesian[] = new int [classes];
		int counterMax[] = new int [classes];
		int counterMean[] =new int [classes];
		int counterProduct[] =new int [classes];
		
		/*
		
		File theDir = new File(rootFolder+polarity+replaceLast(configuration,",",""));
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    theDir.mkdir();
		    }
		BufferedWriter BW = new BufferedWriter(new FileWriter(rootFolder+polarity+replaceLast(configuration,",","")+"/voting-"+fold+".txt"));
		BW.write("FOLD "+fold+"\t - POLARITY: "+polarity +
				"\nDEMOCRATIC\t\t\t\t\t\t\t\tBAYESIAN\t\t\t\t\t\t\t\t\t\t\t\t\t"+configuration+"\n");
	*/
		
		int size=-1;
		for(int x=0; x<conf.length;x++)
			if(conf[x]) {
				size=tagsClassifiers[x].length;
				break;
			}
		
		//System.out.println(size);
		
		for(int i=0; i < size; i++){
			for(int x=0; x<classes;x++)
			{
				count_pol[x]=0;
				prob_pol[x]=0;
			}
			
			for(int y=0; y<conf.length;y++)
				if(conf[y])
					for(int x=0; x<classes;x++) {
						if( tagsClassifiers[y][i][0] ==x+1)
							count_pol[x]++;
					prob_pol[x]+= tagsClassifiers[y][i][x+1]*pMeasures[y][2][x];
					}
			
			
			// calcolo voting democratico
			
			int max_count_pol=count_pol[0],index_max=0;
			for(int y=1; y<classes;y++) {
			//	System.out.println(count_pol[y]);
				if(count_pol[y]>max_count_pol) {
					max_count_pol=count_pol[y];
					index_max=y;
				}
			}
			
			
			counterDemocratic[index_max]++;
	
			// calcola voting bayesiano
			
			double max_prob_pol=prob_pol[0];
			int index_prob=0;
			for(int y=0; y<classes;y++)
				if(prob_pol[y]>max_prob_pol) {
					max_prob_pol=prob_pol[y];
					index_prob=y;
				}
			
			counterBayesian[index_prob]++;
			
			
			String [] tagsString = new String [n_classifier];
			for (int x=0; x<tagsString.length;x++)
				if(!conf[x])
					tagsString[x]="";
				else
					tagsString[x]=Double.toString(tagsClassifiers[x][i][0]);

			
			String count_polarity="",prob_polarity="",merge_tag="";
			
			for(int y=0; y<tagsString.length;y++)
				merge_tag+=tagsString[y]+ "\t";
			
			for(int y=0; y<classes; y++) {
				count_polarity+=Integer.toString(count_pol[y])+" ";
				prob_polarity+=Double.toString(prob_pol[y])+ " ";
			}
			
			/*
			BW.write(Integer.toString(index_max+1)+
					"("+count_polarity+")"
					+"\t"+ Integer.toString(index_prob+1)+ "("+prob_polarity+")\t\t\t"+ merge_tag+
					polarity+"\n");
			*/
			
			//MAX PROBABILITY VOTING
			double maxProb=-1;
			int label=-1;
			
			for(int x=0; x<conf.length;x++) {
				if(conf[x]) {
					if(tagsClassifiers[x][i][1] > maxProb){
						label = (int) tagsClassifiers[x][i][0];
						maxProb = tagsClassifiers[x][i][label];
					}
				}
			}
					
			counterMax[label-1]++;
			
			//MEAN
			double [] arr_mean = new double [classes];
			
			for(int x=0; x<conf.length;x++)
				if(conf[x])
					for(int y=0; y<classes; y++)
						arr_mean[y]+= tagsClassifiers[x][i][y+1];
			
			
			double max_mean=arr_mean[0];
			int index_mean=0;
			for(int y=0; y<classes;y++)
				if(arr_mean[y]>max_mean) {
					max_mean=arr_mean[y];
					index_mean=y;
				}

			
			counterMean[index_mean]++;
			
			//product
			
			double [] arr_product = new double [classes];
			for(int y=0; y<arr_product.length;y++)
				arr_product[y]=1.0;
			
			for(int x=0; x<conf.length;x++)
				if(conf[x])
					for(int y=0; y<classes; y++)
						arr_product[y]*= tagsClassifiers[x][i][y+1];
			
			
			double max_product=arr_product[0];
			int index_product=0;
			for(int y=0; y<classes;y++)
				if(arr_product[y]>max_product) {
					max_product=arr_product[y];
					index_product=y;
				}

			
			counterProduct[index_product]++;
					
			
		}
		
		/*
		
		BW.flush();
		BW.close();

		*/

		int counter[][]={counterDemocratic,counterBayesian,counterMax,counterMean,counterProduct};

		return counter;
		
	}

	
	/**
	 * Consider a specific fold and a specific polarity.
	 * Return a matrix where each line correspond to an instance and has three element
	 * [correct_polarity, prob_pos, prob_neg]
	 * e.g. [1.0, 0.94118, 0.05882]
	 * 
	 * 
	 * 
	 * @param polarity - a specific polarity (1 or 2, pos or neg)
	 * @param path - pMeasures of a specific fold
	 * @return
	 * @throws Exception
	 */
	
	public static int num_corr_polarity (String polarity,String path ) throws Exception{
		
		BufferedReader BR = new BufferedReader(new FileReader(path));

		BR.mark(30000);

		String aux;
		int cont=0;
		while(  (aux=BR.readLine()) != null ){

			if(!aux.isEmpty()){
				String[] fields = aux.split(",");
				String[] valuesCorrectPol = fields[1].split(":");
				
				if(valuesCorrectPol[0].equals(polarity)){
					cont++;
				}

			}
		}
		
		BR.close();
		return cont;
	}
	
	public static double[][] getTagsFromCSV(String polarity, String path) throws Exception{

		BufferedReader BR = new BufferedReader(new FileReader(path));

		BR.mark(99999999);

		String aux;
		int cont=0;
		while(  (aux=BR.readLine()) != null ){

			if(!aux.isEmpty()){
				String[] fields = aux.split(",");
				String[] valuesCorrectPol = fields[1].split(":");
				
				if(valuesCorrectPol[0].equals(polarity)){
					cont++;
				}

			}
		}






		double tags[][] = new double[cont][classes+1];

		BR.reset();

		int i=0;
		while(  (aux=BR.readLine()) != null ){

			if(!aux.isEmpty()){
				String[] fields = aux.split(",");
				String[] valuesCorrectPol = fields[1].split(":");



				if(valuesCorrectPol[0].equals(polarity)){



					String[] valuesPredictedPol = fields[2].split(":");
					//                int index_win_class = Integer.parseInt(valuesPredictedPol[0]) + 3;

					tags[i][0] = Integer.parseInt(valuesPredictedPol[0]);
				//	System.out.print(tags[i][0]+"  ");
					String [] prob = new String [classes];
					for(int x=0; x<classes;x++) {
						prob[x]=fields[x+4].replace("*", "");
						
						tags[i][x+1]=Double.parseDouble(prob[x]);
					//	System.out.print(tags[i][x+1]+"  ");
					}
					
				//	System.out.println();
					


					//                    System.out.println(tags[i][0]+" "+tags[i][1]);
					//                    System.out.println(polarity+" "+valuesPredictedPol[0]+" "+tags[i][1]);
					i++;
				}
			}
		}

		BR.close();
		return tags;
	}



	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(), string.length());
		} else return string;

	}


	public static int num_true(String [] riga) {
		int cont=0;
		for (int i=0; i<riga.length; i++)
			if(riga[i].equals("true"))
				cont++;
		
		return cont;
	}

	public static List<String[]> bool(int n) {
	    return IntStream.range(0, 1 << n)
	            .mapToObj(i -> BitSet.valueOf(new long[] {i}))
	            .map(bs -> {
	                String[] a = new String[n];
	                for (int i = 0; i < n; i++) {
	                    a[n - i - 1] = Boolean.toString(bs.get(i));
	                }
	                return a;
	            })
	            .collect(Collectors.toList());
	}
	

	public static String[] contribution (String path, String [] children, double [] accuracy) throws IOException {
		
		double correct_incorrect [] [] = new double [children.length][2];
		
		for (int i=0; i<children.length;i++) {
		BufferedWriter BW;
		
		System.out.print(children[i]+ " ");
		
		BufferedReader BR = new BufferedReader(new FileReader(path+children[i]));
		BR.readLine(); //skip headline
		String aux;
		
		while(  (aux=BR.readLine()) != null ){

			if(!aux.isEmpty()){
				
				String[] fields = aux.split(",");
				
				if (fields[3].equals("+")) {
					correct_incorrect[i][1]++;
					
				}
				else {
					correct_incorrect[i][0]++;
				}
			
			}
		}

		BR.close();
		}
		
	double p10[] [] = new double [children.length][children.length];
	double p11[] [] = new double [children.length][children.length];
	double p01[] [] = new double [children.length][children.length];
	double p00[] [] = new double [children.length][children.length];
	
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++) 
			if (i!=j) 
				p10[i][j]=(correct_incorrect[i][0]/correct_incorrect[j][1])*(1-accuracy[j]);
			else
				p10[i][j]=0.0;
	
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++) 
			if (i!=j) 
				p11[i][j]=(correct_incorrect[i][0]/correct_incorrect[j][0])*(accuracy[j]);
			else
				p11[i][j]=0.0;
	
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++) 
			if (i!=j) 
				p01[i][j]=(correct_incorrect[i][1]/correct_incorrect[j][0])*(accuracy[j]);
			else
				p01[i][j]=0.0;
	
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++) 
			if (i!=j) 
				p00[i][j]=(correct_incorrect[i][0]/correct_incorrect[j][0])*(1-accuracy[j]);
			else
				p00[i][j]=0.0;
	
	
	double contribution_avg=0.0,avg_prec=0;
	String[] combination = new String [children.length];
	for (int i=0 ; i<combination.length;i++)
		combination[i]="true";
		
	int num_class;
	do {
		System.out.println();
	num_class=0;	
	avg_prec=contribution_avg;
	contribution_avg=0.0;
	
	double sum_col_10 [] = new double [children.length];
	double sum_col_11 [] = new double [children.length];
	double sum_col_01 [] = new double [children.length];
	double sum_col_00 [] = new double [children.length];
	
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++) {
			sum_col_10[j]+=p10[i][j];
			sum_col_11[j]+=p11[i][j];
			sum_col_01[j]+=p01[i][j];
			sum_col_00[j]+=p00[i][j];
		}
	
	double contribution [] = new double [children.length];
	
	
	for(int i=0; i<children.length; i++) {
		
		contribution[i]=(sum_col_10[i]+sum_col_11[i])/(sum_col_01[i]+sum_col_00[i]);
		if(Double.isNaN(contribution[i]))
			contribution[i]=0.0;
	}
	
	
	int index_min=0;
	double contribution_min=9999999999.0;
	for(int i=0; i<children.length; i++) {
		if(contribution[i]!=0.0) {
			contribution_avg+=contribution[i];
			num_class++;
		}
		if (contribution[i]<contribution_min && contribution[i]!=0.0) {
			index_min=i;
			contribution_min=contribution[i];
		}
	}
		
	System.out.println("num class: "+ num_class);
	System.out.println("Contribution min:"+contribution[index_min]);
	System.out.println("Index min:"+index_min);
	contribution_avg/=num_class;
	
	//azzero colonna e riga
	for(int i=0; i<children.length;i++)
		for(int j=0; j<children.length; j++)
			if (j==index_min || i==index_min) {
				p10[i][j]=0.0;
				p01[i][j]=0.0;
				p11[i][j]=0.0;
				p00[i][j]=0.0;
			}
	
	//escludo classificatore dall'ensemble
	combination[index_min]="false";
	System.out.println("Elimino class:"+children[index_min]);
	System.out.println("avg:"+contribution_avg);
	}while(avg_prec<contribution_avg && num_class>3);
	
	
	return combination;
	}
	
}



