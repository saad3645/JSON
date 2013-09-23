package test;

import org.junit.Test;
import org.saadahmed.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class JSONObjectTest {

	@Test
	public void testSimpleStringInsertRetrieval() {

		JSONObject jObject = new JSONObject();
		jObject.put("one", "one");
		jObject.put("two", "two");
		jObject.putString("three", "This is 3");

		assertEquals("one", jObject.get("one"));
		assertEquals("one", jObject.getString("one"));
		assertEquals("two", jObject.get("two"));
		assertEquals("two", jObject.getString("two"));
		assertEquals("This is 3", jObject.get("three"));
		assertEquals("This is 3", jObject.getString("three"));
	}

	@Test
	public void testSimpleIntegerInsertRetrieval() {
		JSONObject jObject = new JSONObject();
		jObject.put("integerOne", 1);
		jObject.put("integerTwo", 2);
		jObject.putInt("integerThree", 3);

		assertEquals(1, jObject.get("integerOne"));
		assertEquals(1, jObject.getInt("integerOne"));
		assertEquals(2, jObject.get("integerTwo"));
		assertEquals(2, jObject.getInt("integerTwo"));
		assertEquals(3, jObject.get("integerThree"));
		assertEquals(3, jObject.getInt("integerThree"));
	}

	@Test
	public void testSimpleDoubleInsertRetrieval() {
		JSONObject jObject = new JSONObject();
		jObject.put("doubleOne", 1.0);
		jObject.put("doubleTwo", 2);
		jObject.putDouble("doubleThree", 3.0000);

		assertEquals(1.0, jObject.get("doubleOne"));
		assertEquals(1.0, jObject.getDouble("doubleOne"), 0);
		assertEquals(2, jObject.get("doubleTwo"));
		assertEquals((double)2, jObject.getDouble("doubleTwo"), 0);
		assertEquals(3.0, jObject.get("doubleThree"));
		assertEquals(3.0, jObject.getDouble("doubleThree"), 0);
	}

	@Test
	public void testSimpleNullInsertRetrieval() {
		JSONObject jObject = new JSONObject();
		jObject.put("Null", null);
		jObject.putNull("Null2");
		jObject.put("NotNull", "null");
		jObject.putString("NullString", null);
		jObject.putString("NotNullString", "null");

		assertNull(jObject.get("Null"));
		assertNull(jObject.getString("Null"));

		assertNull(jObject.get("Null2"));
		assertNull(jObject.getString("Null2"));

		assertNotNull(jObject.get("NotNull"));
		assertNotNull(jObject.getString("NotNull"));
		assertEquals("null", jObject.getString("NotNull"));

		assertNull(jObject.get("NullString"));
		assertNull(jObject.getString("NullString"));

		assertNotNull(jObject.get("NotNullString"));
		assertNotNull(jObject.getString("NotNullString"));
		assertEquals("null", jObject.getString("NotNullString"));
	}

	@Test
	public void testConstructFromSimpleString() {
		String sourceString = "{\"one\":\"two\",\"key\":\"value\"}";
		JSONObject jObject = new JSONObject(sourceString);

		assertNotNull(jObject);

		assertNotNull(jObject.get("one"));
		assertNotNull(jObject.getString("one"));
		assertEquals("two", jObject.get("one"));
		assertEquals("two", jObject.getString("one"));

		assertNotNull(jObject.get("key"));
		assertNotNull(jObject.getString("key"));
		assertEquals("value", jObject.get("key"));
		assertEquals("value", jObject.getString("key"));
	}

	@Test
	public void testConstructFromIndentedString() {
		String sourceString = "{\n" +
				"   \"object_or_array\": \"object\",\n" +
				"   \"empty\": false,\n" +
				"   \"time_milliseconds\": 19608,\n" +
				"   \"validate\": true,\n" +
				"   \"size\": 5\n" +
				"}";

		JSONObject jObject = new JSONObject(sourceString);

		assertNotNull(jObject);

		assertNotNull(jObject.get("object_or_array"));
		assertNotNull(jObject.getString("object_or_array"));
		assertEquals("object", jObject.get("object_or_array"));
		assertEquals("object", jObject.getString("object_or_array"));

		assertNotNull(jObject.get("empty"));
		assertNotNull(jObject.getBoolean("empty"));
		assertEquals(false, jObject.get("empty"));
		assertEquals(false, jObject.getBoolean("empty"));

		assertNotNull(jObject.get("time_milliseconds"));
		assertNotNull(jObject.getInt("time_milliseconds"));
		assertEquals(19608, jObject.get("time_milliseconds"));
		assertEquals(19608, jObject.getInt("time_milliseconds"));

		assertNotNull(jObject.get("validate"));
		assertNotNull(jObject.getBoolean("validate"));
		assertEquals(true, jObject.get("validate"));
		assertEquals(true, jObject.getBoolean("validate"));

		assertNotNull(jObject.get("size"));
		assertNotNull(jObject.getInt("size"));
		assertEquals(5, jObject.get("size"));
		assertEquals(5, jObject.getInt("size"));
	}

	@Test
	public void testToSimpleString() {
		JSONObject jsonObject = new JSONObject(LinkedHashMap.class);
		jsonObject.put("one", "one");
		jsonObject.put("two", "two");
		jsonObject.put("three", "This is 3");

		assertEquals("{\"one\":\"one\",\"two\":\"two\",\"three\":\"This is 3\"}", jsonObject.toString());
	}

	@Test
	public void testFromStringBackToString() {
		String sourceString = "{\n" +
				"   \"object_or_array\": \"object\",\n" +
				"   \"empty\": false,\n" +
				"   \"time_milliseconds\": 19608,\n" +
				"   \"validate\": true,\n" +
				"   \"size\": 5\n" +
				"}";

		JSONObject jsonObject = new JSONObject(sourceString, LinkedHashMap.class);

		assertEquals("{\"object_or_array\":\"object\"," +
				"\"empty\":false," +
				"\"time_milliseconds\":19608," +
				"\"validate\":true," +
				"\"size\":5}", jsonObject.toString());
	}

}
