
package br.com.fiap.easypark.entities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class YesNoConverter implements AttributeConverter<Boolean, String> {
    @Override public String convertToDatabaseColumn(Boolean value) { return (value != null && value) ? "Y" : "N"; }
    @Override public Boolean convertToEntityAttribute(String db) { return "Y".equalsIgnoreCase(db); }
}
