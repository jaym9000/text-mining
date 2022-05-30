package ca.unbsj.cbakerlab.mutation_impact;

import java.util.LinkedList;



/**
 * 
 * CLASS MutationInfo
 *
 */
class MutationInfo implements Comparable, java.io.Serializable {
	MutationInfo(String pa, LinkedList<Mutation> mutations, int offset) {
		PA = pa;
		Mutations = mutations;
		Offset = offset;
	}
	
	MutationInfo(String serialized) {
		String[] split = serialized.split("#"); 
		PA = split[0];
		Mutations = new LinkedList<Mutation>();
		for(String mutationSerialized : split[1].split("_")){
			Mutations.add(new Mutation(mutationSerialized));
		}
		Offset = Integer.parseInt(split[2]);
	}
	
	public int compareTo(Object anotherMutationInfo) throws ClassCastException {
	    if (!(anotherMutationInfo instanceof MutationInfo))
	      throw new ClassCastException("A Mutation object expected.");  
	    return ((MutationInfo) anotherMutationInfo).Mutations.size() - this.Mutations.size();    
	}
	
	String PA;
	LinkedList<Mutation> Mutations;
	int Offset;
	
	String asString(){
		StringBuilder sb = new StringBuilder();
		sb.append(PA+" [");
		for(Mutation m : Mutations){
			sb.append(m.asString()+" ");
		}
		sb.append("] "+Offset);
		return sb.toString();
	}
	
	String serialize(){
		StringBuilder sb = new StringBuilder();
		sb.append(PA+"#");
		for(int i = 0; i<Mutations.size(); i++){
			sb.append(Mutations.get(i).serialize());
			if(i!=Mutations.size()-1) sb.append("_");
		}
		sb.append("#"+Offset);
		return sb.toString();
	}
	
	
}