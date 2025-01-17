package com.unidata.mdm.backend.service.cleanse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionEdgeFactory;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;

/**
 * Cleanse functions utility class.
 * @author ilya.bykov
 */
public class CFUtils {
	/**
	 * Path separator.
	 */
    private static final String SEPARATOR = ".";
	/**
	 * Default constructor.
	 */
	private CFUtils(){
		super();
	}

	/** The Constant EDGE_FACTORY. */
	private static final CompositeFunctionEdgeFactory EDGE_FACTORY = new CompositeFunctionEdgeFactory();

	/**
	 * Topological iterator.
	 *
	 * @param mdagRep
	 *            graph representation of composite cleanse function.
	 * @return the topological order iterator.
	 */
	public static final TopologicalOrderIterator<Node, NodeLink> topologicalIterator(CompositeFunctionMDAGRep mdagRep) {
		TopologicalOrderIterator<Node, NodeLink> iterator = new TopologicalOrderIterator<Node, NodeLink>(mdagRep);

		return iterator;
	}

	/**
	 * Find cycles in graph representation of composite cleanse function.
	 *
	 * @param mdagRep
	 *            graph representation of composite cleanse function.
	 * @return List with cycles(if any). If no cycles found return empty list.
	 */
	public static final List<List<Node>> findCycles(CompositeFunctionMDAGRep mdagRep) {
		CycleDetector<Node, NodeLink> cycleDetector = new CycleDetector<Node, NodeLink>(mdagRep);
		// Does graph which represents composite cleanse function contains any
		// cycles?
		if (!cycleDetector.detectCycles()) {
			return new ArrayList<List<Node>>();
		}
		// If found at least one cycle, all of them should be detected.
		SzwarcfiterLauerSimpleCycles<Node, NodeLink> cycleFinder = new SzwarcfiterLauerSimpleCycles<Node, NodeLink>();
		cycleFinder.setGraph(mdagRep);
		List<List<Node>> cycles = cycleFinder.findSimpleCycles();
		return cycles;

	}

	/**
	 * Convert cleanse function metamodel cleanse function description to graph
	 * form.
	 *
	 * @param function
	 *            composite cleanse function
	 * @return composite cleanse function graph representation.
	 */
	public static final CompositeFunctionMDAGRep convertToGraph(CompositeCleanseFunctionDef function) {
		Map<BigInteger, Node> nodes = new HashMap<BigInteger, Node>();
		List<Node> list = function.getLogic().getNodes();

		CompositeFunctionMDAGRep mdagRep = new CompositeFunctionMDAGRep(EDGE_FACTORY);
		for (Node node : list) {
			nodes.put(node.getNodeId(), node);
			mdagRep.addVertex(node);
		}
		List<NodeLink> links = function.getLogic().getLinks();
		for (NodeLink nodeLink : links) {
			mdagRep.addEdge(nodes.get(nodeLink.getFromNodeId()), nodes.get(nodeLink.getToNodeId()), nodeLink);
		}
		return mdagRep;
	}

	/**
	 * Extract cleanse function by path from the cleanse function group.
	 * @param currentPath cursor.
	 * @param toSearch path to search.
	 * @param toTraverse cleanse function group where search should be performed.
	 * @return cleanse function definition
	 */
	public static CleanseFunctionDef getFunction(String currentPath, String toSearch,  CleanseFunctionGroupDef toTraverse) {
    	if(toTraverse==null){
    		return null;
		}
		List<?> objs = toTraverse.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Object obj : objs) {
			if (obj instanceof CleanseFunctionDef) {
				CleanseFunctionDef cf = (CleanseFunctionDef) obj;
				if (StringUtils.equals(StringUtils.isEmpty(currentPath) ? cf.getFunctionName()
						: String.join(SEPARATOR, currentPath, cf.getFunctionName()), toSearch)) {
					return cf;
				}
			} else {
				CleanseFunctionGroupDef cfg = (CleanseFunctionGroupDef) obj;
				String prefix = StringUtils.isEmpty(currentPath) ? cfg.getGroupName()
						: String.join(SEPARATOR, currentPath, cfg.getGroupName());

				if (!StringUtils.isEmpty(prefix) && StringUtils.startsWith(toSearch, prefix)) {
					return getFunction(prefix, toSearch, cfg);
				}
			}
		}
    	return null;
    }


    /**
     * Initializes provided cleanse function.
     *
     * @param cleanseFunctionDef the cleanse function definition
     * @return the cleanse function as an executable object.
     */
    public static CleanseFunction createCleanseFunction(CleanseFunctionDef cleanseFunctionDef) {
        if (cleanseFunctionDef.getJavaClass() == null) {
            return null;
        }
        CleanseFunction cleanseFunction = null;
        try {
            cleanseFunction = (CleanseFunction) Class.forName(cleanseFunctionDef.getJavaClass()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
           new SystemRuntimeException("Unable to instantiate cleanse function with id {} and class {}",
                    ExceptionId.EX_SYSTEM_CLEANSE_BASIC_INIT_FAILED, cleanseFunctionDef.getFunctionName(),
                    cleanseFunctionDef.getJavaClass());
        }
        return cleanseFunction;
    }
}
