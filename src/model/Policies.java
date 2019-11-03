package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.Permission;

public class Policies {
	
	private List<Policy> policies;
	private Map<String,Permission> actions;
	
	//oggetto di utilità che associa ad ogni politica un permesso ovvero
	//l'azione consentita nel caso in cui una delle politiche venga rispettata
	public Policies()
	{
		policies= new ArrayList<Policy>();
		actions=new HashMap<String, Permission>();
	}

	public List<Policy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<Policy> policies) {
		this.policies = policies;
	}

	public Map<String,Permission> getAction() {
		return actions;
	}

	public void setAction(Map<String,Permission> action) {
		this.actions = action;
	}

}
