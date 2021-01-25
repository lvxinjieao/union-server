package com.u8.server.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.u8.server.config.GlobalConfig;
import com.u8.server.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务日志
 * Created by ant on 2018/6/4.
 */
@Component("blog")
@Scope("singleton")
public class BLog {

    public static final int T_CHANNEL = 1;
    public static final int T_MASTER = 2;
    public static final int T_DEVICE = 3;
    public static final int T_GAME = 4;
    public static final int T_ORDER = 5;
    public static final int T_SUBCHANNEL = 6;
    public static final int T_USER = 7;
    public static final int T_USERLOG = 8;


    private static BLog instance;

    public static BLog getInstance() {
        return instance;
    }


    @Autowired
    private GlobalConfig globalConfig;

    private Map<Integer, Logger> loggers;

    @PostConstruct
    public void init() {

        instance = this;

        if (globalConfig.isPringBLog()) {

            loggers = new HashMap<Integer, Logger>();
            loggers.put(T_CHANNEL, Logger.getLogger("channel"));
            loggers.put(T_MASTER, Logger.getLogger("master"));
            loggers.put(T_DEVICE, Logger.getLogger("device"));
            loggers.put(T_GAME, Logger.getLogger("game"));
            loggers.put(T_ORDER, Logger.getLogger("order"));
            loggers.put(T_SUBCHANNEL, Logger.getLogger("subchannel"));
            loggers.put(T_USER, Logger.getLogger("user"));
            loggers.put(T_USERLOG, Logger.getLogger("userlog"));

        }


    }

    public void printJson(int type, String opType, Object bean) {
        if (bean == null) return;

        JSONObject json = (JSONObject) JSON.toJSON(bean);
        if (json == null) return;
        json.put("log_time", TimeUtils.format_default(new Date()));
        json.put("log_type", opType);

        print(type, json.toString());
    }

    public void print(int type, String msg) {

        try {
            if (!globalConfig.isPringBLog()) {
                return;
            }


            if (!loggers.containsKey(type)) {
                return;
            }

            Log.d("blog a new log:" + type + ";msg:" + msg);

            loggers.get(type).info(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public final class OPTYPE {

        public final class CHANNEL {
            public static final String OP_ADD = "channel_add";
            public static final String OP_UPDATE = "channel_update";
            public static final String OP_DELETE = "channel_delete";
        }


        public final class MASTER {
            public static final String OP_ADD = "master_add";
            public static final String OP_UPDATE = "master_update";
            public static final String OP_DELETE = "master_delete";
        }

        public final class GAME {
            public static final String OP_ADD = "game_add";
            public static final String OP_UPDATE = "game_update";
            public static final String OP_DELETE = "game_delete";
        }

        public final class DEVICE {
            public static final String OP_ADD = "device_add";
        }

        public final class SUBCHANNEL {
            public static final String OP_ADD = "subchannel_add";
            public static final String OP_UPDATE = "subchannel_update";
            public static final String OP_DELETE = "subchannel_delete";
        }

        public final class USER {
            public static final String OP_REG = "user_register";
            public static final String OP_LOGIN = "user_login";
        }

        public final class ORDER {
            public static final String OP_CREATE = "order_create";
            public static final String OP_SUCCESS = "order_success";
            public static final String OP_COMPLETE = "order_complete";
        }

        public final class USERLOG {

            public static final String OP_CREATE_ROLE = "userlog_create_role";
            public static final String OP_ENTER_GAME = "userlog_enter_game";
            public static final String OP_LEVEL_UP = "userlog_level_up";
            public static final String OP_EXIT_GAME = "userlog_exit_game";

        }
    }
}
