package ce.bhesab.dongchi.dongchiApi.endpoint.group.dto;

import java.util.List;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GroupRetrievalModel(
        Long id,
        @NotBlank String groupName,
        @Nullable @Size(max = 500) String description,
        /* estimated size of max 500KB */
        @Nullable @Size(max = 670000) String groupImage,

        @NotEmpty List<MemberRetrievalModel> otherMembers) {

}
