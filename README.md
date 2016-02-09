# robit

Working A Star implementation on a grid, however, using a shape for our 'robot' along with polygonal obstacles. Seems to work okay, still needs work. Since I need a hardcopy of the source I've put up a [LaTeX](robot.tex) document and [PDF](robot.pdf).

![A Star](http://s.idied.rip/view/56b9b163ce3a1.png)

### note

The proper solution when faced with polygonal obstacles and whatnot is to use a navigation mesh with offsets for the shape and size of the robot itself. However, this is pretty difficult and I had only two weeks to learn most of the relevant bits for JavaFX and make it work so it's good enough. _Acceptable_ is the key word here.

The other thing to note is the path A* will generate on a grid might not always be the most straightforward. This is because some F values may tie, that is where the tie breaking modification comes into play. It helps reduce the number of tying F values so you A* does not explore much when it has a direct LOS with the goal. However, it'll still do some funky stuff that seems off. Unfortunately there's not too much you can do about that.