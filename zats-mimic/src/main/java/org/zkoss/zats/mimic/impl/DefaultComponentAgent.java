/* DefaultComponentAgent.java

	Purpose:
		
	Description:
		
	History:
		Mar 20, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zats.mimic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.Conversation;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.PageAgent;
import org.zkoss.zats.mimic.Searcher;
import org.zkoss.zats.mimic.impl.operation.OperationAgentBuilder;
import org.zkoss.zats.mimic.impl.operation.OperationAgentManager;
import org.zkoss.zats.mimic.operation.CheckAgent;
import org.zkoss.zats.mimic.operation.ClickAgent;
import org.zkoss.zats.mimic.operation.FocusAgent;
import org.zkoss.zats.mimic.operation.OperationAgent;
import org.zkoss.zats.mimic.operation.SelectAgent;
import org.zkoss.zats.mimic.operation.TypeAgent;
import org.zkoss.zk.ui.Component;

/**
 * The default implement of component agent. This performs operations through
 * {@link OperationAgentManager}.
 * 
 * @author pao
 */
public class DefaultComponentAgent implements ComponentAgent {

	private PageAgent pageAgent;
	private Component comp;

	public DefaultComponentAgent(PageAgent pageAgent, Component component) {
		this.pageAgent = pageAgent;
		this.comp = component;
	}

	public String getId() {
		return comp.getId();
	}

	public String getType() {
		return comp.getDefinition().getName();
	}

	public Object getAttribute(String name) {
		return comp.getAttribute(name);
	}

	public Map<String, Object> getAttributes() {
		return comp.getAttributes();
	}

	public String getUuid() {
		return comp.getUuid();
	}

	public List<ComponentAgent> getChildren() {
		List<Component> children = comp.getChildren();
		List<ComponentAgent> agents = new ArrayList<ComponentAgent>(
				children.size());
		for (Component child : children)
			agents.add(new DefaultComponentAgent(pageAgent, child));
		return agents;
	}

	public ComponentAgent getChild(int index) {
		Component child = (Component) comp.getChildren().get(index);
		return child != null ? new DefaultComponentAgent(pageAgent, child) : null;
	}

	public ComponentAgent getParent() {
		Component parent = comp.getParent();
		return parent != null ? new DefaultComponentAgent(pageAgent, parent)
				: null;
	}

	public Conversation getConversation() {
		return getDesktop().getConversation();
	}

	public DesktopAgent getDesktop() {
		return pageAgent.getDesktop();
	}

	public PageAgent getPage() {
		return pageAgent;
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Class<T> clazz) {
		if (OperationAgent.class.isAssignableFrom(clazz)) {
			Class<OperationAgent> opc = (Class<OperationAgent>) clazz;
			OperationAgentBuilder<OperationAgent> builder = OperationAgentManager.getBuilder(
					comp, opc);
			if (builder != null)
				return (T) builder.getOperation(this);
		} else if (clazz.isInstance(comp)) {
			return (T) comp;
		}
		throw new AgentException(getType() + " doesn't support "+ clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> boolean is(Class<T> clazz) {
		if (OperationAgent.class.isAssignableFrom(clazz)) {
			Class<OperationAgent> opc = (Class<OperationAgent>) clazz;
			OperationAgentBuilder<OperationAgent> builder = OperationAgentManager.getBuilder(
					comp, opc);
			return builder != null;
		} else if (Component.class.isAssignableFrom(clazz)) {
			return clazz.isInstance(comp);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return comp.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return comp.equals(obj);
	}

	public ComponentAgent find(String selector) {
		return Searcher.find(this,selector);
	}

	public List<ComponentAgent> findAll(String selector) {
		return Searcher.findAll(this,selector);
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName())
			.append("@").append(Integer.toHexString(System.identityHashCode(this)))	
			.append("[").append(comp.toString()).append("]")
			.toString();
	}
	
	//for internal test only utility class
	public void dump() {
		StringBuilder sb = new StringBuilder();
		dump(sb,this,0);
		System.out.println(sb.toString());
	}
	private void dump(StringBuilder sb, ComponentAgent agent,int indent) {
		List<ComponentAgent> children = agent.getChildren();
		StringBuffer idt = new StringBuffer();
		for(int i=0;i<indent;i++){
			idt.append("  ");
		}
		sb.append(idt);
		Component zkc = agent.as(Component.class);
		String nm = zkc.getClass().getSimpleName(); 
		sb.append("<");
		sb.append(nm);
		sb.append(" uuid=\"").append(zkc.getUuid()).append("\"");
		
		String id = zkc.getId();
		if(id != null ){
			sb.append(" id=\"").append(id).append("\"");
		}
		
		if(children.size()>0){
			sb.append(">\n");
		}else{
			sb.append(" />\n");
		}
		
		for(ComponentAgent w:children){
			dump(sb,w,indent+1);
		}
		
		if(children.size()>0){
			sb.append(idt);
			sb.append("</").append(nm).append(">\n");
		}
	}
	
	public Object getDelegatee() {
		return comp;
	}
	
	
	
	
	
	
	
	
	
	//================= the impl. of short-cut method

	public void click() {
		as(ClickAgent.class).click();
	}

	public void type(String value) {
		as(TypeAgent.class).type(value);
	}

	public void focus() {
		as(FocusAgent.class).focus();
	}
	public void blur() {
		as(FocusAgent.class).blur();
	}

	public void select(int index) {
		as(SelectAgent.class).select(index);
	}

	public void check(boolean checked) {
		as(CheckAgent.class).check(checked);
	}
}