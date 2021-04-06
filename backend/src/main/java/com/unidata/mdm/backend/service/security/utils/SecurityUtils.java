package com.unidata.mdm.backend.service.security.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.unidata.mdm.backend.service.security.po.LabelPO;
import org.apache.commons.lang3.ObjectUtils;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.context.ModelStorageSpecificContext;
import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.security.RightDTO;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;

/**
 * The Class SecurityUtils.
 */
public class SecurityUtils {

    /**
     * Default system right.
     */
    @SuppressWarnings("serial")
    public static final Right ALL_ENABLED
            = new ResourceSpecificRightDTO() {
        {
            this.setCreate(true);
            this.setDelete(true);
            this.setRead(true);
            this.setUpdate(true);
            this.setRestore(true);
            this.setMerge(true);
            this.setCreatedAt(new Date());
            this.setUpdatedAt(new Date());
            this.setCreatedBy(SecurityConstants.SYSTEM_USER_NAME);
            this.setUpdatedBy(SecurityConstants.SYSTEM_USER_NAME);
        }
    };

    /**
     * Default system right.
     */
    @SuppressWarnings("serial")
    public static final Right ALL_DISABLED
            = new ResourceSpecificRightDTO() {
        {
            this.setCreate(false);
            this.setDelete(false);
            this.setRead(false);
            this.setUpdate(false);
            this.setRestore(false);
            this.setMerge(false);
            this.setCreatedAt(new Date());
            this.setUpdatedAt(new Date());
            this.setCreatedBy(SecurityConstants.SYSTEM_USER_NAME);
            this.setUpdatedBy(SecurityConstants.SYSTEM_USER_NAME);
        }
    };

    /**
     * Admin system management resource name.
     */
    public static final String ADMIN_SYSTEM_MANAGEMENT_RESOURCE_NAME = "ADMIN_SYSTEM_MANAGEMENT";

    /**
     * Admin data management resource name.
     */
    public static final String ADMIN_DATA_MANAGEMENT_RESOURCE_NAME = "ADMIN_DATA_MANAGEMENT";

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);
    /**
     * Security service instance.
     */
    private static SecurityServiceExt ssvc;

    /**
     * Default storage name to try in case no storageId was supplied. TODO
     * Temporary solution. Read from security context.
     */
    public static final String DEFAULT_STORAGE_NAME = "default";
    /**
     * Unidata security data source.
     */
    public static final String UNIDATA_SECURITY_DATA_SOURCE = "UNIDATA";

    /**
     * Instantiates a new security utils.
     */
    private SecurityUtils() {
        super();
    }

    public static SecurityToken getSecurityTokenForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof SecurityToken) {
            return (SecurityToken) authentication.getDetails();
        }
        return null;
    }

    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ac) {
        try {
            ssvc = ac.getBean(SecurityServiceExt.class);
        } catch (Exception exc) {
            LOGGER.warn("Security service bean GET. Exception caught.", exc);
        }
    }

    /**
     * Gets current user.
     *
     * @return current user or "SYSTEM" if the context was not properly
     * initialized
     */
    public static String getCurrentUserName() {
        // Spring context may be null though while being used by tools
        Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        return auth != null ? auth.getName() : SecurityConstants.SYSTEM_USER_NAME;
    }

    /**
     * Gets current token.
     *
     * @return token or null
     */
    public static String getCurrentUserToken() {
        // Spring context may be null though while being used by tools
        Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null;
        return auth != null ? auth.getCredentials().toString() : null;
    }

    /**
     * Gets the storage ID for the current user. TODO implement via standard
     * spring {@link Authentication} mechanism.
     *
     * @return storage Id.
     */
    public static String getCurrentUserStorageId() {
        return SecurityUtils.DEFAULT_STORAGE_NAME;
    }

    /**
     * Direct approver (no pending versions) check.
     *
     * @return
     */
    public static boolean isAdminUser() {
        String token = getCurrentUserToken();
        // Tool
        if (token == null || ssvc == null) {
            return true;
        }

        User user = ssvc.getUserByToken(token);
        return user.isAdmin() || user.getRoles().stream().anyMatch(r -> StringUtils.equals(r.getName(), "ADMIN"));
    }

    /**
     * Gets storage id either from context, or default, if none defined in the context.
     *
     * @param ctx the context
     * @return the id
     */
    public static String getStorageId(ModelStorageSpecificContext ctx) {
        return Objects.nonNull(ctx.getStorageId()) ? ctx.getStorageId() : getCurrentUserStorageId();
    }

    /**
     * Gets security labels for resource.
     *
     * @param name the name of the resource.
     * @return labels or empty list
     */
    public static List<SecurityLabel> getSecurityLabelsForResource(String name) {

        String token = getCurrentUserToken();

        // Tool
        if (token == null || ssvc == null) {
            return Collections.emptyList();
        }

        // User
        List<SecurityLabel> labels = null;
        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {

            if (tokenObj.getUser().isAdmin()) {
                return Collections.emptyList();
            }

            labels = tokenObj.getLabelsMap().get(name);
        }

        return labels == null ? Collections.emptyList() : labels;
    }

    /**
     * Gets rights for a resource.
     *
     * @param name the name of the resource
     * @return right
     */
    public static Right getRightsForResource(String name) {

        String token = getCurrentUserToken();
        // Tool
        // todo remove after add token to all operations
        if (token == null || ssvc == null) {
            return ALL_ENABLED;
        }

        // User
        Right right = null;
        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {

            if (tokenObj.getUser().isAdmin()) {
                return ALL_ENABLED;
            }

            right = tokenObj.getRightsMap().get(name);
        }
        return right;
    }

    /**
     * Filter resources list by rights
     *
     * @param resourceNames list names list for resources
     * @return filtered list
     */
    public static List<String> filterResourcesByRights(Collection<String> resourceNames, Predicate<Right> toCheck) {
        List<String> result = new ArrayList<>();
        String token = getCurrentUserToken();
        // Tool
        if (token == null || ssvc == null) {
            return result;
        }

        SecurityToken tokenObj = ssvc.getTokenObjectByToken(token);
        if (tokenObj != null) {
            if (tokenObj.getUser().isAdmin()) {
                result.addAll(resourceNames);
            } else {
                result = resourceNames
                        .stream()
                        .filter(s -> toCheck.test(ObjectUtils.defaultIfNull(tokenObj.getRightsMap().get(s), ALL_DISABLED)))
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    /**
     * Gets rights for a resource.
     *
     * @param name the name of the resource
     * @return right
     */
    public static Right getRightsForResourceWithDefault(String name) {
        Right right = getRightsForResource(name);
        return right == null ? ALL_DISABLED : right;
    }

    /**
     * Calculates record state as {@link Right} object.
     *
     * @param name          name of the resource.
     * @param status        current status of the record or relation
     * @param state         current approval state of the record or relation
     * @param hasEditTasks  whether current user has edit tasks
     * @param isContainment if this rights object is calculated for a containment link
     * @return {@link Right} object
     */
    public static ResourceSpecificRightDTO calculateRightsForTopLevelResource(
            String name, RecordStatus status, ApprovalState state, boolean hasEditTasks, boolean isContainment) {

        // 1. Get rights, defined by roles
        Right rights = SecurityUtils.getRightsForResourceWithDefault(name);

        // 2. Check status. Disable Edit/Delete operations for already deleted/merged records.
        if (status == RecordStatus.INACTIVE || status == RecordStatus.MERGED) {

            ResourceSpecificRightDTO result = new ResourceSpecificRightDTO(rights);
            result.setDelete(false);
            result.setUpdate(false);
            result.setMerge(false);

            // 2.1 Check approval state. Prohibit Restore operation for pending records
            if (state == ApprovalState.PENDING) {
                result.setRestore(false);
            } else {
                result.setRestore(status == RecordStatus.INACTIVE && rights.isCreate() && rights.isUpdate());
            }

            return result;
        }

        // 3. Check approval state for pending active records
        if (state == ApprovalState.PENDING && (rights.isUpdate() || rights.isDelete() || rights.isCreate())) {

            ResourceSpecificRightDTO result = new ResourceSpecificRightDTO(rights);
            if (isContainment) {
                result.setDelete(rights.isDelete() && hasEditTasks);
            } else {
                result.setDelete(false);
            }

            result.setCreate(rights.isCreate() && hasEditTasks);
            result.setUpdate(rights.isUpdate() && hasEditTasks);

            return result;
        }

        ResourceSpecificRightDTO result = new ResourceSpecificRightDTO(rights);
        result.setMerge(state != ApprovalState.PENDING
                && result.isUpdate()
                && CollectionUtils.isEmpty(getSecurityLabelsForResource(name)));

        return result;
    }

    /**
     * Creates the rights.
     *
     * @param list the list
     * @return the list
     */
    public static Map<String, Right> createRightsMap(List<Role> list) {

        final Map<String, Right> rights = new HashMap<>();
        for (Role role : list) {

            Map<String, Right> portion = extractRightsMap(role.getRights());
            for (Entry<String, Right> entry : portion.entrySet()) {

                final RightDTO toUpdate = (RightDTO) rights.get(entry.getKey());
                if (Objects.nonNull(toUpdate)) {
                    toUpdate.setCreate(entry.getValue().isCreate() || toUpdate.isCreate());
                    toUpdate.setUpdate(entry.getValue().isUpdate() || toUpdate.isUpdate());
                    toUpdate.setDelete(entry.getValue().isDelete() || toUpdate.isDelete());
                    toUpdate.setRead(entry.getValue().isRead() || toUpdate.isRead());
                } else {
                    rights.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return rights;
    }

    /**
     * Transforms rights list to a map.
     *
     * @param rts list of rights objects
     * @return map
     */
    public static Map<String, Right> extractRightsMap(List<Right> rts) {

        final Map<String, Right> rights = new HashMap<>();
        for (final Right right : rts) {
            if (rights.containsKey(right.getSecuredResource().getName())) {
                final RightDTO toUpdate = (RightDTO) rights.get(right.getSecuredResource().getName());
                toUpdate.setSecuredResource(right.getSecuredResource());
                toUpdate.setCreate(right.isCreate() || toUpdate.isCreate());
                toUpdate.setUpdate(right.isUpdate() || toUpdate.isUpdate());
                toUpdate.setDelete(right.isDelete() || toUpdate.isDelete());
                toUpdate.setRead(right.isRead() || toUpdate.isRead());
            } else {
                final RightDTO toCreate = new RightDTO();
                toCreate.setSecuredResource(right.getSecuredResource());
                toCreate.setCreate(right.isCreate());
                toCreate.setUpdate(right.isUpdate());
                toCreate.setDelete(right.isDelete());
                toCreate.setRead(right.isRead());
                rights.put(right.getSecuredResource().getName(), toCreate);
            }
        }
        return rights;
    }

    /**
     * Returns mapped view of the roles list (k -> role name, v -> role itself)
     *
     * @param roles the list
     * @return map
     */
    public static Map<String, Role> createRolesMap(List<Role> roles) {
        return roles.stream().collect(Collectors.toMap(Role::getName, r -> r));
    }

    /**
     * Collects security labels, grouped by resource names.
     *
     * @param roles the roles list
     * @return map
     */
    public static final Map<String, List<SecurityLabel>> createLabelsMap(List<Role> roles) {

        Map<String, List<SecurityLabel>> labels = new HashMap<>();
        for (Role r : roles) {

            List<SecurityLabel> sls = r.getSecurityLabels();
            if (CollectionUtils.isEmpty(sls)) {
                continue;
            }

            Map<String, List<SecurityLabel>> portion = extractLabelsMap(sls);
            for (Entry<String, List<SecurityLabel>> entry : portion.entrySet()) {
                List<SecurityLabel> existing = labels.get(entry.getKey());
                if (Objects.isNull(existing)) {
                    labels.put(entry.getKey(), entry.getValue());
                } else {
                    existing.addAll(entry.getValue());
                }
            }
        }

        return labels;
    }

    /**
     * Extracts labels and returns them a map.
     *
     * @param sls labels list
     * @return map
     */
    public static Map<String, List<SecurityLabel>> extractLabelsMap(Collection<SecurityLabel> sls) {

        Map<String, List<SecurityLabel>> labels = new HashMap<>();
        for (SecurityLabel l : sls) {

            if (CollectionUtils.isEmpty(l.getAttributes())) {
                continue;
            }

            final String path = l.getAttributes()
                    .get(0)
                    .getPath();

            if (path == null) {
                continue;
            }

            String entityName = path
                .substring(0,
                    path.indexOf('.'));

            List<SecurityLabel> collected = labels.computeIfAbsent(entityName, k -> new ArrayList<>());

            collected.add(l);
        }

        return labels;
    }


    public static Collection<SecurityLabel> mergeSecurityLabels(final List<SecurityLabel> securityLabels1, final List<SecurityLabel> securityLabels2) {
        return Stream.concat(securityLabels1.stream(), securityLabels2.stream())
                .filter(l -> CollectionUtils.isNotEmpty(l.getAttributes()))
                .reduce(new HashMap<>(), SecurityUtils::mergeLabel, SecurityUtils::mergeMaps)
                .values();
    }

    private static Map<Integer, SecurityLabel> mergeLabel(Map<Integer, SecurityLabel> labels, SecurityLabel securityLabel) {
        final Integer key = generateKey(securityLabel);
        labels.putIfAbsent(key, securityLabel);
        return labels;
    }

    private static int generateKey(SecurityLabel securityLabel) {
        return (securityLabel.getName() + ";" + securityLabel.getAttributes().stream()
                .sorted(Comparator.comparing(SecurityLabelAttribute::getName))
                .map(SecurityLabelAttribute::getValue)
                .collect(Collectors.joining(";")))
                .hashCode();
    }

    private static Map<Integer, SecurityLabel> mergeMaps(
            final Map<Integer, SecurityLabel> labels1,
            final Map<Integer, SecurityLabel> labels2
    ) {
        labels2.forEach(labels1::putIfAbsent);
        return labels1;
    }


    /**
     * Convert security labels.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabel> convertSecurityLabels(final List<LabelAttributeValuePO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        final List<ImmutablePair<Integer, SecurityLabel>> target = new ArrayList<>();
        source.forEach(s -> target.add(convertSecurityLabel(s)));

        final List<ImmutablePair<Integer, SecurityLabel>> targetCombined = new ArrayList<>();
        for (ImmutablePair<Integer, SecurityLabel> securityLabel : target) {
            final Optional<ImmutablePair<Integer, SecurityLabel>> sld = targetCombined.stream()
                    .filter(t -> (StringUtils.equals(t.getRight().getName(), securityLabel.getRight().getName()))
                            && Objects.equals(t.getLeft(), securityLabel.getKey()))
                    .findFirst();
            if (sld.isPresent()) {
                sld.get().getRight().getAttributes().addAll(securityLabel.getRight().getAttributes());
            } else {
                targetCombined.add(securityLabel);
            }
        }

        return targetCombined.stream().map(ImmutablePair::getRight)
                .collect(Collectors.toList());
    }

    /**
     * Convert security label.
     *
     * @param source
     *            the source
     * @return the immutable pair
     */
    private static ImmutablePair<Integer, SecurityLabel> convertSecurityLabel(final LabelAttributeValuePO source) {
        if (source == null) {
            return null;
        }
        final SecurityLabelDTO target = new SecurityLabelDTO();
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setName(source.getLabelAttribute().getLabel().getName());
        target.setDisplayName(source.getLabelAttribute().getLabel().getDisplayName());
        final SecurityLabelAttributeDTO attributeDTO = new SecurityLabelAttributeDTO();
        attributeDTO.setId(source.getLabelAttribute().getId());
        attributeDTO.setName(source.getLabelAttribute().getName());
        attributeDTO.setValue(source.getValue());
        attributeDTO.setPath(source.getLabelAttribute().getPath());
        final List<SecurityLabelAttribute> attributeDTOs = new ArrayList<>();
        attributeDTOs.add(attributeDTO);
        target.setAttributes(attributeDTOs);
        return new ImmutablePair<>(source.getGroup(), target);
    }
}
