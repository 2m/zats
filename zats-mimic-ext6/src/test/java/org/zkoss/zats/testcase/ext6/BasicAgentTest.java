/* BasicAgentTest.java

	Purpose:
		
	Description:
		
	History:
		Mar 27, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zats.testcase.ext6;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.ClickAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zats.mimic.operation.SelectByIndexAgent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

/**
 * @author pao
 * 
 */
public class BasicAgentTest {
	@BeforeClass
	public static void init() {
		Zats.init(".");
	}

	@AfterClass
	public static void end() {
		Zats.end();
	}

	@After
	public void after() {
		Zats.cleanup();
	}

	@Test
	public void testToolbarButtonCheck() {
		DesktopAgent desktop = Zats.newClient().connect("/~./basic/toolbar.zul");

		for (int i = 1; i <= 6; ++i)
			assertEquals("tbb" + i, desktop.query("#tbb" + i).as(Toolbarbutton.class).getLabel());

		Label clicked = desktop.query("#clicked").as(Label.class);
		assertEquals("", clicked.getValue());

		for (int i = 1; i <= 6; ++i) {
			desktop.query("#tbb" + i).click();
			assertEquals("tbb" + i, clicked.getValue());
		}

		Label checked = desktop.query("#checked").as(Label.class);
		assertEquals("", checked.getValue());

		String[] values = { "tbb4 ", "tbb4 tbb5 ", "tbb4 tbb5 tbb6 ", "tbb4 tbb6 " };
		for (int i = 4; i <= 6; ++i) {
			desktop.query("#tbb" + i).check(true);
			assertEquals(values[i - 4], checked.getValue());
			assertEquals("tbb6", clicked.getValue()); // "check" should not perform "click"
		}

		desktop.query("#tbb5").check(false);
		assertEquals(values[3], checked.getValue());
	}
	
	@Test
	public void testOpenAgent() {
		DesktopAgent desktop = Zats.newClient().connect("/~./basic/open-ext6.zul");

		Label open = desktop.query("#open").as(Label.class);
		Label close = desktop.query("#close").as(Label.class);
		assertEquals("", open.getValue());
		assertEquals("", close.getValue());

		String values[] = { "", "" };
		// combobutton
		String id = "#aCombobutton";
		values[0] = id.substring(1);
		desktop.query(id).as(OpenAgent.class).open(true);
		assertEquals(values[0], open.getValue());
		assertEquals(values[1], close.getValue());
		values[1] = id.substring(1);
		desktop.query(id).as(OpenAgent.class).open(false);
		assertEquals(values[0], open.getValue());
		assertEquals(values[1], close.getValue());
	}
	
	@Test
	public void testSelectByIndexAgent() {
		DesktopAgent desktop = Zats.newClient().connect("/~./basic/selectbox.zul");
		
		Label msg = desktop.query("#msg").as(Label.class);
		assertEquals("", msg.getValue());

		SelectByIndexAgent sb = desktop.query("#sb").as(SelectByIndexAgent.class);
		for (int i = 3; i >= 1; --i) {
			sb.select(i - 1);
			assertEquals("item" + i, msg.getValue());
		}
	}
	
	@Test
	public void testClickAll() {
		DesktopAgent desktop = Zats.newClient().connect("/~./basic/click-ext6.zul");

		Label target = desktop.query("#target").as(Label.class);
		Label event = desktop.query("#eventName").as(Label.class);
		assertEquals("", target.getValue());
		assertEquals("", event.getValue());

		ComponentAgent comps = desktop.query("#comps");
		assertNotNull(comps);

		String[] names = { "combobutton", "idspace" };
		for (String name : names) {
			ClickAgent agent = comps.query(name).as(ClickAgent.class);
			agent.click();
			assertEquals(name, target.getValue());
			assertEquals(Events.ON_CLICK, event.getValue());
			agent.doubleClick();
			assertEquals(name, target.getValue());
			assertEquals(Events.ON_DOUBLE_CLICK, event.getValue());
			agent.rightClick();
			assertEquals(name, target.getValue());
			assertEquals(Events.ON_RIGHT_CLICK, event.getValue());
		}
	}
}
