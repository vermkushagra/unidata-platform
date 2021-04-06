package com.unidata.mdm.integration.test;

import java.util.Map;
import java.util.Map.Entry;

import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.integration.exits.AuthenticationToken;
import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SampleCheckPropertyValuesImpl implements UpsertListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginUpdate(OriginRecord origin, ExecutionContext ctx) throws ExitException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginInsert(OriginRecord origin, ExecutionContext ctx) throws ExitException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginUpdate(OriginRecord origin, ExecutionContext ctx) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginInsert(OriginRecord origin, ExecutionContext ctx) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdateEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        dumpRolesCustomProperties(ctx);
        dumpUserCustomProperties(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInsertEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        dumpRolesCustomProperties(ctx);
        dumpUserCustomProperties(ctx);
    }

    @SuppressWarnings("unchecked")
    private void dumpRolesCustomProperties(ExecutionContext ctx) {

        // 1. Method
        User user = ctx.getAuthenticationToken().getUserDetails();
        for (Role role : user.getRoles()) {
            for (CustomProperty property : role.getProperties()) {
                System.out.println("Simple iteration -> Role property: " + property.getName() + " has value: " + property.getValue());
            }
        }

        // 2. Method
        Map<String, Role> roles = (Map<String, Role>) ctx.getAuthenticationToken()
                .getSecurityParams()
                .get(AuthenticationToken.SecurityParam.ROLES_MAP);

        for (Entry<String, Role> entry : roles.entrySet()) {
            for (CustomProperty property : entry.getValue().getProperties()) {
                System.out.println("Roles map iteration -> Role property: " + property.getName() + " has value: " + property.getValue());
            }
        }
    }

    private void dumpUserCustomProperties(ExecutionContext ctx) {

        // 1. Method
        User user = ctx.getAuthenticationToken().getUserDetails();
        for (CustomProperty property : user.getCustomProperties()) {
            System.out.println("User iteration -> User property: " + property.getName() + " has value: " + property.getValue());
        }
    }
}
