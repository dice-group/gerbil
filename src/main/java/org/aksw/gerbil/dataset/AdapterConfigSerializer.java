package org.aksw.gerbil.dataset;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;

import java.io.IOException;

@JsonSerialize(using = AdapterConfigSerializer.class)
public class AdapterConfigSerializer extends StdSerializer<AbstractAdapterConfiguration> {


    public AdapterConfigSerializer(Class<AbstractAdapterConfiguration> t) {
        super(AbstractAdapterConfiguration.class);
    }

    @Override
    public void serialize(AbstractAdapterConfiguration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());
        gen.writeStringField("group", value.getGroup());
        gen.writeEndObject();
    }
}
