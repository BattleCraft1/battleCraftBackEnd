package pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria;

import java.util.List;

public class SearchCriteria {

    public SearchCriteria(){

    }

    public SearchCriteria(List<String> keys, String operation, Object value) {
        this.keys = keys;
        this.operation = operation;
        this.value = value;
    }

    private List<String> keys;
    private String operation;
    private Object value;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
