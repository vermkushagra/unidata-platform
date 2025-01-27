package com.unidata.mdm.backend.service.security.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserEndpointDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.service.security.po.ApiPO;
import com.unidata.mdm.backend.service.security.po.PasswordPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.springframework.util.CollectionUtils;


/**
 * The Class UserConverter.
 *
 * @author ilya.bykov
 */
public final class UserConverter {

	/**
	 * No instances.
	 */
	private UserConverter() {
		super();
	}

	/**
	 * Convert po.
	 *
	 * @param source
	 *            the source
	 * @return the user with password dto
	 */
	public static UserWithPasswordDTO convertPO(UserPO source) {

		if (source == null) {
			return null;
		}

		final UserWithPasswordDTO target = new UserWithPasswordDTO();
		target.setActive(source.isActive());
		target.setAdmin(source.isAdmin());
		if (source.getCreatedAt() != null) {
			target.setCreatedAt(source.getCreatedAt());
		}
		if (source.getUpdatedAt() != null) {
			target.setUpdatedAt(source.getUpdatedAt());
		}
		target.setEmail(source.getEmail());
		if (source.getLocale() != null) {
		    target.setLocale(new Locale(source.getLocale()));
		}
		target.setFirstName(source.getFirstName());
		target.setFullName(String.join(" ", source.getFirstName(), source.getLastName()));
		target.setLastName(source.getLastName());
		target.setLogin(source.getLogin());
		target.setUpdatedBy(source.getUpdatedBy());
		target.setCreatedBy(source.getCreatedBy());
		target.setSecurityLabels(SecurityUtils.convertSecurityLabels(source.getLabelAttributeValues()));
		target.setRoles(convertRoles(source.getRoles()));

		target.setExternal(source.isExternal());
		target.setSecurityDataSource(source.getSource());
		target.setEnpoints(convertAPIs(source.getApis()));

		if (source.getPassword() != null) {
			final Optional<PasswordPO> currentPassword = source.getPassword().stream().filter(PasswordPO::getActive)
					.findFirst();
			if (currentPassword.isPresent()) {
				final PasswordPO pwd = currentPassword.get();
				target.setPassword(pwd.getPasswordText());
				if (pwd.getUpdatedAt() != null) {
					target.setPasswordLastChangedAt(pwd.getUpdatedAt());
					target.setPasswordUpdatedBy(pwd.getCreatedBy());
				} else {
					target.setPasswordLastChangedAt(pwd.getCreatedAt());
					target.setPasswordUpdatedBy(pwd.getCreatedBy());
				}
			}
		}
		return target;
	}

	/**
	 * Convert AP is.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<Endpoint> convertAPIs(List<ApiPO> source) {
		if (source == null) {
			return null;
		}
		List<Endpoint> target = new ArrayList<>();
		source.stream().forEach(a -> {
			target.add(convertAPI(a));
		});
		return target;
	}

	/**
	 * Convert API.
	 *
	 * @param source the source
	 * @return the user APIDTO
	 */
	public static Endpoint convertAPI(ApiPO source) {
		if (source == null) {
			return null;
		}
		UserEndpointDTO target = new UserEndpointDTO();
		target.setCreatedAt(source.getCreatedAt());
		target.setCreatedBy(source.getCreatedBy());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setName(source.getName());
		target.setUpdatedAt(source.getUpdatedAt());
		target.setUpdatedBy(source.getUpdatedBy());
		return target;
	}

	/**
	 * Convert p os.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<UserDTO> convertPOs(List<UserPO> source) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<UserDTO> target = new ArrayList<>();
		for (UserPO userPO : source) {
			target.add(convertPO(userPO));
		}

		return target;
	}

	/**
	 * Convert roles po.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	private static List<Role> convertRoles(List<RolePO> source) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<Role> target = new ArrayList<>();
		for (RolePO po : source) {
			target.add(RoleConverter.convertRole(po));
		}

		return target;
	}
}
