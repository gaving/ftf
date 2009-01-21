package net.brokentrain.ftf.core.services;

public interface CoreSearchService extends WebSearchService {

    public void increment();

    public void setInvestigate(boolean investigate);

    public void setProcessingAbstract(boolean processing);

}
