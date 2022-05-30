package ca.unbsj.cbakerlab.mutation_impact;



/**
 * CLASS Mutation
 * @author artjomk
 *
 */
public class Mutation implements Comparable, java.io.Serializable {
	public Mutation(String wt, int pos, String mut) {
		wildtype = wt;
		position = pos;
		mutation = mut;
	}
	
	public Mutation(String mutationStringInWNMFormat) {
		String[] parsed = Utils.parseWNMFormat(mutationStringInWNMFormat);
		wildtype = parsed[0];
		position = Integer.parseInt(parsed[1]);
		mutation = parsed[2];
	}
	
	public int compareTo(Object anotherMutation) throws ClassCastException {
	    if (!(anotherMutation instanceof Mutation))
	      throw new ClassCastException("A Mutation object expected.");
	    int anotherMutationPosition = ((Mutation) anotherMutation).position;  
	    return this.position - anotherMutationPosition;    
	}
	
	public boolean equals(Object anotherMutation) throws ClassCastException {
		if (!(anotherMutation instanceof Mutation))
			throw new ClassCastException("A Mutation object expected.");
		return ((Mutation)anotherMutation).wildtype.equals(this.wildtype) && ((Mutation)anotherMutation).position == this.position;
		
	}

	public String wildtype;
	public int position;
	public String mutation;
	
	public String asString(){
		return wildtype+position+mutation;
	}
	
	public String serialize(){
		return wildtype+position+mutation;
	}
}
