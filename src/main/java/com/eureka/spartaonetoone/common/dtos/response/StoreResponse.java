package com.eureka.spartaonetoone.common.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class StoreResponse {

    @Getter
    public static class GetOwnerId {

        @JsonProperty("userId")
        UUID ownerId;
    }
}
