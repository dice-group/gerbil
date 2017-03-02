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
package org.aksw.gerbil.dataset.impl.erd;

public class ERDTrec {
    
    private String line;
    private ERDTrec befor;
    private int line_number;
    private int count_column;
    
    public ERDTrec(String line, ERDTrec befor) {
        this.line = line;
        this.befor = befor;
        
        if (befor == null) {
            this.line_number = 0;
            this.count_column = 0;
        } else {
            line_number = this.befor.getLineNumber() + 1;
            count_column = this.befor.getColumnCount() + 1;
        }
    }
    
    public int getTextPosition(String text) {
        int pos = line.indexOf(text);
        if (pos > 0) pos = count_column + pos;
        return pos;
    }
    
    protected String getLine(){
        return this.line;
    }
    
    protected int getLineNumber(){
        return this.line_number;
    }
    
    protected int getColumnCount(){
        return this.count_column + line.length();
    }
}
