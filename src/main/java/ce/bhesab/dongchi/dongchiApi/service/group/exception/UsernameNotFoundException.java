package ce.bhesab.dongchi.dongchiApi.service.group.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UsernameNotFoundException extends Throwable {

    public UsernameNotFoundException(String username) {
        super(username);
    }

}
