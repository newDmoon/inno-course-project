package org.innowise.userservice.model.dto;

import java.util.List;

public record UserFilterDTO(
        List<Long> ids,
        String email
) {}
