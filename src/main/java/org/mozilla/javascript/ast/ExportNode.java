package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ExportNode extends ImportNode {
    private AstNode exportedValue = null;
    private String identifier = null;
    private boolean defaultExport = false;

    public ExportNode() {
        super();
        type = Token.EXPORT;
    }

    public AstNode getExportedValue() {
        return exportedValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setExportedValue(AstNode node, String identifier) {
        this.exportedValue = node;
        this.identifier = identifier;
    }

    public void setExportedValue(AstNode node) {
        this.exportedValue = node;
        this.defaultExport = true;
    }

    public boolean isDefaultExport() {
        return defaultExport;
    }
}