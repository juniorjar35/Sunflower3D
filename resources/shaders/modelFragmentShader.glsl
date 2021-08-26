#version 430 core

#define AMBIENT_LIGHT 0.12f

layout(location = 0) out vec3 fbcolor;

in vec2 coordinate;
in vec3 surfaceNormal;
in vec3 outLight;
in vec3 outCamera;

out vec4 color;

uniform sampler2D textureImage;
uniform vec3 lightColor;
uniform float reflectivity;
uniform float shineAngle;
uniform int lighting;

vec4 lightingCalc(vec3 light, vec3 surfaceNormal, vec3 cameraDirection, vec3 lightColor, float shineAngle, float reflectivity, sampler2D textureImage, vec2 coordinate, float ambient){
	vec3 nLight = normalize(light);
	vec3 nNormal = normalize(surfaceNormal);
	vec3 nCamera = normalize(cameraDirection);
	float lightDot = dot(nNormal, nLight);
	float specular = max(dot(reflect(-nLight, nNormal), nCamera), 0.0);
	float SpecAngled = pow(specular, shineAngle);
	return vec4(max(lightDot, ambient) * lightColor, 1.0) * texture(textureImage, coordinate) + vec4(SpecAngled * reflectivity * lightColor, 1.0);
}


void main(void) {
	if (lighting == 1) {
		color = lightingCalc(outLight,surfaceNormal,outCamera, lightColor, shineAngle, reflectivity,textureImage,coordinate,AMBIENT_LIGHT);
	} else {
		color = texture(textureImage, coordinate);
	}
}
