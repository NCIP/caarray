//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.io.logging;

import org.apache.log4j.Logger;

/**
 * Utility methods for logging functionality.
 */
public final class LogUtil {

    private LogUtil() {
        super();
    }

    /**
     * Logs a subsystem entry tracing message (at DEBUG level), listing the method and
     * arguments.
     *
     * @param log write to this log.
     * @param arguments the arguments to the method call.
     */
    public static void logSubsystemEntry(Logger log, Object... arguments) {
        if (log.isDebugEnabled()) {
            logMethodCall(LoggingSource.SUBSYSTEM, log, arguments, MethodTraceType.ENTRY);
        }
    }

    /**
     * Logs a subsystem exit tracing message (at DEBUG level).
     *
     * @param log write to this log.
     */
    public static void logSubsystemExit(Logger log) {
        if (log.isDebugEnabled()) {
            logMethodCall(LoggingSource.SUBSYSTEM, log, new Object[0], MethodTraceType.EXIT);
        }
    }

    @SuppressWarnings("PMD") // suppressing warning for raw Throwable
    private static void logMethodCall(LoggingSource source, Logger log, Object[] arguments, MethodTraceType type) {
        StringBuffer stringBuffer = new StringBuffer();
        addProlog(stringBuffer, source, type);
        Throwable throwable = new Throwable();
        StackTraceElement stackTraceElement = throwable.getStackTrace()[2];
        stringBuffer.append(stackTraceElement.getMethodName());
        for (Object element : arguments) {
            stringBuffer.append("\n\targument[");
            if (element == null) {
                stringBuffer.append("unknown type");
            } else {
                stringBuffer.append(element.getClass().getSimpleName());
            }
            stringBuffer.append("] = ");
            stringBuffer.append(element);
        }
        if (LoggingSource.SUBSYSTEM.equals(source)) {
            log.debug(stringBuffer.toString());
        } else {
            log.trace(stringBuffer.toString());
        }
    }

    private static void addProlog(StringBuffer stringBuffer, LoggingSource source, MethodTraceType type) {
        if (LoggingSource.SUBSYSTEM.equals(source)) {
            stringBuffer.append("[SUBSYSTEM ");
        } else {
            stringBuffer.append("[METHOD ");
        }
        switch (type) {
        case ENTRY:
            stringBuffer.append("ENTRY] ");
            break;
        case EXIT:
            stringBuffer.append("EXIT] ");
            break;
        case EXCEPTION:
            stringBuffer.append("EXCEPTION] ");
            break;
        default:
            throw new IllegalArgumentException("Unsupported MethodTraceType " + type);
        }
    }

}
