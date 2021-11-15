package com.ecer.kafka.connect.oracle;

public interface OracleConnectorSQL{
    //String LOGMINER_SELECT_WITHSCHEMA="SELECT thread#, scn, start_scn, commit_scn,timestamp, operation_code, operation,status, SEG_TYPE_NAME ,info,seg_owner, table_name, username, sql_redo ,row_id, csf, TABLE_SPACE, SESSION_INFO, RS_ID, RBASQN, RBABLK, SEQUENCE#, TX_NAME, SEG_NAME, SEG_TYPE_NAME FROM  v$logmnr_contents  WHERE OPERATION_CODE in (1,2,3) and commit_scn>=? and ";
    // String LOGMINER_SELECT_WITHSCHEMA="SELECT thread#, scn, start_scn, nvl(commit_scn,scn) commit_scn ,(xidusn||'.'||xidslt||'.'||xidsqn) AS xid,timestamp, operation_code, operation,status, SEG_TYPE_NAME ,info,seg_owner, table_name, username, sql_redo ,row_id, csf, TABLE_SPACE, SESSION_INFO, RS_ID, RBASQN, RBABLK, SEQUENCE#, TX_NAME, SEG_NAME, SEG_TYPE_NAME FROM  v$logmnr_contents  WHERE OPERATION_CODE in (1,2,3,5) and nvl(commit_scn,scn)>=? and ";
    String LOGMINER_SELECT_WITHSCHEMA="SELECT thread#, scn, start_scn, nvl(commit_scn,scn) commit_scn ,(xidusn||'.'||xidslt||'.'||xidsqn) AS xid,timestamp, operation_code, operation,status, SEG_TYPE_NAME ,info,seg_owner, table_name, username, sql_redo ,row_id, csf, TABLE_SPACE, SESSION_INFO, RS_ID, RBASQN, RBABLK, SEQUENCE#, TX_NAME, SEG_NAME, SEG_TYPE_NAME FROM  v$logmnr_contents  WHERE OPERATION_CODE in (1,2,3,5)  and ";
    String LOGMINER_SELECT_WITHSCHEMA_DESUPPORT_CM="SELECT thread#, scn, start_scn, commit_scn ,(xidusn||'.'||xidslt||'.'||xidsqn) AS xid_,xid,rollback,timestamp, commit_timestamp,operation_code, operation,status, SEG_TYPE_NAME ,info,seg_owner, table_name, username, sql_redo ,row_id, csf, TABLE_SPACE, SESSION_INFO, RS_ID, RBASQN, RBABLK, SEQUENCE#, TX_NAME, SEG_NAME, SEG_TYPE_NAME FROM  v$logmnr_contents  WHERE (scn>?) and ((OPERATION in ('COMMIT','ROLLBACK','START') and USERNAME not in ('UNKNOWNX','SYS','KMINER')) or (OPERATION in ('INSERT','UPDATE','DELETE','DDL') and ";
    //String START_LOGMINER_CMD="begin \nSYS.DBMS_LOGMNR.START_LOGMNR(STARTSCN => ?,";
    String START_LOGMINER_CMD="begin \nSYS.DBMS_LOGMNR.START_LOGMNR(";
    //指定时间格式
    String LOGMINER_DATEFORMAT = " ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'";
    //指定开始时间
    String START_LOGMINER_START_TIME = " startTime => ?, \n";
    //指定结束时间
    String START_LOGMINER_END_TIME = " endTime => ?,";

    //根据时间范围查询出日志文件
    String FIND_LOGFILE_BYTIME = "with base as\n" +
            "(\n" +
            "    SELECT \n" +
            "    MIN(name) as name,\n" +
            "    first_change#,\n" +
            "    MIN(FIRST_TIME) FIRST_TIME,\n" +
            "    MIN(NEXT_TIME) NEXT_TIME,\n" +
            "    MIN(next_change#)-1 as next_change#\n" +
            "    FROM \n" +
            "    (\n" +
            "        SELECT \n" +
            "        member AS name, \n" +
            "        thread#, \n" +
            "        first_change#, \n" +
            "        next_change#, \n" +
            "        BYTES,\n" +
            "        FIRST_TIME,NEXT_TIME\n" +
            "        FROM v$log l\n" +
            "        INNER JOIN v$logfile f ON l.group# = f.group#\n" +
            "        WHERE (l.STATUS = 'CURRENT' OR l.STATUS = 'ACTIVE') \n" +
            "        UNION\n" +
            "        SELECT \n" +
            "        name,\n" +
            "        thread#,\n" +
            "        first_change#,\n" +
            "        next_change#,\n" +
            "        BLOCKS * BLOCK_SIZE as BYTES,FIRST_TIME,NEXT_TIME\n" +
            "        FROM v$archived_log\n" +
            "        WHERE name IS NOT NULL\n" +
            "        AND STANDBY_DEST = 'NO'\n" +
            "    )first_change#\n" +
            "    GROUP BY first_change# ORDER BY first_change#\n" +
            ")\n" +
            "select * from base where (FIRST_TIME<= ? and NEXT_TIME>= ? )or (FIRST_TIME<=? and NEXT_TIME>=?)";

    //添加第一个日志文件
    String ADD_NEWFILENAME = " SYS.DBMS_LOGMNR.add_logfile('#{}',SYS.DBMS_LOGMNR.new);";
    //添加日志文件
    String ADD_FILENAME= " SYS.DBMS_LOGMNR.add_logfile('#{}');";
    String LOGMINER_START_OPTIONS="OPTIONS =>  SYS.DBMS_LOGMNR.SKIP_CORRUPTION+SYS.DBMS_LOGMNR.NO_SQL_DELIMITER+SYS.DBMS_LOGMNR.NO_ROWID_IN_STMT+SYS.DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG + SYS.DBMS_LOGMNR.CONTINUOUS_MINE+SYS.DBMS_LOGMNR.COMMITTED_DATA_ONLY+SYS.DBMS_LOGMNR.STRING_LITERALS_IN_STMT";
    String LOGMINER_START_OPTIONS_DESUPPORT_CM="OPTIONS =>  SYS.DBMS_LOGMNR.SKIP_CORRUPTION+SYS.DBMS_LOGMNR.NO_SQL_DELIMITER+SYS.DBMS_LOGMNR.NO_ROWID_IN_STMT+SYS.DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG +SYS.DBMS_LOGMNR.STRING_LITERALS_IN_STMT";
    String STOP_LOGMINER_CMD="begin \nSYS.DBMS_LOGMNR.END_LOGMNR; \nend;";
    String CURRENT_DB_SCN_SQL = "select min(current_scn) CURRENT_SCN from gv$database";
    String LASTSCN_STARTPOS = "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# from v$log where ? between FIRST_CHANGE# and NEXT_CHANGE# union select FIRST_CHANGE# from v$archived_log where ? between FIRST_CHANGE# and NEXT_CHANGE# and standby_dest='NO')";
    String TABLE_WITH_COLS ="with dcc as (SELECT dcc.owner,dcc.table_name,dcc2.column_name,1 PK_COLUMN from dba_constraints dcc,dba_cons_columns dcc2 where dcc.owner=dcc2.owner and dcc.table_name=dcc2.table_name and dcc.constraint_name=dcc2.constraint_name and dcc.constraint_type='P'),duq as (select di2.TABLE_OWNER,di2.TABLE_NAME,di2.COLUMN_NAME , 1 UQ_COLUMN from dba_ind_columns di2 join dba_indexes di on di.table_owner=di2.TABLE_OWNER and di.table_name=di2.TABLE_NAME and di.uniqueness='UNIQUE' and di.owner=di2.INDEX_OWNER and di.index_name=di2.INDEX_NAME group by di2.TABLE_OWNER,di2.TABLE_NAME,di2.COLUMN_NAME) select dc.owner,dc.TABLE_NAME,dc.COLUMN_NAME,dc.NULLABLE,dc.DATA_TYPE,nvl(dc.DATA_PRECISION,dc.DATA_LENGTH) DATA_LENGTH,nvl(dc.DATA_SCALE,0) DATA_SCALE,nvl(dc.DATA_PRECISION,0) DATA_PRECISION,nvl(x.pk_column,0) pk_column,nvl(y.uq_column,0) uq_column from dba_tab_cols dc left outer join dcc x on x.owner=dc.owner and x.table_name=dc.TABLE_NAME and dc.COLUMN_NAME=x.column_name left outer join duq y on y.table_owner=dc.owner and y.table_name=dc.TABLE_NAME and y.column_name=dc.COLUMN_NAME where dC.Owner='$TABLE_OWNER$' and dc.TABLE_NAME='$TABLE_NAME$' and dc.HIDDEN_COLUMN='NO' and dc.VIRTUAL_COLUMN='NO' order by dc.TABLE_NAME,dc.COLUMN_ID";
    String DB_VERSION = "select to_number(replace(version,'.','')) version from v$instance";
    String TABLE_WITH_COLS_CDB = "WITH DICTONARY_CONSTRAINTS_COLUMNS AS (SELECT DC.CON_ID, DC.OWNER, DC.TABLE_NAME, DCC.COLUMN_NAME, 1 PK_COLUMN FROM CDB_CONSTRAINTS DC, CDB_CONS_COLUMNS DCC WHERE DC.CON_ID  = DCC.CON_ID AND DC.OWNER=DCC.OWNER AND DC.TABLE_NAME=DCC.TABLE_NAME AND DC.CONSTRAINT_NAME=DCC.CONSTRAINT_NAME AND DC.CONSTRAINT_TYPE='P'), DICTIONARY_CONSTRAINTS_INDEXES AS (SELECT DIC.CON_ID, DIC.TABLE_OWNER, DIC.TABLE_NAME, DIC.COLUMN_NAME , 1 UQ_COLUMN FROM CDB_INDEXES DI JOIN CDB_IND_COLUMNS DIC ON DI.CON_ID = DIC.CON_ID AND DI.TABLE_OWNER=DIC.TABLE_OWNER AND DI.TABLE_NAME=DIC.TABLE_NAME AND DI.UNIQUENESS='UNIQUE'AND DI.OWNER=DIC.INDEX_OWNER AND DI.INDEX_NAME=DIC.INDEX_NAME GROUP BY DIC.CON_ID, DIC.TABLE_OWNER, DIC.TABLE_NAME, DIC.COLUMN_NAME ) SELECT DTC.CON_ID, DTC.OWNER, DTC.TABLE_NAME, DTC.COLUMN_NAME, DTC.NULLABLE, DTC.DATA_TYPE, NVL(DTC.DATA_PRECISION,DTC.DATA_LENGTH) DATA_LENGTH, NVL(DTC.DATA_SCALE,0) DATA_SCALE, NVL(DTC.DATA_PRECISION,0) DATA_PRECISION, NVL(DCC.PK_COLUMN,0) PK_COLUMN, NVL(DCI.UQ_COLUMN,0) UQ_COLUMN FROM CDB_TAB_COLS DTC LEFT OUTER JOIN DICTONARY_CONSTRAINTS_COLUMNS DCC ON DTC.CON_ID = DCC.CON_ID AND DTC.OWNER = DCC.OWNER AND DTC.TABLE_NAME = DCC.TABLE_NAME AND DTC.COLUMN_NAME=DCC.COLUMN_NAME LEFT OUTER JOIN DICTIONARY_CONSTRAINTS_INDEXES DCI ON DTC.CON_ID = DCI.CON_ID AND DCI.TABLE_OWNER = DTC.OWNER AND DCI.TABLE_NAME = DTC.TABLE_NAME AND DCI.COLUMN_NAME = DTC.COLUMN_NAME WHERE DTC.OWNER ='$TABLE_OWNER$' AND DTC.TABLE_NAME ='$TABLE_NAME$' AND DTC.HIDDEN_COLUMN ='NO' AND DTC.VIRTUAL_COLUMN='NO' ORDER BY DTC.TABLE_NAME, DTC.COLUMN_ID";
    String LOGMINER_ADD_LOGFILE = "begin \nDBMS_LOGMNR.ADD_LOGFILE(':logfilename',:option); \nexception when others then if sqlcode= -1289 then null; else raise; end if; \nend;";
    String LOGMINER_LOG_FILES_OLD = "SELECT name FROM (SELECT LISTAGG(a.name, ' ') WITHIN GROUP(ORDER BY a.name ) name FROM (SELECT 'ARCH' REDOTYPE, 0 GROUP#, 'INACTIVE' STATUS, v4.THREAD#, v4.SEQUENCE# SEQUENCE#, v4.FIRST_CHANGE#, v4.FIRST_TIME, v4.NEXT_CHANGE#, v4.NEXT_TIME, NULL MEMBER, v4.NEXT_CHANGE# LAST_REDO_CHANGE#, v4.NEXT_TIME LAST_REDO_TIME, v4.NAME FROM v$archived_log v4 WHERE v4.DEST_ID = 1 AND ( ( :vcurrscn = 0 AND 1 = 2 ) OR ( :vcurrscn > 0 AND ( v4.FIRST_CHANGE# >= :vcurrscn OR ( ( :vcurrscn BETWEEN v4.FIRST_CHANGE# AND v4.NEXT_CHANGE# ) AND ( :vcurrscn != v4.next_change# ) ) ) ) ) UNION ALL SELECT 'REDO' REDOTYPE, v1.GROUP#, v1.STATUS, v1.THREAD#, v1.SEQUENCE# SEQUENCE#, v1.FIRST_CHANGE#, v1.FIRST_TIME, v1.NEXT_CHANGE#, v1.NEXT_TIME, v22.member, decode(v4.ARCHIVED, 'YES', v1.NEXT_CHANGE#, v3.LAST_REDO_CHANGE#) LAST_REDO_CHANGE#, v3.LAST_REDO_TIME, nvl(v4.NAME, v22.member) NAME FROM v$log  v1 LEFT OUTER JOIN v$archived_log   v4 ON v1.THREAD# = v4.THREAD# AND v1.SEQUENCE# = v4.SEQUENCE# AND v4.DEST_ID = 1, (SELECT v2.GROUP#, v2.MEMBER, ROW_NUMBER() OVER(PARTITION BY v2.GROUP# ORDER BY v2.GROUP#) AS rowx FROM v$logfile v2 ) v22, v$thread         v3 WHERE v1.GROUP# = v22.group# AND v22.rowx = v1.MEMBERS AND v1.THREAD# = v3.THREAD#  AND v1.STATUS = 'CURRENT' AND ( ( :vcurrscn = 0 AND v1.STATUS = 'CURRENT' ) OR ( :vcurrscn > 0 AND ( v1.FIRST_CHANGE# >= :vcurrscn ) OR ( ( :vcurrscn BETWEEN v1.FIRST_CHANGE# AND v1.NEXT_CHANGE# ) AND ( :vcurrscn != v1.next_change# ) ) ) ) ) a ORDER BY a.sequence# ) MINUS SELECT LISTAGG(filename, ' ') WITHIN GROUP(ORDER BY filename ) FROM v$logmnr_logs";
    String LOG_MINER_LOG_FILES_OLD2 = "select A.name from ( select 'ARCH' EDOTYPE ,0 GROUP#,'INACTIVE' STATUS,v4.THREAD#,v4.SEQUENCE# SEQUENCE#,v4.FIRST_CHANGE#, v4.FIRST_TIME,v4.NEXT_CHANGE#,v4.NEXT_TIME,NULL MEMBER, v4.NEXT_CHANGE# LAST_REDO_CHANGE#,v4.NEXT_TIME LAST_REDO_TIME,v4.NAME from v$archived_log v4 where v4.DEST_ID=1 and ((:vcurrscn=0 and 1=2) or (:vcurrscn>0 and (v4.FIRST_CHANGE#>=:vcurrscn or ((:vcurrscn between v4.FIRST_CHANGE# and v4.NEXT_CHANGE#)and(:vcurrscn!=v4.NEXT_CHANGE#)))) ) union all select 'REDO' REDOTYPE,v1.GROUP#,v1.STATUS,v1.THREAD#,v1.SEQUENCE# SEQUENCE#,v1.FIRST_CHANGE#, v1.FIRST_TIME,v1.NEXT_CHANGE#,v1.NEXT_TIME ,v22.member, decode(v4.ARCHIVED,'YES',v1.NEXT_CHANGE#,v3.LAST_REDO_CHANGE#) LAST_REDO_CHANGE#,v3.LAST_REDO_TIME,nvl(v4.NAME,v22.member) NAME from v$log v1 left outer join v$archived_log v4 on v1.THREAD#=v4.THREAD# and v1.SEQUENCE#=v4.SEQUENCE# and v4.DEST_ID=1 ,(select v2.GROUP#, v2.MEMBER ,ROW_NUMBER() over (partition by v2.GROUP# order by v2.GROUP#) as rowx from v$logfile v2) v22, v$thread v3 where v1.GROUP#=v22.group# and v22.rowx=v1.MEMBERS and v1.THREAD#=v3.THREAD# and v1.STATUS='CURRENT' and ((:vcurrscn=0 and v1.STATUS='CURRENT') or (:vcurrscn>0 and (v1.FIRST_CHANGE#>=:vcurrscn) or ((:vcurrscn between v1.FIRST_CHANGE# and v1.NEXT_CHANGE#)and(:vcurrscn!=v1.NEXT_CHANGE#))))) A minus select filename  from v$logmnr_logs";
    String LOGMINER_LOG_FILES_LOG$ = "select A.name from ( select 'ARCH' EDOTYPE ,0 GROUP#,'INACTIVE' STATUS,v4.THREAD#,v4.SEQUENCE# SEQUENCE#,v4.FIRST_CHANGE#, v4.FIRST_TIME,v4.NEXT_CHANGE#,v4.NEXT_TIME,NULL MEMBER, v4.NEXT_CHANGE# LAST_REDO_CHANGE#,v4.NEXT_TIME LAST_REDO_TIME,v4.NAME from v$archived_log v4 where v4.DEST_ID=1 and ((:vcurrscn=0 and 1=2) or (:vcurrscn>0 and (v4.FIRST_CHANGE#>=:vcurrscn or ((:vcurrscn between v4.FIRST_CHANGE# and v4.NEXT_CHANGE#)and(:vcurrscn!=v4.NEXT_CHANGE#)))) ) union all select 'REDO' REDOTYPE,v1.GROUP#,v1.STATUS,v1.THREAD#,v1.SEQUENCE# SEQUENCE#,v1.FIRST_CHANGE#, v1.FIRST_TIME,v1.NEXT_CHANGE#,v1.NEXT_TIME ,v22.member, decode(v4.ARCHIVED,'YES',v1.NEXT_CHANGE#,v3.LAST_REDO_CHANGE#) LAST_REDO_CHANGE#,v3.LAST_REDO_TIME,nvl(v4.NAME,v22.member) NAME from v$log v1 left outer join v$archived_log v4 on v1.THREAD#=v4.THREAD# and v1.SEQUENCE#=v4.SEQUENCE# and v4.DEST_ID=1 ,(select v2.GROUP#, v2.MEMBER ,ROW_NUMBER() over (partition by v2.GROUP# order by v2.GROUP#) as rowx from v$logfile v2) v22, v$thread v3 where v1.GROUP#=v22.group# and v22.rowx=v1.MEMBERS and v1.THREAD#=v3.THREAD# and v1.STATUS='CURRENT' and ((:vcurrscn=0 and v1.STATUS='CURRENT') or (:vcurrscn>0 and (v1.FIRST_CHANGE#>=:vcurrscn) or ((:vcurrscn between v1.FIRST_CHANGE# and v1.NEXT_CHANGE#)and(:vcurrscn!=v1.NEXT_CHANGE#))))) A ";
    String LOGMINER_LOG_FILES_LOGMNR$ = "select filename name from v$logmnr_logs";
}