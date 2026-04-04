# 🚀 Barium (for Minecraft 1.21.9)
Barium is a lightweight performance optimization mod for Minecraft 1.21.9. It improves rendering efficiency, chunk loading, and overall game performance while preserving visual quality. Whether you're playing in modded environments or large vanilla worlds, Barium helps ensure smoother gameplay.

## ✅ Key Features
- Improved chunk and block rendering
- Reduced lag spikes and faster world loading
- Compatible with Fabric modpacks
- Ideal for enhancing performance in demanding worlds

## 🧠 Custom renderer Z-prepass pipeline (1.21.9)
Barium now includes an optional Z-prepass hook for **custom opaque geometry** rendered by your own code.

> This does not override vanilla/Sodium world rendering. It runs only for renderers that you register in Barium.

### Enable in config
In Mod Menu → Barium settings, enable:
- `Enable Z-Prepass for Custom Opaque Geometry`
- `Enable Vertex/Backface Culling` (optional)

### Register your opaque renderer
```java
ZPrepassRenderer.registerOpaqueRenderer(() -> {
    // Draw only opaque custom geometry here.
    // Keep vertex transform exactly the same between the two passes.
});
```

Register your renderer once, and Barium runs it from the world render tail when Z-prepass is enabled.

Spark/profiler visibility:
- The pass is wrapped in the profiler section `barium_z_prepass`, so it appears in performance traces when active.

Barium executes:
1. **Depth pass** (color writes disabled, depth writes enabled)
2. **Main pass** (color writes enabled, depth writes disabled, `LEQUAL`)

### Important constraints
- Use only opaque meshes in this path.
- Keep math identical between passes (avoid flicker/z-fighting).
- Gains happen when your fragment shader is expensive and overdraw is high.
