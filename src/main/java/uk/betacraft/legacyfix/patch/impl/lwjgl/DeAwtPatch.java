package uk.betacraft.legacyfix.patch.impl.lwjgl;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class DeAwtPatch {

    public static void premain(String args, Instrumentation inst) {
        // Gut AppletFrame constructor (keep super, remove the rest)
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

        // No-op Frame.setVisible — safety net
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

        inst.addTransformer(new io.github.moehreag.legacylwjgl3.LwjglRedirectTransformer());
    }
}   