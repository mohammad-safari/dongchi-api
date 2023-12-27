package ce.bhesab.dongchi.dongchiApi.service.group.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupNotFoundException extends Throwable {

    public GroupNotFoundException(Long groupId) {
        super(String.valueOf(groupId));
    }

}
