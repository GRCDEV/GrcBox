package es.upv.grc.grcbox.android.activities;

import es.upv.grc.grcbox.common.GrcBoxRule;

public class DisplayGrcBoxRule {
	private GrcBoxRule rule;
	private String name;
	public DisplayGrcBoxRule(GrcBoxRule rule, String name) {
		super();
		this.rule = rule;
		this.name = name;
	}
	
	public GrcBoxRule getRule() {
		return rule;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString(){
		return name + rule.toString();
	}
}
