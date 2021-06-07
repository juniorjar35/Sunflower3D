#version 330 core

in vec2 coordinate;

out vec4 color;

uniform sampler2D textureImage;

void main(void){
	color = texture(textureImage, coordinate);
}
