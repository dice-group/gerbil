package org.aksw.gerbil.transfer.nif.data;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Relation;

public class RelationImpl extends AbstractMarkingImpl implements Relation {

	private Set<String> subjects = new HashSet<String>();
	private Set<String> predicates = new HashSet<String>();
	private Set<String> objects = new HashSet<String>();
	
	public RelationImpl() {
		
	}
	
	public RelationImpl(String subject, String predicate, String object) {
		this.setRelation(subject, predicate, object);
	}
	
	public RelationImpl(Set<String> subjects, Set<String> predicates, Set<String> objects) {
		this.subjects.addAll(subjects);
		this.predicates.addAll(predicates);
		this.objects.addAll(objects);
	}

	@Override
	public void setRelation(String subject, String predicate, String object) {
		this.subjects.add(subject);
		this.predicates.add(predicate);
		this.objects.add(object);
	}

	@Override
	public Set<String>[] getRelation() {
		Set<String>[] relation = new Set[3];
		relation[0]=subjects;
		relation[1]=predicates;
		relation[2]=objects;
		return relation;
	}

	@Override
	public Set<String> getSubject() {
		return subjects;
	}

	@Override
	public Set<String> getPredicate() {
		return predicates;
	}

	@Override
	public Set<String> getObject() {
		return objects;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new RelationImpl(subjects, predicates, objects);
	}

}
