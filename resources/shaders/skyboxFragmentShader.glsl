#version 330 core

in vec3 tc;
out vec4 color;

uniform samplerCube cube;

void main(void){
	color = texture(cube,tc);
}