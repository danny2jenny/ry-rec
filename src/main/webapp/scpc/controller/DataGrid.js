/**
 * Created by danny on 16-10-13.
 * 这里加载所有的数据的View、models、stores
 *
 * 注意，Models应该不用加载，引用的时候自动加载
 */



Ext.define('scpc.controller.DataGrid', {
    extend: 'Ext.app.Controller',
    views: [
        'panel.BaseCapability',
        'panel.Enterprise',
        'panel.EnterpriseStaff',
        'panel.CertificationEnterprise',
        'panel.StaffRequirement',
        'panel.EquipmentCapablity',
        'panel.AnnualCapacity',
        'panel.BadBehaviorType',
        'panel.EnterpriseExpertLine',
        'panel.EnterpriseExpertStation',
        'panel.EnterpriseEquipment',
        'panel.BadBehavior',
        'panel.CorrectedCapability',
        'panel.CapabilityClassify',
        'panel.CapabilityCompareProject',
        'panel.CapabilityCompareOutput',
        'panel.ProjectsPlain',
        'panel.ProjectsProgressYes',
        'panel.ProjectsProgressNo',
        'panel.SurplusCapability'
    ],
    stores: [
        'BaseCapability',
        'Enterprise',
        'EnterpriseStaff',
        'CertificationEnterprise',
        'StaffRequirement',
        'EquipmentCapablity',
        'AnnualCapacity',
        'BadBehaviorType',
        'EnterpriseExpertLine',
        'EnterpriseExpertStation',
        'EnterpriseEquipment',
        'BadBehavior',
        'CorrectedCapability',
        'CapabilityClassify',
        'CapabilityCompare',
        'CapabilityCompareOutput',
        'CapabilityCompareProject',
        'ProjectsPlain',
        'ProjectsProgressYes',
        'ProjectsProgressNo',
        'SurplusCapability'
    ],

    init: function () {

    },
});