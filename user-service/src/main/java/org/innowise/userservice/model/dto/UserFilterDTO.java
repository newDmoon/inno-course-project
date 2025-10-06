package org.innowise.userservice.model.dto;

import lombok.Data;

import java.util.ArrayList;

public record UserFilterDTO(
        ArrayList<Long> ids,
        String email
) {}
