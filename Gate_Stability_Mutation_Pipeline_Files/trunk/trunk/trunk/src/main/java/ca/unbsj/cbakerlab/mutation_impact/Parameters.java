package ca.unbsj.cbakerlab.mutation_impact;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class Parameters {
	private static final Logger log = Logger.getLogger(Parameters.class);

	Map<String,Parameter> parameters = new TreeMap<String,Parameter>();

	public Map<String,Parameter> getParameters(){
		return parameters;
	}
	
	public Parameter getParameter(String key){
		return parameters.get(key);
	}
	
	public void addParameter(String key, Object value, List<String> longTags, List<String> shortTags, String type){
		Parameter p = new Parameter(key, value, longTags,  shortTags, type);
		parameters.put(p.getKey(), p);
	}
	
	public Object getParameterValue(String key){
		return parameters.get(key).getValue();
	}
	
	public void argsToValues(String args) throws JSAPException{
		JSAP jsap = new JSAP();	
		for(Entry<String,Parameter> e : parameters.entrySet()){
			String flaggedOption = e.getKey();
			Parameter p = e.getValue();
			
			if(p.getType().equals("string")){
				FlaggedOption s = new FlaggedOption(flaggedOption).setStringParser(JSAP.STRING_PARSER);
				for(String longTag : p.getLongTags()){
					if(!longTag.equals(""))
						s.setLongFlag(longTag);
				}
				for(String shortTag : p.getShortTags()){
					if(!shortTag.equals(""))
						s.setShortFlag(shortTag.charAt(0));
				}
				

				//s.setHelp("The name input file or directory.");
				jsap.registerParameter(s);
			} else if (p.getType().equals("boolean")) {

				Switch s = new Switch(flaggedOption);
				for(String longTag : p.getLongTags()){
					if(!longTag.equals(""))
						s.setLongFlag(longTag);
				}
				for(String shortTag : p.getShortTags()){
					if(!shortTag.equals(""))
						s.setShortFlag(shortTag.charAt(0));
				}
				jsap.registerParameter(s);

			}
			//System.out.println(flaggedOption);
			//System.out.println(p);
		}	

		
		// Parse the command-line arguments.
		JSAPResult config = jsap.parse(args);

		// Display the helpful help in case trouble happens.
		if (!config.success()) {
			System.err.println();
			System.err.println(" " + jsap.getUsage());
			System.err.println();
			System.err.println(jsap.getHelp());
			System.exit(1);
		}
	
		
		// Assign to variables.
		for(Entry<String,Parameter> e : parameters.entrySet()){
			String flaggedOption = e.getKey();
			Parameter p = e.getValue();
			
			if(p.getType().equals("boolean")){
				boolean value = config.getBoolean(flaggedOption);
				p.setValue(value);
			}
			if(p.getType().equals("string")){
				String value = config.getString(flaggedOption);
				p.setValue(value);
			}
			//log.info(flaggedOption);
			log.info(p.toShortString());
		}
	}
	
}

class Parameter {
	String key;
	Object value;
	List<String> longTags;
	List<String> shortTags;
	String type;
	
	public Parameter(String key, Object value, List<String> longTags, List<String> shortTags, String type){
		this.key = key;
		this.value = value;
		this.longTags = longTags;
		this.shortTags = shortTags;
		this.type = type;
	}
	
	public void setValue(Object value){
		this.value = value;
	}

	public String getKey(){
		return key;
	}
	
	public Object getValue(){
		return value;
	}
	
	public List<String> getLongTags(){
		return longTags;
	}
	
	public List<String> getShortTags(){
		return shortTags;
	}
	public String getType(){
		return type;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(key+" ");
		sb.append(value+" ");
		sb.append(longTags+" ");
		sb.append(shortTags+" ");
		sb.append(type+" ");
		return sb.toString();
	}
	
	public String toShortString(){
		StringBuilder sb = new StringBuilder();
		sb.append(key+": ");
		sb.append(value+" ");
		return sb.toString();
	}
}