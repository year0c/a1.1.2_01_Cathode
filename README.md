# Cathode

A backport of SDL rendering (LWJGL 3.4.1) to **Minecraft a1.1.2_01**.

## Credits

Special thanks to [Moehreag](https://github.com/moehreag) for
[legacy-lwjgl3](https://github.com/moehreag/legacy-lwjgl3), which made this
backport possible.

## Setup

Cathode is a standalone backport, not a drop-in mod. To use it:

1. Set your **LWJGL3 version to 3.4.1** in the launcher.
2. Add the following JVM argument to disable AWT:
-Djava.awt.headless=true

Both are required — without them, the mod will not run.   
