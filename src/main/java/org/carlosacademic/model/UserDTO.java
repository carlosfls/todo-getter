package org.carlosacademic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(int id, String name, String username, String email) {
}
