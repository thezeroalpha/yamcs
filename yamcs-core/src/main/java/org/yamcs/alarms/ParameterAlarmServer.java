package org.yamcs.alarms;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.ProcessorConfig;
import org.yamcs.StandardTupleDefinitions;
import org.yamcs.archive.AlarmRecorder;
import org.yamcs.mdb.Mdb;
import org.yamcs.parameter.ParameterValue;
import org.yamcs.xtce.Parameter;
import org.yamcs.yarch.Tuple;
import static org.yamcs.alarms.ParameterAlarmStreamer.*;

public class ParameterAlarmServer extends AlarmServer<Parameter, ParameterValue> {
    static private final Logger log = LoggerFactory.getLogger(ParameterAlarmServer.class);

    public ParameterAlarmServer(String yamcsInstance, ProcessorConfig procConfig, ScheduledThreadPoolExecutor timer) {
        super(yamcsInstance, procConfig, timer);
    }

    protected void addActiveAlarmFromTuple(Mdb mdb, Tuple tuple, Map<Parameter, ActiveAlarm<ParameterValue>> alarms) {
        String pname = tuple.getColumn(StandardTupleDefinitions.PARAMETER_COLUMN);
        var parameter = mdb.getParameter(pname);

        if (parameter == null) {
            log.info("Not adding alarm for {} because parameter was not found in the MDB", pname);
            return;
        }
        alarms.put(parameter, tupleToActiveAlarm(parameter, tuple));
    }

    protected static ActiveAlarm<ParameterValue> tupleToActiveAlarm(Parameter parameter, Tuple tuple) {
        var o = tuple.getColumn(CNAME_TRIGGER);

        if (o == null || !(o instanceof ParameterValue)) {
            throw new RuntimeException("Cannot extract the triggered PV from the tuple: " + tuple);
        }
        var triggeredValue = (ParameterValue) o;
        triggeredValue.setParameter(parameter);
        int seqNum = tuple.getIntColumn(CNAME_SEQ_NUM);

        var activeAlarm = new ActiveAlarm<ParameterValue>(triggeredValue, false, false, seqNum);

        if (tuple.hasColumn(CNAME_PENDING) && tuple.getBooleanColumn(CNAME_PENDING)) {
            activeAlarm.setPending(true);
        } else {
            activeAlarm.trigger();
        }

        if (tuple.hasColumn(CNAME_VIOLATION_COUNT)) {
            activeAlarm.setViolations(tuple.getIntColumn(CNAME_VIOLATION_COUNT));
        }
        if (tuple.hasColumn(CNAME_VALUE_COUNT)) {
            activeAlarm.setValueCount(tuple.getIntColumn(CNAME_VALUE_COUNT));
        }

        if (tuple.hasColumn(CNAME_ACK_TIME)) {
            long t = tuple.getTimestampColumn(CNAME_ACK_TIME);
            activeAlarm.acknowledge(tuple.getColumn(CNAME_ACK_BY), t, tuple.getColumn(CNAME_ACK_MSG));
        }

        if (tuple.hasColumn(CNAME_SHELVED_TIME)) {
            long t = tuple.getTimestampColumn(CNAME_SHELVED_TIME);
            activeAlarm.shelve(t, tuple.getColumn(CNAME_SHELVED_BY), tuple.getColumn(CNAME_SHELVED_MSG),
                    tuple.getLongColumn(CNAME_SHELVE_DURATION));
        }
        if (tuple.hasColumn(CNAME_LAST_VALUE)) {
            ParameterValue pv = tuple.getColumn(CNAME_LAST_VALUE);
            pv.setParameter(parameter);
            activeAlarm.setCurrentValue(pv);
        }

        if (tuple.hasColumn(CNAME_SEVERITY_INCREASED)) {
            ParameterValue pv = tuple.getColumn(CNAME_SEVERITY_INCREASED);
            pv.setParameter(parameter);
            activeAlarm.setMostSevereValue(pv);
        }
        return activeAlarm;
    }

    protected String alarmTableName() {
        return AlarmRecorder.PARAMETER_ALARM_TABLE_NAME;
    }

    @Override
    protected Parameter getSubject(ParameterValue pv) {
        return pv.getParameter();
    }

    @Override
    protected String getColNameLastEvent() {
        return CNAME_LAST_EVENT;
    }
}
