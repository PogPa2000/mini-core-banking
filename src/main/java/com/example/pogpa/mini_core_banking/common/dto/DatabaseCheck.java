package com.example.pogpa.mini_core_banking.common.dto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseCheck {
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void check() {

        Map<String, Object> result =
                jdbcTemplate.queryForMap("""
                    SELECT
                        current_database(),
                        current_schema()
                """);

        System.out.println("DATABASE = "
                + result.get("current_database"));

        System.out.println("SCHEMA = "
                + result.get("current_schema"));
    }
}
