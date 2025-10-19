package graduation.project.DoDutch_server.domain.auth.model;

import graduation.project.DoDutch_server.domain.member.entity.Member;
import graduation.project.DoDutch_server.global.config.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String socialId;
    private Collection<? extends GrantedAuthority> authorities;

    @Setter
    private Map<String, Object> attributes;

    public static UserPrincipal create(Member member) {
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getRole()));
        return new UserPrincipal(
                member.getId(),
                member.getKakaoId(),
                authorities,
                null
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return socialId;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
