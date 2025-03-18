package org.json;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JSONArray {
    
    private final List<Object> l = new LinkedList<Object>();

    public JSONArray(Object[] value) {
        for(final Object v : value) {
            l.add(v);
        }
    }

    public JSONArray() {
    }

    public JSONArray put(Object o) {
        l.add(o);
        return this;
    }

	public int length() {
        return l.size();
	}

	public JSONArray getJSONArray(int i) {
        return (JSONArray)(l.get(i));
	}

	public int getInt(int i) {
        return (Integer)(l.get(i));
	}

    
}
