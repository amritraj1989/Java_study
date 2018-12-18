package com.mikealbert.data.aspect;

import java.lang.reflect.Field;
import java.util.Date;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.ProceedingJoinPoint;

import com.mikealbert.util.MALUtilities;

import javax.persistence.Temporal;

@Aspect
public class TemporalAspect {

    @Around("set(@Temporal * *)")
    public void advice(ProceedingJoinPoint joinPoint) throws Throwable {

        FieldSignature fieldSig = (FieldSignature) joinPoint.getSignature();
        Field field =  fieldSig.getField();

        Temporal temp = field.getAnnotation(Temporal.class);
        Object[] args = joinPoint.getArgs();
        if("Date".equalsIgnoreCase(temp.value().toString())){
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof java.util.Date && args[i] != null) {
                	args[i] = MALUtilities.clearTimeFromDate((Date) args[i]);
                }
            }
        }
        
        joinPoint.proceed(args);        
    }
}
