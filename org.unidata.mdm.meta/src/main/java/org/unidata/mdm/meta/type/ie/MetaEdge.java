package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;

/**
 * The Class MetaEdge.
 *
 * @param <T>
 *            the generic type
 */
public class MetaEdge<T extends MetaVertex> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new meta edge.
	 */
	private MetaEdge() {
		super();
	}

	/**
	 * Instantiates a new meta edge.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public MetaEdge(T from, T to) {
		this();
		this.from = from;
		this.to = to;
	}

	/** The from. */
	private T from;

	/** The to. */
	private T to;

	/** The existence. */
	private MetaExistence existence;

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public T getFrom() {
		return from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public T getTo() {
		return to;
	}

	/**
	 * Gets the existence.
	 *
	 * @return the existence
	 */
	public MetaExistence getExistence() {
		return existence;
	}

	/**
	 * Sets the existence.
	 *
	 * @param existence
	 *            the new existence
	 */
	public void setExistence(MetaExistence existence) {
		this.existence = existence;
	}

}
