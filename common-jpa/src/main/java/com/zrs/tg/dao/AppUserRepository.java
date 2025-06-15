package com.zrs.tg.dao;

import com.zrs.tg.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findAppUserByTelegramUserId(Long id);

}
