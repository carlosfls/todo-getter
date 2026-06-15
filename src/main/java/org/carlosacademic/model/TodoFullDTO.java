package org.carlosacademic.model;

public record TodoFullDTO(
        TodoDTO todo,
        UserDTO user
) {
}
