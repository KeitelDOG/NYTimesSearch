package com.megalobiz.nytimessearch.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by KeitelRobespierre on 7/23/2016.
 */
public class SearchSettings implements Serializable {

    SimpleDateFormat beforeDate;

    String sortOrder;
    public enum Sort {
        newest, oldest
    }

    ArrayList<String> filters;

    public SearchSettings() {
        // apply default value to settings
        filters = new ArrayList<String>();
    }

    public SearchSettings(Sort sortOrder) {
        this.sortOrder = sortOrder.name();
        filters = new ArrayList<String>();
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public SimpleDateFormat getBeforeDate() {
        return beforeDate;
    }

    public ArrayList<String> getFilters() {
        return filters;
    }

    public void setSortOrder(Sort sortOrder) {
        this.sortOrder = sortOrder.name();
    }

    public void setBeforeDate(SimpleDateFormat beforeDate) {
        this.beforeDate = beforeDate;
    }

    // Add a String to apply Filter
    public void addFilter(String filter) {
        filters.add(filter);
    }

    // generate a string formatted for NY times search filters using Lucene Syntax
    public String generateNewsDeskFiltersOR() {
        String luceneSyntax = "news_desk:(";

        for(String filter : filters) {
            luceneSyntax = luceneSyntax.concat(filter+" ");
        }

        luceneSyntax = luceneSyntax.concat(")");

        return luceneSyntax;
    }

}
