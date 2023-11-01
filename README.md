BeatBox
=======

Background
----------

BeatBox is a command line application which analyses cellular beating patterns in microsopy images.

It takes in an nd2 format image stack file and looks at the pixel intensities for each pixel in the image over the frames of the stack to look for periodic oscillations induced by celluar beating.

For each pixel in the image the program will report the estimated duration of the beating pattern along with some metrics to allow you to guage the signal to noise ratio and the accuracy of the reported periodicity value.

Installation
------------

BeatBox is distributed as a single java jar file. You will also need an installation of a Java Runtime Environment such as those available from https://adoptium.net/.


Running
-------

BeatBox is run in a command line environment.  It should work on any Operating System with a Java Runtime Environment.

```java -jar beatbox.jar --help```

```
BeatBox - Periodicity Analysis for Microscopy Images

Usage:
  java -jar beatbox.jar [--quiet] [--help] [--frametime 1] [--smoothing 50] [input_file] [output_file]

Options:

  --quiet         Suppress all progress messages

  --help          Print this help and then exit

  --version       Print the program version and exit

  --frametime     The duration of one frame (default 1)
  
  --smoothing     The number of frames to use for background smoothing subtraction (default 50)

  input_file      Either the location of a single nd2 format file or a folder
                  containing text of extracted pixel intensities with one
                  file per frame.

  output_file     A text file into which to write the output
```

Output
------

The program's output is a 4 column text file where the columns are:

1. The X position for the image
2. The Y position for the image
3. The mean inter-peak periodicity time for the pixel
4. The median inter-peak periodicity time for the pixel
5. The mode inter-peak periodicity time for the pixel
4. The signal strength variation - how much the pixel intensity deviates from the smoothed mean
5. The standard deviation of the periodicity intervals 









