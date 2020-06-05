/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.database.util;

import org.hibernate.search.batchindexing.impl.SimpleIndexingProgressMonitor;

/**
 *
 * @author akinw
 */
public class CustomIndexerProgressMonitor extends SimpleIndexingProgressMonitor {
    private int main_count = 0;
    private int main_num = 0;

    @Override
    public void addToTotalCount(long count) {
        main_count += count;
        System.out.println("total count: " + main_count);
    }

    @Override
    public void documentsBuilt(int number) {
        main_num += number;
        System.out.println("documents built: " + main_num);
    }
    
}
