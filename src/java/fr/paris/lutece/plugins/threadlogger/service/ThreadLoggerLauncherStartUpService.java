/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.threadlogger.service;

import fr.paris.lutece.portal.service.daemon.DaemonEntry;
import fr.paris.lutece.portal.service.init.PostStartUpService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * ThreadLoggerLauncherStartUpService
 */
public class ThreadLoggerLauncherStartUpService implements PostStartUpService
{
    private static final String SERVICE_NAME = "ThreadLogger Launcher";
    private static final String PROPERTY_LIMIT = "threadlogger.threads.limit";
    private static final int DEFAULT_LIMIT = 4;
    private static final String PROPERTY_DELAY = "threadlogger.watch.delay";
    private static final long DEFAULT_DELAY = 10000;
    private static final String PROPERTY_STATES = "threadlogger.showStackTrace.states";
    private static final String DEFAULT_STATES = "BLOCKED, RUNNABLE";

    @Override
    public void process(  )
    {
        int nLimit = AppPropertiesService.getPropertyInt( PROPERTY_LIMIT, DEFAULT_LIMIT );
        long nDelay = AppPropertiesService.getPropertyLong( PROPERTY_DELAY, DEFAULT_DELAY );
        String strStates = AppPropertiesService.getProperty( PROPERTY_STATES, DEFAULT_STATES );
        WatcherThread watcher = new WatcherThread( nLimit, nDelay, getStates( strStates ) );
        watcher.start(  );
    }

    @Override
    public String getName(  )
    {
        return SERVICE_NAME;
    }

    private String[] getStates( String strStates )
    {
        String[] states = strStates.split( "," );

        for ( int i = 0; i < states.length; i++ )
        {
            states[i] = states[i].trim(  );
        }

        return states;
    }
}
