package io.github.moehreag.legacylwjgl3;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public class LwjglRedirectTransformer implements IClassTransformer {

    private static final String FROM = "org/lwjgl";
    private static final String TO   = "org/lwjglx";

    @Override
    public byte[] transform(String name, String fullName, byte[] classBytes) {
        if (fullName.startsWith(TO + "/")) return classBytes;
        if (fullName.startsWith("java/")) return classBytes;
        if (fullName.startsWith("org/lwjgl/")) return classBytes;

        if (!containsRef(classBytes, FROM)) return classBytes;

        Remapper remapper = new Remapper(Opcodes.ASM9) {
            @Override
            public String map(String internalName) {
                if (internalName.startsWith(FROM + "/")) {
                    return TO + internalName.substring(FROM.length());
                }
                return internalName;
            }
        };

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new ClassRemapper(cw, remapper), 0);
        return cw.toByteArray();
    }

    private static boolean containsRef(byte[] bytes, String ref) {
        return new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1).contains(ref);
    }
}