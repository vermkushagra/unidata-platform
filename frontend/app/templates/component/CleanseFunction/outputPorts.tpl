<div class="unidata-outputports">
    <tpl for="outputPorts">
        <div class="unidata-outputports-row"
             data-port-datatype="{dataType}"
             data-port-required="{required}"
             data-port-name="{name}"
             data-node-id="1"
             id="{endpointId}">
            <div class="unidata-outputports-port">
                <div class="unidata-outputport-item">
                    {description:htmlEncode}
                </div>
                <div class="unidata-outputport-datatype">{dataType:htmlEncode}</div>
            </div>
        </div>
    </tpl>
</div>