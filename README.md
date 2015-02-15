
#Plugin threadlogger

##Plugin threadlogger

###Introduction

This plugin allows you to log all threads with possibly their stack when their number exceeds a given limit.

###Configuration

The configuration can be setup in the threadlogger.properties file located in WEB-INF/conf/plugins/

The limit and the stack options are defined as follow :

```

# threadlogger properties file

# Logs if thread count > 40
threadlogger.threads.limit=40

# delay 20s
threadlogger.watch.delay=20000

# Show current stack for states RUNNABLE, TIMED_WAITING, BLOCKED, WAITING, NEW, TERMINATED
threadlogger.showStackTrace.states=RUNNABLE, BLOCKED                        
                        
```

###Usage

When the plugin is activated, logs will appear in the application.log file as soon as the limit is exceeded.
