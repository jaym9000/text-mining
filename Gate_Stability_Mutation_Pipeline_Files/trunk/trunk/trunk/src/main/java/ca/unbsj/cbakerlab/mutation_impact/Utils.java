package ca.unbsj.cbakerlab.mutation_impact;

import gate.Annotation;
import gate.Document;
import gate.Gate;
import gate.util.GateException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;




public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class);
	private static File configFile;

	public static void initGate() throws GateException, FileNotFoundException, IOException, URISyntaxException {
		if (Gate.isInitialised()){
			return;
		}
		// Initialise GATE.
		Properties pro = new Properties();
		pro.load(new FileInputStream(new File(ClassLoader.getSystemClassLoader().getResource("project.properties").toURI())));
        //pro.load(new FileInputStream(new File(log.getClass().getClassLoader().getResource("project.properties").toURI())));

		//pro.load(log.getClass().getResourceAsStream("/gate.properties"));
		String gateHome = pro.getProperty("GATE_HOME");

		
		log.info("Initializing GATE..."+gateHome);
        Gate.setGateHome(new File(gateHome));
        File pluginsHome = new File(gateHome, "plugins");
        Gate.setPluginsHome(pluginsHome);
        Gate.init();
        log.info("GATE_HOME: "+gateHome);
        log.info("GATE_PLUGINS: "+pluginsHome);
        log.info("Initializing GATE... Done.");

		
        
        
        /*
		log.info("Initializing GATE...");
		try {
			//Gate.setGateHome(new File(gateHome));
			//Gate.setGateHome(new File(ClassLoader.getSystemClassLoader().getResource(gateHome).toURI()));
			//Gate.setGateHome(new File(log.getClass().getResource(gateHome).toURI()));


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String PROGRAM_DIRECTORY=null;
		//log.info(log.getClass().getClassLoader().);
		try {
		    //Attempt to get the path of the actual JAR file, because the working directory is frequently not where the file is.
		    //Example: file:/D:/all/Java/TitanWaterworks/TitanWaterworks-en.jar!/TitanWaterworks.class
		    //Another example: /D:/all/Java/TitanWaterworks/TitanWaterworks.class
		    PROGRAM_DIRECTORY = log.getClass().getClassLoader().getResource("gate").getPath(); // Gets the path of the class or jar.

		    //Find the last ! and cut it off at that location. If this isn't being run from a jar, there is no !, so it'll cause an exception, which is fine.
		    try {
		        PROGRAM_DIRECTORY = PROGRAM_DIRECTORY.substring(0, PROGRAM_DIRECTORY.lastIndexOf('!'));
		    } catch (Exception e) { }

		    //Find the last / and cut it off at that location.
		    PROGRAM_DIRECTORY = PROGRAM_DIRECTORY.substring(0, PROGRAM_DIRECTORY.lastIndexOf('/') + 1);
		    //If it starts with /, cut it off.
		    if (PROGRAM_DIRECTORY.startsWith("/")) PROGRAM_DIRECTORY = PROGRAM_DIRECTORY.substring(1, PROGRAM_DIRECTORY.length());
		    //If it starts with file:/, cut that off, too.
		    if (PROGRAM_DIRECTORY.startsWith("file:/")) PROGRAM_DIRECTORY = PROGRAM_DIRECTORY.substring(6, PROGRAM_DIRECTORY.length());
		} catch (Exception e) {
		    PROGRAM_DIRECTORY = ""; //Current working directory instead.
		}

		log.info("PROGRAM_DIRECTORY: "+PROGRAM_DIRECTORY);
		
		configFile = new File(log.getClass().getClassLoader()
				.getResource("gate/gate.xml").getPath());
		if(Gate.getGateHome() != null)
			Gate.setGateHome(new File(log.getClass().getClassLoader().getResource("gate").getPath()));
		Gate.setPluginsHome(new File(log.getClass().getClassLoader().getResource("gate/plugins").getPath()));
		Gate.setSiteConfigFile(configFile);
*/
		//Gate.init();
		//log.info("Initializing GATE... Done.");
	}

	
	public static Map<String,String> initFileIndex(String fileOrDirName) throws IOException, URISyntaxException{

        File fileOrDir = new File(fileOrDirName);
        Map<String,String> corpus = new TreeMap<String,String>();

        if(fileOrDir.isDirectory()){            	
            String[] list = fileOrDir.list();
            List<String> files = Arrays.asList(list);
            Collections.sort(files);            
            
            for(String fileName : files){//System.out.println(fileOrDir.getCanonicalPath()+"\n"+fileOrDir.getAbsoluteFile());
            	if(fileName.endsWith(".xml")){ 
            		String documentId = fileName.replaceFirst("\\.xml", "");
            		URL u = new File(fileOrDirName+"/"+ fileName).toURI().toURL();
                	corpus.put(documentId, u.toString());
            	}  
            	if(fileName.endsWith(".txt")){ 
            		String documentId = fileName.replaceFirst("\\.txt", "");
            		URL u = new File(fileOrDirName+"/"+ fileName).toURI().toURL();
                	corpus.put(documentId, u.toString());
            	} 
            }
        }else{
	        String fileName = fileOrDir.getName().split("/")[fileOrDir.getName().split("/").length-1];
	        URL u = new File(fileOrDirName+"/"+ fileName).toURI().toURL();
        	corpus.put(fileName, u.toString());
        }
        
        return corpus;
	}
	
	static String[] parseWNMFormat(String mutationString){
		String wtResidue = mutationString.substring(0,1);
		String mResidue = mutationString.substring(mutationString.length()-1,mutationString.length());
		String position = mutationString.substring(1,mutationString.length()-1);
		String[] split = {wtResidue,position,mResidue};	
		return split;
	}
	
	
	public static Integer columnNumber(String columnName, String[] csvHeaderLine) {
		for (int n = 0; n < csvHeaderLine.length; ++n)
			if (csvHeaderLine[n].equalsIgnoreCase(columnName))
				return new Integer(n);
		return null;
	}
	
	
	public static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static String getContent(File file) throws IOException{
		InputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"Windows-1252"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
        }
        String content = sb.toString();
		return content;
	}
	
	/*
	public static Corpus initCorpus(String fileOrDirName) throws IOException, URISyntaxException{
	
         // Initialise corpus.
         
        File fileOrDir = new File(fileOrDirName);
        Corpus corpus = new Corpus(fileOrDir.getName());

        if(fileOrDir.isDirectory()){            	
            String[] list = fileOrDir.list();
            List<String> files = Arrays.asList(list);
            Collections.sort(files);            
            
            for(String fileName : files){//System.out.println(fileOrDir.getCanonicalPath()+"\n"+fileOrDir.getAbsoluteFile());
            	if(fileName.endsWith(".txt")){ 
            		String documentId = fileName.replaceFirst("\\.txt", "");
                	corpus.getContent().put(documentId, Utils.getContent(new File(fileOrDirName+"/"+ fileName)));
            	}                	
            }
        }else{        	
	       // String fileName = fileOrDir.getName().split("/")[fileOrDir.getName().split("/").length-1];
	        if(fileOrDir.getAbsolutePath().endsWith(".txt")){ 
        		String documentId = fileOrDir.getName().replaceFirst("\\.txt", "");
        		//fileOrDir = fileOrDir.getCanonicalFile();
            	corpus.getContent().put(documentId, Utils.getContent(new File(fileOrDirName)));
        	}  
        	//corpus.getContent().put(documentId,  Util.getContent(new File(fileOrDir.getName())));
        }
        
        return corpus;
	}
	*/
		
	
/*
	public static void saveAnalysis(Map<String, DocumentInformation> results, String outputDirName, boolean printAnalysisLogAll, int typeOfAnalysisLogging) throws FileNotFoundException, InvalidOffsetException {
		
        // To analysis.txt
         
        if(printAnalysisLogAll){
            PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputDirName+"/analysis_all.txt")));
            for(Entry<String,DocumentInformation> e : results.entrySet()){            	
            	DocumentInformation docInfo = e.getValue();
            	ps.print("\n==========================================================\n"+
            			"DOCUMENT: "+docInfo.getName()+"		"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+
            			"\n==========================================================\n");
            	ps.print(docInfo.resultsAsPrettyString());
            	ps.print(docInfo.getDocumentInfoStatistics().evaluationInfoAsPrettyString()); 
            	ps.print(docInfo.getDocumentInfoStatistics().gateOutputInfoAsPrettyString());
            	ps.print(docInfo.getDocumentInfoStatistics().mutationStatisticsAsPrettyString());
            	if(typeOfAnalysisLogging==1)ps.print(docInfo.getDocumentInfoStatistics().cooccurrencesAsPrettyString(1));  // 1 - all; 2 - only which are not in results; 3 - only which are not in results and which are wrong
            	else if(typeOfAnalysisLogging==2)ps.print(docInfo.getDocumentInfoStatistics().mergedCooccurrencesAsPrettyString()); 
            	ps.flush();
            }
            ps.close();
        }else{
        	for(Entry<String,DocumentInformation> e : results.entrySet()){            	
            	DocumentInformation docInfo = e.getValue();
                PrintStream ps1 = new PrintStream(new BufferedOutputStream(new FileOutputStream("analysis/analysis_"+docInfo.getName()+".txt")));
            	ps1.print("\n==========================================================\n"+
            			"DOCUMENT: "+docInfo.getName()+"		"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+
            			"\n==========================================================\n");
            	ps1.print(docInfo.resultsAsPrettyString());
            	ps1.print(docInfo.getDocumentInfoStatistics().evaluationInfoAsPrettyString()); 
            	ps1.print(docInfo.getDocumentInfoStatistics().gateOutputInfoAsPrettyString());
            	ps1.print(docInfo.getDocumentInfoStatistics().mutationStatisticsAsPrettyString());
            	if(typeOfAnalysisLogging==1)ps1.print(docInfo.getDocumentInfoStatistics().cooccurrencesAsPrettyString(1));  // 1 - all; 2 - only which are not in results; 3 - only which are not in results and which are wrong
            	else if(typeOfAnalysisLogging==2)ps1.print(docInfo.getDocumentInfoStatistics().mergedCooccurrencesAsPrettyString()); 
            	ps1.flush();
            	ps1.close();
            }
        }

		
	}
*/
	
	private Set<String> getMentionedMutations(gate.Document doc){
		Set<String> mentionedMutations = new TreeSet<String>();
		Iterator annIter = doc.getAnnotations().get("Mutation").iterator();
		while(annIter.hasNext()){
			Annotation mutAnn = (Annotation)annIter.next();
			mentionedMutations.add((String) mutAnn.getFeatures().get("wNm"));
		}
		return mentionedMutations;
	}
	
	
	public static void saveGateXml(gate.Document document, File outputFile, boolean preservingFormat) throws IOException{
		FileWriter fstream = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fstream);
		if(preservingFormat){
			out.write(document.toXml(document.getAnnotations(), true));
		} else {
			out.write(document.toXml());
		}						
		out.close();
		/*String docXMLString = null;
	      // if we want to just write out specific annotation types, we must
	      // extract the annotations into a Set
	      if(annotTypesToWrite != null) {
	        // Create a temporary Set to hold the annotations we wish to write out
	        Set annotationsToWrite = new HashSet();
	        
	        // we only extract annotations from the default (unnamed) AnnotationSet
	        // in this example
	        AnnotationSet defaultAnnots = doc.getAnnotations();
	        Iterator annotTypesIt = annotTypesToWrite.iterator();
	        while(annotTypesIt.hasNext()) {
	          // extract all the annotations of each requested type and add them to
	          // the temporary set
	          AnnotationSet annotsOfThisType =
	              defaultAnnots.get((String)annotTypesIt.next());
	          if(annotsOfThisType != null) {
	            annotationsToWrite.addAll(annotsOfThisType);
	          }
	        }

	        // create the XML string using these annotations
	        docXMLString = doc.toXml(annotationsToWrite);
	      }
	      // otherwise, just write out the whole document as GateXML
	      else {
	        docXMLString = doc.toXml();
	      }

	      // Release the document, as it is no longer needed
	      Factory.deleteResource(doc);

	      // output the XML to <inputFile>.out.xml
	      String outputFileName = docFile.getName() + ".out.xml";
	      File outputFile = new File(docFile.getParentFile(), outputFileName);

	      // Write output files using the same encoding as the original
	      FileOutputStream fos = new FileOutputStream(outputFile);
	      BufferedOutputStream bos = new BufferedOutputStream(fos);
	      OutputStreamWriter out;
	      if(encoding == null) {
	        out = new OutputStreamWriter(bos);
	      }
	      else {
	        out = new OutputStreamWriter(bos, encoding);
	      }

	      out.write(docXMLString);
	      
	      out.close();
	      System.out.println("done");*/

	}
	
	public static void saveGateXhtml(gate.Document document, File outputFile) throws IOException{
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> " +
				"<?xml-stylesheet type=\"text/css\" href=\"gate.css\"?>" +
				"<!DOCTYPE html" +
				"     PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
				"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" +
				"  <head>" +
				"    <title>"+document.getName()+"</title>" +
				"  </head>" +
				"  <body>" +
				"  <p>";
		String tail = "</p> </body> </html> ";
		FileWriter fstream = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(head);
		out.write(document.toXml(document.getAnnotations(), true));
		out.write(tail);
		out.close();
	}

	
	public static Map<String, Annotation> createIndexForGateDocument(Document doc){
		Map<String, Annotation> index = new HashMap<String, Annotation>();
		for(Annotation ann : doc.getAnnotations().get("Token")){
			String string = doc.getContent().toString().substring(ann.getStartNode().getOffset().intValue(), ann.getEndNode().getOffset().intValue());
			index.put(string, ann);
		}
		
		return index;
	}
	
	public static String mapAsPrettyString(Map map){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(Object e : map.keySet()){
			sb.append(e+":"+map.get(e)+"\n");
		}
		return sb.toString();
	}
	
	public static String stringTableAsString(String[][] table){	
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(String[] s : table){
			for(String s2 : s){
				sb.append("'"+s2+"' ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static float Round(float Rval, int Rpl) {
		  float p = (float)Math.pow(10,Rpl);
		  Rval = Rval * p;
		  float tmp = Math.round(Rval);
		  return (float)tmp/p;
	}
	
	
	public static float round(float Rval) {
		  float p = (float)Math.pow(10,6);
		  Rval = Rval * p;
		  float tmp = Math.round(Rval);
		  return (float)tmp/p;
	}
	
	static String replaceCharAt(String s, int pos, char c) {
		//System.out.println(s.substring(0, pos));
		//System.out.println(s.substring(pos + 1));
	    return s.substring(0, pos) + c + s.substring(pos + 1);
	}
	
	static File createFileOrDirectoryIfNotExist(String fileOrDirName){
		File newFileOrDir = null;
		if(fileOrDirName != null){
			try{
				newFileOrDir = new File(fileOrDirName);
				if (!newFileOrDir.exists()) {
					boolean success = (new File(fileOrDirName)).mkdir();
					if (success) {
						System.out.println("File or Directory: " + newFileOrDir + " created");
					}else{
						log.warn("File or Directory is not set.");
					}
				}
			}catch(Exception e ){
				log.warn("File or Directory is not set.");
			}				
		}
		return newFileOrDir;
	}
	
	private static final long timestamp = new Date().getTime();
	private static long instanceCounter = 0;

	public static Resource createTimestampedInstance(Resource klass, Model model) {

		Resource result = model.createResource(klass + "_" + timestamp + "_" + (++instanceCounter));

		result.addProperty(Vocab.type, klass);

		return result;

	} // createTimestampedInstance(Resouce klass,Model model)
	
	public static String createTimestampedUri(Resource klass) {
		String uri = klass + "_" + timestamp + "_" + (++instanceCounter);
		return uri;
	}
	
}
/**
 * CLASS ValueThenKeyComparator
 */
class ValueThenKeyComparator<K extends Comparable<? super K>, V extends Comparable<? super V>> implements Comparator<Map.Entry<K, V>> {

	public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b) {
		int cmp1 = b.getValue().compareTo(a.getValue());
		if (cmp1 != 0) {
			return cmp1;
		} else {
			return a.getKey().compareTo(b.getKey());
		}
	}
	
	

}
