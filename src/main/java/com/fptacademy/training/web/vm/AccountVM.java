package com.fptacademy.training.web.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountVM(String fullName, String avatarUrl, @JsonProperty("tokens") TokenVM tokenVM) {
}
