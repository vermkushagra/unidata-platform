package com.unidata.mdm.backend.service.security.converters;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.service.security.po.ApiPO;
import com.unidata.mdm.backend.service.security.po.PasswordPO;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;



/**
 * The Class UserDTOToPOConverter.
 * @author ilya.bykov
 */
public class UserDTOToPOConverter {


    /**
     * Convert.
     *
     * @param source the source
     * @param target the target
     */
    public static void convert(UserWithPasswordDTO source, UserPO target) {
        if (source == null || target == null) {
            return;
        }
        target.setActive(source.isActive());
        target.setAdmin(source.isAdmin());
        if (source.getCreatedAt() != null) {
            target.setCreatedAt(new Timestamp(source.getCreatedAt().getTime()));
        }
        if (!StringUtils.isEmpty(source.getCreatedBy())) {
            target.setCreatedBy(source.getCreatedBy());
        }
        target.setEmail(source.getEmail());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        // Populate user first and last name, if is need.
        if(StringUtils.isEmpty(target.getFirstName())
                && StringUtils.isEmpty(target.getLastName())
                && StringUtils.isNotEmpty(source.getFullName())){
            target.setFirstName(StringUtils.substringBefore(source.getFullName(), " "));
            target.setLastName(StringUtils.substringAfter(source.getFullName(), " "));
        }
        target.setLogin(source.getLogin());
        target.setNotes(null);
        target.setExternal(source.isExternal());
        target.setSource(source.getSecurityDataSource());
        target.setApis(convertAPIs(source.getEndpoints()));
        if (source.getUpdatedAt() != null) {
            target.setUpdatedAt(new Timestamp(source.getUpdatedAt().getTime()));
        }
        if (!StringUtils.isEmpty(source.getUpdatedBy())) {
            target.setUpdatedBy(source.getUpdatedBy());
        }
        // password changed?
		if (!StringUtils.isEmpty(source.getPassword())) {
			target.getPassword().forEach(p->p.setActive(false));
			PasswordPO password =convertPassword(source.getPassword());
			password.setUser(target);
			target.getPassword().add(password);
		}
    }


    /**
     * Convert AP is.
     *
     * @param source the source
     * @return the list
     */
    private static List<ApiPO> convertAPIs(List<Endpoint> source) {
		if(source==null){
			return null;
		}
		List<ApiPO> target = new ArrayList<>();
		source.stream().forEach(a->{
			target.add(convertAPI(a));
		});
		return target;
	}


	/**
	 * Convert API.
	 *
	 * @param source the source
	 * @return the api PO
	 */
	private static ApiPO convertAPI(Endpoint source) {
		if (source == null) {
			return null;
		}
		ApiPO target = new ApiPO();
		target.setName(source.getName());
		target.setDisplayName(source.getDisplayName());
		target.setDescription(source.getDescription());
		return target;
	}


	/**
	 * Convert password.
	 *
	 * @param password
	 *            the password
	 * @return the password po
	 */
    private static PasswordPO convertPassword(String password) {
        PasswordPO target = new PasswordPO();
        target.setPasswordText(BCrypt.hashpw(password, BCrypt.gensalt()));
        target.setActive(true);
        target.setCreatedAt(new Timestamp(new Date().getTime()));
        target.setCreatedBy(SecurityUtils.getCurrentUserName());
        target.setUpdatedAt(new Timestamp(new Date().getTime()));
        target.setUpdatedBy(SecurityUtils.getCurrentUserName());
        return target;
    }

}
