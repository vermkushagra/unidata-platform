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

package com.unidata.mdm.backend.service.audit;

import com.unidata.mdm.backend.service.audit.actions.AuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.auth.LoginAuthAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.auth.LogoutAuthAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.DeleteClassifierAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.DeleteDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.DeleteRelationAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.ExportDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.GetDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.ImportDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.MergeDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.RestoreDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.RestorePeriodAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.SplitDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertClassifierAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertRelationAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.model.ModelApplyAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.model.ModelDeleteAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.model.ModelDraftRemoveAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.model.ModelImportAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.model.ModelUpsertAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.user.CreateUserAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.user.UpdateUserAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.AcceptWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.AddAttachWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.AddCommentWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.AssignWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.DeclineWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.StartWorkflowAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.workflow.UnassignWorkflowAuditAction;

/**
 * @author Dmitry Kopin on 21.04.2017.
 */
public class AuditActions {
    // auth actions
    public static final AuditAction LOGIN = new LoginAuthAuditAction();
    public static final AuditAction LOGOUT = new LogoutAuthAuditAction();
    // data actions
    public static final AuditAction DATA_DELETE = new DeleteDataAuditAction();
    public static final AuditAction DATA_EXPORT = new ExportDataAuditAction();
    public static final AuditAction DATA_GET = new GetDataAuditAction();
    public static final AuditAction DATA_IMPORT = new ImportDataAuditAction();
    public static final AuditAction DATA_MERGE = new MergeDataAuditAction();
    public static final AuditAction DATA_SPLIT = new SplitDataAuditAction();
    public static final AuditAction DATA_RESTORE = new RestoreDataAuditAction();
    public static final AuditAction PERIOD_RESTORE = new RestorePeriodAuditAction();

    public static final AuditAction DATA_UPSERT = new UpsertDataAuditAction();
    public static final AuditAction DATA_UPSERT_RELATION = new UpsertRelationAuditAction();
    public static final AuditAction DATA_DELETE_RELATION = new DeleteRelationAuditAction();
    public static final AuditAction DATA_UPSERT_CLASSIFIER = new UpsertClassifierAuditAction();
    public static final AuditAction DATA_DELETE_CLASSIFIER = new DeleteClassifierAuditAction();
    // actions with user
    public static final AuditAction USER_CREATE = new CreateUserAuditAction();
    public static final AuditAction USER_UPDATE = new UpdateUserAuditAction();
    // workflow actions
    public static final AuditAction WORKFLOW_ACCEPT = new AcceptWorkflowAuditAction();
    public static final AuditAction WORKFLOW_ADD_ATTACH = new AddAttachWorkflowAuditAction();
    public static final AuditAction WORKFLOW_ADD_COMMENT = new AddCommentWorkflowAuditAction();
    public static final AuditAction WORKFLOW_ASSIGN = new AssignWorkflowAuditAction();
    public static final AuditAction WORKFLOW_DECLINE = new DeclineWorkflowAuditAction();
    public static final AuditAction WORKFLOW_START = new StartWorkflowAuditAction();
    public static final AuditAction WORKFLOW_UNASSIGN = new UnassignWorkflowAuditAction();
    //meta model actions
	public static final AuditAction META_DRAFT_DELETE = new ModelDeleteAuditAction();
	public static final AuditAction META_DRAFT_UPSERT = new ModelUpsertAuditAction();
	public static final AuditAction META_DRAFT_APPLY = new ModelApplyAuditAction();
	public static final AuditAction META_DRAFT_REMOVE = new ModelDraftRemoveAuditAction();
	public static final AuditAction META_IMPORT = new ModelImportAuditAction();
    /**
     * Constructor.
     */
    private AuditActions() {
        super();
    }
}
