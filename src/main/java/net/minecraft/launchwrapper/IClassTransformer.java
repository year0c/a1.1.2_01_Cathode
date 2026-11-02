package net.minecraft.launchwrapper;

public interface IClassTransformer {
    byte[] transform(String name, String fullName, byte[] classBytes);
}   