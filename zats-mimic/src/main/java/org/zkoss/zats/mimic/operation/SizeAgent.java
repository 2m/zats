/* SizeAgent.java

	Purpose:
		
	Description:
		
	History:
		May 4, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.operation;

/**
 * The agent of sizing operation.
 * @author pao
 */
public interface SizeAgent extends OperationAgent {
	/**
	 * To maximized size of a component.
	 * If the component isn't maximizable, it will throw exception. 
	 * @param maximized true indicated maximization.
	 */
	void maximize(boolean maximized);

	/**
	 * To minimized size of a component.
	 * If the component isn't minimizable, it will throw exception.
	 * @param minimized true indicated minimization.
	 */
	void minimize(boolean minimized);
	
	/**
	 * To resize component.
	 * If components didn't configure widths or heights, 
	 * the widths will change to 200px and the heights will change to 100px.
	 * @param width new width or specify -1 if no change.
	 * @param height new height or specify -1 if no change.
	 */
	void resize(int width , int height);
}
