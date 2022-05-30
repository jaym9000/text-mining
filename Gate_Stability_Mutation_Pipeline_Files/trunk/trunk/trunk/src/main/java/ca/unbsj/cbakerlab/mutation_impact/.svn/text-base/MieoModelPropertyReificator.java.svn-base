package ca.unbsj.cbakerlab.mutation_impact;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class MieoModelPropertyReificator {

	/**
	 * Returns Property Reification Class (statement_of_mutation_effect resource)
	 * @param mutationApplication
	 * @param propertyChange
	 * @param model
	 * @return
	 */
	public static Resource reifyMutationApplicationCausesChange(
			Resource mutationApplication,
			Resource propertyChange,
			Model model) {
		//Resource statement_of_mutation_effect = model.createResource(Vocab.StatementOfMutationEffect);
		Resource statement_of_mutation_effect = Utils.createTimestampedInstance(Vocab.StatementOfMutationEffect,model);

		statement_of_mutation_effect.addProperty(Vocab.arg1, mutationApplication);
		statement_of_mutation_effect.addProperty(Vocab.arg2, propertyChange);
		return statement_of_mutation_effect;
	}

	public static Resource reifyPropertyChangeAppliesTo(
			Resource propertyChange, 
			Resource proteinProperty, 
			Model model) {
		Resource protein_property_change_application = model.createResource(Vocab.ProteinPropertyChangeApplication);
		protein_property_change_application.addProperty(Vocab.arg1, propertyChange);
		protein_property_change_application.addProperty(Vocab.arg2, proteinProperty);
		return protein_property_change_application;
	}

}
