package cn.razesoldier.csgo.query.ui.action;

import cn.razesoldier.csgo.query.Main;

public class ActionFactory {
    public static IAction make(String key, Main main) {
        if (key.equals("query")) {
            return new QueryAction(main);
        }
        if (key.equals("parse")) {
            return new ParseAction(main);
        }
        if (key.equals("setting-proxy")) {
            return new SettingProxyAction(main);
        }
        if (key.equals("show-about")) {
            return new ShowAboutAction();
        }
        throw new RuntimeException(key + " no found");
    }
}
