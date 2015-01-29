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

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;


/**
 * ThreadLoggerService
 */
public class ThreadLoggerService
{
    private static ThreadComparator _comparator = new ThreadComparator(  );

    /**
     * Watch threads and log threads infos if the number of thread is greater than a given limit
     * @param nThreadsLimit The limit
     */
    public static void watchThreads( int nThreadsLimit, String[] states )
    {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean(  );
        long[] threadIds = mxBean.getAllThreadIds(  );
        ThreadInfo[] threadInfos = mxBean.getThreadInfo( threadIds );
        int nThreadCount = threadInfos.length;

        if ( nThreadCount > nThreadsLimit )
        {
            Arrays.sort( threadInfos, _comparator );
            AppLogService.info( "Thread count : " + nThreadCount + " is greater than limit : " + nThreadsLimit );
            AppLogService.info( "---------- Threads list -----------" );

            Map<Thread, StackTraceElement[]> mapThreads = Thread.getAllStackTraces(  );

            for ( ThreadInfo threadInfo : threadInfos )
            {
                StringBuilder sbLog = new StringBuilder(  );
                Thread.State state = threadInfo.getThreadState(  );
                LockInfo lock = threadInfo.getLockInfo(  );

                sbLog.append( "[Thread] [" ).append( state.name(  ) ).append( "] [" )
                     .append( threadInfo.getThreadName(  ) ).append( "]" );

                if ( lock != null )
                {
                    sbLog.append( " locked on " ).append( lock.getClassName(  ) );
                }

                sbLog.append( " - bc=" ).append( threadInfo.getBlockedCount(  ) );
                sbLog.append( " - bt=" ).append( threadInfo.getBlockedTime(  ) );
                sbLog.append( " - wc=" ).append( threadInfo.getWaitedCount(  ) );
                sbLog.append( " - wt=" ).append( threadInfo.getWaitedCount(  ) );

                if ( showStackTrace( state.toString(  ), states ) )
                {
                    StackTraceElement[] stackTrace = threadInfo.getStackTrace(  );

                    if ( ( stackTrace != null ) && ( stackTrace.length > 0 ) )
                    {
                        addStackTrace( sbLog, stackTrace );
                    }

                    stackTrace = getThreadStackTrace( mapThreads, threadInfo );

                    if ( ( stackTrace != null ) && ( stackTrace.length > 0 ) )
                    {
                        addStackTrace( sbLog, stackTrace );
                    }

                    LockInfo[] lockInfo = threadInfo.getLockedSynchronizers(  );

                    if ( lockInfo != null )
                    {
                        addLockInfo( sbLog, lockInfo );
                    }

                    MonitorInfo[] monitorInfo = threadInfo.getLockedMonitors(  );

                    if ( monitorInfo != null )
                    {
                        addMonitorInfo( sbLog, monitorInfo );
                    }
                }

                AppLogService.info( sbLog.toString(  ) );
            }

            AppLogService.info( "-----------------------------------" );
        }
    }

    private static StackTraceElement[] getThreadStackTrace( Map<Thread, StackTraceElement[]> mapThreads,
        ThreadInfo threadInfo )
    {
        for ( Thread thread : mapThreads.keySet(  ) )
        {
            if ( thread.getId(  ) == threadInfo.getThreadId(  ) )
            {
                return mapThreads.get( thread );
            }
        }

        return null;
    }

    /**
     * Logs a stacktrace
     * @param sbLog The logs buffer
     * @param stackTrace The stacktrace
     */
    private static void addStackTrace( StringBuilder sbLog, StackTraceElement[] stackTrace )
    {
        sbLog.append( "\n" );

        for ( StackTraceElement element : stackTrace )
        {
            sbLog.append( "\t at " ).append( element.getClassName(  ) ).append( "." ).append( element.getMethodName(  ) );
            sbLog.append( "(" ).append( element.getFileName(  ) ).append( ":" ).append( element.getLineNumber(  ) )
                 .append( ")\n" );
        }
    }

    /**
     * Logs locks infos
     * @param sbLog The logs buffer
     * @param lockInfos The lock infos
     */
    private static void addLockInfo( StringBuilder sbLog, LockInfo[] lockInfos )
    {
        for ( LockInfo lockInfo : lockInfos )
        {
            sbLog.append( lockInfo.getClassName(  ) ).append( "\n" );
        }
    }

    private static void addMonitorInfo( StringBuilder sbLog, MonitorInfo[] monitorInfos )
    {
        for ( MonitorInfo monitorInfo : monitorInfos )
        {
            sbLog.append( monitorInfo.getClassName(  ) ).append( "\n" );

            StackTraceElement stackTraceElement = monitorInfo.getLockedStackFrame(  );

            if ( stackTraceElement != null /* && ( state == Thread.State.BLOCKED ) */ )
            {
                sbLog.append( stackTraceElement.getClassName(  ) ).append( " " )
                     .append( stackTraceElement.getMethodName(  ) ).append( " " )
                     .append( stackTraceElement.getLineNumber(  ) ).append( "\n" );
            }
        }
    }

    /**
     * Return true if a given state is among a given state list
     * @param strState The state
     * @param states the state list
     * @return true if a given state is among a state list
     */
    private static boolean showStackTrace( String strState, String[] states )
    {
        for ( String s : states )
        {
            if ( s.equals( strState ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Comparator to sort thread list by thread name
     */
    private static class ThreadComparator implements Comparator<ThreadInfo>
    {
        /**
         * {@inheritDoc }
         */
        @Override
        public int compare( ThreadInfo o1, ThreadInfo o2 )
        {
            return o1.getThreadName(  ).compareTo( o2.getThreadName(  ) );
        }
    }
}
