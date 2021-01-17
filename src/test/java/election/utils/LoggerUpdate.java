package election.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class LoggerUpdate {
    public static void updateLogger(String file_name, Class<?> clazz){
        LoggerContext ctx = ((org.apache.logging.log4j.core.Logger)LogManager.getRootLogger()).getContext();
        Configuration config = ctx.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("%d{HH:mm:ss.SSS} [Zuraiz] %msg%n")
                .build();

        //create new appender/logger
        LoggerConfig loggerConfig = new LoggerConfig(clazz.getName(), Level.ALL, false);

        FileAppender appender = FileAppender.newBuilder()
                .withFileName(file_name)
                .withAppend(false)
                .withLocking(false)
                .setName("fileAppender")
                .withImmediateFlush(true).setIgnoreExceptions(true).withBufferedIo(true)
                .withBufferSize(8192)
                .setLayout(layout)
                .setFilter(null)
                .withAdvertise(false)
                .withAdvertiseUri("")
                .setConfiguration(config).build();
        appender.start();
        loggerConfig.addAppender(appender, Level.ALL, null);
        config.addLogger(clazz.getName(), loggerConfig);

        ctx.updateLoggers();
    }
}
