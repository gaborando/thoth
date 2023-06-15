package com.thoth.server.service.render.pipes;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DatePipe implements Filter {
    @Override
    public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
        try {
            var value = var.toString();
            var pattern = args[0];
            if (value.startsWith("/Date(")) {
                value = value.replace("/Date(", "").replace(")/", "");
                return ZonedDateTime.from(Instant.ofEpochMilli(Long.parseLong(value)).atZone(ZoneOffset.UTC))
                        .format(DateTimeFormatter.ofPattern(pattern));
            }
            return ZonedDateTime.parse(value).format(DateTimeFormatter.ofPattern(pattern));
        }catch (Exception e){
            return var;
        }
    }

    @Override
    public String getName() {
        return "date";
    }
}
