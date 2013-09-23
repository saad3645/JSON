package org.saadahmed.json;

/*
 Copyright (c) 2002 JSON.org, Saad Ahmed

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 Edited by Saad Ahmed - September 2013.
 */

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


/**
 * A JSONObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having <code>get</code> and <code>opt</code> methods for accessing
 * the values by name, and <code>put</code> methods for adding or replacing
 * values by name. The values can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL</code> object. A
 * JSONObject constructor can be used to convert an external form JSON text
 * into an internal form whose values can be retrieved with the
 * <code>get</code> and <code>opt</code> methods, or to convert values into a
 * JSON text using the <code>put</code> and <code>toString</code> methods. A
 * <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object, which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they
 * do not throw. Instead, they return a specified value, such as null.
 * <p>
 * The <code>put</code> methods add or replace values in an object. For
 * example,
 *
 * <pre>
 * myString = new JSONObject()
 *         .put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 *
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * the JSON syntax rules. The constructors are more forgiving in the texts they
 * will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing
 * spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>,
 * or <code>null</code>.</li>
 * </ul>
 *
 * @author JSON.org, Saad Ahmed
 * @version 2013-09-22
 */
public class JSONObject {
	/**
	 * JSONObject.NULL is equivalent to the value that JavaScript calls null,
	 * whilst Java's null is equivalent to the value that JavaScript calls
	 * undefined.
	 */
	private static final class Null {

		/**
		 * There is only intended to be a single instance of the NULL object,
		 * so the clone method returns itself.
		 *
		 * @return NULL.
		 */
		protected final Object clone() {
			return this;
		}

		/**
		 * A Null object is equal to the null value and to itself.
		 *
		 * @param object
		 *            An object to test for nullness.
		 * @return true if the object parameter is the JSONObject.NULL object or
		 *         null.
		 */
		public boolean equals(Object object) {
			return (object == this);
		}

		/**
		 * Get the "null" string value.
		 *
		 * @return The string "null".
		 */
		public String toString() {
			return "null";
		}
	}

	/**
	 * The map where the JSONObject's properties are kept.
	 */
	protected final Map map;

	/**
	 * It is sometimes more convenient and less ambiguous to have a
	 * <code>NULL</code> object than to use Java's <code>null</code> value.
	 * <code>JSONObject.NULL.equals(null)</code> returns <code>true</code>.
	 * <code>JSONObject.NULL.toString()</code> returns <code>"null"</code>.
	 */
	public static final Object NULL = new Null();

	/**
	 * Constructs an empty JSONObject.
	 */
	public JSONObject() {
		this.map = new HashMap();
	}

	/**
	 * Constructs an empty JSONObject. The JSONObject will be backed up by
	 * a Map type that is specified in the argument.
	 *
	 * @param mapClass
	 *        Class name of the Map implementation that will be used to store
	 *        the properties of the JSONObject
	 */
	public JSONObject(Class<? extends Map> mapClass) {
		if (mapClass != null) {
			Map map;

			try {
				map = mapClass.newInstance();
			} catch (Exception e) {
				map = new HashMap();
			}

			this.map = map;
		}

		else this.map = new HashMap();
	}

	/**
	 * Construct a JSONObject from a Map. The implementation of the input map
	 * also determines the data structure used to backup the data.
	 *
	 * @param map
	 *            A map object that can be used to initialize the contents of
	 *            the JSONObject.
	 * @throws JSONException
	 */
	public JSONObject(Map map) {
		this(map.getClass());

		if (map != null) {
			Set<Map.Entry> entrySet = map.entrySet();
			for (Map.Entry e: entrySet) {
				this.putOpt(e.getKey().toString(), e.getValue());
			}
		}
	}

	/**
	 * Construct a JSONObject from a subset of another JSONObject. An array of
	 * strings is used to identify the keys that should be copied. Missing keys
	 * are ignored.
	 *
	 * @param jo
	 *            A JSONObject.
	 * @param names
	 *            An array of strings.
	 * @throws JSONException
	 * @exception JSONException
	 *                If a value is a non-finite number or if a name is
	 *                duplicated.
	 */
	protected JSONObject(JSONObject jo, String[] names) {
		this(jo, names, HashMap.class);
	}

	/**
	 * Construct a JSONObject from a subset of another JSONObject. An array of
	 * strings is used to identify the keys that should be copied. Missing keys
	 * are ignored.
	 *
	 * @param jo
	 *            A JSONObject.
	 * @param names
	 *            An array of strings.
	 * @param mapClass
	 *            Class name of the Map implementation that will be used to
	 *            backup the data
	 * @throws JSONException
	 * @exception JSONException
	 *                If a value is a non-finite number or if a name is
	 *                duplicated.
	 */
	public JSONObject(JSONObject jo, String[] names, Class<? extends Map> mapClass) {
		this(mapClass);
		for (String name: names) {
			try {
				this.putOnce(name, jo.opt(name));
			} catch (Exception ignore) {
			}
		}
	}


	/**
	 * Construct a JSONObject from an Object using bean getters. It reflects on
	 * all of the public methods of the object. For each of the methods with no
	 * parameters and a name starting with <code>"get"</code> or
	 * <code>"is"</code> followed by an uppercase letter, the method is invoked,
	 * and a key and the value returned from the getter method are put into the
	 * new JSONObject.
	 *
	 * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
	 * prefix. If the second remaining character is not upper case, then the
	 * first character is converted to lower case.
	 *
	 * For example, if an object has a method named <code>"getName"</code>, and
	 * if the result of calling <code>object.getName()</code> is
	 * <code>"Larry Fine"</code>, then the JSONObject will contain
	 * <code>"name": "Larry Fine"</code>.
	 *
	 * @param bean
	 *            An object that has getter methods that should be used to make
	 *            a JSONObject.
	 */
	public JSONObject(Object bean) {
		this(bean, HashMap.class);
	}

	/**
	 * Construct a JSONObject from an Object using bean getters. It reflects on
	 * all of the public methods of the object. For each of the methods with no
	 * parameters and a name starting with <code>"get"</code> or
	 * <code>"is"</code> followed by an uppercase letter, the method is invoked,
	 * and a key and the value returned from the getter method are put into the
	 * new JSONObject.
	 *
	 * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
	 * prefix. If the second remaining character is not upper case, then the
	 * first character is converted to lower case.
	 *
	 * For example, if an object has a method named <code>"getName"</code>, and
	 * if the result of calling <code>object.getName()</code> is
	 * <code>"Larry Fine"</code>, then the JSONObject will contain
	 * <code>"name": "Larry Fine"</code>.
	 *
	 * @param bean
	 *            An object that has getter methods that should be used to make
	 *            a JSONObject.
	 * @param mapClass
	 *            Class name of the Map implementation that will be used to
	 *            backup the data.
	 */
	public JSONObject(Object bean, Class<? extends Map> mapClass) {
		this(mapClass);
		this.populateMap(bean);
	}

	/**
	 * Construct a JSONObject from an Object, using reflection to find the
	 * public members. The resulting JSONObject's keys will be the strings from
	 * the names array, and the values will be the field values associated with
	 * those keys in the object. If a key is not found or not visible, then it
	 * will not be copied into the new JSONObject.
	 *
	 * @param object
	 *            An object that has fields that should be used to make a
	 *            JSONObject.
	 * @param names
	 *            An array of strings, the names of the fields to be obtained
	 *            from the object.
	 */
	public JSONObject(Object object, String[] names) {
		this(object, names, HashMap.class);
	}

	/**
	 * Construct a JSONObject from an Object, using reflection to find the
	 * public members. The resulting JSONObject's keys will be the strings from
	 * the names array, and the values will be the field values associated with
	 * those keys in the object. If a key is not found or not visible, then it
	 * will not be copied into the new JSONObject.
	 *
	 * @param object
	 *            An object that has fields that should be used to make a
	 *            JSONObject.
	 * @param names
	 *            An array of strings, the names of the fields to be obtained
	 *            from the object.
	 * @param mapClass
	 *            Class name of the Map implementation that will be used to
	 *            backup the data.
	 */
	public JSONObject(Object object, String[] names, Class<? extends Map> mapClass) {
		this(mapClass);
		Class c = object.getClass();
		for (String name: names) {
			try {
				this.putOpt(name, c.getField(name).get(object));
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Construct a JSONObject from a JSONTokener.
	 *
	 * @param x
	 *            A JSONTokener object containing the source string.
	 * @throws JSONException
	 *             If there is a syntax error in the source string or a
	 *             duplicated key.
	 */
	public JSONObject(JSONTokener x) throws JSONException {
		this(x, HashMap.class);
	}

	/**
	 * Construct a JSONObject from a JSONTokener.
	 *
	 * @param x
	 *            A JSONTokener object containing the source string.
	 * @param mapClass
	 *            Class name of the Map implementation that will be used to
	 *            backup the data
	 * @throws JSONException
	 *             If there is a syntax error in the source string or a
	 *             duplicated key.
	 */
	public JSONObject(JSONTokener x, Class<? extends Map> mapClass) throws JSONException {
		this(mapClass);
		char c;
		String key;

		if (x.nextClean() != '{') {
			throw x.syntaxError("A JSONObject text must begin with '{'");
		}
		for (;;) {
			c = x.nextClean();
			switch (c) {
				case 0:
					throw x.syntaxError("A JSONObject text must end with '}'");
				case '}':
					return;
				default:
					x.back();
					key = x.nextValue().toString();
			}

//  The key is followed by ':'.

			c = x.nextClean();
			if (c != ':') {
				throw x.syntaxError("Expected a ':' after a key");
			}
			this.putOnce(key, x.nextValue());

//  Pairs are separated by ','.

			switch (x.nextClean()) {
				case ';':
				case ',':
					if (x.nextClean() == '}') {
						return;
					}
					x.back();
					break;
				case '}':
					return;
				default:
					throw x.syntaxError("Expected a ',' or '}'");
			}
		}
	}

	/**
	 * Construct a JSONObject from a source JSON text string. This is the most
	 * commonly used JSONObject constructor.
	 *
	 * @param source
	 *            A string beginning with <code>{</code>&nbsp;<small>(left
	 *            brace)</small> and ending with <code>}</code>
	 *            &nbsp;<small>(right brace)</small>.
	 * @exception JSONException
	 *            If there is a syntax error in the source string or a
	 *            duplicated key.
	 */
	public JSONObject(String source) throws JSONException {
		this(new JSONTokener(source));
	}

	/**
	 * Construct a JSONObject from a source JSON text string. This is the most
	 * commonly used JSONObject constructor.
	 *
	 * @param source
	 *            A string beginning with <code>{</code>&nbsp;<small>(left
	 *            brace)</small> and ending with <code>}</code>
	 *            &nbsp;<small>(right brace)</small>.
	 * @param mapClass
	 *            Class name of the Map implementation that will be used to
	 *            backup the data.
	 * @exception JSONException
	 *            If there is a syntax error in the source string or a
	 *            duplicated key.
	 */
	public JSONObject(String source, Class<? extends Map> mapClass) throws JSONException {
		this(new JSONTokener(source), mapClass);
	}

	/**
	 * Construct a JSONObject from a ResourceBundle.
	 *
	 * @param baseName
	 *            The ResourceBundle base name.
	 * @param locale
	 *            The Locale to load the ResourceBundle for.
	 * @throws JSONException
	 *             If any JSONExceptions are detected.
	 */
	public JSONObject(String baseName, Locale locale) throws JSONException {
		this();
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
				Thread.currentThread().getContextClassLoader());

//  Iterate through the keys in the bundle.

		Enumeration keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key instanceof String) {

//  Go through the path, ensuring that there is a nested JSONObject for each
//  segment except the last. Add the value using the last segment's name into
//  the deepest nested JSONObject.

				String[] path = ((String) key).split("\\.");
				int last = path.length - 1;
				JSONObject target = this;
				for (int i = 0; i < last; i += 1) {
					String segment = path[i];
					JSONObject nextTarget = target.optJSONObject(segment);
					if (nextTarget == null) {
						nextTarget = new JSONObject();
						target.put(segment, nextTarget);
					}
					target = nextTarget;
				}
				target.put(path[last], bundle.getString((String) key));
			}
		}
	}

	/**
	 * Accumulate values under a key. It is similar to the put method except
	 * that if there is already an object stored under the key then a JSONArray
	 * is stored under the key to hold all of the accumulated values. If there
	 * is already a JSONArray, then the new value is appended to it. In
	 * contrast, the put method replaces the previous value.
	 *
	 * If only one value is accumulated that is not a JSONArray, then the result
	 * will be the same as using put. But if multiple values are accumulated,
	 * then the result will be like append.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object to be accumulated under the key.
	 * @return this.
	 * @throws JSONException
	 *             If the value is an invalid number or if the key is null.
	 */
	public JSONObject accumulate(String key, Object value) throws JSONException {
		testValidity(value);
		Object object = this.opt(key);
		if (object == null) {
			this.put(key,
					value instanceof JSONArray ? new JSONArray().put(value)
							: value);
		} else if (object instanceof JSONArray) {
			((JSONArray) object).put(value);
		} else {
			this.put(key, new JSONArray().put(object).put(value));
		}
		return this;
	}

	/**
	 * Append values to the array under a key. If the key does not exist in the
	 * JSONObject, then the key is put in the JSONObject with its value being a
	 * JSONArray containing the value parameter. If the key was already
	 * associated with a JSONArray, then the value parameter is appended to it.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object to be accumulated under the key.
	 * @return this.
	 * @throws JSONException
	 *             If the key is null or if the current value associated with
	 *             the key is not a JSONArray.
	 */
	public JSONObject append(String key, Object value) throws JSONException {
		testValidity(value);
		Object object = this.opt(key);
		if (object == null) {
			this.put(key, new JSONArray().put(value));
		} else if (object instanceof JSONArray) {
			this.put(key, ((JSONArray) object).put(value));
		} else {
			throw new JSONException("JSONObject[" + key
					+ "] is not a JSONArray.");
		}
		return this;
	}

	/**
	 * Produce a string from a double. The string "null" will be returned if the
	 * number is not finite.
	 *
	 * @param d
	 *            A double.
	 * @return A String.
	 */
	public static String doubleToString(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			return "null";
		}

//  Shave off trailing zeros and decimal point, if possible.

		String string = Double.toString(d);
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0
				&& string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
		}
		return string;
	}

	/**
	 * Get the value object associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return The object associated with the key.
	 * @throws JSONException
	 *            If the key is not found.
	 */
	public Object get(String key) throws JSONException {
		if (key == null) {
			throw new IllegalArgumentException("Null key.");
		}

		Object object = this.map.get(key);

		if (object == null) {
			throw new JSONException("Key " + quote(key) + " not found.");
		}

		if (object.equals(JSONObject.NULL)) {
			return null;
		}

		else return object;
	}

	/**
	 * Get the boolean value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return The truth.
	 * @throws JSONException
	 *             if the key is not found or if the value is not a Boolean and
	 *             the String is neither "true" nor "false".
	 */
	public boolean getBoolean(String key) throws JSONException {
		Object object = this.get(key);

		if (object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object)
				.equalsIgnoreCase("false"))) {
			return false;
		}

		if (object.equals(Boolean.TRUE)
				|| (object instanceof String && ((String) object)
				.equalsIgnoreCase("true"))) {
			return true;
		}

		else throw new JSONException("JSONObject[" + quote(key)
				+ "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return The numeric value.
	 * @throws JSONException
	 *             if the key is not found or if the value is not a Number
	 *             object and cannot be converted to a number.
	 */
	public double getDouble(String key) throws JSONException {
		Object object = this.get(key);

		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new JSONException("JSONObject[" + quote(key)
					+ "] is not a number.");
		}
	}

	/**
	 * Get the int value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return The integer value.
	 * @throws JSONException
	 *             if the key is not found or if the value cannot be converted
	 *             to an integer.
	 */
	public int getInt(String key) throws JSONException {
		Object object = this.get(key);

		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new JSONException("JSONObject[" + quote(key)
					+ "] is not an int.");
		}
	}

	/**
	 * Get the JSONArray value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return A JSONArray which is the value.
	 * @throws JSONException
	 *             if the key is not found or if the value is not a JSONArray.
	 */
	public JSONArray getJSONArray(String key) throws JSONException {
		Object object = this.get(key);

		if (object instanceof JSONArray) {
			return (JSONArray) object;
		}

		throw new JSONException("JSONObject[" + quote(key)
				+ "] is not a JSONArray.");
	}

	/**
	 * Get the JSONObject value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return A JSONObject which is the value.
	 * @throws JSONException
	 *             if the key is not found or if the value is not a JSONObject.
	 */
	public JSONObject getJSONObject(String key) throws JSONException {
		Object object = this.get(key);

		if (object instanceof JSONObject) {
			return (JSONObject) object;
		}

		throw new JSONException("JSONObject[" + quote(key)
				+ "] is not a JSONObject.");
	}

	/**
	 * Get the long value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return The long value.
	 * @throws JSONException
	 *             if the key is not found or if the value cannot be converted
	 *             to a long.
	 */
	public long getLong(String key) throws JSONException {
		Object object = this.get(key);

		try {
			return object instanceof Number ? ((Number) object).longValue()
					: Long.parseLong((String) object);
		} catch (Exception e) {
			throw new JSONException("JSONObject[" + quote(key)
					+ "] is not a long.");
		}
	}

	/**
	 * Get the string associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return A string which is the value, or null if the value is null.
	 * @throws JSONException
	 *            if the key is not found or if the value cannot be converted
	 *            to a string or null.
	 */
	public String getString(String key) throws JSONException {
		Object object = this.get(key);

		if (object == null) {
			return null;
		}

		else if (object instanceof String) {
			return (String) object;
		}

		else throw new JSONException("JSONObject[" + quote(key) + "] not a string.");
	}

	/**
	 * Determine if the JSONObject contains a specific key.
	 *
	 * @param key
	 *            A key string.
	 * @return true if the key exists in the JSONObject.
	 */
	public boolean has(String key) {
		return this.map.containsKey(key);
	}

	/**
	 * Increment a property of a JSONObject. If there is no such property,
	 * create one with a value of 1. If there is such a property, and if it is
	 * an Integer, Long, Double, or Float, then add one to it.
	 *
	 * @param key
	 *            A key string.
	 * @return this.
	 * @throws JSONException
	 *             If there is already a property with this name that is not an
	 *             Integer, Long, Double, or Float.
	 */
	public JSONObject increment(String key) throws JSONException {
		Object value = this.opt(key);
		if (value == null) {
			this.put(key, 1);
		} else if (value instanceof Integer) {
			this.put(key, ((Integer) value) + 1);
		} else if (value instanceof Long) {
			this.put(key, ((Long) value) + 1);
		} else if (value instanceof Double) {
			this.put(key, ((Double) value) + 1);
		} else if (value instanceof Float) {
			this.put(key, ((Float) value) + 1);
		} else {
			throw new JSONException("Unable to increment [" + quote(key) + "].");
		}
		return this;
	}

	/**
	 * Determine if the value associated with the key is null or if there is no
	 * value.
	 *
	 * @param key
	 *            A key string.
	 * @return true if there is no value associated with the key or if the value
	 *         is the JSONObject.NULL object.
	 */
	public boolean isNull(String key) {
		try {
			return (this.get(key) == null);
		}

		catch (JSONException e) {
			return false;
		}
	}

	/**
	 * Get an enumeration of the keys of the JSONObject.
	 *
	 * @return An iterator of the keys.
	 */
	public Iterator keyIterator() {
		return this.keySet().iterator();
	}

	/**
	 * Get an array containing all keys of the JSONObject.
	 *
	 * @return An array of Strings containing all the keys of the JSONObject.
	 */
	public String[] keys() {
		int length = this.length();
		String[] keysArray = new String[length];

		Iterator iterator = keyIterator();
		for (int i = 0; iterator.hasNext() && i < length; i++) {
			keysArray[i] = (String)iterator.next();
		}

		return keysArray;
	}

	/**
	 * Get a set of keys of the JSONObject.
	 *
	 * @return A keySet.
	 */
	public Set<String> keySet() {
		return this.map.keySet();
	}

	/**
	 * Get the number of keys stored in the JSONObject.
	 *
	 * @return The number of keys in the JSONObject.
	 */
	public int length() {
		return this.map.size();
	}

	/**
	 * Produce a JSONArray containing the names of the elements of this
	 * JSONObject.
	 *
	 * @return A JSONArray containing the key strings, or null if the JSONObject
	 *         is empty.
	 */
	public JSONArray names() {
		JSONArray ja = new JSONArray();
		Iterator keys = this.keyIterator();
		while (keys.hasNext()) {
			ja.put(keys.next());
		}
		return ja.length() == 0 ? null : ja;
	}

	/**
	 * Produce a string from a Number.
	 *
	 * @param number
	 *            A Number
	 * @return A String.
	 * @throws JSONException
	 *             If n is a non-finite number.
	 */
	public static String numberToString(Number number) throws JSONException {
		if (number == null) {
			throw new JSONException("Null pointer");
		}
		testValidity(number);

//  Shave off trailing zeros and decimal point, if possible.

		String string = number.toString();
		if (string.indexOf('.') > 0 && string.indexOf('e') < 0
				&& string.indexOf('E') < 0) {
			while (string.endsWith("0")) {
				string = string.substring(0, string.length() - 1);
			}
			if (string.endsWith(".")) {
				string = string.substring(0, string.length() - 1);
			}
		}
		return string;
	}

	/**
	 * Get an optional value associated with a key.
	 *
	 * @param key
	 *            A key string.
	 * @return An object which is the value, or null if there is no value.
	 */
	public Object opt(String key) {
		try {
			return this.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get an optional boolean associated with a key. It returns false if there
	 * is no such key, or if the value is not Boolean.TRUE or the String "true".
	 *
	 * @param key
	 *            A key string.
	 * @return The truth.
	 */
	public boolean optBoolean(String key) {
		return this.optBoolean(key, false);
	}

	/**
	 * Get an optional boolean associated with a key. It returns the
	 * defaultValue if there is no such key, or if it is not a Boolean or the
	 * String "true" or "false" (case insensitive).
	 *
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            The default.
	 * @return The truth.
	 */
	public boolean optBoolean(String key, boolean defaultValue) {
		try {
			return this.getBoolean(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get an optional double associated with a key, or NaN if there is no such
	 * key or if its value is not a number. If the value is a string, an attempt
	 * will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A string which is the key.
	 * @return An object which is the value.
	 */
	public double optDouble(String key) {
		return this.optDouble(key, Double.NaN);
	}

	/**
	 * Get an optional double associated with a key, or the defaultValue if
	 * there is no such key or if its value is not a number. If the value is a
	 * string, an attempt will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            The default.
	 * @return An object which is the value.
	 */
	public double optDouble(String key, double defaultValue) {
		try {
			return this.getDouble(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get an optional int value associated with a key, or zero if there is no
	 * such key or if the value is not a number. If the value is a string, an
	 * attempt will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A key string.
	 * @return An object which is the value.
	 */
	public int optInt(String key) {
		return this.optInt(key, 0);
	}

	/**
	 * Get an optional int value associated with a key, or the default if there
	 * is no such key or if the value is not a number. If the value is a string,
	 * an attempt will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            The default.
	 * @return An object which is the value.
	 */
	public int optInt(String key, int defaultValue) {
		try {
			return this.getInt(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get an optional JSONArray associated with a key. It returns null if there
	 * is no such key, or if its value is not a JSONArray.
	 *
	 * @param key
	 *            A key string.
	 * @return A JSONArray which is the value.
	 */
	public JSONArray optJSONArray(String key) {
		Object o = this.opt(key);
		return o instanceof JSONArray ? (JSONArray) o : null;
	}

	/**
	 * Get an optional JSONObject associated with a key. It returns null if
	 * there is no such key, or if its value is not a JSONObject.
	 *
	 * @param key
	 *            A key string.
	 * @return A JSONObject which is the value.
	 */
	public JSONObject optJSONObject(String key) {
		Object object = this.opt(key);
		return object instanceof JSONObject ? (JSONObject) object : null;
	}

	/**
	 * Get an optional long value associated with a key, or zero if there is no
	 * such key or if the value is not a number. If the value is a string, an
	 * attempt will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A key string.
	 * @return An object which is the value.
	 */
	public long optLong(String key) {
		return this.optLong(key, 0);
	}

	/**
	 * Get an optional long value associated with a key, or the default if there
	 * is no such key or if the value is not a number. If the value is a string,
	 * an attempt will be made to evaluate it as a number.
	 *
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            The default.
	 * @return An object which is the value.
	 */
	public long optLong(String key, long defaultValue) {
		try {
			return this.getLong(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get an optional string associated with a key. It returns an empty string
	 * if there is no such key. If the value is not a string and is not null,
	 * then it is converted to a string.
	 *
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 */
	public String optString(String key) {
		return this.optString(key, "");
	}

	/**
	 * Get an optional string associated with a key. It returns the defaultValue
	 * if there is no such key.
	 *
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            The default.
	 * @return A string which is the value.
	 */
	public String optString(String key, String defaultValue) {
		try {
			return this.getString(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private void populateMap(Object bean) {
		Class klass = bean.getClass();

//  If klass is a System class then set includeSuperClass to false.

		boolean includeSuperClass = klass.getClassLoader() != null;

		Method[] methods = includeSuperClass ? klass.getMethods() : klass
				.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
			try {
				Method method = methods[i];
				if (Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if ("getClass".equals(name)
								|| "getDeclaringClass".equals(name)) {
							key = "";
						} else {
							key = name.substring(3);
						}
					} else if (name.startsWith("is")) {
						key = name.substring(2);
					}
					if (key.length() > 0
							&& Character.isUpperCase(key.charAt(0))
							&& method.getParameterTypes().length == 0) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase()
									+ key.substring(1);
						}

						Object result = method.invoke(bean, (Object[]) null);
						if (result != null) {
							this.map.put(key, wrap(result));
						}
					}
				}
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Put a key/boolean pair in the JSONObject.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Boolean which is the value.
	 * @return this.
	 */
	public JSONObject putBoolean(String key, Boolean value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/double pair in the JSONObject.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Double which is the value.
	 * @return this.
	 * @throws JSONException
	 *            If the value is a non-finite number
	 */
	public JSONObject putDouble(String key, Double value) throws JSONException {
		testValidity(value);
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/int pair in the JSONObject.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An Integer which is the value.
	 * @return this.
	 */
	public JSONObject putInt(String key, Integer value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value is a JSONArray.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A JSONArray value.
	 * @return this.
	 */
	public JSONObject putJSONArray(String key, JSONArray value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value will be a
	 * JSONArray produced from a Collection.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 * @throws JSONException
	 */
	public JSONObject putJSONArray(String key, Collection value) throws JSONException {
		this.put(key, new JSONArray(value));
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value is a JSONObject.
	 * JSONObject which is produced from a Map.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A JSONObject value.
	 * @return this.
	 */
	public JSONObject putJSONObject(String key, JSONObject value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value will be a
	 * JSONObject produced from a Map.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Map value.
	 * @return this.
	 * @throws JSONException
	 */
	public JSONObject putJSONObject(String key, Map value) throws JSONException {
		this.put(key, new JSONObject(value));
		return this;
	}

	/**
	 * Put a key/long pair in the JSONObject.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Long which is the value.
	 * @return this.
	 * @throws JSONException
	 *            If the value is a non-finite number
	 */
	public JSONObject putLong(String key, Long value) throws JSONException {
		testValidity(value);
		this.map.put(key, value);
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, where the value is null.
	 *
	 * @param key
	 *            A key string.
	 * @return this.
	 */
	public JSONObject putNull(String key) {
		this.put(key, null);
		return this;
	}

	/**
	 * Put a key/String pair in the JSONObject. The value can be null.
	 *
	 * @param key
	 *            A key string.
	 * @return this.
	 */
	public JSONObject putString(String key, String value) {
		this.put(key, value);
		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject.
	 * NOTE: If the value is not a valid JSON value type, then this will insert
	 * a null value for the key. It is highly recommended to use one of the
	 * type-safe put methods to make sure the correct value type is inserted.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. The value MUST be of one of
	 *            these types: Boolean, Double, Integer, JSONArray, JSONObject,
	 *            Long, String, or null
	 * @return this.
	 * @throws JSONException
	 *             If the value is non-finite number
	 */
	public JSONObject put(String key, Object value) throws JSONException {
		if (key == null) {
			throw new IllegalArgumentException("Null key.");
		}

		testValidity(value);
		this.map.put(key, wrap(value));
		return this;
	}

	/**
	 * Put all key/value pairs from another JSONObject in this JSONObject.
	 * If a key is already present, it is silently ignored.
	 *
	 * @param jo
	 * @return this
	 */
	public JSONObject putAll(JSONObject jo) {
		this.putAll(jo, false);
		return this;
	}

	/**
	 * Put all key/value pairs from another JSONObject in this JSONObject.
	 *
	 * @param jo
	 * @param overwrite
	 * @return this
	 */
	public JSONObject putAll(JSONObject jo, boolean overwrite) {
		if (jo != null) {
			Set<String> keys = jo.keySet();
			for(String key: keys) {
				if (overwrite) {
					this.putOpt(key, jo.get(key));
				}

				else this.putOnce(key, jo.get(key));
			}
		}

		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, but only if the key is non-null,
	 * and if there is not already a member with that name, and the value is
	 * valid (i.e. non-finite for Longs and Doubles).
	 *
	 * @param key
	 * @param value
	 * @return this.
	 */
	public JSONObject putOnce(String key, Object value) {
		if (key != null) {
			if (!this.has(key)) {
				try {
					this.put(key, value);
				} catch (JSONException ignore) {
				}
			}
		}

		return this;
	}

	/**
	 * Put a key/value pair in the JSONObject, but only if the key is non-null,
	 * and the value is valid (i.e. non-finite for Longs and Doubles).
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *            String, or the JSONObject.NULL object.
	 * @return this.
	 */
	public JSONObject putOpt(String key, Object value) {
		if (key != null) {
			try {
				this.put(key, value);
			} catch (JSONException ignore) {
			}
		}
		return this;
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, producing <\/,
	 * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
	 * contain a control character or an unescaped quote or backslash.
	 *
	 * @param string
	 *            A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		StringWriter sw = new StringWriter();
		synchronized (sw.getBuffer()) {
			try {
				return quote(string, sw).toString();
			} catch (IOException ignored) {
				// will never happen - we are writing to a string writer
				return "";
			}
		}
	}

	public static Writer quote(String string, Writer w) throws IOException {
		if (string == null || string.length() == 0) {
			w.write("\"\"");
			return w;
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();

		w.write('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					w.write('\\');
					w.write(c);
					break;
				case '/':
					if (b == '<') {
						w.write('\\');
					}
					w.write(c);
					break;
				case '\b':
					w.write("\\b");
					break;
				case '\t':
					w.write("\\t");
					break;
				case '\n':
					w.write("\\n");
					break;
				case '\f':
					w.write("\\f");
					break;
				case '\r':
					w.write("\\r");
					break;
				default:
					if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
							|| (c >= '\u2000' && c < '\u2100')) {
						w.write("\\u");
						hhhh = Integer.toHexString(c);
						w.write("0000", 0, 4 - hhhh.length());
						w.write(hhhh);
					} else {
						w.write(c);
					}
			}
		}
		w.write('"');
		return w;
	}

	/**
	 * Remove a name and its value, if present.
	 *
	 * @param key
	 *            The name to be removed.
	 * @return The value that was associated with the name, or null if there was
	 *         no value.
	 */
	public Object remove(String key) {
		return this.map.remove(key);
	}

	/**
	 * Try to convert a string into a number, boolean, or null. If the string
	 * can't be converted, return the string.
	 *
	 * @param string
	 *            A String.
	 * @return A simple JSON value.
	 */
	public static Object stringToValue(String string) {
		Double d;
		if (string.equals("")) {
			return string;
		}
		if (string.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		if (string.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		if (string.equalsIgnoreCase("null")) {
			return JSONObject.NULL;
		}

        /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */

		char b = string.charAt(0);
		if ((b >= '0' && b <= '9') || b == '-') {
			try {
				if (string.indexOf('.') > -1 || string.indexOf('e') > -1
						|| string.indexOf('E') > -1) {
					d = Double.valueOf(string);
					if (!d.isInfinite() && !d.isNaN()) {
						return d;
					}
				} else {
					Long myLong = new Long(string);
					if (string.equals(myLong.toString())) {
						if (myLong.longValue() == myLong.intValue()) {
							return new Integer(myLong.intValue());
						} else {
							return myLong;
						}
					}
				}
			} catch (Exception ignore) {
			}
		}
		return string;
	}

	/**
	 * Throw an exception if the object is a NaN or infinite number.
	 *
	 * @param object
	 *            The object to test.
	 * @throws JSONException
	 *             If o is a non-finite number.
	 */
	public static void testValidity(Object object) throws JSONException {
		if (object != null) {
			if (object instanceof Double) {
				if (((Double) object).isInfinite() || ((Double) object).isNaN()) {
					throw new JSONException(
							"JSON does not allow non-finite numbers.");
				}
			} else if (object instanceof Float) {
				if (((Float) object).isInfinite() || ((Float) object).isNaN()) {
					throw new JSONException(
							"JSON does not allow non-finite numbers.");
				}
			}
		}
	}

	/**
	 * Produce a JSONArray containing the values of the members of this
	 * JSONObject.
	 *
	 * @param names
	 *            A JSONArray containing a list of key strings. This determines
	 *            the sequence of the values in the result.
	 * @return A JSONArray of values.
	 * @throws JSONException
	 *             If any of the values are non-finite numbers.
	 */
	public JSONArray toJSONArray(JSONArray names) throws JSONException {
		if (names == null || names.length() == 0) {
			return null;
		}
		JSONArray ja = new JSONArray();
		for (int i = 0; i < names.length(); i += 1) {
			ja.put(this.opt(names.getString(i)));
		}
		return ja;
	}

	/**
	 * Make a JSON text of this JSONObject. For compactness, no whitespace is
	 * added. If this would not result in a syntactically correct JSON text,
	 * then null will be returned instead.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return a printable, displayable, portable, transmittable representation
	 *         of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public String toString() {
		try {
			return this.toString(0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Make a prettyprinted JSON text of this JSONObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation
	 *         of the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 * @throws JSONException
	 *             If the object contains an invalid number.
	 */
	public String toString(int indentFactor) throws JSONException {
		StringWriter w = new StringWriter();
		synchronized (w.getBuffer()) {
			return this.write(w, indentFactor, 0).toString();
		}
	}

	/**
	 * Make a JSON text of an Object value. If the object has an
	 * value.toJSONString() method, then that method will be used to produce the
	 * JSON text. The method is required to produce a strictly conforming text.
	 * If the object does not contain a toJSONString method (which is the most
	 * common case), then a text will be produced by other means. If the value
	 * is an array or Collection, then a JSONArray will be made from it and its
	 * toJSONString method will be called. If the value is a MAP, then a
	 * JSONObject will be made from it and its toJSONString method will be
	 * called. Otherwise, the value's toString method will be called, and the
	 * result will be quoted.
	 *
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param value
	 *            The value to be serialized.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 * @throws JSONException
	 *             If the value is or contains an invalid number.
	 */
	public static String valueToString(Object value) throws JSONException {
		if (value == null || value.equals(JSONObject.NULL)) {
			return "null";
		}
		if (value instanceof JSONString) {
			Object object;
			try {
				object = ((JSONString) value).toJSONString();
			} catch (Exception e) {
				throw new JSONException(e);
			}
			if (object instanceof String) {
				return (String) object;
			}
			throw new JSONException("Bad value from toJSONString: " + object);
		}
		if (value instanceof Number) {
			return numberToString((Number) value);
		}
		if (value instanceof Boolean || value instanceof JSONObject
				|| value instanceof JSONArray) {
			return value.toString();
		}
		if (value instanceof Map) {
			return new JSONObject((Map) value).toString();
		}
		if (value instanceof Collection) {
			return new JSONArray((Collection) value).toString();
		}
		if (value.getClass().isArray()) {
			return new JSONArray(value).toString();
		}
		return quote(value.toString());
	}

	/**
	 * Wrap an object, if necessary. If the object is null, return the NULL
	 * object. If it is an array or collection, wrap it in a JSONArray. If it is
	 * a map, wrap it in a JSONObject. If it is a standard property (Double,
	 * String, et al) then it is already wrapped. Otherwise, if it comes from
	 * one of the java packages, turn it into a string. And if it doesn't, try
	 * to wrap it in a JSONObject. If the wrapping fails, then null is returned.
	 *
	 * @param object
	 *            The object to wrap
	 * @return The wrapped value
	 */
	public static Object wrap(Object object) {
		try {
			if (object == null) {
				return JSONObject.NULL;
			}
			if (object instanceof JSONObject || object instanceof JSONArray
					|| NULL.equals(object) || object instanceof JSONString
					|| object instanceof Byte || object instanceof Character
					|| object instanceof Short || object instanceof Integer
					|| object instanceof Long || object instanceof Boolean
					|| object instanceof Float || object instanceof Double
					|| object instanceof String) {
				return object;
			}

			if (object instanceof Collection) {
				return new JSONArray((Collection) object);
			}
			if (object.getClass().isArray()) {
				return new JSONArray(object);
			}
			if (object instanceof Map) {
				return new JSONObject((Map) object);
			}
			Package objectPackage = object.getClass().getPackage();
			String objectPackageName = objectPackage != null ? objectPackage
					.getName() : "";
			if (objectPackageName.startsWith("java.")
					|| objectPackageName.startsWith("javax.")
					|| object.getClass().getClassLoader() == null) {
				return object.toString();
			}
			return new JSONObject(object);
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * Write the contents of the JSONObject as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return The writer.
	 * @throws JSONException
	 */
	public Writer write(Writer writer) throws JSONException {
		return this.write(writer, 0, 0);
	}

	static Writer writeValue(Writer writer, Object value,
								   int indentFactor, int indent) throws JSONException, IOException {
		if (value == null || value.equals(JSONObject.NULL)) {
			writer.write("null");
		} else if (value instanceof JSONObject) {
			((JSONObject) value).write(writer, indentFactor, indent);
		} else if (value instanceof JSONArray) {
			((JSONArray) value).write(writer, indentFactor, indent);
		} else if (value instanceof Map) {
			new JSONObject((Map) value).write(writer, indentFactor, indent);
		} else if (value instanceof Collection) {
			new JSONArray((Collection) value).write(writer, indentFactor,
					indent);
		} else if (value.getClass().isArray()) {
			new JSONArray(value).write(writer, indentFactor, indent);
		} else if (value instanceof Number) {
			writer.write(numberToString((Number) value));
		} else if (value instanceof Boolean) {
			writer.write(value.toString());
		} else if (value instanceof JSONString) {
			Object o;
			try {
				o = ((JSONString) value).toJSONString();
			} catch (Exception e) {
				throw new JSONException(e);
			}
			writer.write(o != null ? o.toString() : quote(value.toString()));
		} else {
			quote(value.toString(), writer);
		}
		return writer;
	}

	static void indent(Writer writer, int indent) throws IOException {
		for (int i = 0; i < indent; i += 1) {
			writer.write(' ');
		}
	}

	/**
	 * Write the contents of the JSONObject as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return The writer.
	 * @throws JSONException
	 */
	Writer write(Writer writer, int indentFactor, int indent)
			throws JSONException {
		try {
			boolean commanate = false;
			final int length = this.length();
			Iterator keys = this.keyIterator();
			writer.write('{');

			if (length == 1) {
				Object key = keys.next();
				writer.write(quote(key.toString()));
				writer.write(':');
				if (indentFactor > 0) {
					writer.write(' ');
				}
				writeValue(writer, this.map.get(key), indentFactor, indent);
			} else if (length != 0) {
				final int newindent = indent + indentFactor;
				while (keys.hasNext()) {
					Object key = keys.next();
					if (commanate) {
						writer.write(',');
					}
					if (indentFactor > 0) {
						writer.write('\n');
					}
					indent(writer, newindent);
					writer.write(quote(key.toString()));
					writer.write(':');
					if (indentFactor > 0) {
						writer.write(' ');
					}
					writeValue(writer, this.map.get(key), indentFactor,
							newindent);
					commanate = true;
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, indent);
			}
			writer.write('}');
			return writer;
		} catch (IOException exception) {
			throw new JSONException(exception);
		}
	}
}
