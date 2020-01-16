/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.core.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.PasswordPolicyService;

/**
 * @author Alexey Tsarapkin
 */
@Service
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    //private static final Logger LOGGER = LoggerFactory.getLogger(PasswordPolicyServiceImpl.class);

    @Value("${unidata.password.policy.user.allow.change.expired.password:#{true}}")
    private Boolean userAllowChangeExpiredPassword;
    @Value("${unidata.password.policy.expiration.email.notification.period.days:#{10}}")
    private Integer notificationPeriodDays;
    @Value("${unidata.password.policy.admin.expiration.days:#{91}}")
    private Integer adminExpirationDays;
    @Value("${unidata.password.policy.user.expiration.days:#{181}}")
    private Integer userExpirationDays;
    @Value("${unidata.password.policy.min.length:#{0}}")
    private Integer minLength;
    @Value("${unidata.password.policy.regexp:#{null}}")
    private String regexpTemplate;
    @Value("${unidata.password.policy.regexp.example:#{null}}")
    private String regexpExample;
    @Value("${unidata.password.policy.check.last.repeat:#{0}}")
    private Integer lastRepeat;

    @PostConstruct
    public void init(){
        if(StringUtils.isBlank(regexpExample)){
            regexpExample = regexpTemplate;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String regexpExample() {
        return regexpExample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean regexpMatching(String password) {
        if (StringUtils.isBlank(regexpTemplate)) {
            return Boolean.TRUE;
        }
        return password.matches(regexpTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getNotificationPeriodDays() {
        return notificationPeriodDays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLengthEnough(String password) {
        return minLength != null ? StringUtils.length(password) >= minLength : Boolean.TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowChangeExpiredPassword(boolean admin) {
        return admin ? Boolean.TRUE : userAllowChangeExpiredPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpired(Instant from, boolean admin) {
        if (isUnlimitedExpiration(admin)) {
            return Boolean.FALSE;
        }

        Duration d = getRemainingExpiration(LocalDateTime.ofInstant(from, ZoneOffset.UTC), admin);

        return d.getSeconds() < 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getRemainingExpiration(LocalDateTime from, boolean admin) {
        LocalDateTime expirationTime = from.plusDays(admin ? adminExpirationDays : userExpirationDays);
        return Duration.between(LocalDateTime.now(), expirationTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLastRepeatCount() {
        return lastRepeat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnlimitedExpiration(boolean admin) {
        return admin ? adminExpirationDays == null || adminExpirationDays <= 0 : userExpirationDays == null || userExpirationDays <= 0;
    }

}
