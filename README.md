# Barium

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

## Performance behavior

Barium dynamically throttles expensive entity-outline rendering (`LevelRenderer#doEntityOutline`) when frame times trend high.

The current strategy:

- Keeps outlines fully enabled while average frame time stays around 60 FPS.
- Uses a short warmup period before throttling to avoid flicker from momentary spikes.
- Increases skip cadence progressively during sustained load (render every 2nd/3rd/4th eligible frame) to recover frametime.
