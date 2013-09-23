package org.saadahmed.json;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 *
 * @author Saad Ahmed
 */
public class OrderedJSONObject extends JSONObject {


	/**
	 * Constructs an empty OrderedJSONObject.
	 */
	public OrderedJSONObject() {
		super(TreeMap.class);
	}

	/**
	 * Constructs an OrderedJSONObject from a Map.
	 *
	 * @param map
	 *            A map object that can be used to initialize the contents of
	 *            the OrderedJSONObject.
	 */
	public OrderedJSONObject(Map map) {
		super(TreeMap.class);

		if (map != null) {
			Set<Map.Entry> entrySet = map.entrySet();
			for (Map.Entry e: entrySet) {
				this.putOpt(e.getKey().toString(), e.getValue());
			}
		}
	}

	/**
	 * Construct an OrderedJSONObject from another JSONObject.
	 *
	 * @param jo
	 *            A JSONObject
	 */
	public OrderedJSONObject(JSONObject jo) {
		super(TreeMap.class);
		this.putAll(jo);
	}

	/**
	 * Construct an OrderedJSONObject from a subset of another JSONObject.
	 * An array of strings is used to identify the keys that should be copied.
	 * Missing keys are ignored.
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
	public OrderedJSONObject(JSONObject jo, String[] names) {
		super(jo, names, TreeMap.class);
	}

	/**
	 * Construct a OrderedJSONObject from a source JSON text string.
	 *
	 * @param source
	 *            A string beginning with <code>{</code>&nbsp;<small>(left
	 *            brace)</small> and ending with <code>}</code>
	 *            &nbsp;<small>(right brace)</small>.
	 * @exception JSONException
	 *            If there is a syntax error in the source string or a
	 *            duplicated key.
	 */
	public OrderedJSONObject(String source) throws JSONException {
		super(source, TreeMap.class);
	}

	/**
	 * Construct a OrderedJSONObject from an Object using bean getters. It
	 * reflects on all of the public methods of the object. For each of the
	 * methods with no parameters and a name starting with <code>"get"</code> or
	 * <code>"is"</code> followed by an uppercase letter, the method is invoked,
	 * and a key and the value returned from the getter method are put into the
	 * new OrderedJSONObject.
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
	 *            an OrderedJSONObject.
	 */
	public OrderedJSONObject(Object bean) {
		super(bean, TreeMap.class);
	}

	/**
	 * Construct a OrderedJSONObject from an Object, using reflection to find
	 * the public members. The resulting JSONObject's keys will be the strings
	 * from the names array, and the values will be the field values associated
	 * with those keys in the object. If a key is not found or not visible,
	 * then it will not be copied into the new OrderedJSONObject.
	 *
	 * @param object
	 *            An object that has fields that should be used to make an
	 *            OrderedJSONObject.
	 * @param names
	 *            An array of strings, the names of the fields to be obtained
	 *            from the object.
	 */
	public OrderedJSONObject(Object object, String[] names) {
		super(object, TreeMap.class);
	}


}
