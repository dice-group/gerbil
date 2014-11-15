package org.aksw.gerbil.web.config.spring;

import java.io.IOException;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * <p>
 * This {@link TypeFilter} concatenates the given {@link TypeFilter}s using a
 * logic And. It is needed since the spring frameworks uses a logical Or to
 * concatenate filters.
 * </p>
 * 
 * <pre>
 * ClassPathScanningCandidateComponentProvider scanner = ...;
 * scanner.addIncludeFilter(typeFilter1);
 * scanner.addIncludeFilter(typeFilter2);
 * </pre>
 * <p>
 * The filter defined above retrieves all classes that match
 * <code>typeFilter1</code> OR <code>typeFilter2</code>. With the usage of the
 * {@link AndTypeFilter} this behavior can be changed to retrieving only classes
 * that match <code>typeFilter1</code> AND <code>typeFilter2</code>.
 * </p>
 * 
 * <pre>
 * ClassPathScanningCandidateComponentProvider scanner = ...;
 * scanner.addIncludeFilter(new AndTypeFilter(typeFilter1, typeFilter2));
 * </pre>
 * 
 * @author Michael Röder
 * 
 */
public class AndTypeFilter implements TypeFilter {

    private TypeFilter filters[];

    public AndTypeFilter(TypeFilter... filter) {
        this.filters = filter;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        boolean matches = true;
        int filterId = 0;
        while (matches && (filterId < filters.length)) {
            matches = filters[filterId].match(metadataReader, metadataReaderFactory);
            ++filterId;
        }
        return matches;
    }

}
