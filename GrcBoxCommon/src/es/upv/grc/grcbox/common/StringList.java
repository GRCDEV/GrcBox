package es.upv.grc.grcbox.common;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class StringList {
	Collection<String> list;
	
	public StringList(){
		
	};
	
	public StringList(Collection<String> list) {
		super();
		this.list = list;
	}

	public Collection<String> getList() {
		return list;
	}
}
