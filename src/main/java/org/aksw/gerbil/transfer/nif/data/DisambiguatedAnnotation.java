package org.aksw.gerbil.transfer.nif.data;

public class DisambiguatedAnnotation extends AnnotationImpl {

	protected String uri;

	public DisambiguatedAnnotation(int startPosition, int length, String uri) {
		super(startPosition, length);
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisambiguatedAnnotation other = (DisambiguatedAnnotation) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
