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
        throw new RuntimeException(key + " no found");
    }
}
