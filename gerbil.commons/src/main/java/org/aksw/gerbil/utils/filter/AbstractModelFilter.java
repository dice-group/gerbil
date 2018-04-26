/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.utils.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public abstract class AbstractModelFilter<T extends Model> implements ModelFilter<T> {

	
	
    @Override
    public List<T> filterModel(Model model) {
        List<Model> filteredModels = new ArrayList<Model>(1);
        //FIXME this does only work if T == MODEL
        Model filteredModel = ModelFactory.createDefaultModel();
        
        Set<Resource> resIt = retrieveEntities(model);
        for (Resource entity : resIt) {
            if (isEntityGood(entity)) {
            	filteredModel.add(entity.listProperties().toList());
            }
        }
        filteredModels.add(filteredModel);
        
        return (List<T>) filteredModels;
    }

    public abstract Set<Resource> retrieveEntities(Model model);
    
	@Override
    public List<List<T>> filter2ListOfLists(Model model) {
        List<List<T>> filteredMarkings = new ArrayList<List<T>>(1);
        filteredMarkings.add(filterModel(model));
        return filteredMarkings;
    }
}
