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
package com.espertech.esper.common.client.context;

/**
 * Context partition state event.
 */
public abstract class ContextStateEventContextPartition extends ContextStateEvent {
    private final int id;

    /**
     * Ctor.
     *
     * @param runtimeURI          runtime URI
     * @param contextDeploymentId deployment id of create-context statement
     * @param contextName         context name
     * @param id                  context partition id
     */
    public ContextStateEventContextPartition(String runtimeURI, String contextDeploymentId, String contextName, int id) {
        super(runtimeURI, contextDeploymentId, contextName);
        this.id = id;
    }

    /**
     * Returns the context partition id
     *
     * @return id
     */
    public int getId() {
        return id;
    }
}
