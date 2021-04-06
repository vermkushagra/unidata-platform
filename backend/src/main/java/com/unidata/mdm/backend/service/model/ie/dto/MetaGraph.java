package com.unidata.mdm.backend.service.model.ie.dto;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

/**
 * The Class MetaGraph.
 */
public class MetaGraph extends DirectedPseudograph<MetaVertex, MetaEdge<MetaVertex>> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id;
	
	/** The file name. */
	private String fileName;
	private boolean override;

	/**
	 * Instantiates a new meta graph.
	 *
	 * @param ef
	 *            the ef
	 */
	public MetaGraph(EdgeFactory<MetaVertex, MetaEdge<MetaVertex>> ef) {
		super(ef);

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}
}
