package me.model.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.metadata.Attribute;
import me.metadata.Model;
import me.util.Constant;

/**
 * @author VivekMadurai
 *
 */
public class Criteria {
	//applies only for the root criteria
	private Model model;
	private Session session;
	private int limit;
	private int offset;
	private Map<String, Boolean> orderBy;

	//applies for all nested criteria
	private Operator operator;
	private List<Criteria> criteriaList;
	private List<Condition> conditionList;
	
	
	public Criteria(Model model, Session session) {
		this.model    = model;
		this.session  = session; 
		this.operator = Operator.AND;
		this.limit    = Constant.DBLIMIT;
		this.offset   = Constant.DBOFFSET;
		this.orderBy  = new HashMap<String, Boolean>();
		
		//if they directly add the condition in root criteria
		this.conditionList = new ArrayList<Condition>();
		//adding base condition which applies for all query
		Attribute tenantAttr = model.getAttributeByName("Tenant");
		addCondition(tenantAttr.getId(), Operator.EQUAL, session.getCurrentUser().getTenantId());
	}
	
	private Criteria(Operator operator) {
		this.operator = operator;
		this.criteriaList  = new ArrayList<Criteria>();
		this.conditionList = new ArrayList<Condition>();
	}
	
	/**
	 * add your constraint for the results to be retrieved
	 * @param lhsName
	 * @param operator
	 * @param rhsValue
	 * @return
	 */
	public Criteria addCondition(String lhsName, Operator operator, Object rhsValue) {
		conditionList.add(new Condition(lhsName, operator, rhsValue));
		return this;
	}
	
	/**
	 * add your order by criteria here
	 * @param attrName
	 * @return
	 */
	public Criteria addOrder(String attrName) {
		orderBy.put(attrName, false);
		return this;
	}
	
	public Criteria addOrder(String attrName, boolean isDesc) {
		orderBy.put(attrName, isDesc);
		return this;
	}
	
	/**
	 * Set a limit upon the number of objects to be retrieved.
	 * @param limit
	 */
	public Criteria setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	/**
	 * Set the first result to be retrieved.
	 * @param offset
	 */
	public Criteria setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	/**
	 * add your nested `AND` criteria
	 * @return
	 */
	public Criteria addAnd() {
		Criteria criteria = new Criteria(Operator.AND);
		criteriaList.add(criteria);
		return criteria;
	}
	
	/**
	 * add you nested `OR` criteria
	 * @return
	 */
	public Criteria addOr() {
		Criteria criteria = new Criteria(Operator.OR);
		criteriaList.add(criteria);
		return criteria;
	}
	
	Operator getOperator() {
		return operator;
	}
	
	List<Criteria> getCriteriaList() {
		return criteriaList;
	}
	
	List<Condition> getConditionList() {
		return conditionList;
	}
	
	
	// below methods are applicable only for the root criteria 
	Model getModel() {
		return model;
	}
	
	int getLimit() {
		return limit;
	}
	
	int getOffset() {
		return offset;
	}
	
	Map<String, Boolean> getOrder() {
		return orderBy;
	}
	
	class Condition {
		private String lhsName;
		private  Operator operator;
		private Object rhsValue;
		
		public Condition(String lhsName, Operator operator, Object rhsValue) {
			this.lhsName = lhsName;
			this.operator = operator;
			this.rhsValue = rhsValue;
		}

		public String getLhsName() {
			return lhsName;
		}

		public Operator getOperator() {
			return operator;
		}

		public Object getRhsValue() {
			return rhsValue;
		}
	}
	
	public enum Operator {
	    LESS_THAN("<"),
	    LESS_THAN_OR_EQUAL("<="),
	    GREATER_THAN(">"),
	    GREATER_THAN_OR_EQUAL(">="),
	    EQUAL("="),
	    NOT_EQUAL("!="),
	    IN("IN"),
	    LIKE("LIKE"),
	    AND("&&"),
	    OR("||");
	    
	    private final String shortName;
	    private Operator(String shortName) {
	      this.shortName = shortName;
	    }

	    @Override
	    public String toString() {
	      return shortName;
	    }
	}

}
