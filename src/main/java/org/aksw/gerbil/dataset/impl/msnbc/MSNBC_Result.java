package org.aksw.gerbil.dataset.impl.msnbc;

import java.util.List;

public interface MSNBC_Result {

    public List<MSNBC_NamedEntity> getMarkings();
    
    public String getTextFileName();
}
