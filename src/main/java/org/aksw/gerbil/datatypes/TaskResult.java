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

public class TaskResult {
	
	private Object resValue;
	private String resType;
	
	public TaskResult(Object resValue, String resType) {
		super();
		this.resValue = resValue;
		this.resType = resType;
	}
	public Object getResValue() {
		return resValue;
	}
	public void setResValue(Object resValue) {
		this.resValue = resValue;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	
	@Override
	public String toString() {
	    return resValue.toString();
	}
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resType == null) ? 0 : resType.hashCode());
        result = prime * result + ((resValue == null) ? 0 : resValue.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskResult other = (TaskResult) obj;
        if (resType == null) {
            if (other.resType != null)
                return false;
        } else if (!resType.equals(other.resType))
            return false;
        if (resValue == null) {
            if (other.resValue != null)
                return false;
        } else if (!resValue.equals(other.resValue))
            return false;
        return true;
    }
}
