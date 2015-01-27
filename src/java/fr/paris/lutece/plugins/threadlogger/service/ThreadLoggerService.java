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

import fr.paris.lutece.portal.service.util.AppLogService;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * ThreadLoggerService
 */
public class ThreadLoggerService
{

    public static void watchThreads( int nThreadsLimit )
    {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = mxBean.getAllThreadIds();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo( threadIds );
        int nThreadCount = threadInfos.length;
        
        if( nThreadCount > nThreadsLimit )
        {
            AppLogService.info( "Thread count : " + nThreadCount + " is greater than limit : " + nThreadsLimit );
            AppLogService.info( "---------- Threads list -----------" );
            for (ThreadInfo threadInfo : threadInfos)
            {
                StringBuilder sbLog = new StringBuilder();
                Thread.State state = threadInfo.getThreadState();
                sbLog.append( "[Thread] [" ).append( state.name()).append( "] ").append( threadInfo.getThreadName() );
                sbLog.append( "- bc=" ).append( threadInfo.getBlockedCount() );
                sbLog.append( "- bt=" ).append( threadInfo.getBlockedTime() );
                sbLog.append( "- wc=" ).append( threadInfo.getWaitedCount() );
                sbLog.append( "- wt=" ).append( threadInfo.getWaitedCount() );
                if( state == Thread.State.BLOCKED )
                {
                    addStackTrace( sbLog , threadInfo.getStackTrace() );
                }
                AppLogService.info( sbLog.toString() );
            }
            AppLogService.info( "-----------------------------------" );
        }

    }

    private static void addStackTrace( StringBuilder sbLog, StackTraceElement[] stackTrace)
    {
        for( StackTraceElement element : stackTrace )
        {
            sbLog.append( element.getClassName() ).append( " ").append( element.getMethodName()).append( " ").append( element.getLineNumber() ).append( "\n");
        }
    }
}
