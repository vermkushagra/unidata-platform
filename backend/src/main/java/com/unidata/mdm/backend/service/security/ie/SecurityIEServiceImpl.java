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

package com.unidata.mdm.backend.service.security.ie;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.context.ExportContext;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.service.security.RoleServiceExt;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.security.LabelDef;
import com.unidata.mdm.security.RolePropertyDef;
import com.unidata.mdm.security.Security;
import com.unidata.mdm.security.UserDef;
import com.unidata.mdm.security.UserPropertyDef;

@Service
public class SecurityIEServiceImpl implements SecurityIEService {

    private static final String SECURITY = "security";

    private UserService userService;

    private RoleServiceExt roleService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleServiceExt roleService) {
        this.roleService = roleService;
    }

    @Override
    public void exportSecurityObjects(final Path rootPath, final ExportContext exportContext) throws IOException {
        final Path securityFolder = securityFolder(rootPath);

        final Security security = JaxbUtils.getSecurityFactory().createSecurity();

        if (exportContext.isExportUsers()) {
            final List<UserDTO> users = userService.loadAllUsers();
            if (CollectionUtils.isNotEmpty(users)) {
                security
                        .withUsers(
                                UserObjectsToXmlDefinitionsConverter.convertUsers(users, userService.loadUserPasswords())
                        )
                        .withUserProperties(
                                UserObjectsToXmlDefinitionsConverter.convertUserProperties(userService.getAllProperties())
                        );
            }
        }

        if (exportContext.isExportRoles()) {
            final List<RoleDTO> allRoles = roleService.loadAllRoles();
            if (CollectionUtils.isNotEmpty(allRoles)) {
                security
                        .withRoles(RoleObjectsToXmlDefinitionsConverter.convertRoles(allRoles))
                        .withRoleProperties(RoleObjectsToXmlDefinitionsConverter.convertRoleProperties(roleService.loadAllProperties()))
                        .withLabels(RoleObjectsToXmlDefinitionsConverter.convertSecurityLabels(roleService.getAllSecurityLabels()));
            }
        }

        final String result = JaxbUtils.marshalSecurity(security);
        final Path file = Files.createFile(Paths.get(securityFolder.toString(), SECURITY + ".xml"));
        write(result, file);
    }

    @Override
    public void importSecurityObjects(final Security security, boolean importRoles, boolean importUsers) {
        if (security == null) {
            return;
        }

        if (importRoles && CollectionUtils.isNotEmpty(security.getRoles())) {
            loadLabels(security.getLabels());
            final Map<String, Long> rolePropertiesCache = mergeMissingRoleProperties(security.getRoleProperties());
            final List<RoleDTO> roles = filterNotExistsResources(
                    RolesXmlDefinitionsToObjectsConverter.toDTOs(security.getRoles(), rolePropertiesCache)
            );
            roleService.removeRolesByName(
                    roles.stream().map(RoleDTO::getName).collect(Collectors.toList())
            );
            roles.forEach(roleService::create);
            roles.forEach(r->roleService.update(r.getName(), r));
//            addMissingLabels(roles, security.getRoles());
        }

        if (importUsers && CollectionUtils.isNotEmpty(security.getUsers())) {
            final Map<String, Long> userPropertiesCache = mergeMissingProperties(security.getUserProperties());
            final List<UserWithPasswordDTO> users =
                    UsersXmlDefinitionsToObjectsConverter.toDTOs(security.getUsers(), userPropertiesCache);
            userService.removeUsersByLogin(users.stream().map(UserDTO::getLogin).collect(Collectors.toList()));
            userService.saveUsers(users);
            userService.addUsersPasswords(
                    security.getUsers().stream()
                            .collect(Collectors.toMap(UserDef::getLogin, UserDef::getPasswords)));
        }
    }

    private List<RoleDTO> filterNotExistsResources(List<RoleDTO> roles) {
        final Set<String> existsResources = roleService.getAllSecuredResources().stream()
                .map(SecuredResourceDTO::getName)
                .collect(Collectors.toSet());
        return roles.stream()
                .peek(r -> r.setRights(
                        r.getRights().stream()
                                .filter(right -> existsResources.contains(right.getSecuredResource().getName()))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    private Map<String, Long> mergeMissingProperties(final List<UserPropertyDef> userProperties) {
        return merge(
                userProperties,
                () -> userService.getAllProperties(),
                UserPropertyDTO::getName,
                UserPropertyDTO::getId,
                UserPropertyDef::getName,
                UsersXmlDefinitionsToObjectsConverter::toDTO,
                (userPropertyDTO, userPropertyDef) ->
                        !userPropertyDTO.getDisplayName().equals(userPropertyDef.getDisplayName()),
                properties -> properties.forEach(userService::saveProperty)
        );
    }


//
//    private void addMissingLabels(List<RoleDTO> roles, List<RoleDef> rolesDefs) {
//        final Map<String, SecurityLabel> labelsCache = roleService.getAllSecurityLabels().stream()
//                .collect(Collectors.toMap(SecurityLabel::getName, Function.identity()));
//        final Map<String, RoleDTO> existsRoles = roles.stream()
//                .collect(Collectors.toMap(RoleDTO::getName, Function.identity()));
//        final Map<String, Map<String, List<LabelAttributeValueDef>>> rolesLabelsValues = rolesDefs.stream()
//                .collect(Collectors.toMap(
//                        RoleDef::getName,
//                        r -> r.getLabelsValues().stream()
//                                .collect(Collectors.groupingBy(LabelAttributeValueDef::getLabelName))
//                ));
//        rolesDefs.forEach(r -> {
//            final RoleDTO roleDTO = existsRoles.get(r.getName());
//            roleDTO.setSecurityLabels(
//                    r.getRoleLabels().stream()
//                            .map(labelsCache::get)
//                            .filter(Objects::nonNull)
//                            .peek(l ->
//                                    l.getAttributes().addAll(convertLabelValues(rolesLabelsValues, r, l))
//                            )
//                            .collect(Collectors.toList())
//            );
//            roleService.update(r.getName(), roleDTO);
//        });
//    }


    private void loadLabels(final List<LabelDef> labels) {
        final Set<String> existsResources = roleService.getSecuredResourcesFlatList().stream()
                .map(SecuredResourceDTO::getName)
                .collect(Collectors.toSet());
        final List<LabelDef> filteredLabels = labels.stream()
                .filter(l -> l.getAttributes().stream().allMatch(la -> existsResources.contains(la.getPath())))
                .collect(Collectors.toList());
        filteredLabels.stream()
                .map(LabelDef::getName)
                .forEach(roleService::deleteLabel);
        filteredLabels.stream()
                .map(RolesXmlDefinitionsToObjectsConverter::toDTO)
                .forEach(roleService::createLabel);
    }

    private Map<String, Long> mergeMissingRoleProperties(final List<RolePropertyDef> roleProperties) {
        return merge(
                roleProperties,
                () -> roleService.loadAllProperties(),
                RolePropertyDTO::getName,
                RolePropertyDTO::getId,
                RolePropertyDef::getName,
                RolesXmlDefinitionsToObjectsConverter::toDTO,
                (rolePropertyDTO, rolePropertyDef) ->
                        !rolePropertyDTO.getDisplayName().equals(rolePropertyDef.getDisplayName()),
                properties -> properties.forEach(roleService::saveProperty)
        );
    }

    private <K,V, LOC_TYPE, IN_TYPE> Map<K,V> merge(
            final List<IN_TYPE> inElements,
            final Supplier<List<LOC_TYPE>> localElementsProducer,
            final Function<LOC_TYPE, K> cacheKeyGenerator,
            final Function<LOC_TYPE, V> cacheValueGenerator,
            final Function<IN_TYPE, K> inValueKeyGenerator,
            final Function<IN_TYPE, LOC_TYPE> inValueToLocalValueGenerator,
            final BiPredicate<LOC_TYPE, IN_TYPE> applyAllPredicate,
            final Consumer<List<LOC_TYPE>> newElementsConsumer
    ) {
        final List<LOC_TYPE> allLocalElements = localElementsProducer.get();
        final Map<K, V> cache = new HashMap<>(
                allLocalElements.stream()
                        .collect(Collectors.toMap(cacheKeyGenerator, cacheValueGenerator))
        );
        if (CollectionUtils.isNotEmpty(inElements)) {
            final List<LOC_TYPE> newValues = inElements.stream()
                    .filter(el -> !cache.keySet().contains(inValueKeyGenerator.apply(el)))
                    .filter(el -> applyAllPredicate == null ||
                            allLocalElements.stream().allMatch(localEl -> applyAllPredicate.test(localEl, el)))
                    .map(inValueToLocalValueGenerator)
                    .collect(Collectors.toList());
            newElementsConsumer.accept(newValues);
            cache.putAll(
                    newValues.stream().collect(Collectors.toMap(cacheKeyGenerator, cacheValueGenerator))
            );
        }
        return cache;
    }

    private Path securityFolder(final Path rootPath) throws IOException {
        return Files.createDirectories(Paths.get(rootPath.toString(), SECURITY));
    }

    /**
     * Write.
     *
     * @param string
     *            the string
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void write(String string, Path file) throws IOException {
        try (final BufferedWriter bw =
                     new BufferedWriter(
                             new OutputStreamWriter(new FileOutputStream(file.toString()), "UTF-8")
                     )
        ) {
            bw.write(string);
        }
    }
}
