package org.aksw.gerbil.transfer.nif.data;

import java.util.LinkedList;
import java.util.List;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Relation;

public class RelationImpl extends AbstractMarkingImpl implements Relation {

	private Meaning subject;
	private Meaning predicate;
	private Meaning object;
	
	public RelationImpl() {
		
	}
	
	public RelationImpl(Relation relation) throws CloneNotSupportedException {
		this((Meaning)relation.getSubject().clone(), 
				(Meaning)relation.getPredicate().clone(), 
				(Meaning)relation.getObject().clone());
	}
	
	public RelationImpl(Meaning subject, Meaning predicate, Meaning object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public void setRelation(Meaning subject, Meaning predicate, Meaning object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public List<Meaning> getRelation() {
		List<Meaning> relation = new LinkedList<Meaning>();
		relation.add(subject);
		relation.add(predicate);
		relation.add(object);
		return relation;
	}

	@Override
	public Meaning getSubject() {
		return subject;
	}

	@Override
	public Meaning getPredicate() {
		return predicate;
	}

	@Override
	public Meaning getObject() {
		return object;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new RelationImpl((Meaning)subject.clone(), (Meaning)predicate.clone(), (Meaning)object.clone());
	}

	@Override 
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(subject).append(", ");
		builder.append(predicate).append(", ");
		builder.append(object);
		builder.append(')');
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object anotherObj) {
		if(anotherObj instanceof Relation) {
			Relation otherRel = (Relation) anotherObj;
			//check if sameAs applies
			boolean equal = this.subject.equals(otherRel.getSubject());
			equal &= this.predicate.equals(otherRel.getPredicate());
			equal &= this.object.equals(otherRel.getObject());
			return equal;
		}
		return false;
	}
	
}
