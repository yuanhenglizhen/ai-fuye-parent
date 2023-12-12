package com.hanzaitu.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hanzaitu.common.utils.RegexUtils;

import java.io.IOException;
import java.util.Objects;

public class PhoneJsonSerializer extends JsonSerializer {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null && value instanceof String){

            if (!Objects.equals(value,"") && value.toString().length() > 5
                    && RegexUtils.isMobileExact(value.toString())){

                String phone = value.toString();
                phone = phone.substring(0, 3) + "******" + phone.substring(phone.length() - 2);
                gen.writeString(phone);
            } else {
                gen.writeObject(value);
            }
        } else {
            gen.writeObject(value);
        }
    }
}