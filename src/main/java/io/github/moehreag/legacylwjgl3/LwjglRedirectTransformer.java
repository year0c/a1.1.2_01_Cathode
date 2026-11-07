package io.github.moehreag.legacylwjgl3;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class LwjglRedirectTransformer implements IClassTransformer, ClassFileTransformer {

    private static final String OLD_PB = "org/lwjgl/PointerBuffer";
    private static final String NEW_PB = "org/lwjglx/PointerBuffer";

    private static final String[] EXCLUDE = {
        "java/",
        "org/lwjglx/",
        "pl/tomgirl/pylon/",
        "io/github/year0c/cathode/",
        "io/github/moehreag/legacylwjgl3/",
        "uk/betacraft/legacyfix/",
    };

    @Override
    public byte[] transform(String name, String fullName, byte[] classBytes) {
        return doTransform(fullName, classBytes);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain pd,
                            byte[] classfileBuffer) {
        return doTransform(className, classfileBuffer);
    }

    private byte[] doTransform(String fullName, byte[] classBytes) {
        if (classBytes == null) return null;

        if (fullName.equals(OLD_PB)) return classBytes;
        if (fullName.equals(NEW_PB)) return classBytes;

        for (String prefix : EXCLUDE) {
            if (fullName.startsWith(prefix)) return classBytes;
        }

        if (!containsRef(classBytes, OLD_PB)) return classBytes;

        Remapper remapper = new Remapper(Opcodes.ASM9) {
            @Override
            public String map(String internalName) {
                if (internalName.equals(OLD_PB)) return NEW_PB;
                return internalName;
            }
        };

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);   
        cr.accept(new ClassRemapper(cw, remapper), 0);
        return cw.toByteArray();
    }

    private static boolean containsRef(byte[] bytes, String ref) {
        return new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1).contains(ref);
    }
}   