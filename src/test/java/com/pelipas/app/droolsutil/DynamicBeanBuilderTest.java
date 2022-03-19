package com.pelipas.app.droolsutil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.StatelessKieSession;

/**
 * 
 * 
 * @author gpelipas
 *
 */
public class DynamicBeanBuilderTest {

	Map<String, Class<?>> FIELDS = new HashMap<>();

	@Before
	public void init() {
		FIELDS.put("age", Integer.class);
		FIELDS.put("full_name", String.class);
		FIELDS.put("birthDate", Date.class);
		FIELDS.put("gift_price", Float.class);
		FIELDS.put("weird_float_field061", Float.class);
		
		System.setProperty("drools.dateformat", "yyyy-MM-dd");
	}
	
	@Ignore("Disabled for now")	
	@Test(expected = Exception.class)
	public void createDynamicBeanClassErrorsWhenWrongParams() throws Exception {
		DynamicBeanBuilder dbb = new DynamicBeanBuilder();
		dbb.build(null);
	}

	@Ignore("Disabled for now")
	@Test
	public void createDynamicBeanClass() throws Exception {
		/**
		 * Maybe try adding a vm parameter at lauch: java --add-opens
		 * java.base/java.lang=ALL-UNNAMED
		 */

		// System.setProperty("illegal-access", "permit");

		DynamicBeanBuilder dbb = new DynamicBeanBuilder();
		dbb.withClassFields(FIELDS);
		dbb.withSuperClass(TestBaseClass.class);

		String tmp = System.getProperty("java.io.tmpdir");

		Class<?> clz = dbb.build("com.pelipas.whereever.SampleDynamicBeanClazz");

		Assert.assertTrue("SampleDynamicBeanClazz".equals(clz.getSimpleName()));
	}

	@Test
	public void applyDynamicBeanToDrools() throws Exception {
		DynamicBeanBuilder dbb = new DynamicBeanBuilder();
		dbb.withClassFields(FIELDS);
		dbb.withSuperClass(TestBaseClass.class);

		Class<?> dynaClz = dbb.build("com.pelipas.whereever.SampleDynamicBeanClazz");

		Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("age", 100);
		fieldValues.put("full_name", "Bilbo Baggins");
		fieldValues.put("birthDate", new GregorianCalendar(2000, Calendar.DECEMBER, 24).getTime());
		fieldValues.put("gift_price", 110.222);
		fieldValues.put("weird_float_field061", 12.32);
		

		Object dynaBean = dynaClz.getDeclaredConstructor().newInstance(); // dynaClz.newInstance();

		BeanUtils.populate(dynaBean, fieldValues);

		String condition = "( birthDate < \"2022-10-24\" ) && ( gift_price > 1 ) && ( weird_float_field061 > 1.0 ) ";

		String droolScript = newDrlScript(dynaClz, condition);

		executeRule(droolScript, dynaBean);

		String fullName = (String) PropertyUtils.getProperty(dynaBean, "full_name");
		
		Assert.assertTrue("Frodo Baggins".equals(fullName));
		
		System.out.println(fullName);
	}

	private static void executeRule(String droolScript, Object bean) throws Exception {
		StatelessKieSession sess = DroolsUtil.createStatelessKieSession(droolScript);
		sess.execute(bean);
	}

	private static String newDrlScript(Class cls, String rule) {
		final StringBuilder sb = new StringBuilder();

		sb.append("package com.pelipas.hello; \n");
		sb.append("import org.apache.commons.beanutils.PropertyUtils; \n");
		sb.append("import ").append(cls.getName()).append("; \n");
		sb.append("dialect \"mvel\" \n\n");
		sb.append("rule \"testRule").append(System.currentTimeMillis()).append("\" \n");
		sb.append("no-loop true \n");
		sb.append("lock-on-active true \n");
		sb.append("when c : ").append(cls.getSimpleName()).append("(").append(rule).append(") \n");
		sb.append("then \n");
		sb.append(" System.out.println(\"=== Condition Satisfied === \"); \n");
		sb.append(" System.out.println(PropertyUtils.getProperty(c, \"full_name\")); \n");
		sb.append(" c.setFull_name(\"Frodo Baggins\"); \n");
		sb.append(" update(c); \n");
		sb.append("end \n");

		System.out.println("Generated Rule: \n" + sb.toString());

		return sb.toString();
	}

	public static class TestBaseClass implements Serializable {

		private static final long serialVersionUID = 8185417767801927216L;

	}

}
