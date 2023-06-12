package com.thoth.server.service.render.pipes;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

public class PaddingPipe implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        var size = Integer.parseInt(args[0]);
        var character = args[1].charAt(0);
        return String.format("%" + size + "s", var).replace(' ', character);
    }

    @Override
    public String getName() {
        return "padding";
    }
}
