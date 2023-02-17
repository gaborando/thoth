package com.thoth.server.model.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class JpaConverterJson implements AttributeConverter<Map<String, Object>, String> {

  private final ObjectMapper objectMapper;

  public JpaConverterJson(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String convertToDatabaseColumn(Map<String, Object> meta) {
    try {
      return objectMapper.writeValueAsString(meta);
    } catch (JsonProcessingException ex) {
      return null;
      // or throw an error
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    try {
      TypeReference<HashMap<String, Object>> typeRef
              = new TypeReference<HashMap<String, Object>>() {};
      return objectMapper.readValue(dbData, typeRef);
    } catch (IOException ex) {
      return null;
    }
  }

}