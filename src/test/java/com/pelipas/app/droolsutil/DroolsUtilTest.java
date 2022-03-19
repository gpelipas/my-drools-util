package com.pelipas.app.droolsutil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class DroolsUtilTest {

	@Before
	public void init() {
		// ...
	}

	@Test(expected = Exception.class)
	public void createDroolsContainerErrorsWhenEmptyParams() throws Exception {

		KieServices ks = null;

		String drlScript = null;

		DroolsUtil.createDroolsContainer(ks, drlScript);
	}

	@Test(expected = Exception.class)
	public void createDroolsContainerFailsWhenInCorrectParams() throws Exception {

		KieServices ks = KieServices.Factory.get();

		String drlScript = null;

		DroolsUtil.createDroolsContainer(ks, drlScript);
	}

	@Test
	public void createDroolsContainerReturnsWhenCorrectParams() throws Exception {

		KieServices ks = KieServices.Factory.get();

		String drlScript = basicDrlScript();

		KieContainer kc = DroolsUtil.createDroolsContainer(ks, drlScript);

		Assert.assertTrue("DroolsUtil.createDroolsContainer returns KieContainer", kc != null);
	}

	@Test
	public void createStatelessKieSessionReturnsWhenCorrectParams() throws Exception {
		String drlScript = basicDrlScript();
		StatelessKieSession sess = DroolsUtil.createStatelessKieSession(drlScript);

		Assert.assertTrue(sess != null);
	}

	private static String basicDrlScript() {
		StringBuilder sb = new StringBuilder();
		sb.append("package drools.test; \n");
		sb.append("dialect \"mvel\" \n");
		return sb.toString();
	}

}
