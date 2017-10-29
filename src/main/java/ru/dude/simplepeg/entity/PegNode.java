package ru.dude.simplepeg.entity;

import ru.dude.simplepeg.Executable;
import ru.dude.simplepeg.ParseInputException;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree object with result data
 *
 * Created by dude on 29.10.2017.
 */
public abstract class PegNode implements Executable {
    SpegTypes type;
    StringBuilder match = new StringBuilder();
    Integer startPosition;
    Integer endPosition;

    boolean resExist = true;

    List<PegNode> childrens = new ArrayList<>();

    protected void appendChild(PegNode child) {
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
        addtabs(sb,level+1).append("\"type\" :\"").append(type).append("\",\n");
        addtabs(sb,level+1).append("\"match\" :\"").append(match).append("\",\n");
        addtabs(sb,level+1).append("\"startPosition\" :").append(startPosition).append(",\n");
        addtabs(sb,level+1).append("\"endPosition\" :").append(endPosition).append(",\n");

        if (childrens.size()>0) {
            addtabs(sb, level + 1).append("\"childrens\":[\n");
            for (PegNode children : childrens) {
                children.toJson(sb, level + 1);
            }
            addtabs(sb, level + 1).append("]\n");
        }
        addtabs(sb,level).append("}\n");
    }

    private StringBuilder addtabs(StringBuilder sb,int level){
        for (int i=0;i<level;++i){
            sb.append("\t");
        }
        return sb;
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

    public boolean isResExist() {
        return resExist;
    }

    public void setResExist(boolean resExist) {
        this.resExist = resExist;
    }

    public void clearNode(){
        this.match = new StringBuilder();
        this.startPosition = null;
        this.endPosition = null;
        this.childrens = new ArrayList<>();
    }

    public PegNode copyTruncate(){
        PegNode copy = new PegNode() {
            @Override
            public boolean exec() throws ParseInputException {
                return false;
            }
        };

        copy.type = this.type;
        copy.match = this.match;
        copy.startPosition = this.startPosition;
        copy.endPosition = this.endPosition;
        copy.setChildrens(this.childrens);

        clearNode();
        return copy;
    }
}
