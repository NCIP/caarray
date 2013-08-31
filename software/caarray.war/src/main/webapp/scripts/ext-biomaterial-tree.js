disableNodesWithOtherNodeType = function(parent, nodeType) {
    parent.cascadeBy(function(node) {
        if (node.get('nodeType') != nodeType && node.get('checked') != undefined) {
            node.set('disabled', true);
        }
    });
};

Ext.define('Biomaterial.model.Node', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'disabled', type:'bool', defaultValue:false},
        {name: 'nodeType', type:'string'},
        {name: 'text', type:'string'},
        {name: 'sort', type:'string'},
        {name: 'entityId', type:'string'}
    ]
});

Ext.define('Biomaterial.store.Nodes', {
    extend: 'Ext.data.TreeStore',
    model: 'Biomaterial.model.Node',
    requires: 'Biomaterial.model.Node',
    listeners: {
        beforeload: function(store, operation) {
            operation.params["project.id"] = this.projectId;
            operation.params.nodeType = operation.node.get('nodeType');
        },
        load: function(store, node, records) {
            var rootNode = node.getOwnerTree().getRootNode();
            var checkedNode = rootNode.findChild("checked", true, true);
            if (checkedNode != null) {
                disableNodesWithOtherNodeType(node, checkedNode.get('nodeType'));
            }
        }
    },
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    root: {
        text: 'Experiment',
        nodeType: 'ROOT',
        sort: 'Experiment',
        draggable:false, // disable root node dragging
        id:'ROOT'
    }
});

Ext.define('Biomaterial.view.NodeTree', {
    extend: 'Ext.tree.TreePanel',
    plugins: ['disabledNode'],
    animate: true,
    autoScroll: true,
    hidden: true,
    hideMode: 'visibility',
    border: false,
    bodyStyle:'padding-top: 5px; padding-left: 15px',
    rootVisible: false,
    xtype: 'check-tree',
    listeners: {
        beforeselect: function() {
            return false;
        },
        checkchange: function(nd, checked) {
            var rootNode = nd.getOwnerTree().getRootNode();
            if (checked) {
                disableNodesWithOtherNodeType(rootNode, nd.get('nodeType'));
            } else if (!rootNode.findChild("checked", true, true)) {
                rootNode.cascadeBy(function(node) {
                    node.set('disabled', false);
                });
            }
        }
    }
});

Ext.define('Biomaterial.plugin.DisabledNode', {
    alias: 'plugin.disabledNode',
    extend: 'Ext.AbstractPlugin',
    init: function(tree) {
        var me = this;
        me.callParent(arguments);
        Ext.apply(tree.getView(), {
            getRowClass: function(record, index, rowParams, store) {
                return record.get('disabled') ? 'checkbox-disabled' : '';
            },
            onCheckboxChange: function(e, t) {
                var item = e.getTarget(this.getItemSelector(), this.getTargetEl());
                if (item) {
                    var record = this.getRecord(item);
                    if (record.get('disabled')) { return; }
                    
                    var value = !record.get('checked');
                    record.set('checked', value);
                    this.fireEvent('checkchange', record, value);
                }
            }
        });
    }
});
