<div class="unidata-cfblock-title">{data.name} {data.nodeId}
    <div class="unidata-cfblock-remove">Удалить</div>
</div>
<div class="unidata-cfblock">
<div class="unidata-cfblock-inputs">
    <tpl for="inputPorts">
        <div class="unidata-cfblock-row">
            <div class="unidata-cfblock-port" title="{description} {dataType}" data-node-id="{parent.nodeId}"
                 data-port-datatype="{dataType}"
                 data-port-required="{required}"
                 data-port-name="{name}" id="unidata-cfblock-inputport-{parent.id}{name}">
                {description}
                <span>{dataType}</span>
            </div>
        </div>
    </tpl>
</div>
<div class="unidata-cfblock-outputs">
    <tpl for="outputPorts">
        <div class="unidata-cfblock-row">
            <div class="unidata-cfblock-port"
                 title="{description} {dataType}" data-node-id="{parent.nodeId}" data-port-datatype="{dataType}"
                 data-port-name="{name}"
                 data-port-required="{required}"
                 id="unidata-cfblock-outputport-{parent.id}{name}">
                {description}
                <span>{dataType}</span></div>
        </div>
    </tpl>
</div>
</div>