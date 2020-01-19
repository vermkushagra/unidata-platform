package org.unidata.mdm.system.service.impl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.service.RenderingService;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragmentContainer;
import org.unidata.mdm.system.type.rendering.InputFragmentRenderer;
import org.unidata.mdm.system.type.rendering.InputRenderingAction;
import org.unidata.mdm.system.type.rendering.InputSource;
import org.unidata.mdm.system.type.rendering.OutputFragmentRenderer;
import org.unidata.mdm.system.type.rendering.OutputRenderingAction;
import org.unidata.mdm.system.type.rendering.OutputSink;
import org.unidata.mdm.system.type.rendering.RenderingAction;
import org.unidata.mdm.system.type.rendering.RenderingResolver;

/**
 * @author Mikhail Mikhailov on Jan 15, 2020
 */
@Service
public class RenderingServiceImpl implements RenderingService {
    /**
     * Input renderers.
     */
    private final Map<InputRenderingAction, List<InputFragmentRenderer>> inputRenderers = new IdentityHashMap<>();
    /**
     * Output renderers.
     */
    private final Map<OutputRenderingAction, List<OutputFragmentRenderer>> outputRenderers = new IdentityHashMap<>();
    /**
     * The module service.
     */
    @Autowired
    private ModuleService moduleService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void renderInput(InputRenderingAction action, InputFragmentCollector<?> collector, InputSource source) {

        List<InputFragmentRenderer> renderers = inputRenderers.get(action);
        if (CollectionUtils.isNotEmpty(renderers)) {
            for (int i = 0; i < renderers.size(); i++) {
                InputFragmentRenderer ifr = renderers.get(i);
                ifr.render(collector, source);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderOutput(OutputRenderingAction action, OutputFragmentContainer container, OutputSink sink) {

        List<OutputFragmentRenderer> renderers = outputRenderers.get(action);
        if (CollectionUtils.isNotEmpty(renderers)) {
            for (int i = 0; i < renderers.size(); i++) {
                OutputFragmentRenderer ifr = renderers.get(i);
                ifr.render(container, sink);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadRendrerers() {

        List<InputRenderingAction> ira = new ArrayList<>();
        List<OutputRenderingAction> ora = new ArrayList<>();
        List<RenderingResolver> resolvers = new ArrayList<>();

        for (Module m : moduleService.getModules()) {
            RenderingResolver resolver = m.getRenderingResolver();
            if (Objects.nonNull(resolver)) {
                resolvers.add(resolver);
            }

            for (RenderingAction ra : m.getRenderingActions()) {
                switch (ra.actionType()) {
                case INPUT:
                    ira.add((InputRenderingAction) ra);
                    break;
                case OUTPUT:
                    ora.add((OutputRenderingAction) ra);
                    break;
                default:
                    break;
                }
            }
        }

        for (InputRenderingAction ir : ira) {

            List<InputFragmentRenderer> renderers = resolvers.stream()
                .map(r -> r.get(ir))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(renderers)) {
                inputRenderers.put(ir, renderers);
            }
        }

        for (OutputRenderingAction or : ora) {

            List<OutputFragmentRenderer> renderers = resolvers.stream()
                .map(r -> r.get(or))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(renderers)) {
                outputRenderers.put(or, renderers);
            }
        }
    }
}
