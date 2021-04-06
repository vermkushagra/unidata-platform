<div class="unidata-inputports">
    <tpl for="inputPorts">
        <div class="unidata-inputports-row"
             data-port-datatype="{dataType}"
             data-port-required="{required}"
             data-port-name="{name}"
             data-node-id="0" id="{endpointId}">
            <div class="unidata-inputports-port">
                <div class="unidata-inputport-item">
                    {description:htmlEncode}
                </div>
                <div class="unidata-inputport-datatype">{dataType}</div>
            </div>
        </div>
    </tpl>
</div>