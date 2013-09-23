package test;

import org.junit.Test;
import org.saadahmed.json.OrderedJSONObject;

import java.util.Iterator;

import static org.junit.Assert.*;


public class OrderedJSONObjectTest {

	@Test
	public void testSimpleOrderedJSONObject() {
		OrderedJSONObject orderedJSONObject = new OrderedJSONObject();

		orderedJSONObject.put("Germany", "Berlin");
		orderedJSONObject.put("England", "London");
		orderedJSONObject.put("France", "Paris");
		orderedJSONObject.put("United States", "Washington");
		orderedJSONObject.put("Spain", "Madrid");
		orderedJSONObject.put("Austria", "Vienna");

		String[][] expected = new String[orderedJSONObject.length()][2];
		expected[0][0] = "Austria";
		expected[0][1] = "Vienna";
		expected[1][0] = "England";
		expected[1][1] = "London";
		expected[2][0] = "France";
		expected[2][1] = "Paris";
		expected[3][0] = "Germany";
		expected[3][1] = "Berlin";
		expected[4][0] = "Spain";
		expected[4][1] = "Madrid";
		expected[5][0] = "United States";
		expected[5][1] = "Washington";

		String[][] actual = new String[orderedJSONObject.length()][2];

		Iterator<String> itr = orderedJSONObject.keyIterator();
		for (int i = 0; i < actual.length && itr.hasNext(); i++) {
			String key = itr.next();
			String value = (String)orderedJSONObject.get(key);
			actual[i][0] = key;
			actual[i][1] = value;
		}

		assertArrayEquals(expected, actual);

	}


	@Test
	public void testOrderedJSONObjectToString() {
		OrderedJSONObject orderedJSONObject = new OrderedJSONObject();

		orderedJSONObject.put("Germany", "Berlin");
		orderedJSONObject.put("France", "Paris");
		orderedJSONObject.put("England", "London");
		orderedJSONObject.put("United States", "Washington");
		orderedJSONObject.put("Spain", "Madrid");
		orderedJSONObject.put("Austria", "Vienna");

		assertEquals("{\"Austria\":\"Vienna\"," +
				"\"England\":\"London\"," +
				"\"France\":\"Paris\"," +
				"\"Germany\":\"Berlin\"," +
				"\"Spain\":\"Madrid\"," +
				"\"United States\":\"Washington\"}", orderedJSONObject.toString());
	}

}
