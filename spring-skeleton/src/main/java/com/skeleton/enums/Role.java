package com.skeleton.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ADMIN("Administrador"),
    ROLE_USER("Usuário"),
    ROLE_MANAGER("Gerente");

    private final String description;
}
