package net.brokentrain.ftf.core.services.lookup;

public interface ProcessingArticle {

    public String getAbstractLink();

    public String getAbstractText();

    public String getFullTextLink();

    public boolean hasFullTextLink();

    public void setAbstractLink(String abstractLink);

    public void setAbstractText(String abstractText);

    public void setFullTextLink(String fullTextLink);

    public void setHasFullTextLink(boolean hasFullTextLink);
}
