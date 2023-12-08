package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import java.util.Set;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupCreateRequest(
        @NotBlank String groupName,
        @Nullable @Size(max = 500) String description,
        /* estimated size of max 500KB */
        @Nullable @Size(max = 66667) String groupImage,
        @Nullable Set<String> otherMembers) {

}
