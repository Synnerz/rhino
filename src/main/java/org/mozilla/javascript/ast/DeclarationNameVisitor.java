package org.mozilla.javascript.ast;

import org.mozilla.javascript.Kit;

import java.util.ArrayList;
import java.util.List;

public class DeclarationNameVisitor implements NodeVisitor {
    private List<Name> names = new ArrayList<>();

    public List<Name> getNames() {
        return names;
    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof FunctionNode) {
            names.add(((FunctionNode) node).getFunctionName());
            return false;
        }

        if (node instanceof VariableInitializer) {
            visit(((VariableInitializer) node).getTarget());
            return false;
        }

        if (node instanceof ObjectLiteral) {
            return ((ObjectLiteral) node).isDestructuring();
        }

        if (node instanceof ObjectProperty) {
            ObjectProperty prop = (ObjectProperty) node;
            assert !prop.isMethod();

            if (Boolean.TRUE.equals(prop.getProp(AstNode.SHORTHAND_PROPERTY_NAME))) {
                visit(prop.getLeft());
                return false;
            }

            visit(prop.getRight());
            return false;
        }

        if (node instanceof Name) {
            names.add((Name) node);
            return false;
        }

        if (node instanceof StringLiteral) {
            throw Kit.codeBug("TODO: Is this possible?");
        }

        return true;
    }
}
