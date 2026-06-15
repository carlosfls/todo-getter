package org.carlosacademic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TodoDTO(int userId, int id, String title, boolean completed) {
}
