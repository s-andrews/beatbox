BeatBox
=======

Background
----------

BeatBox is a command line application which analyses cellular beating patterns in microsopy images.

It takes in an nd2 format image stack file and looks at the pixel intensities for each pixel in the image over the frames of the stack to look for periodic oscillations induced by celluar beating such as the ones shown in the very zoomed in image below.

![Cellular Beating](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/cellbeating.gif)

For each pixel in the image the program will report the estimated duration of the beating pattern along with some metrics to allow you to guage the signal to noise ratio and the accuracy of the reported periodicity value.

Installation
------------

BeatBox is distributed as a single java jar file. You will also need an installation of a Java Runtime Environment such as those available from https://adoptium.net/.

BeatBox makes use of the [BioFormats](https://www.openmicroscopy.org/bio-formats/) libraries for reading nd2 files.


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

  --framerate     The number of frames per second. Taken from the metadata by default.  Set to 20 if not found
  
  --smoothing     The number of frames to use for background smoothing subtraction (default 50)

  input_file      Either the location of a single nd2 format file or a folder
                  containing text of extracted pixel intensities with one
                  file per frame.

  output_file     A text file into which to write the output
```

Output
------

The program's output is an 8 column text file where the columns are:

1. X pixel position
2. Y pixel position
3. Frequency Mean (Hz)
4. Frequency Median (Hz)
5. Frequency Mode (Hz)
6. Average pixel intensity
7. Periodicity Signal Strength
8. Frequency Standard Deviation


Algorithm
---------

BeatBox analyses the set of per-frame intensity values for each pixel separately. 

It starts by calculating a rolling mean to smooth out any overall shifts in image intensity over time.  The amount of smoothing can be controlled by setting the number of frames to smooth over with the ```--smoothing``` option.

![Background Smoothing](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/background_smoothing.png)

Then it identifies the places where the signal crosses the smoothed background and takes the time between crossings.

![Background Smoothing](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/crossing_points.png)

We then calculate the mean median and mode of the crossing point times.  These are then doubled (because one cycle is two crossings) and multipled by the frame time set in ```--frametime```.  We also calculate the Standard Deviation of the crossing times and the mean absolute difference for the signal from the smoothed signal to get an idea of the amount of intensity variation.


Analysis
--------

We can see that the signal measure will show that some parts of your image are more informative for periodicity calculation than other parts.

![Positional Signal](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/signal.png)

The periodicity values will vary based on the signal.  At low signal levels you will get short periods which likely just represent noise.  The true periodicity will emerge at higher signal levels.

![Signal vs Periodicity](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/signal_vs_periodicity.png)

You will also see the positional periodicity follows the same pattern as the signal.

![Positional Periodicity](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/positional_periodicity.png)

A simple summary of the periodicity for an image can be generated by filtering on signal and then taking the mean of any of the metrics which are provided.  You can also look at their distribution to show how stable the measurements are.

![Periodicity Distribution](https://raw.githubusercontent.com/s-andrews/beatbox/main/uk/ac/babraham/Beatbox/Help/periodicity_distribution.png)


Problems / Bugs
---------------

If you have any questions about this software or have any problems using it please either report these as an issue in our github repository, or send them directly to simon.andrews@babraham.ac.uk.






