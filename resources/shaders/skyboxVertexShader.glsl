#version 330 core

in vec3 pos;
out vec3 tc;

uniform mat4 pm;
uniform mat4 vm;

void main(void){
	gl_Position = pm * vm * vec4(pos,1.0);
	tc = pos;
}