package com.authorization.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GranteeAccount {
    private String accountId;
    private boolean canWrite;
    private boolean canRead;
}
