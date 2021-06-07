#version 330 core



layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 textureCoordinate;



out vec2 coordinate;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(pos,1.0f);
	coordinate = textureCoordinate;
}
