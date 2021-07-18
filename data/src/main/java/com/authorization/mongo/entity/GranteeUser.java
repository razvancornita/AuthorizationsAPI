package com.authorization.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GranteeUser {
    private String granteeName;
    private boolean canWrite;
    private boolean canRead;
}
