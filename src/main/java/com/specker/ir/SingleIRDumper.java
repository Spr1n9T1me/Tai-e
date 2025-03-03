package com.specker.ir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pascal.taie.ir.IR;
import pascal.taie.ir.IRPrinter;
import pascal.taie.ir.exp.Var;
import pascal.taie.language.annotation.Annotation;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JField;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.classes.Modifier;
import pascal.taie.language.type.Type;
import pascal.taie.util.collection.Maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: Tai-e
 * @description: dumper for single IR
 * @author: springtime
 * @create: 2025-03-03 10:04
 **/
public  class SingleIRDumper {
    private static final String IR_DIR = "tir";
    private static final String SUFFIX = ".tir";

    private static final String INDENT = "    ";
    private static final Logger logger = LogManager.getLogger(SingleIRDumper.class);
    public final File dumpDir;

    public final JClass jclass;

    public PrintStream out;

    public SingleIRDumper(File dumpDir, JClass jclass) {
        this.dumpDir = dumpDir;
        this.jclass = jclass;
    }

    public void dump() {
        String fileName = jclass.getName() + SUFFIX;
        try (PrintStream out = new PrintStream(new FileOutputStream(
                new File(dumpDir, fileName)))) {
            this.out = out;
            dumpClassDeclaration();
            out.println(" {");
            out.println();
            if (!jclass.getDeclaredFields().isEmpty()) {
                jclass.getDeclaredFields().forEach(this::dumpField);
            }
            jclass.getDeclaredMethods().forEach(this::dumpMethod);
            out.println("}");
        } catch (FileNotFoundException e) {
            logger.warn("Failed to dump class {}", jclass, e);
        }
    }

    public void dumpClassDeclaration() {
        // dump annotations
        jclass.getAnnotations().forEach(out::println);
        // dump class modifiers
        jclass.getModifiers()
                .stream()
                // if jclass is an interface, then don't dump modifiers
                // "interface" and "abstract"
                .filter(m -> !jclass.isInterface() ||
                        (m != Modifier.INTERFACE && m != Modifier.ABSTRACT))
                .forEach(m -> out.print(m + " "));
        // dump class type
        if (jclass.isInterface()) {
            out.print("interface");
        } else {
            out.print("class");
        }
        out.print(' ');
        // dump class name
        out.print(jclass.getName());
        // dump super class
        JClass superClass = jclass.getSuperClass();
        if (superClass != null) {
            out.print(" extends ");
            out.print(superClass.getName());
        }
        // dump interfaces
        if (!jclass.getInterfaces().isEmpty()) {
            out.print(" implements ");
            out.print(jclass.getInterfaces()
                    .stream()
                    .map(JClass::getName)
                    .collect(Collectors.joining(", ")));
        }
    }

    public void dumpField(JField field) {
        for (Annotation annotation : field.getAnnotations()) {
            out.print(INDENT);
            out.println(annotation);
        }
        out.print(INDENT);
        dumpModifiers(field.getModifiers());
        out.printf("%s %s;%n%n", field.getType().getName(), field.getName());
    }

    public void dumpModifiers(Set<Modifier> mods) {
        mods.forEach(m -> out.print(m + " "));
    }

    public void dumpMethod(JMethod method) {
        for (Annotation annotation : method.getAnnotations()) {
            out.print(INDENT);
            out.println(annotation);
        }
        out.print(INDENT);
        dumpMethodDeclaration(method);
        if (hasIR(method)) {
            out.println(" {");
            IR ir = method.getIR();
            // dump variables
            dumpVariables(ir);
            // dump statements
            ir.forEach(s -> out.printf("%s%s%s%n",
                    INDENT, INDENT, IRPrinter.toString(s)));
            // dump exception entries
            if (!ir.getExceptionEntries().isEmpty()) {
                out.println();
                ir.getExceptionEntries().forEach(e ->
                        out.printf("%s%s%s%n", INDENT, INDENT, e));
            }
            out.printf("%s}%n", INDENT);
        } else {
            out.println(";");
        }
        out.println();
    }

    public void dumpMethodDeclaration(JMethod method) {
        dumpModifiers(method.getModifiers());
        out.printf("%s %s", method.getReturnType(), method.getName());
        // dump parameters
        StringJoiner paramsJoiner = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < method.getParamCount(); ++i) {
            StringJoiner joiner = new StringJoiner(" ");
            method.getParamAnnotations(i)
                    .forEach(anno -> joiner.add(anno.toString()));
            joiner.add(method.getParamType(i).getName());
            // if the method has explicit parameter names
            // or the method has IR, then dump parameter names
            String paramName = method.getParamName(i);
            if (paramName != null) {
                joiner.add(paramName);
            } else if (hasIR(method)) {
                joiner.add(method.getIR().getParam(i).getName());
            }
            paramsJoiner.add(joiner.toString());
        }
        out.print(paramsJoiner);
    }

    public static boolean hasIR(JMethod method) {
        return !method.isAbstract();
    }

    public void dumpVariables(IR ir) {
        // group variables by their types;
        Map<Type, List<Var>> vars = Maps.newLinkedHashMap();
        ir.getVars().stream()
                .filter(v -> v != ir.getThis())
                .filter(v -> !ir.getParams().contains(v))
                .forEach(v -> vars.computeIfAbsent(v.getType(),
                                __ -> new ArrayList<>())
                        .add(v));
        vars.forEach((t, vs) -> {
            out.printf("%s%s%s ", INDENT, INDENT, t);
            out.print(vs.stream()
                    .map(Var::getName)
                    .collect(Collectors.joining(", ")));
            out.println(";");
        });
    }
}
