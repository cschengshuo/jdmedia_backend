package com.winsyo.jdmedia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 代理平台用户
 */
@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
public class User implements UserDetails {

  /**
   * ID
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  /**
   * 登录名
   */
  @Column
  private String username;

  /**
   * 密码
   */
  @Column
  private String password;

  /**
   * 用户角色列表
   */
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private List<Role> roles;

  /**
   * 账户是否启用
   */
  @Column(nullable = false)
  private boolean enabled;

  /**
   * 账户是否失效
   */
  @Column(nullable = false)
  private boolean expired;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<Role> roles = this.getRoles();
    List<SimpleGrantedAuthority> authorities = roles.stream().map((role) -> role.getRole()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    return authorities;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return !expired;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

}
