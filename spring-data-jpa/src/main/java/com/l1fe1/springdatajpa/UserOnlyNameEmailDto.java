package com.l1fe1.springdatajpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserOnlyNameEmailDto {
    private String name;
    private String email;
}
