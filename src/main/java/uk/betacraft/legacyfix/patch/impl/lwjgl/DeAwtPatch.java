package uk.betacraft.legacyfix.patch.impl.lwjgl;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class DeAwtPatch {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined, ProtectionDomain pd,
                                    byte[] classfileBuffer) {

                if (!className.equals("java/awt/Frame") && !className.equals("java/awt/Canvas"))
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

                    insns.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                    insns.add(new LdcInsnNode("(AWT) Killed."));
                    insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                    insns.add(new InsnNode(Opcodes.RETURN));
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                cn.accept(cw);
                return cw.toByteArray();
            }
        });
    }
}   