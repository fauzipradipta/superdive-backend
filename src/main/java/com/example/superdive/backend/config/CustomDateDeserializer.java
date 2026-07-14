package com.example.superdive.backend.config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomDateDeserializer extends JsonDeserializer<Date>{
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        try {
            return new Date(formatter.parse(date).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
