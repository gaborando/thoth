package com.thoth.server.service.render.pipes;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

public class TrimPipe implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        try {
            return var.toString().trim();
        }catch (Exception e){
            return var;
        }
    }

    @Override
    public String getName() {
        return "trim";
    }
}
