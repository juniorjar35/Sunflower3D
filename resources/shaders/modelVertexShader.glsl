#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in vec3 normalVector;

out vec2 coordinate;
out vec3 surfaceNormal;
out vec3 outLight;
out vec3 outCamera;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform vec3 lightPosition;

void main(void) {
	vec4 world = transformationMatrix * vec4(pos,1.0);
	gl_Position = projectionMatrix * viewMatrix * world;
	coordinate = textureCoordinate;
	surfaceNormal = (transformationMatrix * vec4(normalVector,0.0)).xyz;
	outLight = lightPosition - world.xyz;
	outCamera = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz - world.xyz;
}
