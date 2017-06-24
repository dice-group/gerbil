package org.aksw.agdistis.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.util.Version;

public class LiteralAnalyzer extends Analyzer {

	private final Version matchVersion;

	/**
	 * Creates a new {@link SimpleAnalyzer}
	 * 
	 * @param matchVersion
	 *            Lucene version to match See {@link <a href="#version">above</a>}
	 */
	public LiteralAnalyzer(Version matchVersion) {
		this.matchVersion = matchVersion;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer source = new LowerCaseTokenizer();
		return new TokenStreamComponents(source, new ASCIIFoldingFilter(source));
	}
}