/**
 * Экран "Классификаторы" (model)
 *
 * @author Sergey Shishigin
 * @date 2016-10-25
 */
Ext.define('Unidata.view.steward.cluster.merge.MergeModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.cluster.merge',

    data: {
        metaRecord: null,
        clusterRecord: null,
        matchingRule: null,
        matchingGroup: null,
        status: null,
        mergeDataRecordCount: null,
        winnerEtalonId: null,
        readOnly: null
    },

    formulas: {
        mergeTitle: {
            bind: {
                clusterRecord: '{clusterRecord}',
                matchingRule: '{matchingRule}',
                matchingGroup: '{matchingGroup}'
            },
            get: function (getter) {
                var title,
                    titleParts,
                    matchingGroup = getter.matchingGroup,
                    matchingRule  = getter.matchingRule,
                    DELIMITER     = ' | ';

                titleParts = [Unidata.i18n.t('cluster>cluster')];

                if (matchingGroup) {
                    titleParts.push(matchingGroup.get('name'));
                }

                if (matchingRule) {
                    titleParts.push(matchingRule.get('name'));
                }

                title = titleParts.join(DELIMITER);
                title = Ext.coalesceDefined(title, '');

                return title;
            }
        },
        mergeButtonVisible: {
            bind: {
                metaRecord: '{metaRecord}',
                status: '{status}',
                readOnly: '{readOnly}',
                mergeDataRecordCount: '{mergeDataRecordCount}'
            },
            get: function (getter) {
                var visible,
                    status               = getter.status,
                    readOnly             = getter.readOnly,
                    mergeDataRecordCount = getter.mergeDataRecordCount,
                    metaRecord           = getter.metaRecord,
                    entityName,
                    userHasUpdateRight,
                    MergeStatusConstant  = Unidata.view.steward.cluster.merge.MergeStatusConstant;

                if (!metaRecord) {
                    return false;
                }

                entityName = metaRecord.get('name');

                userHasUpdateRight = Unidata.Config.userHasRight(entityName, 'update');

                status               = status || MergeStatusConstant.NONE;
                mergeDataRecordCount = mergeDataRecordCount || 0;

                visible = !readOnly && userHasUpdateRight && mergeDataRecordCount > 1 && status !== MergeStatusConstant.MERGED;

                visible = Ext.coalesceDefined(visible, true);

                return visible;
            }
        },
        cancelButtonVisible: {
            bind: {
                status: '{status}'
            },
            get: function (getter) {
                var visible,
                    status              = getter.status,
                    MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant;

                status = status || MergeStatusConstant.NONE;

                visible = status !== MergeStatusConstant.MERGED;

                visible = Ext.coalesceDefined(visible, true);

                return visible;
            }
        },

        footerBarVisible: {
            bind: {
                mergeButtonVisible: '{mergeButtonVisible}',
                cancelButtonVisible: '{cancelButtonVisible}'
            },
            get: function (getter) {
                var mergeButtonVisible = getter.mergeButtonVisible,
                    cancelButtonVisible = getter.cancelButtonVisible,
                    footerBarVisible;

                footerBarVisible = mergeButtonVisible ||
                                   cancelButtonVisible;

                return footerBarVisible;
            }
        },

        openWinnerRecordButtonVisible: {
            bind: {
                winnerEtalonId: '{winnerEtalonId}'
            },
            get: function (getter) {
                var visible,
                    winnerEtalonId = getter.winnerEtalonId;

                visible = Boolean(winnerEtalonId);

                visible = Ext.coalesceDefined(visible, true);

                return visible;
            }
        }
    }
});
