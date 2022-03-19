package com.pelipas.app.droolsutil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * 
 * 
 * @author gpelipas
 *
 */
public class DeclaredTypeTest {

	@Before
	public void init() {
		System.setProperty("drools.dateformat", "yyyy-MM-dd"); 
	}

	@Test
	public void declaredTypeWorks() throws Exception {
		
		Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("age", 100);
		fieldValues.put("full_name", "Bilbo Baggins");
		fieldValues.put("dateOfBirth", new GregorianCalendar(2000, Calendar.DECEMBER, 24).getTime());
		fieldValues.put("gift_price", 334.201F);

		String drlScript = newDrlScript("Student", "( dateOfBirth < \"2022-10-24\" )");
		
		KieContainer kc = DroolsUtil.createDroolsContainer(drlScript);
		StatelessKieSession sess = kc.newStatelessKieSession();

		FactType declaredType = DroolsUtil.getFactType(kc, "com.pelipas.app.pkg", "Student");

		Object declaredTypeObject = declaredType.newInstance();
		
		declaredType.setFromMap(declaredTypeObject, fieldValues);
		
		sess.execute(declaredTypeObject);
		
		String fullName = (String) PropertyUtils.getProperty(declaredTypeObject, "full_name");
		
		System.out.println(fullName);
		
		System.out.println(PropertyUtils.getProperty(declaredTypeObject, "dateOfBirth"));
		
		Assert.assertTrue("Frodo Baggins".equals(fullName));
		
	}

	private static String newDrlScript(String declaredTypeName, String rule) {
		final StringBuilder sb = new StringBuilder();

		sb.append("package com.pelipas.app.pkg \n");
		sb.append("\n");
		//sb.append("dialect \"mvel\" \n");
		sb.append("import org.apache.commons.beanutils.PropertyUtils; \n");
		sb.append("\n");
		sb.append(declaredType());
		sb.append("\n");
		sb.append("rule \"testRule").append(System.currentTimeMillis()).append("\" \n");
		sb.append("no-loop true \n");
		sb.append("lock-on-active true \n");
		sb.append("when c : ").append(declaredTypeName).append("(").append(rule).append(") \n");
		sb.append("then \n");
		sb.append(" System.out.println(\"=== Condition Satisfied === \"); \n");
		sb.append(" System.out.println(PropertyUtils.getProperty(c, \"full_name\")); \n");
		sb.append(" c.setFull_name(\"Frodo Baggins\"); \n");
		sb.append(" update(c); \n");
		sb.append("end \n");

		System.out.println("Generated Rule: \n" + sb.toString());

		return sb.toString();
	}

	private static String declaredType() {
		final StringBuilder sb = new StringBuilder();
		sb.append("import java.util.Date; \n");
		sb.append("\n\n");
		sb.append("declare Person \n");
		sb.append("  full_name : String  \n");
		sb.append("  age : Integer \n");
		sb.append("  dateOfBirth : Date \n");
		sb.append("  gift_price : Float \n");
		sb.append("end \n");
		sb.append("\n");
		sb.append("declare Student extends Person \n");
		sb.append("  schoolName : String  \n");
		sb.append("end \n");

		return sb.toString();
	}

}
