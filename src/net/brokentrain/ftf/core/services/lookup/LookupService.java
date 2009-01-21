package net.brokentrain.ftf.core.services.lookup;

import java.util.List;

public interface LookupService {

    public List<Article> getArticles();

    public boolean hasResults();

    public void lookup();
}
