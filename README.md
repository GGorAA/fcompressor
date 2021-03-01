# FCompressor: a convenient tool for compressing a lot of videos at the same time with no effort

FCompressor has actually born when I needed to compress a
lot of anime series I downloaded to watch on a trip with friends.
Videos were pretty heavy, and also considering that my laptop has only
256 GB of storage, I needed to compress that all. I knew I can use FFMpeg
for compression, but I don't want to constantly start a new process for
compressing every video. I just wanted to start the process, leave
the laptop to do it's work, and then watch the result. And this is where FCompressor has born :D

The name is FCompressor, which is translated to Folder Compressor. Compressor of all videos in a folder. Pretty self-explanatory, huh? :D

## Usage
Very easy, you just pass the input dir, and the output dir, like this:
```bash
java -jar FCompressor.jar "/path/to/input" "/path/to/output" <args>
```
There are also a few args:

`--crf` - passes the Constant Rate Factor. Basically how hard the compression is. It is recommended to not touch this setting. Default is 28.

`--codec` - self-explanatory, sets the ffmpeg codec. By default, it is `libx265`. It is recommended to not touch this setting, only if you know what you do.

Basically you can just pass the input and the output, FCompressor will do the rest. 

## Requirements
Installed ffmpeg, ffprobe, and JRE 11, that's all. That's the only thing you'll need to start FCompressor.

If you don't have ffmpeg and ffprobe, install it using this command:
```bash
brew install ffmpeg
```

---

That's all, I hope you will be satisfied with this tool :D Happy using!