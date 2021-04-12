
# Project Title
RuneScape Classic in LWJGL

## Description
A crude attempt to rebuild RuneScape Classic in OpenGL, using LWJGL 2.x .. The application reads the jagex cache files (thanks to RSC-Remastered for the code that does that) and then constructs a 3D world using the data. Consider this project more of a proof of concept, or a "3D cache debugger" than an actual game, because it's FAR from playable, and far from even performing the basics properly.
Some issues include: the map data seems to be flipped? diagonal walls weren't finished. sector edge tiles need interpolated to connect properly. some things use GL immediate mode (faster development, but bad performance). See NOTE.txt for more information / details.

## Getting Started
The project is ready to run in Eclipse, though, you can use any IDE you want.

## Screenshot
![alt text](https://github.com/rscgl/rsc_lwjgl/blob/master/screenshot.png)


## License
This project is unlicensed. Do whatever you want with it. If you end up forking and getting somewhere, please send me a screenshot I'd love to see it!

## Acknowledgments
* [Morgue](https://github.com/ModsByMorgue) Creator of the lwjgl engine. Merged RSC-Remastered with LWJGL. Finally managed to get the basics started after multiple failed attempts... I'm not familiar with RSC client code at all so this project was far from easy, but it really a great learning experience. I know a LOT about the RSC client now.
* [RSC-Remastered](https://github.com/Danjb1/rsc-remastered/) Dan's project provided a lot of helpful code, which I was able to integrate into RSCGL. This includes basically everything related to reading and managing jagex cache data, sector loading, and a lot more of the behind-the-scenes data that gets stored in memory and is used to construct the 3D world.
* [OpenRSC Community](https://github.com/Open-RSC) I referred to ORSC for variable naming, and other miscellnarous information because the ORSC team has done a great job with their open source client. I also took their audio to add sounds to RSCGL.

## Special Thanks
This project wouldn't have been possible without the help from OpenRSC discord members, and other RSC developer repositories.
* [ORSC]Luis provided me with a dump of all RSC 3D models in .obj format model, and while they're not used in RSCGL yet, they are there if I ever add GameObject support to the ptoject.
* [ORSC]Logg provided a lot of miscellaneous information about RSC client quirks, and other feedback which helped me do my tasks.
* [ORSC]pyramin provided a LOT of amazingly useful information, such as tile colors, wall data, and general details about how the RSC client used data from the jagex cache. He/she was building an RSC map editor so the information was plentiful.
* [ORSC]Marwolf provided a link to a random 2003scape repo which had dumped all of the RSC textures. I used these to texture the game world in RSCGL.
* [2003scape]Repository provided a lot of various assets.
* [RSC-Remastered]Repository provided the foundation RSCGL used to read jagex cache information, and storing it in memory. 
