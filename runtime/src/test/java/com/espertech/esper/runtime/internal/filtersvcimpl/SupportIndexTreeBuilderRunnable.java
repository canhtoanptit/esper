/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.runtime.internal.filtersvcimpl;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.internal.filterspec.FilterSpecActivatable;
import com.espertech.esper.common.internal.filterspec.FilterValueSetParam;
import com.espertech.esper.common.internal.filtersvc.FilterHandle;
import com.espertech.esper.runtime.internal.support.ObjectReservationSingleton;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class SupportIndexTreeBuilderRunnable implements Runnable {
    protected final static Random random = new Random(System.currentTimeMillis());

    private FilterHandleSetNode topNode;
    private Vector<FilterSpecActivatable> testFilterSpecs;
    private Vector<EventBean> matchedEvents;
    private Vector<EventBean> unmatchedEvents;
    private final EventType eventType;
    private FilterServiceGranularLockFactory lockFactory = new FilterServiceGranularLockFactoryReentrant();

    public SupportIndexTreeBuilderRunnable(EventType eventType, FilterHandleSetNode topNode, Vector<FilterSpecActivatable> testFilterSpecs, Vector<EventBean> matchedEvents, Vector<EventBean> unmatchedEvents) {
        this.eventType = eventType;
        this.topNode = topNode;
        this.testFilterSpecs = testFilterSpecs;
        this.matchedEvents = matchedEvents;
        this.unmatchedEvents = unmatchedEvents;
    }

    public void run() {
        long currentThreadId = Thread.currentThread().getId();

        // Choose one of filter specifications, randomly, then reserve to make sure no one else has the same
        FilterSpecActivatable filterSpec = null;
        EventBean unmatchedEvent = null;
        EventBean matchedEvent = null;

        int index = 0;
        do {
            index = random.nextInt(testFilterSpecs.size());
            filterSpec = testFilterSpecs.get(index);
            unmatchedEvent = unmatchedEvents.get(index);
            matchedEvent = matchedEvents.get(index);
        }
        while (!ObjectReservationSingleton.getInstance().reserve(filterSpec));

        // Add expression
        FilterValueSetParam[][] filterValues = filterSpec.getValueSet(null, null, null, null);
        FilterHandle filterCallback = new SupportFilterHandle();
        IndexTreeBuilderAdd.add(filterValues, filterCallback, topNode, lockFactory);

        // Fire a no-match
        List<FilterHandle> matches = new LinkedList<FilterHandle>();
        topNode.matchEvent(unmatchedEvent, matches);

        if (matches.size() != 0) {
            log.error(".run (" + currentThreadId + ") Got a match but expected no-match, matchCount=" + matches.size() + "  bean=" + unmatchedEvent +
                    "  match=" + matches.get(0).hashCode());
            TestCase.assertFalse(true);
        }

        // Fire a match
        topNode.matchEvent(matchedEvent, matches);

        if (matches.size() != 1) {
            log.error(".run (" + currentThreadId + ") Got zero or two or more match but expected a match, count=" + matches.size() +
                    "  bean=" + matchedEvent);
            TestCase.assertFalse(true);
        }

        // Remove the same expression again
        IndexTreeBuilderRemove.remove(eventType, filterCallback, filterValues[0], topNode);
        log.debug(".run (" + Thread.currentThread().getId() + ")" + " Completed");

        ObjectReservationSingleton.getInstance().unreserve(filterSpec);
    }

    private static final Logger log = LoggerFactory.getLogger(SupportIndexTreeBuilderRunnable.class);
}
