package com.example.pogpa.mini_core_banking.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderTypeConverter implements AttributeConverter<GenderType, String> {
    @Override
    public String convertToDatabaseColumn(GenderType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public GenderType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GenderType.fromValue(dbData);
    }
}
