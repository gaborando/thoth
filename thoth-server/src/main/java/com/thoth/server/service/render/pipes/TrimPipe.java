package com.thoth.server.service.render.pipes;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

public class TrimPipe implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        if(var == null) return "";
        return var.toString().trim();
    }

    @Override
    public String getName() {
        return "trim";
    }
}
