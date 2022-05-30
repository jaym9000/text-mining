package ca.unbsj.cbakerlab.mutation_impact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MentionTable {
	private int count = 0;

	private List<MentionTableEntry> content;
	
	private Map<String,MentionTableEntry> indexById;
	private Map<String,MentionTableEntry> indexBySurfaceString;


	MentionTable(){
		content = new ArrayList<MentionTableEntry>();
		indexById = new HashMap<String,MentionTableEntry>();
		indexBySurfaceString = new HashMap<String,MentionTableEntry>();
	}
	
	public List<MentionTableEntry> getContent(){
		return content;
	}
	
	public void addStringAndLocation(String surfaceString, Long[] location){
		if(indexById.containsKey(surfaceString)){
			indexById.get(surfaceString).addSurfaceString(surfaceString);
			indexById.get(surfaceString).addLocation(location);
		}else{
			MentionTableEntry newEntry = new MentionTableEntry();
			count++;
			//newEntry.setId(Integer.toString(count));
			newEntry.addSurfaceString(surfaceString);
			newEntry.addLocation(location);
			indexBySurfaceString.put(surfaceString, newEntry);
			content.add(newEntry);
		}		
	}
	
	public void addIdAndStringAndLocation(String id, String surfaceString, Long[] location){
		if(indexById.containsKey(surfaceString)){
			indexById.get(surfaceString).addSurfaceString(surfaceString);
			indexById.get(surfaceString).addLocation(location);
		}else{
			MentionTableEntry newEntry = new MentionTableEntry();
			count++;
			//newEntry.setId(Integer.toString(count));
			newEntry.addSurfaceString(surfaceString);
			newEntry.addLocation(location);
			indexBySurfaceString.put(surfaceString, newEntry);
			content.add(newEntry);
		}		
	}
	
	public String toString(){
		return content.toString();
	}
	
	//public boolean containsId(String id){
		//if(ids.contains(id)) return true;
		//else return false;
	//}
	
}


class MentionTableEntry {
	private String id;
	private Set<String> surfaceStrings;
	private List<Long[]> locations;
	
	MentionTableEntry(){
		surfaceStrings = new HashSet();
		locations = new ArrayList();
	}
	
	public String getId(){
		return id;
	}
	
	public Set<String> getSurfaceStrings(){
		return surfaceStrings;
	}
	
	public List<Long[]> getLocations(){
		return locations;
	}
	
	
	public void setId(String id){
		this.id = id;
	}
	
	public void addSurfaceString(String surfaceString){
		surfaceStrings.add(surfaceString);
	}
	
	public void addLocation(Long[] location){
		locations.add(location);
	}
	
	public String toString(){
		return id+" "+surfaceStrings;
	}
	
}