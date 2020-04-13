package uk.ac.ox.krr.logmap2.oaei;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.Searcher;

import uk.ac.ox.krr.logmap2.io.LogOutput;

/**
 * This class represents a specific behaviour to extract the information of instances in one of the tracks of the OAEI 2015.
 * The task requires to sum the values of specific data property assertions (for example: number of citations) or the occurrence of object property assertions 
 * (for example number of publications).
 *  
 * @author ernesto
 *
 */
public class OAEI2015InstanceProcessing {

	
	
	public int getPublicationCount(OWLOntology onto, OWLNamedIndividual indiv){
		
		OWLObjectProperty author_of = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLObjectProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#author_of"));
		
		OWLDataProperty publication_count = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLDataProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#publication_count"));
		
		//Set<OWLIndividual> publications = indiv.getObjectPropertyValues(author_of, onto);
//		Set<OWLObjectPropertyAssertionAxiom> objectPropertyAssertionAxioms = onto.getObjectPropertyAssertionAxioms(indiv);
		Stream<OWLObjectPropertyAssertionAxiom> axioms = onto.objectPropertyAssertionAxioms(indiv);
		Collection<OWLIndividual> publications = Searcher.values(axioms, author_of).collect(Collectors.toList());
		
		Collection<OWLLiteral> data_values;
		
		try {
			//They have not been grouped or there are not publications
			if (publications.size()>1 || publications.isEmpty()){
				return publications.size();
			}
			
			for (OWLIndividual pub : publications){
				
				//data_values = pub.getDataPropertyValues(publication_count, onto);

				data_values = Searcher.values(onto.dataPropertyAssertionAxioms(pub), publication_count).collect(Collectors.toList());
				
				for (OWLLiteral value : data_values){
					//if (value.isInteger()){
						return value.parseInteger();
					//}
				}
			}
			return 1;
			
		}
		catch (Exception e){
			LogOutput.printError("Error extracting information about publications.");
			return 0;
		}
		
	}
	
	
	public int getNumberOfCitations(OWLOntology onto, OWLNamedIndividual indiv){
		
		
		OWLObjectProperty author_of = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLObjectProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#author_of"));
		
		OWLDataProperty citations = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLDataProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#citations"));
		
		OWLDataProperty sum_of_citations = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLDataProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#sum_of_citations"));
		
		//Set<OWLIndividual> publications = indiv.getObjectPropertyValues(author_of, onto);
		Collection<OWLIndividual> publications = Searcher.values(onto.objectPropertyAssertionAxioms(indiv), author_of).collect(Collectors.toList());
		Collection<OWLLiteral> data_values;
		
		try {
			//There are not publications
			if (publications.isEmpty()){
				return 0;
			}
			
			int num_citations = 0;
			
			for (OWLIndividual pub : publications){				
				
				//Grouped citations: publications report
				//data_values = pub.getDataPropertyValues(sum_of_citations, onto);				
				data_values = Searcher.values(onto.dataPropertyAssertionAxioms(pub), sum_of_citations).collect(Collectors.toList());
				for (OWLLiteral value : data_values){
					//if (value.isInteger()){
						return value.parseInteger();
					//}
				}
				
				
				//We sum num citations in each publication
				//data_values = pub.getDataPropertyValues(citations, onto);
				data_values = Searcher.values(onto.dataPropertyAssertionAxioms(pub), citations).collect(Collectors.toList());
				for (OWLLiteral value : data_values){
					//if (value.isInteger()){
						num_citations += value.parseInteger();
					//}
				}
				
				
				
				
			}
			return num_citations;
			
		}
		catch (Exception e){
			LogOutput.printError("Error extracting information about publications.");
			return 0;
		}
	}
	
	public int getActiveFromYear(OWLOntology onto, OWLNamedIndividual indiv){
		
		
		OWLObjectProperty author_of = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLObjectProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#author_of"));
		
		OWLDataProperty active_from = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLDataProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#active_from"));
		
		OWLDataProperty year = onto.getOWLOntologyManager().getOWLDataFactory().
				getOWLDataProperty(IRI.create("http://islab.di.unimi.it/imoaei2015#year"));
										       
		//Set<OWLIndividual> publications = indiv.getObjectPropertyValues(author_of, onto);
		Collection<OWLIndividual> publications = Searcher.values(onto.objectPropertyAssertionAxioms(indiv), author_of).collect(Collectors.toList());
		Collection<OWLLiteral> data_values;
		
		
		int output_year=30000;
		
		try {
			//There are not publications
			if (publications.isEmpty()){
				return output_year;
			}
			
			for (OWLIndividual pub : publications){				
				
				//Grouped citations: publications report
				//data_values = pub.getDataPropertyValues(active_from, onto);
				data_values = Searcher.values(onto.dataPropertyAssertionAxioms(pub), active_from).collect(Collectors.toList());

				for (OWLLiteral value : data_values){
					return value.parseInteger();					
				}
				
				
				//We sum num citations in each publication
				//data_values = pub.getDataPropertyValues(year, onto);		
				data_values = Searcher.values(onto.dataPropertyAssertionAxioms(pub), year).collect(Collectors.toList());


				for (OWLLiteral value : data_values){
					if (value.parseInteger()<output_year)
						output_year=value.parseInteger();
				}
				
				
				
				
			}
			return output_year;
			
		}
		catch (Exception e){			
			LogOutput.printError("Error extracting information about publications.");
			return output_year;
		}
	}
	
	
	
	
	
	
}
