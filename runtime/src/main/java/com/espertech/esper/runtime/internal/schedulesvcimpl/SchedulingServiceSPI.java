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
package com.espertech.esper.runtime.internal.schedulesvcimpl;

import com.espertech.esper.common.internal.schedule.SchedulingService;

import java.util.Set;

/**
 * Service provider interface for scheduling service.
 */
public interface SchedulingServiceSPI extends SchedulingService {
    Long getNearestTimeHandle();

    void visitSchedules(ScheduleVisitor visitor);

    /**
     * Initialization is optional and provides a chance to preload things after statements are available.
     */
    void init();

    void transfer(Set<Integer> statementIds, SchedulingServiceSPI schedulingService);
}
