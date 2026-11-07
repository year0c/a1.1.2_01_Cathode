package uk.betacraft.legacyfix.patch.impl.lwjgl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Iterator;

public final class DeAwtPatch {
    private static final String POINTER_BUFFER = "org/lwjgl/PointerBuffer";
    private static final String CUSTOM_BUFFER = "org/lwjgl/system/CustomBuffer";
    private static final String POINTER_BUFFER_DESC = "Lorg/lwjgl/PointerBuffer;";
    private static final String CUSTOM_BUFFER_DESC = "Lorg/lwjgl/system/CustomBuffer;";

    private DeAwtPatch() {
    }

    public static void premain(String args, Instrumentation inst) {
        System.err.println("[DeAwtPatch] agent loaded");

        inst.addTransformer(new PointerBufferTransformer());
        inst.addTransformer(new BufferUtilsTransformer());
        inst.addTransformer(new AppletFrameTransformer());
        inst.addTransformer(new AwtFrameTransformer());
    }

    private static final class PointerBufferTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {

            if (!POINTER_BUFFER.equals(className)) {
                return null;
            }

            try {
                ClassNode cn = new ClassNode();
                new ClassReader(classfileBuffer).accept(cn, 0);

                String originalSuper = cn.superName;
                cn.superName = CUSTOM_BUFFER;

                // Preserve the original interfaces, including Comparable.
                if (!cn.interfaces.contains("java/lang/Comparable")) {
                    cn.interfaces.add("java/lang/Comparable");
                }

                rewriteConstructors(cn);
                patchPointerBufferState(cn);
                rewriteGetPutMethods(cn);
                rewriteCreateMethod(cn);
                addCustomBufferBridges(cn);
                replaceSizeof(cn);

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                byte[] transformed = cw.toByteArray();

                System.err.println(
                        "[DeAwtPatch] PointerBuffer transformed; loader=" + loader
                                + ", originalSuper=" + originalSuper
                                + ", newSuper=" + new ClassReader(transformed).getSuperName());

                return transformed;
            } catch (Throwable t) {
                System.err.println("[DeAwtPatch] Failed to transform PointerBuffer");
                t.printStackTrace();
                throw new RuntimeException(t);
            }
        }
    }

    private static void rewriteConstructors(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (!"<init>".equals(mn.name)) {
                continue;
            }

            clearMethodBody(mn);

            if ("(I)V".equals(mn.desc)) {
                // Allocate size * 8 bytes, then delegate to the address-based constructor.
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.IMUL));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "nmemAlloc",
                        "(J)J",
                        false));
                mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        POINTER_BUFFER,
                        "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V",
                        false));
            } else if ("(JLjava/nio/ByteBuffer;IIII)V".equals(mn.desc)) {
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        CUSTOM_BUFFER,
                        "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V",
                        false));
            } else {
                // Fallback for legacy constructor signatures not used by this adapter.
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new InsnNode(Opcodes.LCONST_0));
                mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        CUSTOM_BUFFER,
                        "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V",
                        false));
            }

            mn.instructions.add(new InsnNode(Opcodes.RETURN));
        }

        if (!hasMethod(cn, "<init>", "(JLjava/nio/ByteBuffer;IIII)V")) {
            MethodNode ctor = new MethodNode(
                    Opcodes.ACC_PROTECTED,
                    "<init>",
                    "(JLjava/nio/ByteBuffer;IIII)V",
                    null,
                    null);

            ctor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            ctor.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
            ctor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
            ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
            ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
            ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
            ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
            ctor.instructions.add(new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    CUSTOM_BUFFER,
                    "<init>",
                    "(JLjava/nio/ByteBuffer;IIII)V",
                    false));
            ctor.instructions.add(new InsnNode(Opcodes.RETURN));
            cn.methods.add(ctor);
        }
    }

    private static void patchPointerBufferState(ClassNode cn) {
        patchGetter(cn, "position", "()I", Opcodes.IRETURN);
        patchGetter(cn, "limit", "()I", Opcodes.IRETURN);
        patchGetter(cn, "capacity", "()I", Opcodes.IRETURN);
        patchGetter(cn, "remaining", "()I", Opcodes.IRETURN);
        patchGetter(cn, "hasRemaining", "()Z", Opcodes.IRETURN);

        patchFluentSetter(cn, "position");
        patchFluentSetter(cn, "limit");

        patchFluentMethod(cn, "mark", false);
        patchFluentMethod(cn, "reset", false);
        patchFluentMethod(cn, "clear", false);
        patchFluentMethod(cn, "flip", false);
        patchFluentMethod(cn, "rewind", false);
        patchFluentMethod(cn, "compact", false);

        patchFluentMethod(cn, "duplicate", true);
        patchFluentMethod(cn, "slice", true);
    }

    private static void patchGetter(
            ClassNode cn,
            String name,
            String desc,
            int returnOpcode) {

        MethodNode method = findMethod(cn, name, desc);
        if (method == null) {
            return;
        }

        clearMethodBody(method);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                CUSTOM_BUFFER,
                name,
                desc,
                false));
        method.instructions.add(new InsnNode(returnOpcode));
    }

    private static void patchFluentSetter(ClassNode cn, String name) {
        String desc = "(I)" + POINTER_BUFFER_DESC;
        MethodNode method = findMethod(cn, name, desc);
        if (method == null) {
            return;
        }

        clearMethodBody(method);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                CUSTOM_BUFFER,
                name,
                "(I)" + CUSTOM_BUFFER_DESC,
                false));
        method.instructions.add(new InsnNode(Opcodes.POP));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new InsnNode(Opcodes.ARETURN));
    }

    private static void patchFluentMethod(
            ClassNode cn,
            String name,
            boolean castResult) {

        String desc = "()" + POINTER_BUFFER_DESC;
        MethodNode method = findMethod(cn, name, desc);
        if (method == null) {
            return;
        }

        clearMethodBody(method);
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                CUSTOM_BUFFER,
                name,
                "()" + CUSTOM_BUFFER_DESC,
                false));

        if (castResult) {
            method.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, POINTER_BUFFER));
        } else {
            method.instructions.add(new InsnNode(Opcodes.POP));
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        }

        method.instructions.add(new InsnNode(Opcodes.ARETURN));
    }

    private static void rewriteGetPutMethods(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if ("get".equals(mn.name) && "()J".equals(mn.desc)) {
                clearMethodBody(mn);

                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "address", "J"));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        CUSTOM_BUFFER,
                        "nextGetIndex",
                        "()I",
                        false));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "toUnsignedLong",
                        "(I)J",
                        false));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new InsnNode(Opcodes.LMUL));
                mn.instructions.add(new InsnNode(Opcodes.LADD));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "memGetAddress",
                        "(J)J",
                        false));
                mn.instructions.add(new InsnNode(Opcodes.LRETURN));
            } else if ("get".equals(mn.name) && "(I)J".equals(mn.desc)) {
                clearMethodBody(mn);

                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "address", "J"));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "limit", "I"));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/Checks",
                        "check",
                        "(II)J",
                        false));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new InsnNode(Opcodes.LMUL));
                mn.instructions.add(new InsnNode(Opcodes.LADD));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "memGetAddress",
                        "(J)J",
                        false));
                mn.instructions.add(new InsnNode(Opcodes.LRETURN));
            } else if ("put".equals(mn.name)
                    && "(J)Lorg/lwjgl/PointerBuffer;".equals(mn.desc)) {
                clearMethodBody(mn);

                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "address", "J"));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        CUSTOM_BUFFER,
                        "nextPutIndex",
                        "()I",
                        false));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "toUnsignedLong",
                        "(I)J",
                        false));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new InsnNode(Opcodes.LMUL));
                mn.instructions.add(new InsnNode(Opcodes.LADD));
                mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "memPutAddress",
                        "(JJ)V",
                        false));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new InsnNode(Opcodes.ARETURN));
            } else if ("put".equals(mn.name)
                    && "(IJ)Lorg/lwjgl/PointerBuffer;".equals(mn.desc)) {
                clearMethodBody(mn);

                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "address", "J"));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new FieldInsnNode(
                        Opcodes.GETFIELD, POINTER_BUFFER, "limit", "I"));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/Checks",
                        "check",
                        "(II)J",
                        false));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new InsnNode(Opcodes.LMUL));
                mn.instructions.add(new InsnNode(Opcodes.LADD));
                mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 2));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "memPutAddress",
                        "(JJ)V",
                        false));
                mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                mn.instructions.add(new InsnNode(Opcodes.ARETURN));
            }
        }
    }

    private static void rewriteCreateMethod(ClassNode cn) {
        String desc = "(JLjava/nio/ByteBuffer;IIII)Lorg/lwjgl/PointerBuffer;";
        MethodNode method = findMethod(cn, "create", desc);
        if (method == null) {
            return;
        }

        clearMethodBody(method);
        method.access |= Opcodes.ACC_PROTECTED | Opcodes.ACC_BRIDGE;
        method.instructions.add(new TypeInsnNode(Opcodes.NEW, POINTER_BUFFER));
        method.instructions.add(new InsnNode(Opcodes.DUP));
        method.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
        method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
        method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
        method.instructions.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                POINTER_BUFFER,
                "<init>",
                "(JLjava/nio/ByteBuffer;IIII)V",
                false));
        method.instructions.add(new InsnNode(Opcodes.ARETURN));
    }

    private static void addCustomBufferBridges(ClassNode cn) {
        String selfBridge = "()" + CUSTOM_BUFFER_DESC;
        if (!hasMethod(cn, "self", selfBridge)) {
            MethodNode method = new MethodNode(
                    Opcodes.ACC_PROTECTED | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
                    "self",
                    selfBridge,
                    null,
                    null);
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new InsnNode(Opcodes.ARETURN));
            cn.methods.add(method);
        }

        String createBridge = "(JLjava/nio/ByteBuffer;IIII)" + CUSTOM_BUFFER_DESC;
        if (!hasMethod(cn, "create", createBridge)) {
            MethodNode method = new MethodNode(
                    Opcodes.ACC_PROTECTED | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
                    "create",
                    createBridge,
                    null,
                    null);
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
            method.instructions.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    POINTER_BUFFER,
                    "create",
                    "(JLjava/nio/ByteBuffer;IIII)" + POINTER_BUFFER_DESC,
                    false));
            method.instructions.add(new InsnNode(Opcodes.ARETURN));
            cn.methods.add(method);
        }
    }

    private static void replaceSizeof(ClassNode cn) {
        for (Iterator<MethodNode> it = cn.methods.iterator(); it.hasNext();) {
            MethodNode method = it.next();
            if ("sizeof".equals(method.name) && "()I".equals(method.desc)) {
                it.remove();
            }
        }

        MethodNode method = new MethodNode(
                Opcodes.ACC_PUBLIC,
                "sizeof",
                "()I",
                null,
                null);
        method.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
        method.instructions.add(new InsnNode(Opcodes.IRETURN));
        cn.methods.add(method);
    }

    private static final class BufferUtilsTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {

            if (!"org/lwjgl/BufferUtils".equals(className)) {
                return null;
            }

            ClassNode cn = new ClassNode();
            new ClassReader(classfileBuffer).accept(cn, 0);

            boolean changed = false;

            for (MethodNode mn : cn.methods) {
                if (!"createPointerBuffer".equals(mn.name)
                        || !"(I)Lorg/lwjgl/PointerBuffer;".equals(mn.desc)) {
                    continue;
                }

                clearMethodBody(mn);
                changed = true;

                // Preserve static/instance semantics and use the corresponding argument slot.
                boolean isStatic = (mn.access & Opcodes.ACC_STATIC) != 0;
                int sizeSlot = isStatic ? 0 : 1;
                int addressSlot = isStatic ? 1 : 2;

                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, sizeSlot));
                mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                mn.instructions.add(new InsnNode(Opcodes.IMUL));
                mn.instructions.add(new InsnNode(Opcodes.I2L));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "org/lwjgl/system/MemoryUtil",
                        "nmemAlloc",
                        "(J)J",
                        false));
                mn.instructions.add(new VarInsnNode(Opcodes.LSTORE, addressSlot));

                mn.instructions.add(new TypeInsnNode(Opcodes.NEW, POINTER_BUFFER));
                mn.instructions.add(new InsnNode(Opcodes.DUP));
                mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, addressSlot));
                mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, sizeSlot));
                mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, sizeSlot));
                mn.instructions.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        POINTER_BUFFER,
                        "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V",
                        false));
                mn.instructions.add(new InsnNode(Opcodes.ARETURN));
            }

            if (!changed) {
                return null;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
    }

    private static final class AppletFrameTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {

            if (!"net/fabricmc/loader/impl/game/minecraft/applet/AppletFrame".equals(className)) {
                return null;
            }

            ClassNode cn = new ClassNode();
            new ClassReader(classfileBuffer).accept(cn, 0);

            for (MethodNode mn : cn.methods) {
                if (!"<init>".equals(mn.name)) {
                    continue;
                }

                AbstractInsnNode superCall = null;
                for (AbstractInsnNode insn = mn.instructions.getFirst();
                     insn != null;
                     insn = insn.getNext()) {
                    if (insn instanceof MethodInsnNode
                            && insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        superCall = insn;
                        break;
                    }
                }

                if (superCall == null) {
                    continue;
                }

                AbstractInsnNode after = superCall.getNext();
                while (after != null) {
                    AbstractInsnNode next = after.getNext();
                    mn.instructions.remove(after);
                    after = next;
                }

                mn.tryCatchBlocks.clear();
                mn.localVariables = null;
                mn.instructions.add(new InsnNode(Opcodes.RETURN));
                break;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
    }

    private static final class AwtFrameTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer) {

            if (!"java/awt/Frame".equals(className)) {
                return null;
            }

            ClassNode cn = new ClassNode();
            new ClassReader(classfileBuffer).accept(cn, 0);

            for (MethodNode mn : cn.methods) {
                if (!"setVisible".equals(mn.name) || !"(Z)V".equals(mn.desc)) {
                    continue;
                }

                clearMethodBody(mn);
                mn.instructions.add(new InsnNode(Opcodes.RETURN));
                break;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }
    }

    private static MethodNode findMethod(ClassNode cn, String name, String desc) {
        for (MethodNode mn : cn.methods) {
            if (name.equals(mn.name) && desc.equals(mn.desc)) {
                return mn;
            }
        }
        return null;
    }

    private static boolean hasMethod(ClassNode cn, String name, String desc) {
        return findMethod(cn, name, desc) != null;
    }

    private static void clearMethodBody(MethodNode mn) {
        mn.instructions.clear();
        mn.tryCatchBlocks.clear();
        mn.localVariables = null;

        // Do not clear ACC_STATIC: rewritten methods must keep their original invocation shape.
        mn.access &= ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE);
    }
}
