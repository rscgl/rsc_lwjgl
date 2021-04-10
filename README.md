
# Project Title
RuneScape Classic in LWJGL

## Description
A crude attempt to rebuild RuneScape Classic in OpenGL, using LWJGL 2.x .. The application reads the jagex cache files (thanks to RSC-Remastered for the code that does that) and then constructs a 3D world using the data. Consider this project more of a proof of concept, or a "3D cache debugger" than an actual game, because it's FAR from playable, and far from even performing the basics properly.
Some issues include: the map data seems to be flipped? diagonal walls weren't finished. sector edge tiles need interpolated to connect properly. some things use GL immediate mode (faster development, but bad performance). See NOTE.txt for more information / details.

## Getting Started
The project is ready to run in Eclipse, though, you can use any IDE you want.

## Screenshot
![alt text](http://url/to/img.png)


## License
This project is unlicensed. Do whatever you want with it. If you end up forking and getting somewhere, please send me a screenshot I'd love to see it!

## Acknowledgments
Information, code snippets, etc.
* [Morgue](https://.github.com/ModsByMorgue)
* [RSC-Remastered](https://github.com/Danjb1/rsc-remastered/)
* [OpenRSC Community](https://github.com/Open-RSC)