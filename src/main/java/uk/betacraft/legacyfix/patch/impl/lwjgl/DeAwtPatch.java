package uk.betacraft.legacyfix.patch.impl.lwjgl;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class DeAwtPatch {

    public static void premain(String args, Instrumentation inst) {

        // 1. Patch LWJGL2 PointerBuffer to behave like LWJGL3's
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain pd,
                                    byte[] classfileBuffer) {

                if (!className.equals("org/lwjgl/PointerBuffer")) return null;

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                if ("org/lwjgl/system/CustomBuffer".equals(cn.superName)) return null;

                cn.superName = "org/lwjgl/system/CustomBuffer";
                cn.interfaces.clear();

                // Fix constructors
                for (MethodNode mn : cn.methods) {
                    if (!mn.name.equals("<init>")) continue;

                    mn.instructions.clear();
                    mn.tryCatchBlocks.clear();
                    mn.exceptions.clear();

                    if (mn.desc.equals("(I)V")) {
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.IMUL));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "org/lwjgl/system/MemoryUtil", "nmemAlloc", "(J)J", false));
                        mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                            "org/lwjgl/PointerBuffer", "<init>",
                            "(JLjava/nio/ByteBuffer;IIII)V", false));
                    } else if (mn.desc.equals("(JLjava/nio/ByteBuffer;IIII)V")) {
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                            "org/lwjgl/system/CustomBuffer", "<init>",
                            "(JLjava/nio/ByteBuffer;IIII)V", false));
                    } else {
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new InsnNode(Opcodes.LCONST_0));
                        mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                            "org/lwjgl/system/CustomBuffer", "<init>",
                            "(JLjava/nio/ByteBuffer;IIII)V", false));
                    }
                    mn.instructions.add(new InsnNode(Opcodes.RETURN));
                }

                // Ensure 6-arg constructor exists
                boolean has6Arg = false;
                for (MethodNode mn : cn.methods) {
                    if (mn.name.equals("<init>") && mn.desc.equals("(JLjava/nio/ByteBuffer;IIII)V")) {
                        has6Arg = true;
                        break;
                    }
                }
                if (!has6Arg) {
                    MethodNode ctor = new MethodNode(Opcodes.ACC_PROTECTED, "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V", null, null);
                    ctor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    ctor.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                    ctor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
                    ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
                    ctor.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                    ctor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                        "org/lwjgl/system/CustomBuffer", "<init>",
                        "(JLjava/nio/ByteBuffer;IIII)V", false));
                    ctor.instructions.add(new InsnNode(Opcodes.RETURN));
                    cn.methods.add(ctor);
                }

                // Rewrite instance get/put to use MemoryUtil (LWJGL3 style)
                for (MethodNode mn : cn.methods) {
                    if (mn.name.equals("get") && mn.desc.equals("()J")) {
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "address", "J"));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/lwjgl/system/CustomBuffer", "nextGetIndex", "()I", false));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "toUnsignedLong", "(I)J", false));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new InsnNode(Opcodes.LMUL));
                        mn.instructions.add(new InsnNode(Opcodes.LADD));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memGetAddress", "(J)J", false));
                        mn.instructions.add(new InsnNode(Opcodes.LRETURN));
                    }
                    else if (mn.name.equals("get") && mn.desc.equals("(I)J")) {
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "address", "J"));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "limit", "I"));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/Checks", "check", "(II)J", false));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new InsnNode(Opcodes.LMUL));
                        mn.instructions.add(new InsnNode(Opcodes.LADD));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memGetAddress", "(J)J", false));
                        mn.instructions.add(new InsnNode(Opcodes.LRETURN));
                    }
                    else if (mn.name.equals("put") && mn.desc.equals("(J)Lorg/lwjgl/PointerBuffer;")) {
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "address", "J"));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/lwjgl/system/CustomBuffer", "nextPutIndex", "()I", false));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "toUnsignedLong", "(I)J", false));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new InsnNode(Opcodes.LMUL));
                        mn.instructions.add(new InsnNode(Opcodes.LADD));
                        mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutAddress", "(JJ)V", false));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                    else if (mn.name.equals("put") && mn.desc.equals("(IJ)Lorg/lwjgl/PointerBuffer;")) {
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "address", "J"));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/PointerBuffer", "limit", "I"));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/Checks", "check", "(II)J", false));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new InsnNode(Opcodes.LMUL));
                        mn.instructions.add(new InsnNode(Opcodes.LADD));
                        mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 2));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutAddress", "(JJ)V", false));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                    if (mn.name.equals("self") && mn.desc.equals("()Lorg/lwjgl/PointerBuffer;")) {
                        mn.access |= Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE;
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                    if (mn.name.equals("sizeof") && mn.desc.equals("()I")) {
                        mn.access |= Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE;
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
                    }
                    if (mn.name.equals("create") && mn.desc.equals("(JLjava/nio/ByteBuffer;IIII)Lorg/lwjgl/PointerBuffer;")) {
                        mn.access |= Opcodes.ACC_PROTECTED | Opcodes.ACC_BRIDGE;
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "org/lwjgl/PointerBuffer"));
                        mn.instructions.add(new InsnNode(Opcodes.DUP));
                        mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 7));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                            "org/lwjgl/PointerBuffer", "<init>",
                            "(JLjava/nio/ByteBuffer;IIII)V", false));
                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            }
        });

        // 2. Rewrite BufferUtils.createPointerBuffer to avoid LongBuffer internals
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain pd,
                                    byte[] classfileBuffer) {

                if (!className.equals("org/lwjgl/BufferUtils")) return null;

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                for (MethodNode mn : cn.methods) {
                    if (!mn.name.equals("createPointerBuffer")) continue;

                    if (mn.desc.equals("(I)Lorg/lwjgl/PointerBuffer;")) {
                        mn.instructions.clear();
                        mn.tryCatchBlocks.clear();
                        mn.exceptions.clear();

                        // long addr = MemoryUtil.nmemAlloc(size * 8)
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        mn.instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
                        mn.instructions.add(new InsnNode(Opcodes.IMUL));
                        mn.instructions.add(new InsnNode(Opcodes.I2L));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "org/lwjgl/system/MemoryUtil", "nmemAlloc", "(J)J", false));
                        // store addr in local 1 (slots 1+2)
                        mn.instructions.add(new VarInsnNode(Opcodes.LSTORE, 1));

                        // new PointerBuffer(addr, null, -1, 0, size, size)
                        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "org/lwjgl/PointerBuffer"));
                        mn.instructions.add(new InsnNode(Opcodes.DUP));
                        mn.instructions.add(new VarInsnNode(Opcodes.LLOAD, 1));
                        mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_M1));
                        mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                            "org/lwjgl/PointerBuffer", "<init>",
                            "(JLjava/nio/ByteBuffer;IIII)V", false));

                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            }
        });

        // 3. Gut AppletFrame constructor
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain pd,
                                    byte[] classfileBuffer) {

                if (!className.equals("net/fabricmc/loader/impl/game/minecraft/applet/AppletFrame"))
                    return null;

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                for (MethodNode mn : cn.methods) {
                    if (!mn.name.equals("<init>")) continue;

                    InsnList insns = mn.instructions;
                    AbstractInsnNode superCall = null;
                    for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
                        if (insn instanceof MethodInsnNode
                            && ((MethodInsnNode) insn).getOpcode() == Opcodes.INVOKESPECIAL) {
                            superCall = insn;
                            break;
                        }
                    }
                    if (superCall == null) continue;

                    AbstractInsnNode after = superCall.getNext();
                    while (after != null) {
                        AbstractInsnNode next = after.getNext();
                        insns.remove(after);
                        after = next;
                    }

                    mn.tryCatchBlocks.clear();
                    mn.exceptions.clear();
                    insns.add(new InsnNode(Opcodes.RETURN));
                    break;
                }

                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                return cw.toByteArray();
            }
        });

        // 4. No-op Frame.setVisible
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain pd,
                                    byte[] classfileBuffer) {

                if (!className.equals("java/awt/Frame")) return null;

                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                for (MethodNode mn : cn.methods) {
                    if (!mn.name.equals("setVisible") || !mn.desc.equals("(Z)V")) continue;

                    mn.instructions.clear();
                    mn.tryCatchBlocks.clear();
                    mn.exceptions.clear();
                    mn.instructions.add(new InsnNode(Opcodes.RETURN));
                    break;
                }

                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                return cw.toByteArray();
            }
        });
    }
}   