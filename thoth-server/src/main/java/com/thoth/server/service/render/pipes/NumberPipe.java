package com.thoth.server.service.render.pipes;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

public class NumberPipe implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        try {
            var value = var.toString();
            var format = args[0];
            return String.format("%" + format + "f", Double.parseDouble(value));
        }catch (Exception e){
            return var;
        }
    }

    @Override
    public String getName() {
        return "number";
    }
}
