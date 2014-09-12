package org.aksw.gerbil.web;

import java.util.List;

public class Choice {
	private List<String> type;
	private List<String> news;
	private List<String> products;
	private List<String> annotator;
	public List<String> getAnnotator() {
		return annotator;
	}
	public void setAnnotator(List<String> annotator) {
		this.annotator = annotator;
	}
	public List<String> getType() {
		return type;
	}
	public void setType(List<String> type) {
		this.type = type;
	}
	public List<String> getNews() {
		return news;
	}
	public void setNews(List<String> news) {
		this.news = news;
	}
	public List<String> getProducts() {
		return products;
	}
	public void setProducts(List<String> products) {
		this.products = products;
	}
}
