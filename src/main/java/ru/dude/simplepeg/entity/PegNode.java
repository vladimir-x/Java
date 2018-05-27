package ru.dude.simplepeg.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree object with result data
 *
 * Created by dude on 29.10.2017.
 */
public class PegNode{

    private static Integer nextId = 0;

    Integer id;
    SpegTypes type;
    StringBuilder match = new StringBuilder();
    Integer startPosition;
    Integer endPosition;

    List<PegNode> childrens = new ArrayList<>();

    ResultType resultType = ResultType.NONE;
    String error;
    private String execName;

    public PegNode(){
        id = nextId++;
    }

    public void appendChild(PegNode child) {
        childrens.add(child);
        match.append(child.match);

        if (startPosition == null || getStartPosition() > child.getStartPosition()) {
            startPosition = child.getStartPosition();
        }

        if (endPosition == null || endPosition < child.getEndPosition()) {
            endPosition =child.getEndPosition();
        }
    }



    public void toJson(StringBuilder sb,int level){

        addtabs(sb,level).append("{\n");
        addtabs(sb,level+1).append("\"id\" :\"").append(id).append("\",\n");
        addtabs(sb,level+1).append("\"execName\" :\"").append(execName).append("\",\n");
        addtabs(sb,level+1).append("\"type\" :\"").append(type).append("\",\n");
        addtabs(sb,level+1).append("\"match\" :\"").append(match).append("\",\n");
        addtabs(sb,level+1).append("\"startPosition\" :").append(startPosition).append(",\n");
        addtabs(sb,level+1).append("\"endPosition\" :").append(endPosition).append(",\n");

        if (childrens.size()>0) {
            addtabs(sb, level + 1).append("\"childrens\":[\n");
            for (PegNode children : childrens) {
                children.toJson(sb, level + 2);
            }
            addtabs(sb, level + 1).append("]\n");
        }
        addtabs(sb,level).append("},\n");
    }

    private StringBuilder addtabs(StringBuilder sb,int level){
        for (int i=0;i<level;++i){
            sb.append("\t");
        }
        return sb;
    }

    public PegNode child(String execName){

        for (PegNode ch : childrens) {
            if (ch.getExecName().equals(execName)){
                return ch;
            }
        }
        return null;
    }


    public SpegTypes getType() {
        return type;
    }

    public PegNode setType(SpegTypes type) {
        this.type = type;
        return this;
    }

    public StringBuilder getMatch() {
        return match;
    }

    public PegNode addMatch(String match) {
        this.match.append(match);
        return this;
    }

    public PegNode setMatch(StringBuilder match) {
        this.match = match;
        return this;
    }

    public Integer getStartPosition() {
        return startPosition;

    }

    public PegNode setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public PegNode setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
        return this;
    }

    public List<PegNode> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<PegNode> childrens) {
        this.childrens = childrens;
    }


    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setExecName(String execName) {
        this.execName = execName;
    }

    public String getExecName() {
        return execName;
    }
}
