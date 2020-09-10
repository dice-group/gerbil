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
package org.aksw.gerbil.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The type of an experiment.
 *
 * @author m.roeder
 *
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExperimentType implements Describable {

   // MT("MT", "Machine Translation."),
    NLG("NLG", "NLG"),
    WebNLG_RDF2Text("WebNLG RDF2Text", "WebNLG RDF2Text"),
    WebNLG_Text2RDF("WebNLG Text2RDF", "WebNLG Text2RDF"),
    IR("IR", "IR"),
    Ent_Type("Ent_Type", "Ent_Type"),
    Partial("Partial", "Partial"),
    Strict("Strict", "Strict"),
    Exact("Exact", "Exact");

    private String label;
    private String description;

    ExperimentType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }

    public boolean equalsOrContainsType(ExperimentType type) {
        switch (this) {
         /*   case MT: {
                switch (type) {
                    case MT: {
                        return type == MT;
                    }
                }
            }

          */
            case NLG: {
                switch (type) {
                    case NLG: {
                        return type == NLG;
                    }
                }
            }
            case IR: {
                switch (type) {
                    case IR: {
                        return type == IR;
                    }
                }
            }
            case WebNLG_RDF2Text: {
                switch (type) {
                    case WebNLG_RDF2Text: {
                        return type == WebNLG_RDF2Text;
                    }
                }
            }
            case Ent_Type:{
                return type == Ent_Type;
            }
            case Partial:{
                return type == Partial;
            }
            case Strict:{
                return type == Strict;
            }
            case Exact:{
                return type == Exact;
            }
            case WebNLG_Text2RDF: {
                switch (type) {
                    case WebNLG_Text2RDF: {
                        return type == WebNLG_Text2RDF;
                    }
                    case Ent_Type:
                    case Partial:
                    case Strict:
                    case Exact:
                }
            }
        }
        return false;
    }



    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{name:");
        builder.append(name());
        builder.append(", label:");
        builder.append(getLabel());
        builder.append(", description:");
        builder.append(getDescription());
        builder.append('}');
        return builder.toString();
    }
}