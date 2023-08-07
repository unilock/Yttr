#version 150

#moj_import <fog.glsl>
#moj_import <light.glsl>

in vec3 Position;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform vec4 ColorModulator;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 normal;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, normal.xyz, ColorModulator);
    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
}
