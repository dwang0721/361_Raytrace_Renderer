# 361_Raytrace_Renderer
A ray-tracing renderer implemented in Java:

    1. compile: `javac -cp ./src ./src/client/Main.java`
    2. run:     `java -cp ./src client.Main [filename]`
*[filename]* can be empty, or use any simp file names.


## Lighting Equation
The renderer uses Phong lighting model to calculate pixel values:
![Lighting Equation](https://github.com/dwang0721/361_Raytrace_Renderer/blob/master/pictures/lighting%20model.JPG?raw=true)



## Shading style
There are three options for the rendering style:
1. Flat
2. Gouraud
3. Phong

In flat shading, one averages the vertices of a polygon to find a center point on the primitive. 
If there are normals present at the vertices, 
then the renormalized average of these normals is used the normal for the lighting calculation:

    N = normalize(𝑁1+𝑁2+𝑁3).

Otherwise the face normal is used:

    N = normalize((V2 - V1) dot (V3 – V1)).


### Flat shading Calculation
![Flat](https://github.com/dwang0721/361_Raytrace_Renderer/blob/master/pictures/flat_shading.jpg?raw=true)

>Using the center point with the normal, apply the lighting model to get a lighting value (RGB vector). 
The lighting-calculation is using the same color for all pixels in the polygon. 

### Gouraud shading Calculation
![Gouraud](https://github.com/dwang0721/361_Raytrace_Renderer/blob/master/pictures/gouraud_shading.JPG?raw=true)
>At each vertex of a polygon, find a normal. If the vertex is from an obj file and has a specified normal, use it. 
Otherwise, the face normal is at every vertex.
At each vertex, apply the lighting model to get a lighting value. Then blerp these lighting values across the polygon, 
using the blerped value as the pixel value at each pixel.

### Phong shading Calculation
![Phong](https://github.com/dwang0721/361_Raytrace_Renderer/blob/master/pictures/phong_shading.JPG?raw=true)
Find a normal at each vertex as with gouraud shading. 
Normals are blerped, so as the camera space points, and color (if necessary) across the polygon. 
At each pixel, used the blerped point, (renormalized) normal, and color in the lighting model to obtain a pixel value for the pixel.


## Render Obj file
This renderer can import obj files and choose which shading style to use. Here is an example:
![Megaman](https://github.com/dwang0721/361_Raytrace_Renderer/blob/master/pictures/megaman_X.JPG?raw=true)
