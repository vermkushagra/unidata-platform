package com.unidata.mdm.backend.api.rest.dto.meta.ie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class MetaGraphRO.
 * 
 * @author ilya.bykov
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaGraphRO {

	/** The override. */
	private boolean override;
	/** The id. */
	private String id;

	/** The file name. */
	private String fileName;

	/** The vertexes. */
	private List<MetaVertexRO> vertexes;

	/** The edges. */
	private List<MetaEdgeRO> edges;
	/**
	 * Vertex comparator.
	 */
	private static Comparator<MetaVertexRO> V_COMPARATOR = new Comparator<MetaVertexRO>() {

		@Override
		public int compare(MetaVertexRO o1, MetaVertexRO o2) {
			if (o1 == null || o2 == null || o1.getDisplayName() == null || o2.getDisplayName() == null) {
				return 0;
			}
			return o1.getDisplayName().compareTo(o2.getDisplayName());
		}
	};

	/**
	 * Instantiates a new meta graph RO.
	 */
	public MetaGraphRO() {

	}

	/**
	 * Instantiates a new meta graph RO.
	 *
	 * @param id
	 *            the id
	 * @param fileName
	 *            the file name
	 * @param vertexes
	 *            the vertexes
	 * @param edges
	 *            the edges
	 */
	public MetaGraphRO(String id, String fileName, List<MetaVertexRO> vertexes, List<MetaEdgeRO> edges) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.vertexes = vertexes;
		if (this.vertexes != null) {
			Collections.sort(this.vertexes, V_COMPARATOR);
		}
		this.edges = edges;
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
	 * @param id
	 *            the new id
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
	 * @param fileName
	 *            the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the vertexes.
	 *
	 * @return the vertexes
	 */
	public List<MetaVertexRO> getVertexes() {
		if (vertexes == null) {
			vertexes = new ArrayList<>();
		}
		return vertexes;
	}

	/**
	 * Sets the vertexes.
	 *
	 * @param vertexes
	 *            the new vertexes
	 */
	public void setVertexes(List<MetaVertexRO> vertexes) {
		this.vertexes = vertexes;
	}

	/**
	 * Gets the edges.
	 *
	 * @return the edges
	 */
	public List<MetaEdgeRO> getEdges() {
		if (edges == null) {
			edges = new ArrayList<>();
		}
		return edges;
	}

	/**
	 * Sets the edges.
	 *
	 * @param edges
	 *            the new edges
	 */
	public void setEdges(List<MetaEdgeRO> edges) {
		this.edges = edges;
	}

	/**
	 * Checks if is override.
	 *
	 * @return true, if is override
	 */
	public boolean isOverride() {
		return override;
	}

	/**
	 * Sets the override.
	 *
	 * @param override
	 *            the new override
	 */
	public void setOverride(boolean override) {
		this.override = override;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaGraphRO [id=" + id + ", fileName=" + fileName + ", vertexes=" + vertexes + ", edges=" + edges + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((vertexes == null) ? 0 : vertexes.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaGraphRO other = (MetaGraphRO) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (vertexes == null) {
			if (other.vertexes != null)
				return false;
		} else if (!vertexes.equals(other.vertexes))
			return false;
		return true;
	}

}
