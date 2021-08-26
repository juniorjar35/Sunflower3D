#version 430 core

in vec3 pos;
in vec2 textureCoordinate;
in vec3 normalVector;

out vec2 coordinate;
out vec3 surfaceNormal;
out vec3 outLight;
out vec3 outCamera;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform vec3 lightPosition;
uniform int lighting;

void main(void) {
	vec4 world = transformationMatrix * vec4(pos,1.0);
	gl_Position = projectionMatrix * viewMatrix * world;
	coordinate = textureCoordinate;
	if (lighting == 1) {
		surfaceNormal = (transformationMatrix * vec4(normalVector,0.0)).xyz;
		outLight = lightPosition - world.xyz;
		outCamera = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz - world.xyz;
	}
}
